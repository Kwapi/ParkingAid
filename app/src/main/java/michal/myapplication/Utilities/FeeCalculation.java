package michal.myapplication.Utilities;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.GregorianCalendar;

/**
 *
 * @author vtv13qau
 */
public class FeeCalculation {


    public static int calculateFee(GregorianCalendar start, GregorianCalendar end){


        int fee = 0;
        int startDayOfWeek = start.get(GregorianCalendar.DAY_OF_WEEK);
        int endDayOfWeek = end.get(GregorianCalendar.DAY_OF_WEEK);

        boolean sameDay = startDayOfWeek == endDayOfWeek;

        if(sameDay){
            int dayOfWeek = startDayOfWeek; // does not matter if we use start of end

            //   WEEKEND - £1 flat fee
            if( dayOfWeek == GregorianCalendar.SATURDAY || dayOfWeek == GregorianCalendar.SUNDAY){
                System.out.println("WEEKEND");
                return 1;
            }

            //   NOT WEEKEND - PROCEED TO CALCULATE THE FEE
            int startHour = start.get(GregorianCalendar.HOUR_OF_DAY);
            int startMinute = start.get(GregorianCalendar.MINUTE);

            int endHour = end.get(GregorianCalendar.HOUR_OF_DAY);
            int endMinute = end.get(GregorianCalendar.MINUTE);


            int currentHour = startHour;
            int previousHour = -1;

            System.out.println("Calculating fee from: " + startHour + ":" + startMinute + " to " + endHour + ":" + endMinute );

            while(currentHour < endHour || (currentHour==endHour && startMinute < endMinute)){

                //   PEAK TIME - £5 per hour
                if(isPeakTime(currentHour)){
                    fee += 5;
                    System.out.println("Current hour:\t" + currentHour + ":" + startMinute);
                    System.out.println("Peak Time + 5");
                }

                //   OFF-PEAK TIME - £ 1 per hour
                if(isOffPeakTime(currentHour)){
                    fee += 1;

                    System.out.println("Current hour:\t" + currentHour + ":" + startMinute);
                    System.out.println("Off-peak Time + 1");
                }

                //   OTHER TIME - £1 flat rate
                //   (only added the first time
                //   we enter that price zone from a different price zone
                //   hence the check for the previous hour)
                if(isOtherTime(currentHour) && !isOtherTime(previousHour)){
                    fee += 1;

                    System.out.println("Current hour:\t" + currentHour + ":" + startMinute);
                    System.out.println("Other time + 1");
                }
                previousHour = currentHour;
                currentHour ++;
            }


        }else{
            //not implemented
        }



        return fee;
    }

    /**
     * Peak time - 6:00 til 9:59
     * @param hour
     * @return
     */
    private static boolean isPeakTime(int hour){

        switch(hour){
            case 6:
            case 7:
            case 8:
            case 9:
                return true;

        }
        return false;

    }
    /**
     * Other time 18:00 - 5:59
     * @param hour
     * @return
     */
    private static boolean isOtherTime(int hour){
        switch(hour){
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                return true;
        }
        return false;
    }

    /**
     * Off peak time 10:00 - 17:59
     * @param hour
     * @return
     */
    private static boolean isOffPeakTime(int hour){
        switch (hour){
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
                return true;
        }
        return false;
    }
}

