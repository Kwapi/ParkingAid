package Framework.Gps;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 *
 * @author George Hatt/Michal Zak
 */


public class AndroidGpsManager extends GpsManager implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
    {


    private Context context;
    private boolean ready;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location currentLocation;

    private static final String TAG = AndroidGpsManager.class.getSimpleName();

    /**
     * returns current gps location ion gps tag object
     * the string passed will be the name of the gps tag
     * @return
     */

    public AndroidGpsManager(Context context){

        this.context = context;
        initialise();
    }

    private void initialise(){

        ready = false;
        buildGoogleApiClient();
        googleApiClient.connect();
        createLocationRequest();
    }
    private void startLocationUpdates(){
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest,this);
    }

    @Override
    public GpsTag getCurrentGpsLocation(String name) {

        GpsTag result = null;

        if(currentLocation!=null){
            result = new GpsTag(name, currentLocation.getLatitude(),currentLocation.getLongitude(), currentLocation.getAltitude());
        }else{
            Log.e(TAG,"LOCATION IS NULL");
        }

        return result;
    }

    public Location getCurrentLocation(){

        if(currentLocation!=null){
            return currentLocation;
        }else{
            Log.e(TAG,"LOCATION IS NULL");

            return null;
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Connected");
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        ready = false;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "Location Changed : " + location.getLatitude() + ", " + location.getLongitude());
        currentLocation = location;
        ready = true;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection Failed");

        ready = false;
    }

    private synchronized void buildGoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void createLocationRequest(){
        locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000); //   1 second
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public boolean isReady(){
        return ready;
    }




}
