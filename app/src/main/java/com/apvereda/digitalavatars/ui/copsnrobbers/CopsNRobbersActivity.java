package com.apvereda.digitalavatars.ui.copsnrobbers;

import android.bluetooth.le.AdvertiseSettings;
import android.os.Bundle;

import com.apvereda.digitalavatars.R;
import com.apvereda.copsnrobbers.CopsNRobbersApp;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class CopsNRobbersActivity extends AppCompatActivity {

    boolean isCop;
    public BeaconTransmitter beaconTransmitter;
    Beacon beacon;
    String urlString = "https://bit.ly/2uNTMJi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copsnrobbers);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CopsNRobbersApp.getApp(true);
        Spinner spinner = findViewById(R.id.spinner);
        String[] items = new String[]{"Cop", "Robber"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (adapterView.getItemAtPosition(i).toString()){
                    case "Cop" :
                        isCop = true;
                        CopsNRobbersApp.getApp(isCop).setIsCop(isCop);
                        break;
                    case "Robber" :
                        isCop = false;
                        CopsNRobbersApp.getApp(isCop).setIsCop(isCop);
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        FloatingActionButton fabstart = findViewById(R.id.fab_startgame);
        fabstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                urlString = isCop? "https://cop.com/12345"  : "https://rob.com/12345";
                //urlString += Avatar.getAvatar().getOneSignalID();
                Log.i("Digital avatars", "url transmitted: "+urlString);
                try {
                    byte[] buf = UrlBeaconUrlCompressor.compress(urlString);
                    beacon = new Beacon.Builder()
                            //.setId1(getHex(urlString.getBytes(StandardCharsets.UTF_8)))
                            .setId1(getHex(buf))
                            //.setId2(getHex(urlString.getBytes(StandardCharsets.UTF_8)))
                            .setManufacturer(0xFEAA)
                            .setTxPower(-59)
                            .build();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                BeaconParser beaconParser = new BeaconParser()
                        .setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT);
                beaconTransmitter = new BeaconTransmitter(getApplicationContext(), beaconParser);
                /*
                AdvertiseSettings.ADVERTISE_MODE_BALANCED 3 Hz
                AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY 10 Hz
                AdvertiseSettings.ADVERTISE_MODE_LOW_POWER 1 Hz
                 */
                beaconTransmitter.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
                /*
                AdvertiseSettings.ADVERTISE_TX_POWER_HIGH -56 dBm @ 1 meter with Nexus 5
                AdvertiseSettings.ADVERTISE_TX_POWER_LOW -75 dBm @ 1 meter with Nexus 5
                AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM -66 dBm @ 1 meter with Nexus 5
                AdvertiseSettings.ADVERTISE_TX_POWER_ULTRA_LOW not detected with Nexus 5
                 */
                beaconTransmitter.setAdvertiseTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM);
                beaconTransmitter.startAdvertising(beacon);
                Snackbar.make(view, "Ya estas jugando y emitiendo señal", Snackbar.LENGTH_LONG).show();
            }
        });

        FloatingActionButton fabstop = findViewById(R.id.fab_stopgame);
        fabstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                beaconTransmitter.stopAdvertising();
                Snackbar.make(view, "Juego parado", Snackbar.LENGTH_LONG).show();
            }
        });


    }

    public static List<Long> bytesToListOfLongs(byte[] bytes) {
        List<Long> longs = new ArrayList<Long>();
        for (byte b: bytes) {
            longs.add(new Long(b));
        }
        return longs;
    }

    static final String HEXES = "0123456789ABCDEF";
    public static String getHex( byte [] raw ) {
        if ( raw == null ) {
            return null;
        }
        final StringBuilder hex = new StringBuilder( 2 * raw.length );
        for ( final byte b : raw ) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4))
                    .append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }
}