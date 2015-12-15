package com.example.beatrizgomes.beaconlocation.ui.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.example.beatrizgomes.beaconlocation.R;
import com.example.beatrizgomes.beaconlocation.adapter.monitor.BeaconsScanMonitorAdapter;
import com.example.beatrizgomes.beaconlocation.model.BeaconWrapper;
import com.example.beatrizgomes.beaconlocation.util.Utils;
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
import com.kontakt.sdk.android.ble.util.BluetoothUtils;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;
import com.kontakt.sdk.android.common.profile.IEddystoneDevice;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class BeaconsScanActivity extends BaseActivity implements ProximityManager.ProximityListener{

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.list_beacons)
    ExpandableListView listBeacons;

    private static final int REQUEST_CODE_ENABLE_BLUETOOTH = 1;

    private BeaconsScanMonitorAdapter beaconsAdapter;

    private ProximityManager deviceManager;

    public ScanContext scanContext;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacons_scan);

        ButterKnife.bind(this);
        setUpActionBar(toolbar);
        setUpActionBarTitle("Scan All Beacons");

        beaconsAdapter = new BeaconsScanMonitorAdapter(this);
        Log.i("BeaconActivity", "creating Adapter var");


        deviceManager = new ProximityManager(this);

        listBeacons.setAdapter(beaconsAdapter);

        /*
            groupPosition: get index of the main list of the selected child:
                0 - IBeacon
                1 - Eddystone
            childPosition: get index in the list of the selected child
         */
        listBeacons.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                BeaconWrapper child = beaconsAdapter.getChild(groupPosition, childPosition);
                if (DeviceProfile.IBEACON == child.getDeviceProfile()) {
                    final IBeaconDevice ibeacon = child.getBeaconDevice();

                    /*
                    // Cria intent para a passagem para a atividade seguinte
                    Intent intentDetailsActivity = new Intent(BeaconsScanActivity.this, IBeaconDetails.class);
                    intentDetailsActivity.putExtra("IBEACON", ibeacon);

                    // Inicia a atividade seguinte
                    startActivity(intentDetailsActivity);
                    */
                    Log.i("BeaconActivity", "" + ibeacon.getRssi());
                    new CountDownTimer(10000, 1000) {
                        public void onFinish() {
                            // When timer is finished
                            // Execute your code here
                            Log.i("BeaconActivity", "" + ibeacon.getRssi());
                        }

                        public void onTick(long millisUntilFinished) {
                            // millisUntilFinished    The amount of time until finished.
                        }
                    }.start();
                } else if (DeviceProfile.EDDYSTONE == child.getDeviceProfile()) {
                    IEddystoneDevice eddystone = child.getEddystoneDevice();

                    Intent intentDetailsActivity = new Intent(BeaconsScanActivity.this, EddystoneDetailsActivity.class);
                    intentDetailsActivity.putExtra("EDDYSTONE", eddystone);
                    startActivity(intentDetailsActivity);

                } else {
                    Log.i("setOnClick", "No profile detected");

                }


                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_beacons_scan, menu);
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

    public void startScan() {

        deviceManager.initializeScan(getOrCreateScanContext(), new OnServiceReadyListener() {
            @Override
            public void onServiceReady() {
                deviceManager.attachListener(BeaconsScanActivity.this);
            }

            @Override
            public void onConnectionFailure() {
                Utils.showToast(BeaconsScanActivity.this, "Erro durante conexão");
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

    @Override
    protected void onResume() {
        super.onResume();

        if (!BluetoothUtils.isBluetoothEnabled()) {
            final Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_CODE_ENABLE_BLUETOOTH);
        } else {
            startScan();
        }
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
    public void onScanStart() {

    }


    @Override
    public void onScanStop() {

    }

    @Override
    public void onEvent(BluetoothDeviceEvent event) {
        switch (event.getEventType()) {
            case DEVICES_UPDATE:
                onDevicesUpdateEvent(event);
                break;
        }
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                beaconsAdapter.replaceEddystoneBeacons(event.getDeviceList());
            }
        });
    }

    private void onIBeaconDevicesList(final IBeaconDeviceEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                beaconsAdapter.replaceIBeacons(event.getDeviceList());
            }
        });
    }


}
