package com.example.beatrizgomes.beaconlocation.ui.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.beatrizgomes.beaconlocation.R;
import com.kontakt.sdk.android.ble.configuration.ActivityCheckConfiguration;
import com.kontakt.sdk.android.ble.configuration.ForceScanConfiguration;
import com.kontakt.sdk.android.ble.configuration.scan.IBeaconScanContext;
import com.kontakt.sdk.android.ble.configuration.scan.ScanContext;
import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.device.DeviceProfile;
import com.kontakt.sdk.android.ble.discovery.BluetoothDeviceEvent;
import com.kontakt.sdk.android.ble.discovery.EventType;
import com.kontakt.sdk.android.ble.discovery.ibeacon.IBeaconDeviceEvent;
import com.kontakt.sdk.android.ble.filter.ibeacon.IBeaconFilters;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.rssi.RssiCalculators;
import com.kontakt.sdk.android.ble.util.BluetoothUtils;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IBeaconDetailsActivity extends BaseActivity implements ProximityManager.ProximityListener {

    private static final int REQUEST_CODE_ENABLE_BLUETOOTH = 1;
    @Bind(R.id.ibeacon_name)
    public TextView nameTextView;
    @Bind(R.id.power)
    public TextView txPowerTextView;
    @Bind(R.id.major)
    public TextView majorTextView;
    @Bind(R.id.minor)
    public TextView minorTextView;
    @Bind(R.id.rssi)
    public TextView rssiTextView;
    @Bind(R.id.proximity)
    public TextView proximityTextView;
    @Bind(R.id.distance)
    public TextView distanceTextView;
    @Bind(R.id.battery)
    public TextView batteryTextView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    IBeaconDevice ibeacon;
    public double distance;
    private String beaconIdentifier;
    private List<EventType> eventTypes = new ArrayList<EventType>() {{
        add(EventType.DEVICES_UPDATE);
    }};

    public ProximityManager deviceManager;
    public ScanContext scanContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_details);



        ButterKnife.bind(this);
        setUpActionBar(toolbar);
        setUpActionBarTitle("Detalhes");

        deviceManager = new ProximityManager(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // Recebe da atividade anterior como parâmetro o dispositivo selecionado
            ibeacon = (IBeaconDevice) extras.get("IBEACON");
            nameTextView.setText(Html.fromHtml("<b>Nome:</b> &nbsp;&nbsp;" + ibeacon.getUniqueId()));
            distanceTextView.setText(Html.fromHtml("<b>Distância:</b> &nbsp;&nbsp;"));
            distanceTextView.append(String.format("%.2f m", ibeacon.getDistance()));
            majorTextView.setText(Html.fromHtml("<b>Major:</b> &nbsp;&nbsp;" + ibeacon.getMajor()));
            minorTextView.setText(Html.fromHtml("<b>Minor:</b> &nbsp;&nbsp;" + ibeacon.getMinor()));
            rssiTextView.setText(Html.fromHtml("<b>RSSI:</b> &nbsp;&nbsp;" + ibeacon.getRssi() + " dBm"));
            txPowerTextView.setText(Html.fromHtml("<b>Tx Power:</b> &nbsp;&nbsp;" + ibeacon.getTxPower()));
            batteryTextView.setText(Html.fromHtml("<b>Bateria:</b> &nbsp;&nbsp;" + ibeacon.getBatteryPower() + "%"));
            switch (ibeacon.getProximity().toString()) {
                case "FAR":
                    proximityTextView.setText(Html.fromHtml("<b>Proximidade:</b> &nbsp;&nbsp;Longe"));
                    break;
                case "NEAR":
                    proximityTextView.setText(Html.fromHtml("<b>Proximidade:</b> &nbsp;&nbsp;Perto"));
                    break;
                case "IMMEDIATE":
                    proximityTextView.setText(Html.fromHtml("<b>Proximidade:</b> &nbsp;&nbsp;Muito Perto"));
                    break;
            }

            beaconIdentifier = ibeacon.getName();
            startScan();

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_eddystone);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intentDetailsActivity = new Intent(IBeaconDetailsActivity.this, DistanceRangeIBeaconActivity.class);
                    intentDetailsActivity.putExtra("IBEACON", ibeacon);
                    startActivity(intentDetailsActivity);
                    finish();
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                }
            });

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_beacon_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                onDevicesUpdateEvent(bluetoothDeviceEvent);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!BluetoothUtils.isBluetoothEnabled()) {
            final Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_CODE_ENABLE_BLUETOOTH);
        }
        startScan();
    }

    @Override
    protected void onPause() {
        super.onPause();

        deviceManager.finishScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deviceManager.disconnect();
        deviceManager = null;
        ButterKnife.unbind(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // do something on back.
            Intent intentScanActivity = new Intent(IBeaconDetailsActivity.this, BeaconsScanActivity.class);
            //intentScanActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentScanActivity);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void startScan() {

        deviceManager.initializeScan(getOrCreateScanContext(), new OnServiceReadyListener() {
            @Override
            public void onServiceReady() {
                deviceManager.attachListener(IBeaconDetailsActivity.this);
            }

            @Override
            public void onConnectionFailure() {
                //Utils.showToast(BeaconsScanActivity.this, "Erro durante conexão");
            }
        });

    }

    public ScanContext getOrCreateScanContext() {

        IBeaconScanContext beaconScanContext;

        beaconScanContext = new IBeaconScanContext.Builder()
                .setEventTypes(eventTypes) //only specified events we be called on callback
                .setIBeaconFilters(Arrays.asList(
                        IBeaconFilters.newDeviceNameFilter(beaconIdentifier)
                ))
                .setRssiCalculator(RssiCalculators.DEFAULT)
                .build();


        if (scanContext == null) {
            scanContext = new ScanContext.Builder()
                    .setScanMode(ProximityManager.SCAN_MODE_BALANCED)
                    .setIBeaconScanContext(beaconScanContext)
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
        }
    }

    private void onIBeaconDevicesList(final IBeaconDeviceEvent event) {

        List<IBeaconDevice> iBeaconDevices = event.getDeviceList();

        for (IBeaconDevice iBeaconDevice : iBeaconDevices) {

                TextView distanceTextView = (TextView) findViewById(R.id.distance);
                distanceTextView.setText(Html.fromHtml("<b>Distância:</b> &nbsp;&nbsp;"));
                distanceTextView.append(String.format("%.2f m", iBeaconDevice.getDistance()));

                TextView rssiTextView = (TextView) findViewById(R.id.rssi);
                rssiTextView.setText(Html.fromHtml("<b>RSSI:</b> &nbsp;&nbsp;"));
                rssiTextView.append(String.format("%.2f dBm", iBeaconDevice.getRssi()));

                TextView proximityTextView = (TextView) findViewById(R.id.proximity);
                switch (iBeaconDevice.getProximity().toString()) {
                    case "FAR":
                        proximityTextView.setText(Html.fromHtml("<b>Proximidade:</b> &nbsp;&nbsp;Longe"));
                        break;
                    case "NEAR":
                        proximityTextView.setText(Html.fromHtml("<b>Proximidade:</b> &nbsp;&nbsp;Perto"));
                        break;
                    case "IMMEDIATE":
                        proximityTextView.setText(Html.fromHtml("<b>Proximidade:</b> &nbsp;&nbsp;Muito Perto"));
                        break;
                }

                //proximityTextView.setText(Html.fromHtml("<b>Proximidade:</b> &nbsp;&nbsp;" + iBeaconDevice.getProximity()));
                //}


        }

    }
}
