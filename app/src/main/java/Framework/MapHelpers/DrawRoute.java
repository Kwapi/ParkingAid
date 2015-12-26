package Framework.MapHelpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Framework.Gps.GpsTag;


/**
 * Created by Michal on 30/11/2015.
 */
public class DrawRoute{


    private static Polyline line;
    //TODO:
    //might need to be replaced - has to be tested if it works on multiple apps
    private static String              myApiKey ="AIzaSyDjMCLbxy0wmqr1SbuMDo8W7SRn8flWIqw";

    /**
     * Call this method to generate and overlay a path onto the provided map
     *
     * @param from
     * @param to
     * @param map
     * @param context
     */
    public static void drawRoute(GpsTag from, GpsTag to, GoogleMap map,Context context ){

        //make url request to google directions api
        String url = makeURL(from.getLatitude(), from.getLongitude(), to.getLatitude(), to.getLongitude());

        drawPathAsyncTask task = new drawPathAsyncTask(url, map, context);
        task.execute();
    }



    private static String makeURL (double sourcelat, double sourcelog, double destlat, double destlog ){
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

    private static void overlayPolylines(String  result, GoogleMap map) {

        try {
            //Tranform the string into a json object
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);

            //clear the map from the previously drawn polyline
            if(line!=null){
                line.remove();
            }
            line = map.addPolyline(new PolylineOptions()
                            .addAll(list)
                            .width(12)
                            .color(Color.parseColor("#05b1fb"))//Google maps blue color
                            .geodesic(true)
            );

        }
        catch (JSONException e) {

        }
    }

    private static class drawPathAsyncTask extends AsyncTask<Void, Void, String> {
        private ProgressDialog progressDialog;
        private GoogleMap map;
        private Context context;

        String url;
        drawPathAsyncTask(String urlPass, GoogleMap map, Context context){

            url = urlPass;
            this.map = map;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Fetching route, Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
            */
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
            //progressDialog.hide();
            if(result!=null){
                overlayPolylines(result,map);
            }
        }
    }

    private static List<LatLng> decodePoly(String encoded) {

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
