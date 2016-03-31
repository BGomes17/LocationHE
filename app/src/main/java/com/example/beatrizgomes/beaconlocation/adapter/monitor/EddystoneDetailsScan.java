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

    /**
     * The Device manager.
     */
    public ProximityManager deviceManager;
    /**
     * The Scan context.
     */
    public ScanContext scanContext;
    /**
     * The Beacon identifier.
     */
    public String beaconIdentifier;
    /**
     * The Distance.
     */
    public double distance;
    /**
     * The Rssi mode.
     */
    public ArrayList<Double> rssiMode = new ArrayList<>();
    /**
     * The Rssi array.
     */
    public ArrayList<Double> rssiArray = new ArrayList<>();
    /**
     * The Count.
     */
    int count = 0; // variavel usada no metodo calculateDistance();
    private Context context;
    private List<EventType> eventTypes = new ArrayList<EventType>() {{
        add(EventType.DEVICES_UPDATE);
    }};

    /**
     * Instantiates a new Eddystone details scan.
     *
     * @param context the context
     */
    public EddystoneDetailsScan(Context context) {

        this.context = context;
        deviceManager = new ProximityManager(context);

    }


    /**
     * Instantiates a new Eddystone details scan.
     *
     * @param context    the context
     * @param identifier the identifier
     */
    public EddystoneDetailsScan(Context context, String identifier) {

        this.beaconIdentifier = identifier;
        this.context = context;
        deviceManager = new ProximityManager(context);
    }

    /**
     * Start scan.
     *
     * @param listener the listener
     */
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

    /**
     * Gets or create scan context.
     *
     * @return the or create scan context
     */
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

    /**
     * On devices update event.
     *
     * @param event the event
     */
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

            } else if (context == DistanceRangeActivity.getContext()) {
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

    /**
     * Calculate distance.
     *
     * @param txPower      the tx power
     * @param receivedRssi the received rssi
     */
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

        } else {
            double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
            distanceTextView.setText(Html.fromHtml("<b>Distância:</b> &nbsp;&nbsp;"));
            distanceTextView.append(String.format("%.2f cm", accuracy));
        }
    }

    /**
     * Rssi suavization double.
     *
     * @param rssi Parametro obtido do metodo calculateDistance(int txPower, double receivedRssi), que representa o RSSI lido.
     * @return Retorna a media dos RSSI's
     */
    public double rssiSuavization(double rssi) {

        double variation = 0, modeValue = 0;

        if (rssiMode.size() < 15) {
            // Preencher o ArryList rssiMode() com 15 valores sem qualquer filtro.
            rssiMode.add(rssi);
            count = 0;
        } else { // rssiMode() cheio.

            // modeValue fica com a MODA dos valores de rssi obtidos.
            modeValue = mode();

            // variation fica com a diferença entre o rssi obtido com a MODA dos Rssi's
            variation = Math.abs(rssi) - Math.abs(modeValue);

            // Janela de valores aceites a serem considerados para o calculo da nova MODA.
            if (variation >= 0 && variation <= 5) {
                count = 0;
                rssiMode.remove(0);
                rssiMode.add(rssi);
            } else{
                // Se o rssi for descartado incrementamos a variavel count.
                count++;
            }

            // modeValue fica com a nova MODA
            modeValue = mode();

            // variation fica com a diferença entre o rssi obtido com a nova MODA dos Rssi's
            variation = Math.abs(rssi) - Math.abs(modeValue);

            // Janela de valores aceites a serem considerados para o calculo da Media dos Rssi's.
            if (variation >= 0 && variation <= 3) {
                if (rssiArray.size() >= 20)
                    rssiArray.remove(0);

                rssiArray.add(rssi);
            }
        }
        /**
         * Se count == 5, significa que 5 rssi seguidos foram descartados.
         * É muito provavél que nos estejamos a deslocar e que estejamos a descartar valores importantes.
         * Vamos retirar metade dos valores dos ArrayList's de maneira a deixar entrar novos valores para o calculo da nova
         * MODA e Média.
          */
        if (count == 5) {
            Log.i("EddystoneDetailsScan", "rssiSuavization(): Count =  5 ");
            for (int i = 0; i < 9; i++) {
                rssiMode.remove(0);
                if (rssiArray.size() >= 12  )
                    rssiArray.remove(0);
            }
        }

        Log.i("EddystoneDetailsScan", "rssiSuavization(): mode: " + modeValue);

        // Retorna a média dos Rssi's, valor que irá ser usado para o calculo da distancia.
        return averageRssi();
    }

    /**
     * public double mode() {
     *
     * @return Retorna um double que representa a MODA do conjunto de valores presente no ArrayList<Double> rssiMode.
     */
    public double mode() {
        HashMap<Double, Integer> hm = new HashMap<>();
        double temp = rssiMode.get(rssiMode.size() - 1);
        int count = 0, max = 1;
        for (int i = 0; i < rssiMode.size(); i++) {
            if (hm.get(rssiMode.get(i)) != null) {
                count = hm.get(rssiMode.get(i));
                count++;
                hm.put(rssiMode.get(i), count);
                if (count > max) {
                    max = count;
                    temp = rssiMode.get(i);
                }
            } else {
                hm.put(rssiMode.get(i), 1);
            }
        }
        return temp;
    }

    /**
     * public double averageRssi() {
     *
     * @return Retorna um double que representa a Média do conjunto de valores presente no ArrayList<Double> rssiArray.
     */
    public double averageRssi() {

        if (rssiArray.size() == 0)
            return 0.0;
        double sum = 0;

        for (double val : rssiArray) {
            sum += val;
        }

        return sum / rssiArray.size();
    }


}


