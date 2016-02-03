package com.example.beatrizgomes.beaconlocation.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.beatrizgomes.beaconlocation.R;
import com.kontakt.sdk.android.ble.configuration.ActivityCheckConfiguration;
import com.kontakt.sdk.android.ble.configuration.ForceScanConfiguration;
import com.kontakt.sdk.android.ble.configuration.scan.EddystoneScanContext;
import com.kontakt.sdk.android.ble.configuration.scan.ScanContext;
import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.device.DeviceProfile;
import com.kontakt.sdk.android.ble.discovery.BluetoothDeviceEvent;
import com.kontakt.sdk.android.ble.discovery.EventType;
import com.kontakt.sdk.android.ble.discovery.eddystone.EddystoneDeviceEvent;
import com.kontakt.sdk.android.ble.filter.eddystone.EddystoneFilters;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.rssi.RssiCalculators;
import com.kontakt.sdk.android.common.profile.IEddystoneDevice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;

public class DistanceRangeEddystoneActivity  extends BaseActivity implements ProximityManager.ProximityListener {


    public ProximityManager deviceManager;
    IEddystoneDevice eddystone;
    public String beaconIdentifier;
    public ScanContext scanContext;
    public double distance;

    private List<EventType> eventTypes = new ArrayList<EventType>() {{
        add(EventType.DEVICES_UPDATE);
    }};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance_range);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
        setUpActionBar(toolbar);
        setUpActionBarTitle("Distance");

        deviceManager = new ProximityManager(this);




        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            // Recebe da atividade anterior o parâmetro 'SECRET_WORD'
            if (extras.get("EDDYSTONE") != null) {
                eddystone = (IEddystoneDevice) extras.get("EDDYSTONE");

                beaconIdentifier = eddystone.getInstanceId();
                Log.i("EddystoneDetails", "OnCreate(): " + beaconIdentifier);
                startScan();
            }
        }



    }

    @Override
    public void onScanStart() {

    }

    @Override
    public void onScanStop() {

    }

    @Override
    public void onEvent(BluetoothDeviceEvent bluetoothDeviceEvent) {
        Log.i("EddystoneDetails", "OnEvent(): ");
        switch (bluetoothDeviceEvent.getEventType()) {
            case DEVICES_UPDATE:
                onDevicesUpdateEvent(bluetoothDeviceEvent);
                break;
        }
    }

    public void startScan() {

        deviceManager.initializeScan(getOrCreateScanContext(), new OnServiceReadyListener() {
            @Override
            public void onServiceReady() {
                deviceManager.attachListener(DistanceRangeEddystoneActivity.this);
            }

            @Override
            public void onConnectionFailure() {
                //Utils.showToast(BeaconsScanActivity.this, "Erro durante conexão");
            }
        });

    }

    public ScanContext getOrCreateScanContext() {

        EddystoneScanContext eddystoneScanContext;

        eddystoneScanContext = new EddystoneScanContext.Builder()
                .setEventTypes(eventTypes)
                .setDevicesUpdateCallbackInterval(350)
                .setUIDFilters(Arrays.asList(
                        EddystoneFilters.newUIDFilter("f7826da6bc5b71e0893e", beaconIdentifier)
                ))
                .setRssiCalculator(RssiCalculators.DEFAULT)
                .build();

        if (scanContext == null) {
            scanContext = new ScanContext.Builder()
                    .setScanMode(ProximityManager.SCAN_MODE_BALANCED)
                    .setEddystoneScanContext(eddystoneScanContext)
                    .setActivityCheckConfiguration(ActivityCheckConfiguration.DEFAULT)
                    .setForceScanConfiguration(ForceScanConfiguration.DEFAULT)
                    .build();
        }

        return scanContext;
    }

    public void onDevicesUpdateEvent(BluetoothDeviceEvent event) {
        DeviceProfile deviceProfile = event.getDeviceProfile();
        switch (deviceProfile) {
            case EDDYSTONE:
                onEddystoneDevicesList((EddystoneDeviceEvent) event);
                break;
        }
    }

    private void onEddystoneDevicesList(final EddystoneDeviceEvent event) {

        List<IEddystoneDevice> eddystoneDevices = event.getDeviceList();

        for (IEddystoneDevice eddystoneDevice : eddystoneDevices) {

                ImageView imageDistance = (ImageView) findViewById(R.id.image_distance);
                TextView distanceRangeTextView = (TextView) findViewById(R.id.distance_range);
                TextView deviceNameTextView = (TextView) findViewById(R.id.name_indistance);
                deviceNameTextView.setText(eddystoneDevice.getInstanceId());
                distance = eddystoneDevice.getDistance() / 100;
                Log.i("DisRangeEddystoneAct", "Eddystone Distance: " + distance);
                //distanceRangeTextView.setText("Distância: " + distance);

                if (distance >= 0 && distance < 2) {
                    distanceRangeTextView.setText("Distância: 0m - 2m");
                    imageDistance.setImageResource(R.drawable.i4);
                } else if (distance >= 2 && distance < 5) {
                    distanceRangeTextView.setText("Distância: 2m - 5m");
                    imageDistance.setImageResource(R.drawable.i3);
                } else if (distance >= 5 && distance < 8) {
                    distanceRangeTextView.setText("Distância: 5m - 8m");
                    imageDistance.setImageResource(R.drawable.i2);
                } else if (distance >= 8 && distance < 12) {
                    distanceRangeTextView.setText("Distância: 8m - 12m");
                    imageDistance.setImageResource(R.drawable.i1);
                } else if (distance >= 12) {
                    distanceRangeTextView.setText("Distância: > 12m");
                    imageDistance.setImageResource(R.drawable.i0);
                }

            }


        }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // do something on back.
            Intent intentScanActivity = new Intent(DistanceRangeEddystoneActivity.this, EddystoneDetailsActivity.class);
            intentScanActivity.putExtra("EDDYSTONE", eddystone);
            //intentScanActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentScanActivity);
            finish();
            return true;
        }


        return super.onKeyDown(keyCode, event);
    }

}
