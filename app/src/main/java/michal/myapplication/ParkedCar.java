package michal.myapplication;

import android.content.Context;

import com.google.android.gms.location.LocationRequestCreator;

import java.io.Serializable;
import java.util.GregorianCalendar;

import Framework.FileIO;
import Framework.Gps.GpsTag;
import Framework.Gps.LocationManager;


public class ParkedCar implements Serializable{
    static final String FILE_NAME = "parkedCar.pa";
    static final long serialVersionUID = 1L;
    public static final String PARKED_CAR_LOCATION = "parkedCarLocation";

    public GpsTag parkingLocation = null;
    public static final String TAG = ParkedCar.class.getSimpleName();
    private static ParkedCar instance = null;
    private String notes;
    private boolean openDayMode;
    private GregorianCalendar parkTime;
    private GregorianCalendar endParkTime;

    public GregorianCalendar getEndParkTime() {
        return endParkTime;
    }

    public void setEndParkTime(GregorianCalendar endParkTime) {
        this.endParkTime = endParkTime;
    }

    private ParkedCar(){
    }

    public static ParkedCar getInstance(){
        if(instance!=null){
            return instance;
        }else{
            return new ParkedCar();
        }
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

    /*
    public GpsTag getLocation(){
        return LocationManager.getStoredLocation(PARKED_CAR_LOCATION);
    }
    */

    public GpsTag getLocation(){
        return parkingLocation;
    }

    public void setLocation(GpsTag parkingLocation){
        this.parkingLocation = parkingLocation;
    }


    public String toString(){
        StringBuilder sb = new StringBuilder();

        sb.append("Location:\t" + getLocation().getLatitude() + ", " + getLocation().getLongitude());
        sb.append("\n");
        sb.append("Parking time:\t" + parkTime.getTime().toString());
        sb.append("\n");
        sb.append("Parking end time:\t" + endParkTime.getTime().toString());
        sb.append("\n");
        sb.append("Notes:\t"+ notes);
        sb.append("\n");
        sb.append("Open day mode: \t" + openDayMode);

        return sb.toString();
    }

    public void save(Context context){
        FileIO.saveToFile(this, context, FILE_NAME);
    }

    public static ParkedCar read(Context context){
       return (ParkedCar) FileIO.readFromFile(context, FILE_NAME);
    }

    public void delete(Context context){
        FileIO.deleteFile(context, FILE_NAME);
    }


}


