import java.util.GregorianCalendar;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello World!");


        GregorianCalendar now = new GregorianCalendar();
        GregorianCalendar then = (GregorianCalendar) now.clone();

        int minutes = 100;

        System.out.println(now.getTime().toString());

        then.add(GregorianCalendar.MINUTE, minutes);

        System.out.println(then.getTime().toString());

        //  limitation of software (check if the dates are still within the same day)
        if (now.get(GregorianCalendar.DAY_OF_WEEK) == then.get(GregorianCalendar.DAY_OF_WEEK)){
            // they are




        }else{
            // fuck knows for now
        }

    }
}
