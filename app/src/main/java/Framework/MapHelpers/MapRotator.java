package Framework.MapHelpers;

import android.content.Context;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;

import Framework.Gps.GpsTag;

/**
 * MapRotator class provides functionality to rotate the provided GoogleMap object based on the direction the device is currently facing.

 * @author Michal Zak
 */
public class MapRotator implements SensorEventListener {
    private float[]             rotationMatrix = new float[16];
    private double              declination = 0;
    private GoogleMap           map;
    private Context             context;
    private SensorManager       sensorManager;

    /**
     * Constructs a MapRotator object which is used to rotate the map based on device's sensors
     * @param context - Android Context (Activity/Application)
     * @param map - GoogleMap - will be linked to this MapRotator
     */
    public MapRotator(Context context, GoogleMap map){
        this.context = context;
        this.map = map;

        sensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(
                    rotationMatrix, event.values);
            float[] orientation = new float[3];
            SensorManager.getOrientation(rotationMatrix, orientation);

            //wait for declination to be initialised
            if(declination!=0) {
                float bearing = (float) (Math.toDegrees(orientation[0]) + declination);
                updateCamera(bearing);
            }
       }
    }

    /**
     * Update (rotate) camera based on compass bearing
     * @param bearing - compass bearing
     */
    private void updateCamera(float bearing) {
        CameraPosition oldPos = map.getCameraPosition();

        CameraPosition pos = CameraPosition.builder(oldPos).bearing(bearing).build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
    }

    /**
     * Updates declination - used for rotating the camera - by providing currentLocation
     * @param currentLocation - GpsTag
     */
    public void updateDeclination(GpsTag currentLocation){
        GeomagneticField field = new GeomagneticField(
                (float)currentLocation.getLatitude(),
                (float)currentLocation.getLongitude(),
                (float)currentLocation.getAltitude(),
                System.currentTimeMillis()
        );

        declination = field.getDeclination();

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing
    }
}
