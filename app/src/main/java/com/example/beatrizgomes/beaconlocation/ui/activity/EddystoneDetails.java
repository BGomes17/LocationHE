package com.example.beatrizgomes.beaconlocation.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.beatrizgomes.beaconlocation.R;
import com.kontakt.sdk.android.common.profile.IEddystoneDevice;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EddystoneDetails extends BaseActivity {


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eddystone_details);

        ButterKnife.bind(this);
        setUpActionBar(toolbar);
        setUpActionBarTitle("Detalhes");
        View convertView;

        IEddystoneDevice eddystone;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // Recebe da atividade anterior o parâmetro 'SECRET_WORD'
            eddystone = (IEddystoneDevice) extras.get("EDDYSTONE");
            nameTextView.setText(Html.fromHtml("<b>Nome:</b> &nbsp;&nbsp;" + eddystone.getNamespaceId()));
            distanceTextView.setText(Html.fromHtml("<b>Distância:</b> &nbsp;&nbsp;"));
            distanceTextView.append(String.format("%.2f cm", eddystone.getDistance()));
            namespaceTextView.setText(Html.fromHtml("<b>Namespace:</b> &nbsp;&nbsp;" + eddystone.getNamespaceId()));
            instaceTextView.setText(Html.fromHtml("<b>Instance:</b> &nbsp;&nbsp;" + eddystone.getInstanceId()));
            //profileTextView.setText(String.format("Perfil: IBeacon"));
            rssiTextView.setText(Html.fromHtml("<b>RSSI:</b> &nbsp;&nbsp;" + eddystone.getRssi() + " dBm"));
            txPowerTextView.setText(Html.fromHtml("<b>Tx Power:</b> &nbsp;&nbsp;" + eddystone.getTxPower()));
            batteryTextView.setText(Html.fromHtml("<b>Bateria:</b> &nbsp;&nbsp;" + eddystone.getBatteryVoltage() + "V"));
            proximityTextView.setText(Html.fromHtml("<b>Proximidade:</b> &nbsp;&nbsp;" + eddystone.getProximity()));
            temperatureTextView.setText(Html.fromHtml("<b>Temperatura:</b> &nbsp;&nbsp;" + eddystone.getTemperature() + "ºC"));
            urlTextView.setText(Html.fromHtml("<b>Url:</b> &nbsp;&nbsp;" + eddystone.getUrl()));
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

}
