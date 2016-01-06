package com.example.beatrizgomes.beaconlocation.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.beatrizgomes.beaconlocation.R;
import com.example.beatrizgomes.beaconlocation.adapter.monitor.BeaconsDetailsScan;
import com.kontakt.sdk.android.ble.discovery.BluetoothDeviceEvent;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.common.profile.IEddystoneDevice;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DistanceRangeActivity extends BaseActivity implements ProximityManager.ProximityListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.image_distance)
    ImageView imageDistance;

    IEddystoneDevice eddystone;

    BeaconsDetailsScan beaconScan;

    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance_range);

        context = this;

        ButterKnife.bind(this);
        setUpActionBar(toolbar);
        setUpActionBarTitle("Distance");



        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            // Recebe da atividade anterior o parâmetro 'SECRET_WORD'

            eddystone = (IEddystoneDevice) extras.get("EDDYSTONE");

            beaconScan = new BeaconsDetailsScan(this, eddystone.getInstanceId());
            beaconScan.startScan(DistanceRangeActivity.this);
            /*
            double distance = eddystone.getDistance() / 100;
            Toast.makeText(this, "" + eddystone.getDistance(), Toast.LENGTH_LONG).show();
            if(distance >= 0 && distance<3) {
                imageDistance.setImageResource(R.drawable.i03m);
            }
            else if(distance >= 3 && distance < 7) {
                imageDistance.setImageResource(R.drawable.i37m);
            }
            else if(distance >= 7 && distance < 12) {
                imageDistance.setImageResource(R.drawable.i712m);
            }
            else if(distance >= 12 && distance < 20) {
                imageDistance.setImageResource(R.drawable.i1220m);
            }
            else if(distance >= 20) {
                imageDistance.setImageResource(R.drawable.i2050);
            }
            */
            //nameTextView.setText(Html.fromHtml("<b>Nome:</b> &nbsp;&nbsp;" + eddystone.getNamespaceId()));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconScan.deviceManager.disconnect();
        beaconScan.deviceManager = null;
        ButterKnife.unbind(this);
    }

    public static Context getContext() {
        return context;
    }


    @Override
    public void onScanStart() {

    }

    @Override
    public void onScanStop() {

    }

    @Override
    public void onEvent(BluetoothDeviceEvent bluetoothDeviceEvent) {
        switch (bluetoothDeviceEvent.getEventType()) {
            case DEVICES_UPDATE:
                beaconScan.onDevicesUpdateEvent(bluetoothDeviceEvent);
                break;
        }
    }
}
