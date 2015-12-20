package michal.myapplication.Utilities;

import com.google.android.gms.maps.model.LatLng;

import Framework.Gps.GpsTag;

/**
 * Created by Domowy on 21.12.2015.
 */
public class Utils {



    static LatLng toLatLng(GpsTag gpsTag){

        return new LatLng(gpsTag.getLatitude(),gpsTag.getLongitude());

    }

}
