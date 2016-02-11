package com.example.beatrizgomes.beaconlocation.ui.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.example.beatrizgomes.beaconlocation.R;
import com.example.beatrizgomes.beaconlocation.adapter.monitor.EddystoneDetailsScan;
import com.example.beatrizgomes.beaconlocation.adapter.monitor.IBeaconsDetailsScan;
import com.kontakt.sdk.android.ble.discovery.BluetoothDeviceEvent;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.util.BluetoothUtils;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;
import com.kontakt.sdk.android.common.profile.IEddystoneDevice;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DistanceRangeActivity extends BaseActivity implements ProximityManager.ProximityListener {

    private static final int REQUEST_CODE_ENABLE_BLUETOOTH = 1;
    public static Context context;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.image_distance)
    ImageView imageDistance;
    IEddystoneDevice eddystone;
    IBeaconDevice ibeacon;
    IBeaconsDetailsScan ibeaconScan;
    EddystoneDetailsScan eddystoneScan;

    public static Context getContext() {
        return context;
    }

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
            if (extras.get("EDDYSTONE") != null) {
                eddystone = (IEddystoneDevice) extras.get("EDDYSTONE");

                eddystoneScan = new EddystoneDetailsScan(this, eddystone.getInstanceId());
                eddystoneScan.startScan(DistanceRangeActivity.this);
            } else {
                ibeacon = (IBeaconDevice) extras.get("IBEACON");

                ibeaconScan = new IBeaconsDetailsScan(this, ibeacon.getName());
                ibeaconScan.startScan(DistanceRangeActivity.this);
            }
            /**
            eddystone = (IEddystoneDevice) extras.get("EDDYSTONE");

            ibeaconScan = new IBeaconsDetailsScan(this, eddystone.getInstanceId());
            ibeaconScan.startScan(DistanceRangeActivity.this);
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
            }*/

            //nameTextView.setText(Html.fromHtml("<b>Nome:</b> &nbsp;&nbsp;" + eddystone.getNamespaceId()));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!BluetoothUtils.isBluetoothEnabled()) {
            final Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_CODE_ENABLE_BLUETOOTH);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (eddystone != null)
            eddystoneScan.deviceManager.finishScan();
        else
            ibeaconScan.deviceManager.finishScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (eddystone != null) {
            eddystoneScan.deviceManager.disconnect();
            eddystoneScan.deviceManager = null;
        } else {
            ibeaconScan.deviceManager.disconnect();
            ibeaconScan.deviceManager = null;
        }
        ButterKnife.unbind(this);

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
                if (eddystone != null)
                    eddystoneScan.onDevicesUpdateEvent(bluetoothDeviceEvent);
                else
                    ibeaconScan.onDevicesUpdateEvent(bluetoothDeviceEvent);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // do something on back.
            Intent intentDetailsActivity;
            if (eddystone != null) {
                intentDetailsActivity = new Intent(DistanceRangeActivity.this, EddystoneDetailsActivity.class);
                intentDetailsActivity.putExtra("EDDYSTONE", eddystone);
            } else {
                intentDetailsActivity = new Intent(DistanceRangeActivity.this, IBeaconDetailsActivity.class);
                intentDetailsActivity.putExtra("IBEACON", ibeacon);
            }
            startActivity(intentDetailsActivity);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
