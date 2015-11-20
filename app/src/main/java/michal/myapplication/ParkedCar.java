package michal.myapplication;

import java.util.GregorianCalendar;


public class ParkedCar {

    private static ParkedCar instance = null;
    private String notes;
    private boolean openDayMode;
    private GregorianCalendar parkTime;
    private int desiredDuration;


    private ParkedCar(){
    }

    private static ParkedCar getInstance(){
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
}


