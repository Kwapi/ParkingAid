package michal.myapplication;

import android.content.Context;

import java.io.Serializable;
import java.util.GregorianCalendar;

import Framework.FileIO;
import Framework.Gps.GpsTag;
import michal.myapplication.Utilities.FeeCalculation;

/**
 * A class to store and interact with parking information. Stored information:
 *  - parking location
 *  - notes
 *  - whether the parking was during the open day
 *  - start of the parking
 *  - end of the parking
 */
public class ParkedCar implements Serializable{
    public static final String FILE_NAME = "parkedCar.pa";
    public static final long serialVersionUID = 1L;
    public static final String PARKED_CAR_LOCATION = "parkedCarLocation";

    private static final String TAG = ParkedCar.class.getSimpleName();
    private static ParkedCar instance = null;

    private GpsTag parkingLocation = null;
    private String notes;
    private boolean openDayMode;
    private GregorianCalendar parkTime;
    private GregorianCalendar endParkTime;

    /**
     * Returns date and time of end of parking
     * @return  end of parking - GregorianCalendar
     */
    public GregorianCalendar getEndParkTime() {
        return endParkTime;
    }

    /**
     * Sets date and time of end of parking
     * @param endParkTime - GregorianCalendar - end of parking
     */
    public void setEndParkTime(GregorianCalendar endParkTime) {
        this.endParkTime = endParkTime;
    }

    /**
     * Default constructor
     */
    private ParkedCar(){
    }

    /**
     * Returns an instance of ParkedCar objects
     * @return instance of this object - ParkedCar
     */
    public static ParkedCar getInstance(){
        if(instance!=null){
            return instance;
        }else{
            return new ParkedCar();
        }
    }

    /**
     * Returns date and time of start of parking
     * @return  start of parking - GregorianCalendar
     */
    public GregorianCalendar getParkTime() {
        return parkTime;
    }

    /**
     * Sets start of parking date and time
     * @param parkTime - GregorianCalendar
     */
    public void setParkTime(GregorianCalendar parkTime) {
        this.parkTime = parkTime;
    }

    /**
     * Returns the information whether this ParkedCar was parked with open day mode enabled
     * @return true if in OpenDayMode, false otherwise
     */
    public boolean isOpenDayMode() {
        return openDayMode;
    }

    /**
     * Sets open day mode
     * @param openDayMode - boolean
     */
    public void setOpenDayMode(boolean openDayMode) {
        this.openDayMode = openDayMode;
    }

    /**
     * Returns parking notes
     * @return parking notes - String
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets parking notes
     * @param notes - parking notes
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /*
    DEPRECATED

    public GpsTag getLocation(){
        return LocationManager.getStoredLocation(PARKED_CAR_LOCATION);
    }
    */

    /**
     * Returns parking location
     * @return GpsTag - parking location
     */
    public GpsTag getLocation(){
        return parkingLocation;
    }

    /**
     * Sets parking location
     * @param parkingLocation - GpsTag
     */
    public void setLocation(GpsTag parkingLocation){
        this.parkingLocation = parkingLocation;
    }

    /**
     * Returns String representation of this ParkedCar
     * @return String representation of this ParkedCar
     */
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
        sb.append("\n");
        sb.append("Estimated total fee: \t" + calculateEstimatedTotalFee());
        sb.append("\n");
        sb.append("Current fee: \t" + calculateFeeSoFar());


        return sb.toString();
    }

    /**
     * Calculates the estimated total fee based on the start and end park times
     * @return estimated total fee formatted String with £ prefix and decimal places added
     */
    public String calculateEstimatedTotalFee(){
        String result = "£" + FeeCalculation.calculateFee(parkTime,endParkTime) + ".00";

        return result;
    }

    /**
     * Calculates the current fee based on the start and current time
     * @return  current fee formatted String with £ prefix and decimal places added
     */
    public String calculateFeeSoFar(){
        String result = "£" + FeeCalculation.calculateFee(parkTime,new GregorianCalendar()) + ".00";
        return result;
    }

    /**
     * Returns formatted start park time component in the form of a String
     * @return String - formatted start park time
     */
    public String getStartTime(){
        return getFormattedTime(parkTime);
    }

    /**
     * Returns formatted end park time component in the form of a String
     * @return String - formatted end park time
     */
    public String getEndTime(){
        return getFormattedTime(endParkTime);
    }

    /**
     * Returns only the time component of GregorianCalendar object
     * @param time - GregorianCalendar
     * @return formatted String representing time component
     */
    private String getFormattedTime(GregorianCalendar time){
        StringBuilder stringBuilder = new StringBuilder();
        int hour = time.get(GregorianCalendar.HOUR_OF_DAY);
        int minute = time.get(GregorianCalendar.MINUTE);

        stringBuilder.append(hour);
        stringBuilder.append(":");
        if(minute<10){
            stringBuilder.append("0");
        }
        stringBuilder.append(minute);

        return stringBuilder.toString();

    }

    /**
     * Perist the object into internal storage
     * @param context - Android Context (Activity/Aplication)
     */
    public void save(Context context){
        FileIO.saveToFile(this, context, FILE_NAME);
    }

    /**
     * Read and load the object from file
     * @param context - Android Context (Activity/Aplication)
     * @return - ParkedCar - loaded object from file
     */
    public static ParkedCar read(Context context){
       return (ParkedCar) FileIO.readFromFile(context, FILE_NAME);
    }

    /**
     * Delete this object file from internal storage
     * @param context - Android Context (Activity/Aplication)
     */
    public void delete(Context context){
        FileIO.deleteFile(context, FILE_NAME);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ParkedCar)) {
            return false;
        }

        ParkedCar that = (ParkedCar) other;

        boolean parkTimeEqual, notesEqual,openModeEqual,parkingLocationEqual,parkEndTimeEqual;


        parkTimeEqual = this.parkTime.getTimeInMillis() == that.getParkTime().getTimeInMillis();
        parkEndTimeEqual = this.endParkTime.getTimeInMillis() == that.getEndParkTime().getTimeInMillis();
        notesEqual = this.notes.equals(that.getNotes());
        openModeEqual = this.openDayMode == that.isOpenDayMode();
        parkingLocationEqual = (this.parkingLocation.getLatitude() == that.getLocation().getLatitude())
                                &&
                                (this.parkingLocation.getLongitude() == that.getLocation().getLongitude());

        return parkTimeEqual && parkEndTimeEqual && notesEqual && openModeEqual && parkingLocationEqual;


    }

}


