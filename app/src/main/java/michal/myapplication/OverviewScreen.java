package michal.myapplication;

import android.content.Context;
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
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import Framework.Gps.GpsTag;
import Framework.Gps.LocationManager;
import Framework.MapHelpers.MapRotator;
import Framework.MapHelpers.Utils;
import michal.myapplication.Utilities.AlertDialogues;

public class OverviewScreen extends AppCompatActivity implements OnMapReadyCallback {

    public static final String TAG = OverviewScreen.class.getSimpleName();

    private ParkedCar parkedCar;


    //UI ELEMENTS

    private Button      navigateToCarButton;
    private Button      deleteParkingInformationButton;
    private TextView    parkStartContent;
    private TextView    parkEndContent;
    private TextView    currentFeeContent;
    private TextView    estimatedFeeContent;
    private TextView    notesContent;

    private GoogleMap map;
    private GpsTag currentLocation;
    private GpsTag parkingLocation;
    private LocationManager locationManager;
    private MapRotator mapRotator;
    final Handler h = new Handler();


    public void updateMarker(GpsTag location){
       LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 15.0f));
    }

    public void updateLocation(){

        // wait for the gpsManager to be ready - can get current location
        if(locationManager.isReady()) {

            //adding a name because of the framework specification
            GpsTag newLocation = locationManager.getCurrentLocation(ParkedCar.PARKED_CAR_LOCATION);


            //only update when location changes
            if(!GpsTag.isSameLocation(currentLocation,newLocation) ){
                currentLocation = newLocation;
                mapRotator.updateDeclination(currentLocation);
                updateMarker(currentLocation);

                showCurrentLocationAndParkedCar();
            }

        }else{
            Log.i(TAG, "GPSmanager is still not connected");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview_screen);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //  initialise GPSManager - start listening for location
        locationManager = LocationManager.getInstance(this);

        //  WIRE UI ELEMENTS
        navigateToCarButton =      (Button)    findViewById(R.id.navigateToCarButton);
        deleteParkingInformationButton = (Button) findViewById(R.id.deleteParkingLocationButton);
        parkStartContent    =   (TextView)  findViewById(R.id.parkStartTimeContent);
        parkEndContent      =   (TextView)  findViewById(R.id.parkEndTimeContent);
        notesContent        =   (TextView)  findViewById(R.id.notesContent);
        currentFeeContent   =   (TextView)  findViewById(R.id.currentFeeContent);
        estimatedFeeContent =   (TextView)  findViewById(R.id.estimatedFeeContent);



        //  GET PARKED CAR OBJECT
        Bundle b = this.getIntent().getExtras();
        if(b !=null){
            // THE PARKED CAR OBJECT WAS PERSISTED THROUGH BUNDLE FROM PREVIOUS ACTIVITY

            parkedCar = (ParkedCar) b.getSerializable("parkedCar");

        }else{
            // WE TRY TO GET THE PARKED CAR OBJECT FROM FILE
            parkedCar = ParkedCar.read(this);

            if(parkedCar == null){
                Log.e(TAG, "No parkedCar from previous activity was persisted");
                AlertDialogues.getNoCarSavedBackToParkCarScreen(this).show();
            }

        }

        parkingLocation = parkedCar.getLocation();
        updateTextViews(parkedCar);

        //  GET MAP
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        //  UPDATE LOCATION EVERY 3 SECONDS
        final int delay = 3000; //milliseconds
        h.postDelayed(new Runnable() {
            public void run() {
                //do something
                updateLocation();
                h.postDelayed(this, delay);

            }
        }, delay);

        //  UI ACTIONS
        navigateToCarButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                navigateToCar();
            }
        });

        deleteParkingInformationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deleteParkingInformation();
            }
        });


    }

    private void updateTextViews(ParkedCar parkedCar){
        parkStartContent.setText(parkedCar.getStartTime());
        parkEndContent.setText(parkedCar.getEndTime());
        String openDayModeDisclaimer = "FREE";
        if(parkedCar.isOpenDayMode()){
            currentFeeContent.setText(openDayModeDisclaimer);
            estimatedFeeContent.setText(openDayModeDisclaimer);
        }else {
            currentFeeContent.setText(parkedCar.calculateFeeSoFar());
            estimatedFeeContent.setText(parkedCar.calculateEstimatedTotalFee());
        }
        if(!parkedCar.getNotes().isEmpty()){
            notesContent.setText(parkedCar.getNotes());
        }

    }
    /**
     * Delete parked car file and return to ParkCarScreen
     */
    public void deleteParkingInformation(){
        //  delete parked car file from internal storage
        parkedCar.delete(getApplicationContext());

        //  navigate to ParkCarScreen
        Intent intent = new Intent(this,ParkCarScreen.class);
        startActivity(intent);
    }

    /**
     * Pass the parkedCar object to NavigateToCarScreen activity
     */
    public void navigateToCar(){
        Bundle b = new Bundle();
        Intent intent = new Intent(this, NavigateToCarScreen.class);
        b.putSerializable("parkedCar", parkedCar);
        intent.putExtras(b);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    // Deals with changing map type from the settings menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.select_map_type) {
            if(map!=null) {
                Utils.getMapTypeSelectorDialog(map, OverviewScreen.this).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setCompassEnabled(false);

        map.addMarker(new MarkerOptions().
                position(Utils.toLatLng(parkingLocation))
                .title("Your car"));

        // start automatic mapRotation
        mapRotator = new MapRotator(this,map);
        updateLocation();
    }

    public void showCurrentLocationAndParkedCar(){

        if(currentLocation!=null && parkingLocation!=null) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            builder.include(Utils.toLatLng(currentLocation));
            builder.include(Utils.toLatLng(parkingLocation));

            LatLngBounds bounds = builder.build();
            int padding = 50;

            CameraUpdate fitZoom = CameraUpdateFactory.newLatLngBounds(bounds,padding);

            map.moveCamera(fitZoom);

        }else {
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
            map.moveCamera(zoom);
        }
    }

}
