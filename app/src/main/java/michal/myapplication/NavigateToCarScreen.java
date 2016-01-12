package michal.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import Framework.Gps.GpsTag;
import Framework.Gps.LocationManager;
import Framework.MapHelpers.DrawRoute;
import Framework.MapHelpers.MapRotator;
import michal.myapplication.Utilities.AlertDialogues;
import Framework.MapHelpers.Utils;

public class NavigateToCarScreen extends AppCompatActivity implements OnMapReadyCallback {

    public static final String TAG = NavigateToCarScreen.class.getSimpleName();

    private ParkedCar parkedCar;


    //UI ELEMENTS
    private Switch navigationModeSwitch;
    private TextView    notesContent;
    private Button  foundMyCarButton;

    private boolean navigationMode = true;

    private GoogleMap map;
    private GpsTag currentLocation;
    private GpsTag parkingLocation;
    private LocationManager locationManager;
    private MapRotator mapRotator;
    final Handler h = new Handler();

    /**
     *  Called when using Navigation mode
     *
     *  Close zoom, and tilt to simulate sat nav mode
     */
    public void updateMapDisplay(){

        if(currentLocation!=null) {

            map.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(Utils.toLatLng(currentLocation))
                                    .tilt(60)
                                    .zoom(20.0f)
                                    .build())
            );
        }
    }

    /**
     *  Retrieves new current location and if it's different to the one already stored - update the view
     */
    public void updateLocation(){

        // wait for the gpsManager to be ready - can get current location
        if(locationManager.isReady()) {

            //adding a name because of the framework specification
            GpsTag newLocation = locationManager.getCurrentLocation(ParkedCar.PARKED_CAR_LOCATION);


            //only update when location changes
            if(!GpsTag.isSameLocation(currentLocation,newLocation)){
                currentLocation = newLocation;
                mapRotator.updateDeclination(currentLocation);

                if(parkingLocation!=null){
                    DrawRoute.drawRoute(currentLocation, parkingLocation, map, getApplicationContext());
                }

                if(navigationMode) {
                    updateMapDisplay();
                }
            }else{
                Log.i(TAG, "Location hasn't changed");
            }

        }else{
            Log.i(TAG, "GPSmanager is still not connected");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate_to_car_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //  WIRE UI ELEMENTS
        navigationModeSwitch = (Switch)         findViewById(R.id.navigationModeSwitch);
        notesContent = (TextView)               findViewById(R.id.notesContent);
        foundMyCarButton =         (Button)     findViewById(R.id.carFoundButton);

        // UI ACTIONS
        // Switch between navigation and free view modes
        navigationModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    changeToNavigationMode();
                } else {
                    changeToFreeViewMode();
                }
            }
        });
        //  Found my car - delete parking information and go back to ParkCarScreen
        foundMyCarButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deleteParkingInformation();
            }
        });


        //  get the ParkedCar from Overview Screen
        Bundle b = this.getIntent().getExtras();
        if(b !=null){
            parkedCar = (ParkedCar) b.getSerializable("parkedCar");

            parkingLocation = parkedCar.getLocation();
            String parkingNotes = parkedCar.getNotes();

            if(!parkingNotes.isEmpty()){
                notesContent.setText(parkingNotes);
            }

            //  initialise GPSManager - start listening for location
            locationManager = LocationManager.getInstance(this);

            // GET MAP
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);


            //  check if there's a location update every 3 seconds
            final int delay = 3000; //milliseconds
            h.postDelayed(new Runnable() {
                public void run() {
                    //do something
                    updateLocation();
                    h.postDelayed(this, delay);
                }
            }, delay);

        }else{
            Log.e(TAG, "No parkedCar from previous activity was persisted");
            AlertDialogues.getNoCarSavedBackToParkCarScreen(this).show();
        }
    }

    @Override
    /**
     *  Triggered when the map is ready
     *  By default starts in navigation mode
     *
     */
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(false);

        // add parking location marker
        map.addMarker(new MarkerOptions().position(Utils.toLatLng(parkingLocation)));

        // start automatic mapRotation
        mapRotator = new MapRotator(this,map);

        // update the location

        updateLocation();
        // start by default in navigation mode
        changeToNavigationMode();
    }

    /**
     * Navigation mode - constantly updates the view
     *
     * User is restricted from moving around or zooming
     *
     * Calls updateMapDisplay - default function - close zoom, tilted view, like satnav
     */
    public void changeToNavigationMode(){
        map.getUiSettings().setRotateGesturesEnabled(false);
        map.getUiSettings().setTiltGesturesEnabled(false);
        map.getUiSettings().setScrollGesturesEnabled(false);
        map.getUiSettings().setZoomGesturesEnabled(false);
        map.getUiSettings().setZoomControlsEnabled(false);

        navigationMode = true;

        //force map update
        updateMapDisplay();

    }

    /**
     *  Free view mode - starts with a view that shows both current location and parking location
     *
     *  User can zoom and move around - location update does not override the view
     */
    public void changeToFreeViewMode(){
        // enable user to scroll and zoom - no rotation allowed (already done automatically)
        map.getUiSettings().setRotateGesturesEnabled(false);
        map.getUiSettings().setScrollGesturesEnabled(true);
        map.getUiSettings().setZoomGesturesEnabled(true);

        navigationMode = false;


        if(currentLocation!=null && parkingLocation!=null) {

            // make sure the view includes both the current location and parking location

            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            builder.include(Utils.toLatLng(currentLocation));
            builder.include(Utils.toLatLng(parkingLocation));

            LatLngBounds bounds = builder.build();
            int padding = 150;

            CameraUpdate fitZoom = CameraUpdateFactory.newLatLngBounds(bounds,padding);

            map.moveCamera(fitZoom);

        }else {
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
            map.moveCamera(zoom);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    /**
     *  Method that deals with the settings drawer
     *  Opens up map type selector dialogue
     */
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.select_map_type) {

            if(map!=null) {
                Utils.getMapTypeSelectorDialog(map, NavigateToCarScreen.this).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Delete parked car file and navigate back to ParkCarScreen
     */
    public void deleteParkingInformation(){
        //  delete parked car file from internal storage
        parkedCar.delete(getApplicationContext());

        //  navigate to ParkCarScreen
        Intent intent = new Intent(this,ParkCarScreen.class);
        startActivity(intent);
    }

}
