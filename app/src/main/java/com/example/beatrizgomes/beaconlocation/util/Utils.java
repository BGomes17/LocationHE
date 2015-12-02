package com.example.beatrizgomes.beaconlocation.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by beatrizgomes on 02/12/15.
 */
public final class Utils {

    public static void showToast(final Context context, final String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

}
