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
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import Framework.Gps.GpsTag;
import Framework.Gps.LocationManager;
import Framework.MapHelpers.MapRotator;
import Framework.MapHelpers.Utils;

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
            GpsTag newLocation = locationManager.getCurrentLocation("parkedCarLocation");


            //only update when location changes
            if(!GpsTag.isSameLocation(currentLocation,newLocation)){
                currentLocation = newLocation;
                mapRotator.updateDeclination(currentLocation);
                updateMarker(currentLocation);
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


        final int delay = 3000; //milliseconds

        h.postDelayed(new Runnable() {
            public void run() {
                //do something
                updateLocation();
                h.postDelayed(this, delay);

            }
        }, delay);


        navigateToCarButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                navigateToCar();
            }
        });


    }

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

        // start automatic mapRotation
        mapRotator = new MapRotator(this,map);
        updateLocation();
    }

}
