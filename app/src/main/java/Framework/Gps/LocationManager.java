/*
 * A class to store all functionality relating to locations and gps
 */
package Framework.Gps;

import android.content.Context;

/**
 *
 * @author George Hatt
 */
public class LocationManager {
    private static LocationManager instance;
    private static GpsTagTree storedLocations;
    private static GpsManager gm;
    
    
    private LocationManager(Object object){
        // initilise the tag tree
        storedLocations = new GpsTagTree();
        // detect os version
        int osVersion = 0;
        
        // detect the os version and select the appropriate GPS Manager
        switch(osVersion){
            default:
                gm = new AndroidGpsManager((Context)object);
                break;
        }
    }

    public static LocationManager getInstance(Object object){
        if(instance==null){
            instance = new LocationManager(object);
        }
        return instance;
    }
    
    /**
     * returns current gps location ion gps tag object
     * the string passed will be the name of the gps tag
     * @return
     */
    public GpsTag getCurrentLocation(String name){
        return gm.getCurrentGpsLocation(name);
    }
    
    /**
     * stores the GpsTag given in a tree structure under the name given
     * if a GpsTag under the same name already exists it will be overridden
     * @param name
     * @param gt
     */
    public void storeGpsLocation(String name,GpsTag gt){
        storedLocations.addGpsTag(name, gt);
    }
    
    /**
     * extracts a stored location with the name given
     * @param name
     * @return
     */
    public static GpsTag getStoredLocation(String name){
        return storedLocations.getGpsTag(name);
    }

    public boolean isReady(){
        return gm.isConnected();
    }

}
