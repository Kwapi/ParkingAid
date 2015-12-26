package Framework.MapHelpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import Framework.Gps.GpsTag;

/**
 * Various useful static methods to help with the maps
 */
public class Utils {


    /**
     * Converts gpsTag to LatLng which is useful for any changes with the way the map is displayed (i.e. in CameraUpdate)
     *
     * @param gpsTag
     * @return
     */
    public static LatLng toLatLng(GpsTag gpsTag) {
        return new LatLng(gpsTag.getLatitude(), gpsTag.getLongitude());
    }

    /**
     * Returns a map type selector dialog (4 options - Road Map, Hybrid, Satellite, Terrain)
     * Make sure to call the method show() from the desired activity to display it.
     *
     * @param map
     * @param context - use ThisActivityName.this to obtain the right context
     * @return AlertDialog - map type selector dialog
     */
    public static AlertDialog getMapTypeSelectorDialog(final GoogleMap map, Context context) {

        final CharSequence[] MAP_TYPE_ITEMS =
                {"Road Map", "Hybrid", "Satellite", "Terrain"};

        // Prepare the dialog by setting up a Builder.
        final String fDialogTitle = "Select Map Type";
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(fDialogTitle);

        // Find the current map type to pre-check the item representing the current state.
        int checkItem = map.getMapType() - 1;

        // Add an OnClickListener to the dialog, so that the selection will be handled.
        builder.setSingleChoiceItems(
                MAP_TYPE_ITEMS,
                checkItem,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {
                        // Locally create a finalised object.

                        // Perform an action depending on which item was selected.
                        switch (item) {
                            case 1:
                                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                break;
                            case 2:
                                map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                break;
                            case 3:
                                map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                break;
                            default:
                                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        }
                        dialog.dismiss();
                    }
                }
        );

        // Build the dialog and show it.
        AlertDialog fMapTypeDialog = builder.create();
        fMapTypeDialog.setCanceledOnTouchOutside(true);
        return fMapTypeDialog;
    }

}
