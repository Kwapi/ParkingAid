/*
 * A base class to inherit from to enable
 * multiple gps managers for different OS versions
 */
package Framework.Gps;

import android.app.Service;

/**
 *
 * @author George Hatt
 */
public abstract class GpsManager{
    /**
     * returns current gps location ion gps tag object
     * the string passed will be the name of the gps tag
     * @param name
     * @return
     */
    public abstract GpsTag getCurrentGpsLocation(String name);


}
