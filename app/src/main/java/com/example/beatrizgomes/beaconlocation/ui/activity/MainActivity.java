package com.example.beatrizgomes.beaconlocation.ui.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

import com.example.beatrizgomes.beaconlocation.R;
import com.kontakt.sdk.android.ble.util.BluetoothUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends BaseActivity {

    private static final int REQUEST_CODE_ENABLE_BLUETOOTH = 1;

    @Bind(R.id.button_scan)
    Button btn_scan;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setUpActionBar(toolbar);
        setUpActionBarTitle(getString(R.string.app_name));

    }

    @Override
    protected  void onResume() {

        super.onResume();

        if (!BluetoothUtils.isBluetoothEnabled()) {
            final Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_CODE_ENABLE_BLUETOOTH);
        } else {
            //Toast.makeText(this, "Bluetooth Enabled", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {


    }

    @OnClick (R.id.button_scan)
    void startScan() {

        startActivity(new Intent(MainActivity.this, BeaconsScanActivity.class));
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    */
}
