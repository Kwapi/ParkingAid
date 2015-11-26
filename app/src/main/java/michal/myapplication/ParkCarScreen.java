package michal.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import Framework.Gps.GpsTag;
import Framework.Gps.LocationManager;

public class ParkCarScreen extends AppCompatActivity implements OnMapReadyCallback {

    public static final String TAG = ParkCarScreen.class.getSimpleName();
    private String              myApiKey ="AIzaSyDjMCLbxy0wmqr1SbuMDo8W7SRn8flWIqw";

    //UI ELEMENTS
    private EditText    desiredDurationEdit;
    private EditText    notesEdit;
    private Button      updateLocationButton;
    private CheckBox    openDayModeCheckbox;
    private Button      saveCarLocButton;
    private Button      drawRouteButton;
    private GoogleMap   mMap;



    private GpsTag              currentLocation;
    private GpsTag              parkingLocation;
    private LocationManager     locationManager;




    public void drawRoute(GpsTag from, GpsTag to){
        //make url request to google directions api
        String url = makeURL(from.getLatitude(), from.getLongitude(), to.getLatitude(), to.getLongitude());

        drawPathAsyncTask task = new drawPathAsyncTask(url);
        task.execute();
    }


    public void updateMarker(GpsTag location){
        mMap.clear();

        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().
                position(currentPosition)
                .title("You are here"));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 15.0f));
    }
    public void updateLocation(){
        // wait for the gpsManager to be connected
         if(locationManager.isReady()) {

             //adding a name because of the framework specification
            currentLocation = locationManager.getCurrentLocation("parkedCarLocation");

            //mLatitudeText.setText(String.valueOf(currentLocation.getLatitude()));
            //mLongitudeText.setText(String.valueOf(currentLocation.getLongitude()));

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

        parkingLocation = new GpsTag("parkingLocation", 1.241826,52.623247);



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
                drawRoute(currentLocation,parkingLocation);
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
        b.putSerializable("parkedCar",parkedCar);
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
        //mMap.setMyLocationEnabled(true);


        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }



    public String makeURL (double sourcelat, double sourcelog, double destlat, double destlog ){
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString
                .append(Double.toString(sourcelog));
        urlString.append("&destination=");// to
        urlString
                .append(Double.toString(destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("&sensor=false&mode=walking&alternatives=true");
        urlString.append("&key=");
        urlString.append(myApiKey);
        return urlString.toString();
    }

    public void drawPath(String  result) {

        try {
            //Tranform the string into a json object
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);
            Polyline line = mMap.addPolyline(new PolylineOptions()
                            .addAll(list)
                            .width(12)
                            .color(Color.parseColor("#05b1fb"))//Google maps blue color
                            .geodesic(true)
            );
           /*
           for(int z = 0; z<list.size()-1;z++){
                LatLng src= list.get(z);
                LatLng dest= list.get(z+1);
                Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude,   dest.longitude))
                .width(2)
                .color(Color.BLUE).geodesic(true));
            }
           */
        }
        catch (JSONException e) {

        }
    }

    private class drawPathAsyncTask extends AsyncTask<Void, Void, String> {
        private ProgressDialog progressDialog;
        String url;
        drawPathAsyncTask(String urlPass){
            url = urlPass;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ParkCarScreen.this);
            progressDialog.setMessage("Fetching route, Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }
        @Override
        protected String doInBackground(Void... params) {
            JSONParser jParser = new JSONParser();
            String json = jParser.getJSONFromUrl(url);
            return json;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.hide();
            if(result!=null){
                drawPath(result);
            }
        }
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }
}
