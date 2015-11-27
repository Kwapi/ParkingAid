package michal.myapplication;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;

import Framework.Gps.GpsTag;
import Framework.Gps.LocationManager;

public class CompassExample extends AppCompatActivity implements SensorEventListener {

    // Accelerometer X, Y, and Z values
    private TextView accelXValue;
    private TextView accelYValue;
    private TextView accelZValue;

    // Orientation X, Y, and Z values
    private TextView orientXValue;
    private TextView orientYValue;
    private TextView orientZValue;

    private Arrow arrow;
    private ImageView arrowView;

    private SensorManager sensorManager = null;
    private GpsTag currentLocation;
    private GpsTag targetLocation;
    private LocationManager locationManager;



    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get a reference to a SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        setContentView(R.layout.activity_compass_example);

        arrowView = (ImageView) findViewById(R.id.arrowView);
        arrowView.setImageResource(R.drawable.arrow);

        targetLocation = new GpsTag("parkingLocation",52.623247,1.241826,29);
        locationManager = new LocationManager(this);


        // Capture accelerometer related view elements
        accelXValue = (TextView) findViewById(R.id.accel_x_value);
        accelYValue = (TextView) findViewById(R.id.accel_y_value);
        accelZValue = (TextView) findViewById(R.id.accel_z_value);

        // Capture orientation related view elements
        orientXValue = (TextView) findViewById(R.id.orient_x_value);
        orientYValue = (TextView) findViewById(R.id.orient_y_value);
        orientZValue = (TextView) findViewById(R.id.orient_z_value);

        // Initialize accelerometer related view elements
        accelXValue.setText("0.00");
        accelYValue.setText("0.00");
        accelZValue.setText("0.00");

        // Initialize orientation related view elements
        orientXValue.setText("0.00");
        orientYValue.setText("0.00");
        orientZValue.setText("0.00");
    }

    // This method will update the UI on new sensor events
    public void onSensorChanged(SensorEvent sensorEvent) {

        currentLocation = locationManager.getCurrentLocation("parkedCarLocation");

        synchronized (this) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accelXValue.setText(Float.toString(sensorEvent.values[0]));
                accelYValue.setText(Float.toString(sensorEvent.values[1]));
                accelZValue.setText(Float.toString(sensorEvent.values[2]));
            }

            if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                orientXValue.setText(Float.toString(sensorEvent.values[0]));
                orientYValue.setText(Float.toString(sensorEvent.values[1]));
                orientZValue.setText(Float.toString(sensorEvent.values[2]));
            }
        }

/*

    // If we don't have a Location, we break out
    if ( currentLocation == null ) return;

    float azimuth = sensorEvent.values[0];
    float baseAzimuth = azimuth;

    GeomagneticField geoField = new GeomagneticField( Double
        .valueOf( currentLocation.getLatitude() ).floatValue(), Double
        .valueOf( currentLocation.getLongitude() ).floatValue(),
        Double.valueOf( currentLocation.getAltitude() ).floatValue(),
        System.currentTimeMillis() );

    azimuth -= geoField.getDeclination(); // converts magnetic north into true north


    // Store the bearingTo in the bearTo variable
    float bearTo = currentLocation.bearingTo( destinationObj );

    // If the bearTo is smaller than 0, add 360 to get the rotation clockwise.
    if (bearTo < 0) {
        bearTo = bearTo + 360;
    }

    //This is where we choose to point it
    float direction = bearTo - azimuth;

    // If the direction is smaller than 0, add 360 to get the rotation clockwise.
    if (direction < 0) {
        direction = direction + 360;
    }

    rotateImageView( arrow, R.drawable.arrow, direction );

    //Set the field
    String bearingText = "N";

    if ( (360 >= baseAzimuth && baseAzimuth >= 337.5) || (0 <= baseAzimuth && baseAzimuth <= 22.5) ) bearingText = "N";
    else if (baseAzimuth > 22.5 && baseAzimuth < 67.5) bearingText = "NE";
    else if (baseAzimuth >= 67.5 && baseAzimuth <= 112.5) bearingText = "E";
    else if (baseAzimuth > 112.5 && baseAzimuth < 157.5) bearingText = "SE";
    else if (baseAzimuth >= 157.5 && baseAzimuth <= 202.5) bearingText = "S";
    else if (baseAzimuth > 202.5 && baseAzimuth < 247.5) bearingText = "SW";
    else if (baseAzimuth >= 247.5 && baseAzimuth <= 292.5) bearingText = "W";
    else if (baseA5 && baseAzimuth < 337.5) bearingText = "NW";
    else bearingText = "?";

    fieldBearing.setText(bearingText);
*/

}


    

    // I've chosen to not implement this method
    public void onAccuracyChanged(Sensor arg0, int arg1) {


    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register this class as a listener for the accelerometer sensor
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        // ...and the orientation sensor
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop() {
        // Unregister the listener
        sensorManager.unregisterListener(this);
        super.onStop();
    }



    private void rotateImageView( ImageView imageView, int drawable, float rotate ) {

        // Decode the drawable into a bitmap
        Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(),
                drawable);

        // Get the width/height of the drawable
        DisplayMetrics dm = new DisplayMetrics(); getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = bitmapOrg.getWidth(), height = bitmapOrg.getHeight();

        // Initialize a new Matrix
        Matrix matrix = new Matrix();

        // Decide on how much to rotate
        rotate = rotate % 360;

        // Actually rotate the image
        matrix.postRotate( rotate, width, height );

        // recreate the new Bitmap via a couple conditions
        Bitmap rotatedBitmap = Bitmap.createBitmap( bitmapOrg, 0, 0, width, height, matrix, true );
        //BitmapDrawable bmd = new BitmapDrawable( rotatedBitmap );

        //imageView.setImageBitmap( rotatedBitmap );
        imageView.setImageDrawable(new BitmapDrawable(getResources(), rotatedBitmap));
        imageView.setScaleType(ImageView.ScaleType.CENTER );
    }

}
