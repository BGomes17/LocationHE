package com.example.beatrizgomes.beaconlocation.ui.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

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

    @Bind(R.id.distance_range)
    TextView distanceRangeTextView;

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

            distanceRangeTextView.setText(Html.fromHtml("<b>Distância:</b> &nbsp;&nbsp;<i>a calibrar . . .</i>"));


            // Recebe da atividade anterior como parâmetro o dispositivo selecionado
            if (extras.get("EDDYSTONE") != null) {
                eddystone = (IEddystoneDevice) extras.get("EDDYSTONE");


                eddystoneScan = new EddystoneDetailsScan(this, eddystone.getInstanceId());
                eddystoneScan.startScan(DistanceRangeActivity.this);
            } else {
                ibeacon = (IBeaconDevice) extras.get("IBEACON");


                ibeaconScan = new IBeaconsDetailsScan(this, ibeacon.getName());
                ibeaconScan.startScan(DistanceRangeActivity.this);
            }

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
