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
    private GpsTagTree storedLocations;
    private GpsManager gm;
    
    
    public LocationManager(Object object){
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
    public GpsTag getStoredLocation(String name){
        return storedLocations.getGpsTag(name);
    }

    public boolean
}
