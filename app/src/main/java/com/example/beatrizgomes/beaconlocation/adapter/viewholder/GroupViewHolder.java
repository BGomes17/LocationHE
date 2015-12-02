package com.example.beatrizgomes.beaconlocation.adapter.viewholder;

import android.view.View;
import android.widget.TextView;

import com.example.beatrizgomes.beaconlocation.R;


public class GroupViewHolder {

    public TextView header;

    public GroupViewHolder(View view) {
        header = (TextView) view.findViewById(R.id.header);
    }


}
