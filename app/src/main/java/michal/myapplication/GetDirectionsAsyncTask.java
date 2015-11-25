package michal.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Michal on 25/11/2015.
 */
public class GetDirectionsAsyncTask extends AsyncTask<Map<String, String>, Object, ArrayList<LatLng>> {

    public static final String USER_CURRENT_LAT = "user_current_lat";
    public static final String USER_CURRENT_LONG = "user_current_long";
    public static final String DESTINATION_LAT = "destination_lat";
    public static final String DESTINATION_LONG = "destination_long";
    public static final String DIRECTIONS_MODE = "directions_mode";
    private ParkCarScreen activity;
    private String url;

    private final String TAG = this.getClass().getSimpleName();
    private Exception exception;

    private Dialog progressDialog;

    public GetDirectionsAsyncTask(ParkCarScreen activity /*String url*/)
    {
        super();
        this.activity = activity;

        //  this.url = url;
    }

    public void onPreExecute() {

    }

    @Override
    public void onPostExecute(ArrayList<LatLng> result) {
        progressDialog.dismiss();

        if (exception == null) {
            activity.handleGetDirectionsResult(result);
        } else {
            processException();
        }
    }

    @Override
    protected ArrayList<LatLng> doInBackground(Map<String, String>... params) {

        Map<String, String> paramMap = params[0];
        try{
            LatLng fromPosition = new LatLng(Double.valueOf(paramMap.get(USER_CURRENT_LAT)) , Double.valueOf(paramMap.get(USER_CURRENT_LONG)));
            LatLng toPosition = new LatLng(Double.valueOf(paramMap.get(DESTINATION_LAT)) , Double.valueOf(paramMap.get(DESTINATION_LONG)));
            GMapV2Direction md = new GMapV2Direction();
            Document doc = md.getDocument(fromPosition, toPosition, paramMap.get(DIRECTIONS_MODE));
            ArrayList<LatLng> directionPoints = md.getDirection(doc);
            return directionPoints;

        }
        catch (Exception e) {
            exception = e;
            return null;
        }
    }

    private void processException() {
        Log.i(TAG,"something went wrong");
    }

}