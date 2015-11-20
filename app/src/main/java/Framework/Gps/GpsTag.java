/*
 * A data object for storing longitude and latitude for gps locations
 */
package Framework.Gps;

/**
 *
 * @author George Hatt
 */
public class GpsTag {
    private double longitude,latitude;
    private String name;
    
    public GpsTag(String name, double longitude, double latitude){
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
    }
    
    /**
     * returns the longitude for the gps location
     * @return
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * returns the latitude for the gps location
     * @return
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * return the name associated with the gps location
     * @return
     */
    public String getName() {
        return name;
    }
    
    @Override
    public String toString(){
        return name +": "+longitude+","+latitude;
    }
    
}
