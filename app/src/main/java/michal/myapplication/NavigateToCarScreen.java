package michal.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import Framework.Gps.GpsTag;
import Framework.Gps.LocationManager;
import Framework.MapHelpers.DrawRoute;
import Framework.MapHelpers.MapRotator;
import michal.myapplication.Utilities.AlertDialogues;

public class NavigateToCarScreen extends AppCompatActivity implements OnMapReadyCallback {

    public static final String TAG = NavigateToCarScreen.class.getSimpleName();

    private ParkedCar parkedCar;


    //UI ELEMENTS

    private Button      navigateToCarButton;
    private GoogleMap map;
    private GpsTag currentLocation;
    private GpsTag parkingLocation;
    private LocationManager locationManager;
    private MapRotator mapRotator;
    final Handler h = new Handler();


    public void updateMarker(GpsTag location){
        map.clear();
        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 15.0f));
    }

    public void updateLocation(){

        // wait for the gpsManager to be connected
        if(locationManager.isReady()) {

            //adding a name because of the framework specification
            GpsTag newLocation = locationManager.getCurrentLocation("parkedCarLocation");


            //only update when location changes
            if(!GpsTag.isSameLocation(currentLocation,newLocation)){
                currentLocation = newLocation;
                mapRotator.updateDeclination(currentLocation);

                if(parkingLocation!=null){
                    DrawRoute.drawRoute(currentLocation, parkingLocation, map, getApplicationContext());
                }
                updateMarker(currentLocation);
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


        // GET MAP
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //  get the ParkedCar from previous activity
        Bundle b = this.getIntent().getExtras();
        if(b !=null){
            parkedCar = (ParkedCar) b.getSerializable("parkedCar");

            parkingLocation = parkedCar.getLocation();

            //  initialise GPSManager - start listening for location
            locationManager = LocationManager.getInstance(this);

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
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(false);

        // start automatic mapRotation
        mapRotator = new MapRotator(this,map);
        updateLocation();
    }

}
