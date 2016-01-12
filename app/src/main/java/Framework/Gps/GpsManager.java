/*
 * A base class to inherit from to enable
 * multiple gps managers for different OS versions
 */
package Framework.Gps;

import android.app.Service;

import java.util.Observable;

/**
 *
 * @author George Hatt/Michal Zak
 */
public abstract class GpsManager{
    /**
     * returns current gps location ion gps tag object
     * the string passed will be the name of the gps tag
     * @param name
     * @return
     */
    public abstract GpsTag getCurrentGpsLocation(String name);

    public abstract boolean isReady();

}
