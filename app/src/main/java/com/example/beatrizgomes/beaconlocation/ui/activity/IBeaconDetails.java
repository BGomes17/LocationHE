package com.example.beatrizgomes.beaconlocation.ui.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.beatrizgomes.beaconlocation.R;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IBeaconDetails extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

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

    IBeaconDevice ibeacon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_details);

        ButterKnife.bind(this);
        setUpActionBar(toolbar);
        setUpActionBarTitle("Detalhes");
        View convertView;




        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // Recebe da atividade anterior o parâmetro 'SECRET_WORD'
            ibeacon = (IBeaconDevice) extras.get("IBEACON");
            nameTextView.setText(Html.fromHtml("<b>Nome:</b> &nbsp;&nbsp;" + ibeacon.getUniqueId()));
            distanceTextView.setText(Html.fromHtml("<b>Distância:</b> &nbsp;&nbsp;"));
            distanceTextView.append(String.format("%.2f m", ibeacon.getDistance()));
            majorTextView.setText(Html.fromHtml("<b>Major:</b> &nbsp;&nbsp;" + ibeacon.getMajor()));
            minorTextView.setText(Html.fromHtml("<b>Minor:</b> &nbsp;&nbsp;" + ibeacon.getMinor()));
            //profileTextView.setText(String.format("Perfil: IBeacon"));
            rssiTextView.setText(Html.fromHtml("<b>RSSI:</b> &nbsp;&nbsp;" + ibeacon.getRssi() + " dBm"));
            txPowerTextView.setText(Html.fromHtml("<b>Tx Power:</b> &nbsp;&nbsp;" + ibeacon.getTxPower()));
            batteryTextView.setText(Html.fromHtml("<b>Bateria:</b> &nbsp;&nbsp;" + ibeacon.getBatteryPower() + "%"));
            //proximityTextView.setText(Html.fromHtml("<b>Proximidade:</b> &nbsp;&nbsp;" + ibeacon.getProximity()));

            new CountDownTimer(30000, 1000) {
                public void onFinish() {
                    // When timer is finished
                    // Execute your code here
                    proximityTextView.setText(Html.fromHtml("<b>RSSI:</b> &nbsp;&nbsp;" + ibeacon.getRssi() + " dBm"));

                }

                public void onTick(long millisUntilFinished) {
                    // millisUntilFinished    The amount of time until finished.
                }
            }.start();

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
