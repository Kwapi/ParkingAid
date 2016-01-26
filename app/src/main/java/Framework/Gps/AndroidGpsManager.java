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
 * Android Implementation of the GpsManager class dealing with location updates
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


    public AndroidGpsManager(Context context){

        this.context = context;
        initialise();
    }

        /**
         * Initialises location updating functionality
         * - build GoogleAPi Client
         * - connect it
         * - create LocationRequest
         */
    private void initialise(){

        ready = false;
        buildGoogleApiClient();
        googleApiClient.connect();
        createLocationRequest();
    }

        /**
         * Start location updates using LocationServices
         * (requires GoogleApiClient and LocationRequest objects to be initialised)
         */
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

        /**
         * Builds Google Api Client required for location updates
          */
    private synchronized void buildGoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

        /**
         * Creates a LocationRequest object and sets the interval to 5 seconds, fastest interval to 1 second
         * guarantees high accuracy readings
         */
    private void createLocationRequest(){
        locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000); //   1 second
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

        /**
         * Returns the status of the AndroidGpsManager
         * If it's active and at least one location updates has been performed, the manager is considered ready
         *
         * @return ready - boolean
         */
    public boolean isReady(){
        return ready;
    }




}
