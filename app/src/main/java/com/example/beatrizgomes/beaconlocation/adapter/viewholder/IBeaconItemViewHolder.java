package com.example.beatrizgomes.beaconlocation.adapter.viewholder;

import android.view.View;
import android.widget.TextView;

import com.example.beatrizgomes.beaconlocation.R;


public class IBeaconItemViewHolder {
    public TextView nameTextView;
    public TextView majorTextView;
    public TextView minorTextView;
    public TextView beaconUniqueIdTextView;
    public TextView firmwareVersionTextView;
    public TextView txPowerTextView;
    public TextView rssiTextView;
    public TextView proximityTextView;
    public TextView proximityUUIDTextView;

    public IBeaconItemViewHolder(View view) {
        nameTextView = (TextView) view.findViewById(R.id.device_name);
        /*
        majorTextView = (TextView) view.findViewById(R.id.major);
        minorTextView = (TextView) view.findViewById(R.id.minor);
        txPowerTextView = (TextView) view.findViewById(R.id.power);
        */
        rssiTextView = (TextView) view.findViewById(R.id.rssi);
        /*
        proximityUUIDTextView = (TextView) view.findViewById(R.id.proximity_uuid);
        beaconUniqueIdTextView = (TextView) view.findViewById(R.id.beacon_unique_id);
        firmwareVersionTextView = (TextView) view.findViewById(R.id.firmware_version);
        */
        proximityTextView = (TextView) view.findViewById(R.id.proximity);

    }
}
