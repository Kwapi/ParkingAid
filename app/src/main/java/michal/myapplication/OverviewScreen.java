package michal.myapplication;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class OverviewScreen extends AppCompatActivity {

        public static final String TAG = OverviewScreen.class.getSimpleName();

        private ParkedCar parkedCar;
        private TextView parkedCarString;

        @Override
        protected void onCreate(Bundle savedInstanceState) {


            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_overview_screen);


            parkedCarString = (TextView) findViewById(R.id.parkedCarString);

            Bundle b = this.getIntent().getExtras();


           
            if(b !=null){
                parkedCar = (ParkedCar) b.getSerializable("parkedCar");

                parkedCarString.setText(parkedCar.toString());
            }
    }

}
