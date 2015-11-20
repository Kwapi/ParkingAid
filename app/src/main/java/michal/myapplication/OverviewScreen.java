package michal.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class OverviewScreen extends AppCompatActivity {
    public static final String TAG = OverviewScreen.class.getSimpleName();

    private ParkedCar parkedCar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview_screen);

        parkedCar = ParkedCar.getInstance();

        Log.i(TAG,parkedCar.getLocation().getLatitude() + " , " + parkedCar.getLocation().getLongitude());

    }


}
