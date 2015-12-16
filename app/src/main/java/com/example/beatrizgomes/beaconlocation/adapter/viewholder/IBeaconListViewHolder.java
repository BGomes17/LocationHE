package com.example.beatrizgomes.beaconlocation.adapter.viewholder;

import android.view.View;
import android.widget.TextView;

import com.example.beatrizgomes.beaconlocation.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by beatrizgomes on 09/12/15.
 */
public class IBeaconListViewHolder {
    @Bind(R.id.device_name)
    public TextView nameTextView;

    @Bind(R.id.list_proximity)
    public TextView proximityTextView;

    @Bind(R.id.list_rssi)
    public TextView rssiTextView;

    public IBeaconListViewHolder(View rootView) {
        ButterKnife.bind(this, rootView);
    }

}
