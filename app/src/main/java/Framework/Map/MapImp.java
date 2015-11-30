package Framework.Map;

import com.google.android.gms.maps.SupportMapFragment;

import Framework.Gps.GpsManager;
import Framework.Gps.GpsTag;

/**
 * Created by George Hatt on 15-Nov-15.
 */
public abstract class MapImp extends Map{
    public abstract void GenerateMap(GpsTag to, GpsManager from);
    public abstract void setMapType(int type);
    public abstract boolean isMapTypeSupported(int type);
}
