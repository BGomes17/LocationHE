package com.example.beatrizgomes.beaconlocation.adapter.viewholder;

import android.view.View;
import android.widget.TextView;


import com.example.beatrizgomes.beaconlocation.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EddystoneItemViewHolder {
    /*
    @Bind(R.id.power)
    public TextView txPowerTextView;
    @Bind(R.id.namespace_id)
    public TextView namespace;
    */
    @Bind(R.id.instance_id)
    public TextView instance;
    /*
    @Bind(R.id.url)
    public TextView url;
    @Bind(R.id.temperature)
    public TextView temperature;
    @Bind(R.id.battery_voltage)
    public TextView batteryVoltage;
    @Bind(R.id.pdu_count)
    public TextView pduCount;
    @Bind(R.id.time_since_power_up)
    public TextView timeSincePowerUp;
    @Bind(R.id.telemetry_version)
    public TextView telemetryVersion;
    */
    @Bind(R.id.rssi)
    public TextView rssi;
    @Bind(R.id.proximity)
    public  TextView proximity;

    public EddystoneItemViewHolder(View rootView) {
        ButterKnife.bind(this, rootView);
    }
}
