package michal.myapplication;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.GregorianCalendar;

import Framework.Gps.GpsTag;
import Framework.Gps.LocationManager;

import Framework.MapHelpers.MapRotator;
import Framework.MapHelpers.Utils;
import michal.myapplication.Utilities.AlertDialogues;

/**
 * ParkCarScreen is an Activity that enables user to store the current location and time and save it in a ParkedCar object.
 * Additionally the user can add notes. Pressing the 'Park Car' button persists the data, prepares and schedules a reminder notification
 * and navigates to the next screen/Activity- OverviewScreen
 */
public class ParkCarScreen extends AppCompatActivity  implements OnMapReadyCallback{

    public static final String TAG = ParkCarScreen.class.getSimpleName();

    //  UI ELEMENTS
    private EditText pickParkingEndTime;
    private EditText    notesEdit;
    private CheckBox    openDayModeCheckbox;
    private Button      parkCarButton;

    //  in minutes
    private final int DEFAULT_REMINDER_TIME = 10;

    private GoogleMap           map;
    private GpsTag              currentLocation;
    private GpsTag              parkingLocation;
    private LocationManager     locationManager;
    private MapRotator          mapRotator;
    private GregorianCalendar   parkingEndTime;
    private final Handler h = new Handler();

    /**
     * Update marker and move camera to it
     * @param location - location to be put in the updated marker
     */
    public void updateMarker(GpsTag location){
        map.clear();
        LatLng currentPosition = Utils.toLatLng(location);

        map.addMarker(new MarkerOptions().
                position(currentPosition)
                .title("You are here"));

        //  default zoom
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 15.0f));
    }


    /**
     * Check if there is a location update
     *
     * If there is - update the map (updateMarker)
     */
    public void updateLocation(){

        // wait for the gpsManager to be ready - can get current location
         if(locationManager.isReady()) {

             // adding a name because of the framework specification
             GpsTag newLocation = locationManager.getCurrentLocation(ParkedCar.PARKED_CAR_LOCATION);

             // only update location if it's different to the one we've already got
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

        //  WIRE UI ELEMENTS
        pickParkingEndTime =   (EditText)  findViewById(R.id.pickParkingEndTime);
        notesEdit =             (EditText)  findViewById(R.id.notesEdit);
        openDayModeCheckbox =   (CheckBox)  findViewById(R.id.openDayCheckbox);
        parkCarButton =         (Button)    findViewById(R.id.parkCarButton);

        if(ParkedCar.read(this)!=null){  // we've already got a car parked
            // open OverviewScreen
            Intent intent = new Intent(this, OverviewScreen.class);
            startActivity(intent);
        }

        //  hardcoded parking location for testing purposes
        //parkingLocation = new GpsTag(ParkedCar.PARKED_CAR_LOCATION,52.623247,1.241826,29);

        //  initialise GPSManager - start listening for location
        locationManager = LocationManager.getInstance(this);


        // GET MAP
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //  UI ACTIONS
        parkCarButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                parkCar();
            }
        });

        pickParkingEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });


    }

    /**
     * Creates and shows a time picker that updates parkingEndTime variable
     */
    public void showTimePicker(){
        final GregorianCalendar mcurrentTime = new GregorianCalendar();
        int hour = mcurrentTime.get(GregorianCalendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(GregorianCalendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(ParkCarScreen.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                // check if the time is in the future
                if (selectedHour >= mcurrentTime.get(GregorianCalendar.HOUR_OF_DAY) && selectedMinute >= mcurrentTime.get(GregorianCalendar.MINUTE)) {

                    //  set the parkingEndTime to the chosen values
                    parkingEndTime = new GregorianCalendar();
                    parkingEndTime.set(GregorianCalendar.HOUR_OF_DAY, selectedHour);
                    parkingEndTime.set(GregorianCalendar.MINUTE, selectedMinute);

                    //  update the textView
                    String selectedMinuteStr = String.valueOf(selectedMinute);
                    if(selectedMinute<10){
                        selectedMinuteStr = "0" + selectedMinuteStr;
                    }
                    pickParkingEndTime.setText(selectedHour + ":" + selectedMinuteStr);

                } else {
                    Toast.makeText(ParkCarScreen.this, (String) "The selected time has to be in the future",
                            Toast.LENGTH_LONG).show();
                }
            }
        }, hour, minute, true);
        mTimePicker.setTitle("Select Parking End Time");
        mTimePicker.show();
    }


    /**
     * Save all provided information along with parking location in a ParkedCar object,
     * schedule a reminder notification and proceed to OverviewScreen
     */
    public void parkCar(){

        //  check if the end of parking time has been picked
        if(pickParkingEndTime.getText().toString().isEmpty()){
            AlertDialogues.noEndParkingTimePicked(this).show();
            return;
        }

        //  set timeParked to current time
        GregorianCalendar timeParked = new GregorianCalendar();

        //  make sure end of parking time is not in the past
        if(parkingEndTime.getTimeInMillis() <= timeParked.getTimeInMillis()){
            AlertDialogues.parkingTimeInThePast(this).show();
            return;
        }

        //  collect info from forms
        String notes = notesEdit.getText().toString();
        boolean openDayMode = openDayModeCheckbox.isChecked();

        //  set parking location to current location
        parkingLocation = currentLocation;

        //  create new ParkedCar object
        ParkedCar parkedCar = ParkedCar.getInstance();
            parkedCar.setOpenDayMode(openDayMode);
            parkedCar.setNotes(notes);
            parkedCar.setParkTime(timeParked);
            parkedCar.setEndParkTime(parkingEndTime);
            parkedCar.setLocation(parkingLocation);

        //  persist the object into file
        parkedCar.save(getApplicationContext());

        // schedule notification that will pop up before the parking end time
        scheduleNotification(getNotification("Time to go back to your car"), parkingEndTime);

        //  transfer the object via bundle
        Intent intent = new Intent(this, OverviewScreen.class);
        Bundle b = new Bundle();
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
                Utils.getMapTypeSelectorDialog(map, ParkCarScreen.this).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setCompassEnabled(false);

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

    /**
     * Schedules the notification to be raised before the provided time extracted form the GregorianCalendar object.
     * The reminder time is dependant on the amount of time between start of parking and end of parking. Default value can be set
     * by changing DEFAULT_REMINDER_TIME
     *
     * @param notification - notification to be raised
     * @param date - date before which the notification will be raised
     */
    private void scheduleNotification(Notification notification, GregorianCalendar date) {
        //  PREPARE THE NOTIFICATION

        //  choose activity that is going to trigger the notification when woken up
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //  SCHEDULE THE NOTIFICATION
        long currentTime = System.currentTimeMillis();
        long endParkingTime = date.getTimeInMillis();
        long delay = endParkingTime - currentTime;
        long reminderTime = DEFAULT_REMINDER_TIME * 60 * 1000;
        long notificationTime = 0;
        long systemTime = SystemClock.elapsedRealtime();

        //  if the parking end time is within the next 15 minutes - remind in 3/4 of the delay time
        //  i.e. parking time - 15:00
        //       parking end time - 15:04
        //       reminder pops up at 15:03
        if(delay <= reminderTime){
            notificationTime = systemTime;
            long newDelay = (long) (((float) 3/4) * delay);
            notificationTime += newDelay;
        }
        else{   // use the default reminder time (15 minutes)
            notificationTime = systemTime + delay - reminderTime;
        }

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        //  wake up the app by opening NotificationPublisher which in turn will publish the notification
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, notificationTime, pendingIntent);
    }

    /**
     * Prepare the notification which when clicked takes the user to OverviewScreen
     *
     * @param content - content text within the notification
     * @return
     */
    private Notification getNotification(String content) {

        //  where I want the notification to take the user once clicked
        Intent resultIntent = new Intent(this, OverviewScreen.class);

        //  pending intent which will be placed in the notification
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        //  build the notification
        Notification.Builder builder = new Notification.Builder(this);
            builder.setContentTitle("Parking Aid - Parking Reminder");
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
