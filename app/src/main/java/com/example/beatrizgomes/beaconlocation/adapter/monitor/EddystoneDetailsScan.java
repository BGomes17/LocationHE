package com.example.beatrizgomes.beaconlocation.adapter.monitor;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.beatrizgomes.beaconlocation.R;
import com.example.beatrizgomes.beaconlocation.ui.activity.DistanceRangeActivity;
import com.example.beatrizgomes.beaconlocation.ui.activity.EddystoneDetailsActivity;
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

/**
 * Created by beatrizgomes on 13/01/16.
 */

public class EddystoneDetailsScan {

    public ProximityManager deviceManager;
    public ScanContext scanContext;
    public String beaconIdentifier;
    public double distance;
    private Context context;
    private List<EventType> eventTypes = new ArrayList<EventType>() {{
        add(EventType.DEVICES_UPDATE);
    }};

    public EddystoneDetailsScan(Context context) {

        this.context = context;
        deviceManager = new ProximityManager(context);

    }


    public EddystoneDetailsScan(Context context, String identifier) {

        this.beaconIdentifier = identifier;
        this.context = context;
        deviceManager = new ProximityManager(context);
    }

    public void startScan(final ProximityManager.ProximityListener listener) {

        deviceManager.initializeScan(getOrCreateScanContext(), new OnServiceReadyListener() {
            @Override
            public void onServiceReady() {
                deviceManager.attachListener(listener);
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
                .setDevicesUpdateCallbackInterval(1000)
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

            if (context == EddystoneDetailsActivity.getContext()) {

                TextView distanceTextView = (TextView) ((Activity) context).findViewById(R.id.eddystone_distance);
                distanceTextView.setText(Html.fromHtml("<b>Distância:</b> &nbsp;&nbsp;"));
                distanceTextView.append(String.format("%.2f cm", eddystoneDevice.getDistance()));

                TextView rssiTextView = (TextView) ((Activity) context).findViewById(R.id.eddystone_rssi);
                rssiTextView.setText(Html.fromHtml("<b>RSSI:</b> &nbsp;&nbsp;"));
                rssiTextView.append(String.format("%.2f dBm", eddystoneDevice.getRssi()));

                TextView proximityTextView = (TextView) ((Activity) context).findViewById(R.id.eddystone_proximity);

                /*
                switch (eddystoneDevice.getProximity().toString()) {
                    case "FAR":
                        proximityTextView.setText(Html.fromHtml("<b>Proximidade:</b> &nbsp;&nbsp;Longe"));
                        break;
                    case "NEAR":
                        proximityTextView.setText(Html.fromHtml("<b>Proximidade:</b> &nbsp;&nbsp;Perto"));
                        break;
                    case "IMMEDIATE":
                        proximityTextView.setText(Html.fromHtml("<b>Proximidade:</b> &nbsp;&nbsp;Mto Perto"));
                        break;
                }*/

            } else if (context == DistanceRangeActivity.getContext()) {
                ImageView imageDistance = (ImageView) ((Activity) context).findViewById(R.id.image_distance);
                TextView distanceRangeTextView = (TextView) ((Activity) context).findViewById(R.id.distance_range);
                TextView deviceNameTextView = (TextView) ((Activity) context).findViewById(R.id.name_indistance);
                deviceNameTextView.setText(eddystoneDevice.getInstanceId());
                distance = eddystoneDevice.getDistance() / 100;
                Log.i("EddystoneDetailsScan", "Eddystone Distance: " + distance);
                distanceRangeTextView.setText("Distância: " + distance);

                if (distance >= 0 && distance < 1) {
                    distanceRangeTextView.setText("Distância: 0m - 3m");
                    imageDistance.setImageResource(R.drawable.i4);
                } else if (distance >= 1 && distance < 3) {
                    distanceRangeTextView.setText("Distância: 3m - 7m");
                    imageDistance.setImageResource(R.drawable.i3);
                } else if (distance >= 3 && distance < 12) {
                    distanceRangeTextView.setText("Distância: 7m - 12m");
                    imageDistance.setImageResource(R.drawable.i2);
                } else if (distance >= 12 && distance < 20) {
                    distanceRangeTextView.setText("Distância: 12m - 20m");
                    imageDistance.setImageResource(R.drawable.i1);
                } else if (distance >= 20) {
                    distanceRangeTextView.setText("Distância: > 20m");
                    imageDistance.setImageResource(R.drawable.i0);
                }

            }

        }

    }
}


