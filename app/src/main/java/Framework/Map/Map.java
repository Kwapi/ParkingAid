package Framework.Map;

import Framework.Gps.GpsManager;
import Framework.Gps.GpsTag;

/**
 * Created by George Hatt on 15-Nov-15.
 */
public abstract class Map {
    public abstract void GenerateMap(GpsTag to, GpsManager from);
    public abstract void setMapType(int type);
    public abstract boolean isMapTypeSupported(int type);
}
