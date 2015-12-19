package Framework.Gps;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.Observable;

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
    private boolean connected;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location currentLocation;

    public static final String TAG = AndroidGpsManager.class.getSimpleName();

    /**
     * returns current gps location ion gps tag object
     * the string passed will be the name of the gps tag
     * @return
     */

    public AndroidGpsManager(Context context){

        this.context = context;
        initialiseTings();
    }

    protected void initialiseTings(){

        connected = false;
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        createLocationRequest();
    }
    protected void startLocationUpdates(){
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,mLocationRequest,this);
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
        connected = true;
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        connected = false;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "Location Changed : " + location.getLatitude() + ", " + location.getLongitude());
        currentLocation = location;
        connected = true;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection Failed");

        connected = false;
    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(1000); //   1 second
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public boolean isConnected(){
        return connected;
    }




}
