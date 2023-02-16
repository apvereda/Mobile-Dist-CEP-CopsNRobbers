package com.apvereda.digitalavatars.ui.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.apvereda.digitalavatars.R;
import com.apvereda.digitalavatars.ui.copsnrobbers.CopsNRobbersActivity;
import com.apvereda.receiver.CopsNRobbersReceiver;
import com.apvereda.copsnrobbers.CopsNRobbersApp;
import com.apvereda.utils.SiddhiService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.wso2.extension.siddhi.io.android.source.BeaconHandler;
import org.wso2.siddhi.android.platform.SiddhiAppService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private String appName="";
    View root;
    List<String> apps;
    List<String> appnames;
    CopsNRobbersReceiver copsNRobbersReceiver;
    DataUpdateReceiver d;
    BeaconHandler beaconHandler;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);
        setRetainInstance(true);
        if(savedInstanceState !=null) {
            //textView.setText(savedInstanceState.getString("text"));
            if(savedInstanceState.getInt("btn_play")==View.VISIBLE) {
                root.findViewById(R.id.fabplay).setVisibility(View.VISIBLE);
                root.findViewById(R.id.fabstop).setVisibility(View.GONE);
            } else{
                root.findViewById(R.id.fabplay).setVisibility(View.GONE);
                root.findViewById(R.id.fabstop).setVisibility(View.VISIBLE);
            }
        }
        apps = new ArrayList<String>();
        appnames = new ArrayList<String>();
        //SiddhiService.getServiceConnection(getActivity().getApplicationContext());
        Button btncopsnrobbers = root.findViewById(R.id.cpsnrobberbtn);
        btncopsnrobbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), CopsNRobbersActivity.class);
                startActivity(i);
            }
        });
        FloatingActionButton fabplay = root.findViewById(R.id.fabplay);
        fabplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startApp();
                    root.findViewById(R.id.fabplay).setVisibility(View.GONE);
                    root.findViewById(R.id.fabstop).setVisibility(View.VISIBLE);
                    Snackbar.make(view, "Siddhi app running", Snackbar.LENGTH_LONG).show();
                    //textView.setText("Siddhi app running");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        FloatingActionButton fabstop = root.findViewById(R.id.fabstop);
        fabstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    stopApp();
                    root.findViewById(R.id.fabplay).setVisibility(View.VISIBLE);
                    root.findViewById(R.id.fabstop).setVisibility(View.GONE);
                    Snackbar.make(view, "Siddhi app stopped", Snackbar.LENGTH_LONG).show();
                    //textView.setText("Siddhi app stopped");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        root.findViewById(R.id.fabplay).setVisibility(View.VISIBLE);
        root.findViewById(R.id.fabstop).setVisibility(View.GONE);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState !=null) {
            //TextView textView = root.findViewById(R.id.text_home);
            //textView.setText(savedInstanceState.getString("text"));
            if(savedInstanceState.getInt("btn_play")==View.VISIBLE) {
                root.findViewById(R.id.fabplay).setVisibility(View.VISIBLE);
                root.findViewById(R.id.fabstop).setVisibility(View.GONE);
            } else{
                root.findViewById(R.id.fabplay).setVisibility(View.GONE);
                root.findViewById(R.id.fabstop).setVisibility(View.VISIBLE);
            }
        }

    }

    // invoked when the activity may be temporarily destroyed, save the instance state here
    @Override
    public void onSaveInstanceState(Bundle outState) {
        //TextView textView = root.findViewById(R.id.text_home);
        outState.putInt("btn_play", root.findViewById(R.id.fabplay).getVisibility());
        //outState.putString("text", textView.getText().toString());

        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
    }

    private void startApp() throws RemoteException {
        String appTest = "@app:name('CopsNRobbersTest')" +

                "@source(type='android-beacon'," +
                "@map(type='keyvalue',fail.on.missing.attribute='false'," +
                "@attributes(url='url')))" +
                "define stream receiveBeacon(url String);"+

                "@source(type='android-message', appid ='CopsNRobbers_TestMessage'," +
                "@map(type='keyvalue',fail.on.missing.attribute='false'," +
                "@attributes(sender='sender', message='message')))" +
                "define stream receiveTest(sender String, message String);"+


                "@sink(type='android-broadcast' , identifier='Beacon', " +
                "@map(type='keyvalue'))" +
                "define stream beacon(url String); " +

                "@sink(type='android-message' , appid='CopsNRobbers_TestMessage', recipients='MeTest'," +
                "@map(type='keyvalue'))"+
                "define stream testMessage(message String); " +

                "@sink(type='android-notification', title='CopsNRobbers',multiple.notifications = 'true'," +
                    "@map(type='keyvalue'))" +
                "define stream notifyStream(message String); " +

                "from receiveBeacon select * insert into beacon;"+
                "from receiveBeacon select url as message insert into testMessage;"+
                "from receiveTest select message insert into notifyStream;";

        String app = "@app:name('CopsNRobbers')" +

                "@source(type='android-beacon'," +
                "@map(type='keyvalue',fail.on.missing.attribute='false'," +
                "@attributes(url='url')))" +
                "define stream receiveBeacon(url String);"+

                "@source(type='android-broadcast', identifier='CopsNRobbers_CopDetected'," +
                "@map(type='keyvalue',fail.on.missing.attribute='false'," +
                "@attributes( time='time', user='user', onesignalid='onesignalid')))" +
                "define stream copDetected(time String, user String, onesignalid String);" +

                "@source(type='android-broadcast', identifier='CopsNRobbers_RobberDetected'," +
                "@map(type='keyvalue',fail.on.missing.attribute='false'," +
                "@attributes( time='time', user='user', onesignalid='onesignalid')))" +
                "define stream robberDetected(time String, user String, onesignalid String);" +

                "@source(type='android-broadcast', identifier='CopsNRobbers_TreasureDetected'," +
                "@map(type='keyvalue',fail.on.missing.attribute='false'," +
                "@attributes( time='time', id='id')))" +
                "define stream treasureDetected(time String, id String);" +

                "@source(type='android-broadcast', identifier='CopsNRobbers_Arrest'," +
                "@map(type='keyvalue',fail.on.missing.attribute='false'," +
                "@attributes( message='message', recipient='recipient')))" +
                "define stream arrest(message String, recipient String);" +

                "@source(type='android-message', appid ='CopsNRobbers_Arrest_Message'," +
                "@map(type='keyvalue',fail.on.missing.attribute='false'," +
                "@attributes(sender='sender', message='message')))" +
                "define stream arrested(sender String, message String);"+



                "@sink(type='android-broadcast' , identifier='CopsNRobbers_BeaconReceived', " +
                "@map(type='keyvalue'))" +
                "define stream beaconReceived(url String); " +

                "@sink(type='android-broadcast' , identifier='CopsNRobbers_TreasureStolen', " +
                "@map(type='keyvalue'))" +
                "define stream treasureStolen(treasureId String); " +

                "@sink(type='android-broadcast' , identifier='CopsNRobbers_RobberArrested', " +
                "@map(type='keyvalue'))" +
                "define stream robberArrested(robberId String); " +

                "@sink(type='android-message' , appid='CopsNRobbers_Arrest_Message', recipients='Relations'," +
                "@map(type='keyvalue'))"+
                "define stream arrestMessage(message String, recipient String); " +

                "@sink(type='android-notification', title='Estas Arrestado',multiple.notifications = 'true'," +
                "@map(type='keyvalue'))" +
                "define stream arrestedNotification (sender String, message String); " +


                "from receiveBeacon select * insert into beaconReceived;"+
                "from not copDetected for 2 min and e1=treasureDetected select e1.id as treasureId insert into treasureStolen;" +
                //"from e1=treasureDetected -> not e2=copDetected<2:> for '1 min' select e1.id as treasureId insert into treasureStolen;"+
                //"from e1=robberDetected -> e2=copDetected within 1 min select e1.onesignalid as robberId e1.user as robber insert into robberArrested"+
                "from r1=robberDetected -> e2=copDetected within 1 min select r1.onesignalid as robberId insert into robberArrested;"+
                "from arrest select * insert into arrestMessage;"+
                "from arrested select * insert into arrestedNotification;"+

                "from receiveBeacon select * insert into beacon;"+
                "from arrested select * insert into arrestedNotification;";


        apps.add(appTest);
        copsNRobbersReceiver = new CopsNRobbersReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CopsNRobbersApp.EV_BEACONRECEIVED);
        intentFilter.addAction(CopsNRobbersApp.EV_TREASURESTOLEN);
        intentFilter.addAction(CopsNRobbersApp.EV_ROBBERARRESTED);
        SiddhiAppService.getServiceInstance().registerReceiver(copsNRobbersReceiver, intentFilter);
        beaconHandler = BeaconHandler.getInstance();
        beaconHandler.setParentReceiverActivity(getActivity());
        beaconHandler.startBeaconScan();
        d = new DataUpdateReceiver();
        IntentFilter infi = new IntentFilter();
        infi.addAction("Beacon");
        SiddhiAppService.getServiceInstance().registerReceiver(d, infi);
        for(String apps: apps) {
            appnames.add(SiddhiService.getServiceConnection(getActivity().getApplicationContext()).startSiddhiApp(apps));
        }
    }

    private void stopApp() throws RemoteException{
        for( String app : appnames) {
            SiddhiService.getServiceConnection(getContext()).stopSiddhiApp(app);
        }
        SiddhiAppService.getServiceInstance().unregisterReceiver(copsNRobbersReceiver);
        beaconHandler.unbind();
        removeApps();
    }

    public void removeApps() {
        apps = new ArrayList<String>();
        appnames = new ArrayList<String>();
    }

    public void readFile(){
        apps = new ArrayList<String>();
        File fileDirectory = new File(getContext().getExternalFilesDir(null)+"/SiddhiApps");
        if(!fileDirectory.exists()){
            fileDirectory.mkdir();
        }
        File[] dirFiles = fileDirectory.listFiles();
        Snackbar.make(this.root, "Leyendo Siddhi Apps", Snackbar.LENGTH_LONG).show();
        String app = "";
        String line;
        for (File f : dirFiles) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(f));
                while ((line = br.readLine()) != null){
                    app += line;
                }
                apps.add(app);
                System.out.println(app);
                app = "";
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class DataUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("Beacon")) {
                Log.i("Digital-Avatars", "Beacon recibido con url: "+intent.getStringExtra("url"));
                Toast toast1 = Toast.makeText(context,
                        "Beacon recibido con url: "+intent.getStringExtra("url"), Toast.LENGTH_LONG);
                toast1.show();
            }
        }
    }

}
