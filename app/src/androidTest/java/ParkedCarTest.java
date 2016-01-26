import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.GregorianCalendar;

import Framework.Gps.GpsTag;
import michal.myapplication.ParkCarScreen;
import michal.myapplication.ParkedCar;
import michal.myapplication.R;


@RunWith(AndroidJUnit4.class)
public class ParkedCarTest {
    ParkedCar parkedCar = null;
    Context context;
    GregorianCalendar parkedTime,parkEndTime;
    GpsTag parkingLocation;

    @Before
    public void parkedCarPersistanceSetup(){
        parkedCar = ParkedCar.getInstance();

        parkedTime = new GregorianCalendar();
        parkEndTime = new GregorianCalendar();
            parkEndTime.add(GregorianCalendar.MINUTE, 400);
        parkingLocation = new GpsTag("carLocation",45,20);

        parkedCar.setParkTime(parkedTime);
        parkedCar.setEndParkTime(parkEndTime);
        parkedCar.setLocation(parkingLocation);
        parkedCar.setNotes("Tralalala");
        parkedCar.setOpenDayMode(false);
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    }


    @Test
    public void saveLoadTest(){
        parkedCar.save(context);
        ParkedCar loaded = ParkedCar.read(context);
        Assert.assertEquals(parkedCar, loaded);
    }

} 
