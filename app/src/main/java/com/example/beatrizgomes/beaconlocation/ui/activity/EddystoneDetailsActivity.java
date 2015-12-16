package com.example.beatrizgomes.beaconlocation.ui.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.beatrizgomes.beaconlocation.R;
import com.example.beatrizgomes.beaconlocation.adapter.monitor.BeaconsDetailsScan;
import com.kontakt.sdk.android.ble.discovery.BluetoothDeviceEvent;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.util.BluetoothUtils;
import com.kontakt.sdk.android.common.profile.IEddystoneDevice;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EddystoneDetailsActivity extends BaseActivity implements ProximityManager.ProximityListener {


    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.eddystone_name)
    public TextView nameTextView;

    @Bind(R.id.txpower_level)
    public TextView txPowerTextView;

    @Bind(R.id.namespace)
    public TextView namespaceTextView;

    @Bind(R.id.instance_id)
    public TextView instaceTextView;

    @Bind(R.id.eddystone_rssi)
    public TextView rssiTextView;

    @Bind(R.id.eddystone_proximity)
    public TextView proximityTextView;

    @Bind(R.id.eddystone_distance)
    public TextView distanceTextView;

    @Bind(R.id.battery_voltage)
    public TextView batteryTextView;

    @Bind(R.id.eddystone_temperature)
    public TextView temperatureTextView;

    @Bind(R.id.eddystone_url)
    public TextView urlTextView;

    BeaconsDetailsScan beaconScan;

    IEddystoneDevice eddystone;

    private static final int REQUEST_CODE_ENABLE_BLUETOOTH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eddystone_details);

        ButterKnife.bind(this);
        setUpActionBar(toolbar);
        setUpActionBarTitle("Detalhes");

        beaconScan = new BeaconsDetailsScan(this);



        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            // Recebe da atividade anterior o parâmetro 'SECRET_WORD'
            eddystone = (IEddystoneDevice) extras.get("EDDYSTONE");

            nameTextView.setText(Html.fromHtml("<b>Nome:</b> &nbsp;&nbsp;" + eddystone.getNamespaceId()));
            distanceTextView.setText(Html.fromHtml("<b>Distância:</b> &nbsp;&nbsp;"));
            distanceTextView.append(String.format("%.2f cm", eddystone.getDistance()));
            namespaceTextView.setText(Html.fromHtml("<b>Namespace:</b> &nbsp;&nbsp;" + eddystone.getNamespaceId()));
            instaceTextView.setText(Html.fromHtml("<b>Instance:</b> &nbsp;&nbsp;" + eddystone.getInstanceId()));
            rssiTextView.setText(Html.fromHtml("<b>RSSI:</b> &nbsp;&nbsp;" + eddystone.getRssi() + " dBm"));
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

            beaconScan.beaconAddress = eddystone.getAddress();
            beaconScan.startScan(EddystoneDetailsActivity.this);

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
                beaconScan.onDevicesUpdateEvent(bluetoothDeviceEvent);
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
    }

    @Override
    protected void onPause() {
        super.onPause();

        beaconScan.deviceManager.finishScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconScan.deviceManager.disconnect();
        beaconScan.deviceManager = null;
        ButterKnife.unbind(this);
    }

}
