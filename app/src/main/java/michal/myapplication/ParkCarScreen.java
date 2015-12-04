package michal.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

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

public class ParkCarScreen extends AppCompatActivity  implements OnMapReadyCallback{

    public static final String TAG = ParkCarScreen.class.getSimpleName();
    private String              myApiKey ="AIzaSyDjMCLbxy0wmqr1SbuMDo8W7SRn8flWIqw";


    //UI ELEMENTS
    private EditText    desiredDurationEdit;
    private EditText    notesEdit;
    private Button      updateLocationButton;
    private CheckBox    openDayModeCheckbox;
    private Button      saveCarLocButton;
    private Button      drawRouteButton;

    private GoogleMap map;
    private GpsTag              currentLocation;
    private GpsTag              parkingLocation;
    private LocationManager     locationManager;
    private MapRotator          mapRotator;





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
            Log.i(TAG,"GPSmanager is still not connected");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_car_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //WIRE UI ELEMENTS
        desiredDurationEdit =   (EditText)  findViewById(R.id.desDurEdit);
        notesEdit =             (EditText)  findViewById(R.id.notesEdit);
        updateLocationButton =  (Button)    findViewById(R.id.updateLocation);
        openDayModeCheckbox =   (CheckBox)  findViewById(R.id.openDayCheckbox);
        saveCarLocButton =      (Button)    findViewById(R.id.saveCarLocButton);
        drawRouteButton =       (Button)    findViewById(R.id.drawRouteButton);


        //initialise GPSManager - start listening for location
        locationManager = new LocationManager(this);

        //hardcoded parking location for testing purposes
        parkingLocation = new GpsTag("parkingLocation",52.623247,1.241826,29);


        //UI ACTIONS
        //update location when 'UPDATE LOCATION' button is clicked
        updateLocationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateLocation();
            }
        });

        saveCarLocButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                parkCar();
            }
        });

        drawRouteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateLocation();
                DrawRoute.drawRoute(currentLocation, parkingLocation, map, getApplicationContext());
            }
        });


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);


    }

    public void parkCar(){
        //get info from forms
        //TODO: input-check
        int desiredDuration  = Integer.parseInt(desiredDurationEdit.getText().toString());
        String notes = notesEdit.getText().toString();
        boolean openDayMode = openDayModeCheckbox.isChecked();

        //set timeParked to current time
        GregorianCalendar timeParked = new GregorianCalendar();


        ParkedCar parkedCar = ParkedCar.getInstance();

        parkedCar.setDesiredDuration(desiredDuration);
        parkedCar.setOpenDayMode(openDayMode);
        parkedCar.setNotes(notes);
        parkedCar.setParkTime(timeParked);

        locationManager.storeGpsLocation("parkedCarLocation", currentLocation);

        Intent intent = new Intent(this, OverviewScreen.class);
        Bundle b = new Bundle();
        b.putSerializable("parkedCar", parkedCar);
        intent.putExtras(b);

        startActivity(intent);

    }
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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);

        // start automatic mapRotation
        mapRotator = new MapRotator(this,map);
        updateLocation();
    }
}
