package michal.myapplication;

import com.google.android.gms.location.LocationRequestCreator;

import java.io.Serializable;
import java.util.GregorianCalendar;

import Framework.Gps.GpsTag;
import Framework.Gps.LocationManager;


public class ParkedCar implements Serializable{

    static final long serialVersionUID = 1L;

    public static final String TAG = ParkedCar.class.getSimpleName();
    private static ParkedCar instance = null;
    private String notes;
    private boolean openDayMode;
    private GregorianCalendar parkTime;
    private int desiredDuration;



    private ParkedCar(){
    }

    public static ParkedCar getInstance(){
        if(instance!=null){
            return instance;
        }else{
            return new ParkedCar();
        }
    }
    public int getDesiredDuration() {
        return desiredDuration;
    }

    public void setDesiredDuration(int desiredDuration) {
        this.desiredDuration = desiredDuration;
    }

    public GregorianCalendar getParkTime() {
        return parkTime;
    }

    public void setParkTime(GregorianCalendar parkTime) {
        this.parkTime = parkTime;
    }

    public boolean isOpenDayMode() {
        return openDayMode;
    }

    public void setOpenDayMode(boolean openDayMode) {
        this.openDayMode = openDayMode;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public GpsTag getLocation(){
        return LocationManager.getStoredLocation("parkedCarLocation");
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();

        sb.append("Location:\t" + getLocation().getLatitude() + ", " + getLocation().getLongitude());
        sb.append("\n");
        sb.append("Parking time:\t" + parkTime.toString());
        sb.append("\n");
        sb.append("Desired duration:\t" + desiredDuration);
        sb.append("\n");
        sb.append("Notes:\t"+ notes);

        return sb.toString();
    }
}


