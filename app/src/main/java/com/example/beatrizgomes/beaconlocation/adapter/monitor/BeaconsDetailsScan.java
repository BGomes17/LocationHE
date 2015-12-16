package com.example.beatrizgomes.beaconlocation.adapter.monitor;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.widget.TextView;

import com.example.beatrizgomes.beaconlocation.R;
import com.kontakt.sdk.android.ble.configuration.ActivityCheckConfiguration;
import com.kontakt.sdk.android.ble.configuration.ForceScanConfiguration;
import com.kontakt.sdk.android.ble.configuration.scan.EddystoneScanContext;
import com.kontakt.sdk.android.ble.configuration.scan.IBeaconScanContext;
import com.kontakt.sdk.android.ble.configuration.scan.ScanContext;
import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.device.DeviceProfile;
import com.kontakt.sdk.android.ble.discovery.BluetoothDeviceEvent;
import com.kontakt.sdk.android.ble.discovery.EventType;
import com.kontakt.sdk.android.ble.discovery.eddystone.EddystoneDeviceEvent;
import com.kontakt.sdk.android.ble.discovery.ibeacon.IBeaconDeviceEvent;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.rssi.RssiCalculators;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;
import com.kontakt.sdk.android.common.profile.IEddystoneDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by André Pinto on 15/12/2015.
 */
public class BeaconsDetailsScan {

    private Context context;

    public ProximityManager deviceManager;

    public ScanContext scanContext;

    public String beaconAddress;

    private List<EventType> eventTypes = new ArrayList<EventType>() {{
        add(EventType.DEVICES_UPDATE);
    }};

    private IBeaconScanContext beaconScanContext = new IBeaconScanContext.Builder()
            .setEventTypes(eventTypes) //only specified events we be called on callback
            .setRssiCalculator(RssiCalculators.newLimitedMeanRssiCalculator(5))
            .build();

    private EddystoneScanContext eddystoneScanContext = new EddystoneScanContext.Builder()
            .setEventTypes(eventTypes)
            .setRssiCalculator(RssiCalculators.newLimitedMeanRssiCalculator(5))
            .build();

    public BeaconsDetailsScan(Context context) {

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
        if (scanContext == null) {
            scanContext = new ScanContext.Builder()
                    .setScanMode(ProximityManager.SCAN_MODE_BALANCED)
                    .setIBeaconScanContext(beaconScanContext)
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
            case IBEACON:
                onIBeaconDevicesList((IBeaconDeviceEvent) event);
                break;
            case EDDYSTONE:
                onEddystoneDevicesList((EddystoneDeviceEvent) event);
                break;
        }
    }

    private void onEddystoneDevicesList(final EddystoneDeviceEvent event) {


        List<IEddystoneDevice> eddystoneDevices = event.getDeviceList();

        for (IEddystoneDevice eddystoneDevice : eddystoneDevices) {
            if (eddystoneDevice.getAddress().equals(beaconAddress)) {

                TextView distanceTextView = (TextView) ((Activity) context).findViewById(R.id.eddystone_distance);
                distanceTextView.setText(Html.fromHtml("<b>Distância:</b> &nbsp;&nbsp;"));
                distanceTextView.append(String.format("%.2f cm", eddystoneDevice.getDistance()));

                TextView rssiTextView = (TextView) ((Activity) context).findViewById(R.id.eddystone_rssi);
                rssiTextView.setText(Html.fromHtml("<b>RSSI:</b> &nbsp;&nbsp;"));
                rssiTextView.append(String.format("%.2f dBm", eddystoneDevice.getRssi()));

                TextView proximityTextView = (TextView) ((Activity) context).findViewById(R.id.eddystone_proximity);

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
                }
                //proximityTextView.setText(Html.fromHtml("<b>Proximidade:</b> &nbsp;&nbsp;" + eddystoneDevice.getProximity()));

            }
        }
    }

    private void onIBeaconDevicesList(final IBeaconDeviceEvent event) {

        List<IBeaconDevice> iBeaconDevices = event.getDeviceList();

        for (IBeaconDevice iBeaconDevice : iBeaconDevices) {
            if (iBeaconDevice.getAddress().equals(beaconAddress)) {

                TextView distanceTextView = (TextView) ((Activity) context).findViewById(R.id.distance);
                distanceTextView.setText(Html.fromHtml("<b>Distância:</b> &nbsp;&nbsp;"));
                distanceTextView.append(String.format("%.2f m", iBeaconDevice.getDistance()));

                TextView rssiTextView = (TextView) ((Activity) context).findViewById(R.id.rssi);
                rssiTextView.setText(Html.fromHtml("<b>RSSI:</b> &nbsp;&nbsp;"));
                rssiTextView.append(String.format("%.2f dBm", iBeaconDevice.getRssi()));

                TextView proximityTextView = (TextView) ((Activity) context).findViewById(R.id.proximity);
                switch (iBeaconDevice.getProximity().toString()) {
                    case "FAR":
                        proximityTextView.setText(Html.fromHtml("<b>Proximidade:</b> &nbsp;&nbsp;Longe"));
                        break;
                    case "NEAR":
                        proximityTextView.setText(Html.fromHtml("<b>Proximidade:</b> &nbsp;&nbsp;Perto"));
                        break;
                    case "IMMEDIATE":
                        proximityTextView.setText(Html.fromHtml("<b>Proximidade:</b> &nbsp;&nbsp;Mto Perto"));
                        break;
                }

                //proximityTextView.setText(Html.fromHtml("<b>Proximidade:</b> &nbsp;&nbsp;" + iBeaconDevice.getProximity()));
            }

        }

    }
}


