package michal.myapplication;

import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import michal.myapplication.ParkedCar;
import michal.myapplication.Utilities.FeeCalculation;

import static org.junit.Assert.*;


public class FeeCalculationTest {

    @Test
    public void weekendFeeCalculationTest(){
        GregorianCalendar weekend = new GregorianCalendar();
        GregorianCalendar weekend2 = new GregorianCalendar();

        weekend2.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        weekend.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);

        weekend2.add(Calendar.MINUTE,20);

        int actual = FeeCalculation.calculateFee(weekend, weekend2);

        int expected = 1;

        assertEquals("Testing calculating fee for parking during on the weekend",expected,actual);

    }



}