package michal.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.GregorianCalendar;

import Framework.Gps.GpsTag;
import Framework.Gps.LocationManager;
import Framework.MapHelpers.DrawRoute;
import Framework.MapHelpers.MapRotator;

public class OverviewScreen extends AppCompatActivity implements OnMapReadyCallback {

    public static final String TAG = OverviewScreen.class.getSimpleName();

    private ParkedCar parkedCar;


    //UI ELEMENTS

    private Button      navigateToCarButton;
    private TextView parkedCarString;

    private GoogleMap map;
    private GpsTag currentLocation;
    private GpsTag parkingLocation;
    private LocationManager locationManager;
    private MapRotator mapRotator;
    final Handler h = new Handler();


    public void updateMarker(GpsTag location){
        map.clear();
        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        /*map.addMarker(new MarkerOptions().
                position(currentPosition)
                .title("You are here"));
        */
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 15.0f));
    }

    public void updateLocation(){

        // wait for the gpsManager to be connected
        if(locationManager.isReady()) {

            //adding a name because of the framework specification
            currentLocation = locationManager.getCurrentLocation("parkedCarLocation");

            mapRotator.updateDeclination(currentLocation);
            updateMarker(currentLocation);

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

        parkedCarString = (TextView) findViewById(R.id.parkedCarString);

        Bundle b = this.getIntent().getExtras();



        if(b !=null){
            parkedCar = (ParkedCar) b.getSerializable("parkedCar");

            parkedCarString.setText(parkedCar.toString());
        }

        //WIRE UI ELEMENTS

        navigateToCarButton =      (Button)    findViewById(R.id.navigateToCarButton);



        //initialise GPSManager - start listening for location
        locationManager = LocationManager.getInstance(this);


        //UI ACTIONS
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);


        final int delay = 1000; //milliseconds

        h.postDelayed(new Runnable() {
            public void run() {
                //do something
                Log.i(TAG,"locationUpdated");
                updateLocation();
                h.postDelayed(this, delay);

            }
        }, delay);
    }


    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_park_car_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);

        // start automatic mapRotation
        mapRotator = new MapRotator(this,map);
        updateLocation();
    }

}
