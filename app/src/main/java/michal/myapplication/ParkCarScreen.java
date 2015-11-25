package michal.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import Framework.Gps.AndroidGpsManager;
import Framework.Gps.GpsTag;
import Framework.Gps.LocationManager;

public class ParkCarScreen extends AppCompatActivity implements OnMapReadyCallback {
    public static final String TAG = ParkCarScreen.class.getSimpleName();
    //UI ELEMENTS
    private TextView    mLatitudeText;
    private TextView    mLongitudeText;
    private EditText    desiredDurationEdit;
    private EditText    notesEdit;
    private Button      updateLocationButton;
    private CheckBox    openDayModeCheckbox;
    private Button      saveCarLocButton;
    private GoogleMap   mMap;

    private GpsTag              currentLocation;
    private LocationManager     locationManager;

    public void updateMarker(GpsTag location){
        mMap.clear();

        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().
                position(currentPosition)
                .title("You are here"));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 20.0f));
    }
    public void updateLocation(){
        // wait for the gpsManager to be connected
         if(locationManager.isReady()) {

             //adding a name because of the framework specification
            currentLocation = locationManager.getCurrentLocation("parkedCarLocation");

            mLatitudeText.setText(String.valueOf(currentLocation.getLatitude()));
            mLongitudeText.setText(String.valueOf(currentLocation.getLongitude()));

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
        mLatitudeText =         (TextView)  findViewById(R.id.latitudeText);
        mLongitudeText =        (TextView)  findViewById(R.id.longitudeText);
        desiredDurationEdit =   (EditText)  findViewById(R.id.desDurEdit);
        notesEdit =             (EditText)  findViewById(R.id.notesEdit);
        updateLocationButton =  (Button)    findViewById(R.id.updateLocation);
        openDayModeCheckbox =   (CheckBox)  findViewById(R.id.openDayCheckbox);
        saveCarLocButton =      (Button)    findViewById(R.id.saveCarLocButton);

        //initialise GPSManager - start listening for location
        locationManager = new LocationManager(this);



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

        locationManager.storeGpsLocation("parkedCarLocation",currentLocation);

        Intent intent = new Intent(this, OverviewScreen.class);
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


    public void handleGetDirectionsResult(ArrayList<LatLng> directionPoints)
    {
        Polyline newPolyline;
        GoogleMap mMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        PolylineOptions rectLine = new PolylineOptions().width(3).color(Color.RED);

        for(int i = 0 ; i < directionPoints.size() ; i++)
        {
            rectLine.add(directionPoints.get(i));
        }
        newPolyline = mMap.addPolyline(rectLine);
    }


    public void findDirections(double fromPositionDoubleLat, double fromPositionDoubleLong, double toPositionDoubleLat, double toPositionDoubleLong, String mode)
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put(GetDirectionsAsyncTask.USER_CURRENT_LAT, String.valueOf(fromPositionDoubleLat));
        map.put(GetDirectionsAsyncTask.USER_CURRENT_LONG, String.valueOf(fromPositionDoubleLong));
        map.put(GetDirectionsAsyncTask.DESTINATION_LAT, String.valueOf(toPositionDoubleLat));
        map.put(GetDirectionsAsyncTask.DESTINATION_LONG, String.valueOf(toPositionDoubleLong));
        map.put(GetDirectionsAsyncTask.DIRECTIONS_MODE, mode);

        GetDirectionsAsyncTask asyncTask = new GetDirectionsAsyncTask(this);
        asyncTask.execute(map);
    }







    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
