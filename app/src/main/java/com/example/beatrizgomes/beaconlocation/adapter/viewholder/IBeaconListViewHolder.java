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
    /**
     * The Name text view.
     */
    @Bind(R.id.device_name)
    public TextView nameTextView;

    /**
     * The Proximity text view.
     */
    @Bind(R.id.list_proximity)
    public TextView proximityTextView;

    /**
     * The Rssi text view.
     */
    @Bind(R.id.list_rssi)
    public TextView rssiTextView;

    /**
     * Instantiates a new Beacon list view holder.
     *
     * @param rootView the root view
     */
    public IBeaconListViewHolder(View rootView) {
        ButterKnife.bind(this, rootView);
    }

}
