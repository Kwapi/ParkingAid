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

            parkedCarString = (TextView) findViewById(R.id.parkedCarString);

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_overview_screen);

            Bundle b = this.getIntent().getExtras();


            //todo: fix the parkedCarString null
            if(b !=null){
                parkedCar = (ParkedCar) b.getSerializable("parkedCar");

                parkedCarString.setText(parkedCar.toString());
            }
    }

}
