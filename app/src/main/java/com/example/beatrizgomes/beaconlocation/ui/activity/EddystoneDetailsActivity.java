package com.example.beatrizgomes.beaconlocation.ui.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.beatrizgomes.beaconlocation.R;
import com.example.beatrizgomes.beaconlocation.adapter.monitor.EddystoneDetailsScan;
import com.kontakt.sdk.android.ble.configuration.scan.ScanContext;
import com.kontakt.sdk.android.ble.discovery.BluetoothDeviceEvent;
import com.kontakt.sdk.android.ble.discovery.EventType;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.util.BluetoothUtils;
import com.kontakt.sdk.android.common.profile.IEddystoneDevice;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * The type Eddystone details activity.
 */
public class EddystoneDetailsActivity extends BaseActivity implements ProximityManager.ProximityListener {


    private static final int REQUEST_CODE_ENABLE_BLUETOOTH = 1;
    /**
     * The constant context.
     */
    public static Context context;
    /**
     * The Name text view.
     */
    @Bind(R.id.eddystone_name)
    public TextView nameTextView;
    /**
     * The Tx power text view.
     */
    @Bind(R.id.txpower_level)
    public TextView txPowerTextView;
    /**
     * The Namespace text view.
     */
    @Bind(R.id.namespace)
    public TextView namespaceTextView;
    /**
     * The Instace text view.
     */
    @Bind(R.id.instance_id)
    public TextView instaceTextView;
    /**
     * The Rssi text view.
     */
    @Bind(R.id.eddystone_rssi)
    public TextView rssiTextView;
    /**
     * The Proximity text view.
     */
    @Bind(R.id.eddystone_proximity)
    public TextView proximityTextView;
    /**
     * The Distance text view.
     */
    @Bind(R.id.eddystone_distance)
    public TextView distanceTextView;
    /**
     * The Battery text view.
     */
    @Bind(R.id.battery_voltage)
    public TextView batteryTextView;
    /**
     * The Temperature text view.
     */
    @Bind(R.id.eddystone_temperature)
    public TextView temperatureTextView;
    /**
     * The Url text view.
     */
    @Bind(R.id.eddystone_url)
    public TextView urlTextView;
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
     * The Toolbar.
     */
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    /**
     * The Eddystone scan.
     */
    EddystoneDetailsScan eddystoneScan;

    /**
     * The Eddystone.
     */
    IEddystoneDevice eddystone;
    private List<EventType> eventTypes = new ArrayList<EventType>() {{
        add(EventType.DEVICES_UPDATE);
    }};

    /**
     * Gets context.
     *
     * @return the context
     */
    public static Context getContext() {
        return context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eddystone_details);


        context = this;

        ButterKnife.bind(this);
        setUpActionBar(toolbar);
        setUpActionBarTitle("Detalhes");

        deviceManager = new ProximityManager(this);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            // Recebe da atividade anterior como parâmetro o dispositivo selecionado
            eddystone = (IEddystoneDevice) extras.get("EDDYSTONE");
            //beaconScan = new EddystoneDetailsScan(this, eddystone.getInstanceId());
            nameTextView.setText(Html.fromHtml("<b>Nome:</b> &nbsp;&nbsp;" + eddystone.getNamespaceId()));
            distanceTextView.setText(Html.fromHtml("<b>Distância:</b> &nbsp;&nbsp;"));
            distanceTextView.append(String.format("%.2f cm", eddystone.getDistance()));
            namespaceTextView.setText(Html.fromHtml("<b>Namespace:</b> &nbsp;&nbsp;" + eddystone.getNamespaceId()));
            instaceTextView.setText(Html.fromHtml("<b>Instance:</b> &nbsp;&nbsp;" + eddystone.getInstanceId()));
            rssiTextView.setText(Html.fromHtml("<b>RSSI:</b> &nbsp;&nbsp;"));
            rssiTextView.append(String.format("%.2f dBm", eddystone.getRssi()));
            txPowerTextView.setText(Html.fromHtml("<b>Tx Power:</b> &nbsp;&nbsp;" + eddystone.getTxPower()));
            batteryTextView.setText(Html.fromHtml("<b>Bateria:</b> &nbsp;&nbsp;" + eddystone.getBatteryVoltage() + "V"));
            temperatureTextView.setText(Html.fromHtml("<b>Temperatura:</b> &nbsp;&nbsp;" + eddystone.getTemperature() + "ºC"));
            urlTextView.setText(Html.fromHtml("<b>Url:</b> &nbsp;&nbsp;" + eddystone.getUrl()));

            switch (eddystone.getProximity().toString()) {
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

            beaconIdentifier = eddystone.getInstanceId();
            eddystoneScan = new EddystoneDetailsScan(context, beaconIdentifier);


            Log.i("EddystoneDetails", "OnCreate(): ");

        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_eddystone);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentDetailsActivity = new Intent(EddystoneDetailsActivity.this, DistanceRangeActivity.class);
                intentDetailsActivity.putExtra("EDDYSTONE", eddystone);
                startActivity(intentDetailsActivity);
                finish();
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

            }
        });

    }

    /*    public static Context getContext() {
            return context;
        }
    */
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
        Log.i("EddystoneDetails", "OnEvent(): ");
        switch (bluetoothDeviceEvent.getEventType()) {
            case DEVICES_UPDATE:
                eddystoneScan.onDevicesUpdateEvent(bluetoothDeviceEvent);
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
        Log.i("EddystoneDetails", "onResume():");
        eddystoneScan.startScan(EddystoneDetailsActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        eddystoneScan.deviceManager.finishScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eddystoneScan.deviceManager.disconnect();
        eddystoneScan.deviceManager = null;
        ButterKnife.unbind(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intentScanActivity = new Intent(EddystoneDetailsActivity.this, BeaconsScanActivity.class);
            startActivity(intentScanActivity);
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }





}
