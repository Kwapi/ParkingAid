package michal.myapplication.Utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.google.android.gms.maps.GoogleMap;

import michal.myapplication.ParkCarScreen;

/**
 * Created by Michal on 19/12/2015.
 */
public class AlertDialogues {


    public static AlertDialog getNoCarSavedBackToParkCarScreen(Context cont){
        final Context context = cont;
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);

        dlgAlert.setMessage("No saved car location");
        dlgAlert.setTitle("Error");
        dlgAlert.setPositiveButton("Save car location", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                returnToParkCarScreen(context);
            }
        });
        dlgAlert.setCancelable(false);

        return dlgAlert.create();
    }


    private static void returnToParkCarScreen(Context context){
        Intent intent = new Intent(context,ParkCarScreen.class);
        context.startActivity(intent);
    }

    public static AlertDialog noDurationInput(Context cont){
        final Context context = cont;
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);

        dlgAlert.setMessage("Please put in the desired duration of stay");
        dlgAlert.setTitle("Error");
        dlgAlert.setCancelable(true);

        return dlgAlert.create();
    }


}
