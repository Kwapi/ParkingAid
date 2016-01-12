package michal.myapplication;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import java.util.GregorianCalendar;

import Framework.Gps.GpsTag;
import Framework.Gps.LocationManager;
import Framework.MapHelpers.DrawRoute;

import Framework.MapHelpers.MapRotator;
import Framework.MapHelpers.Utils;
import michal.myapplication.Utilities.AlertDialogues;

public class ParkCarScreen extends AppCompatActivity  implements OnMapReadyCallback{

    public static final String TAG = ParkCarScreen.class.getSimpleName();




    //UI ELEMENTS
    private EditText    desiredDurationEdit;
    private EditText    notesEdit;
    private CheckBox    openDayModeCheckbox;
    private Button      parkCarButton;
    private Button      notifyButton;

    private GoogleMap map;
    private GpsTag              currentLocation;
    private GpsTag              parkingLocation;
    private LocationManager     locationManager;
    private MapRotator          mapRotator;
    final Handler h = new Handler();




    public void updateMarker(GpsTag location){
        map.clear();
        LatLng currentPosition = Utils.toLatLng(location);

        map.addMarker(new MarkerOptions().
                position(currentPosition)
                .title("You are here"));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 15.0f));
    }



    public void updateLocation(){

        // wait for the gpsManager to be ready - can get current location
         if(locationManager.isReady()) {

             //adding a name because of the framework specification
             GpsTag newLocation = locationManager.getCurrentLocation(ParkedCar.PARKED_CAR_LOCATION);

             //only update location if it's different to the one we've already got
             if(!GpsTag.isSameLocation(currentLocation,newLocation)){
                 currentLocation = newLocation;
                 mapRotator.updateDeclination(currentLocation);
                 updateMarker(currentLocation);
             }


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
        openDayModeCheckbox =   (CheckBox)  findViewById(R.id.openDayCheckbox);
        parkCarButton =         (Button)    findViewById(R.id.parkCarButton);
        notifyButton =          (Button)    findViewById(R.id.notifyButton);




        if(ParkedCar.read(this)!=null){
            // we've already got a car parked

            // open OverviewScreen
            Intent intent = new Intent(this, OverviewScreen.class);
            startActivity(intent);
        }

        //hardcoded parking location for testing purposes
        parkingLocation = new GpsTag(ParkedCar.PARKED_CAR_LOCATION,52.623247,1.241826,29);
        //initialise GPSManager - start listening for location
        locationManager = LocationManager.getInstance(this);




        parkCarButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                parkCar();
            }
        });

        notifyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

    }

    public void parkCar(){
        //get info from forms
        String desiredDurationText = desiredDurationEdit.getText().toString();

        if(desiredDurationText.isEmpty()){
            AlertDialogues.noDurationInput(this).show();
            return;
        }
        int desiredDuration  = Integer.parseInt(desiredDurationText);

        int timeToNotify = desiredDuration * 1000 * 60;
        scheduleNotification(getNotification("NOTIFICATION"),5000);

        String notes = notesEdit.getText().toString();
        boolean openDayMode = openDayModeCheckbox.isChecked();

        //set timeParked to current time
        GregorianCalendar timeParked = new GregorianCalendar();


        ParkedCar parkedCar = ParkedCar.getInstance();

        parkedCar.setDesiredDuration(desiredDuration);
        parkedCar.setOpenDayMode(openDayMode);
        parkedCar.setNotes(notes);
        parkedCar.setParkTime(timeParked);
        parkedCar.setLocation(parkingLocation);

        //persist the object
        parkedCar.save(getApplicationContext());


        //transfer the object via bundle
        Intent intent = new Intent(this, OverviewScreen.class);
        Bundle b = new Bundle();
        b.putSerializable("parkedCar", parkedCar);
        intent.putExtras(b);




        startActivity(intent);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.select_map_type) {
            if(map!=null) {
                Utils.getMapTypeSelectorDialog(map, ParkCarScreen.this).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        //map.setMyLocationEnabled(true);

        // start automatic mapRotation
        mapRotator = new MapRotator(this,map);


        final int delay = 2000; //milliseconds

        // hack - we know it takes a bit of time
        // for the gpsManager to be ready
        // wait 2 seconds and then try to updateLocation
        h.postDelayed(new Runnable() {
            public void run() {
                //do something
                updateLocation();
                h.postDelayed(this, delay);

            }
        }, delay);

    }


    private void scheduleNotification(Notification notification, int delay) {

        //activity that is going to trigger the notification when woken up
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        //wake up the app by opening NotificationPublisher which in turn will publish the notification
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private Notification getNotification(String content) {

        //where I want the notification to take the user once clicked
        Intent resultIntent = new Intent(this, OverviewScreen.class);

        //pending intent which will be placed in the notification
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );


        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Scheduled Notification");
        builder.setContentText(content);
        builder.setContentIntent(resultPendingIntent);
        builder.setSmallIcon(R.drawable.icon_transparent);
        builder.setAutoCancel(true);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);
        builder.setVibrate(new long[] { 0,1000,1000});
        return builder.build();
    }


}
