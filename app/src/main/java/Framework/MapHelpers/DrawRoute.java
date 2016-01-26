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
 *  A class that enables route drawing functionality
 *  @author Michal Zak
 */
public class DrawRoute{


    private static Polyline line;
    private static String              myApiKey ="AIzaSyDjMCLbxy0wmqr1SbuMDo8W7SRn8flWIqw";

    /**
     * Call this method to generate and overlay a path onto the provided map
     *
     * @param from      - source location
     * @param to        - destination location
     * @param map       - map object for the route to be drawn onto
     * @param context   - Application/Activity Context
     */
    public static void drawRoute(GpsTag from, GpsTag to, GoogleMap map,Context context ){

        //make url request to google directions api
        String url = makeURL(from.getLatitude(), from.getLongitude(), to.getLatitude(), to.getLongitude());

        drawPathAsyncTask task = new drawPathAsyncTask(url, map, context);
        task.execute();
    }


    /**
     * Generate a custom GET query to GoogleApi servers based on provided parameters.
     * Note: the default path type is set to Walking
     * @param sourceLat - source latitude
     * @param sourceLog - source longitude
     * @param destLat   - destination latitude
     * @param destLog   - destination longitude
     * @return  String - URL
     */
    private static String makeURL (double sourceLat, double sourceLog, double destLat, double destLog ){
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourceLat));
        urlString.append(",");
        urlString
                .append(Double.toString(sourceLog));
        urlString.append("&destination=");// to
        urlString
                .append(Double.toString(destLat));
        urlString.append(",");
        urlString.append(Double.toString(destLog));
        urlString.append("&sensor=false&mode=walking&alternatives=true");
        urlString.append("&key=");
        urlString.append(myApiKey);
        return urlString.toString();
    }

    /**
     * Overlays the polylines(representing a path) on the provided map
     * @param result - String representation of JSON response from GoogleAPI directions query
     * @param map   - GoogleMap onto which to overlay the polylines
     */
    private static void overlayPolylines(String result, GoogleMap map) {

        try {
            //  Transform the string into a json object
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);

            //  create and draw the new polyline
            Polyline line_old = line;

            line = map.addPolyline(new PolylineOptions()
                            .addAll(list)
                            .width(12)
                            .color(Color.parseColor("#05b1fb")) //Google maps blue color
                            .geodesic(true)
            );

            //  clear the map from the previously drawn polyline
            if(line_old!=null){
                line_old.remove();
            }

        }
        catch (JSONException e) {

        }
    }

    /**
     * Asynchronous task that takes care of path drawing
     * -    parse directions JSON object
     * -    overlay polylines on the map
     */
    private static class drawPathAsyncTask extends AsyncTask<Void, Void, String> {
        private GoogleMap map;
        private Context context;

        String url;
        public drawPathAsyncTask(String urlPass, GoogleMap map, Context context){

            url = urlPass;
            this.map = map;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
            if(result!=null){
                overlayPolylines(result,map);
            }
        }
    }

    /**
     * Converts the encoded JSON string into a List of LatLng objects containing coordinates
     * to all important change-direction points
     * @param encoded - encoded JSON string
     * @return List<LatLng> - list of coordinates of important change-direction points
     */
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
