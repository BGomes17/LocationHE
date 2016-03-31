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
import java.util.HashMap;
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
    public ArrayList<Double> rssiMode = new ArrayList<>();
    public ArrayList<Double> rssiArray = new ArrayList<>();
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
                .setDevicesUpdateCallbackInterval(250)
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

                calculateDistance(eddystoneDevice.getTxPower(), eddystoneDevice.getRssi());

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
                }//*/

            } else

            if (context == DistanceRangeActivity.getContext()) {
                ImageView imageDistance = (ImageView) ((Activity) context).findViewById(R.id.image_distance);
                TextView distanceRangeTextView = (TextView) ((Activity) context).findViewById(R.id.distance_range);
                TextView deviceNameTextView = (TextView) ((Activity) context).findViewById(R.id.name_indistance);
                deviceNameTextView.setText(eddystoneDevice.getInstanceId());
                distance = eddystoneDevice.getDistance() / 100;

                distanceRangeTextView.setText("Distância: " + distance);

                if (distance >= 0 && distance < 1) {
                    distanceRangeTextView.setText("Distância: 0m - 1m");
                    imageDistance.setImageResource(R.drawable.i4);
                } else if (distance >= 1 && distance < 3) {
                    distanceRangeTextView.setText("Distância: 1m - 3m");
                    imageDistance.setImageResource(R.drawable.i3);
                } else if (distance >= 3 && distance < 6) {
                    distanceRangeTextView.setText("Distância: 3m - 6m");
                    imageDistance.setImageResource(R.drawable.i2);
                } else if (distance >= 6 && distance < 9) {
                    distanceRangeTextView.setText("Distância: 6m - 9m");
                    imageDistance.setImageResource(R.drawable.i1);
                } else if (distance >= 9) {
                    distanceRangeTextView.setText("Distância: > 9m");
                    imageDistance.setImageResource(R.drawable.i0);
                }

            }

        }

    }

    public void calculateDistance(int txPower, double receivedRssi) {

        Log.i("EddystoneDetailsScan", "calculateDistance(): receivedRssi: " + receivedRssi);

        TextView distanceTextView = (TextView) ((Activity) context).findViewById(R.id.eddystone_distance);

        double rssi = rssiSuavization(receivedRssi);
        Log.i("EddystoneDetailsScan", "calculateDistance(): rssi suavizado: " + rssi);

        if (rssi == 0) {
            distanceTextView.setText(Html.fromHtml("<b>Distância:</b> &nbsp;&nbsp;"));
            distanceTextView.append(String.format("a calibrar . . ."));
            //return -1.0; // if we cannot determine distance, return -1.
        }

        double ratio = rssi * 1.0 / txPower;
        if (ratio < 1.0) {
            //return Math.pow(ratio,10);
            distanceTextView.setText(Html.fromHtml("<b>Distância:</b> &nbsp;&nbsp;"));
            distanceTextView.append(String.format("%.2f cm", Math.pow(ratio, 10)));

        }
        else {
            double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            distanceTextView.setText(Html.fromHtml("<b>Distância:</b> &nbsp;&nbsp;"));
            distanceTextView.append(String.format("%.2f cm", accuracy));
        }
    }

    public double rssiSuavization(double rssi) {

        double variation = 0, modeValue = 0;
        double nrssi = rssi;
        Log.i("rssiSuavization","nrssi" + nrssi);
        if(rssiMode.size() < 15) {
            rssiMode.add(nrssi);
        }
        else {
            modeValue = mode();
            variation = Math.abs(nrssi) - Math.abs(modeValue);
            if (variation >= 1 && variation <= 5){
                rssiMode.remove(0);
                rssiMode.add(nrssi);
            }
            modeValue = mode();
            variation = Math.abs(nrssi) - Math.abs(modeValue);
            if (variation >= -2 && variation <= 5) {
                if(rssiArray.size() >= 20)
                    rssiArray.remove(0);

                rssiArray.add(nrssi);
                }
        }
        Log.i("EddystoneDetailsScan", "rssiSuavization(): mode: " + modeValue);

        return averageRssi();
    }

    public double mode() {
        HashMap<Double,Integer> hm=new HashMap<>();
        double temp = rssiMode.get(rssiMode.size() - 1);
        int count = 0, max = 1;
        for(int i = 0; i < rssiMode.size(); i++) {
            if(hm.get(rssiMode.get(i))!=null) {
                count = hm.get(rssiMode.get(i));
                count++;
                hm.put(rssiMode.get(i), count);
                if(count > max) {
                    max = count;
                    temp = rssiMode.get(i);
                }
            }
            else {
                hm.put(rssiMode.get(i), 1);
            }
        }
        return temp;
    }

    public double averageRssi() {

        if (rssiArray.size() == 0)
            return 0.0;
        double sum = 0;

        for(double val : rssiArray) {
            sum += val;
        }

        return sum/rssiArray.size();
    }



}


