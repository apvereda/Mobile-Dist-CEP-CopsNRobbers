package com.apvereda.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.apvereda.db.Contact;
import com.apvereda.copsnrobbers.CopsNRobbersApp;

import org.wso2.siddhi.android.platform.SiddhiAppService;

public class CopsNRobbersReceiver extends BroadcastReceiver {
    private CopsNRobbersApp app;

    public CopsNRobbersReceiver() {
        super();
        app = CopsNRobbersApp.getApp(true);
    }

    @Override
        public void onReceive(Context context, Intent intent) {
            /**
             * Once we receive a beacon signal we have to clasify it according to the role of the player
             * or if it is a treasure
             */
            if (intent.getAction().equals(CopsNRobbersApp.EV_BEACONRECEIVED)) {
                String url = intent.getStringExtra("url");
                //url = "https://cop.com/12345";
                String onesignal = url.substring(url.lastIndexOf("/"));
                Contact c = Contact.getContactByOneSignal(onesignal);
                //Log.i("BEACON DETECTED", "Beacon Detected with url: "+url);
                if (url.contains("cop")){
                    Intent i = new Intent(CopsNRobbersApp.EV_COPDETECTED);
                    i.putExtra(CopsNRobbersApp.USER, c.getEmail());
                    i.putExtra(CopsNRobbersApp.TIME, Long.toString(System.currentTimeMillis()));
                    i.putExtra(CopsNRobbersApp.ONESIGNAL, c.getOneSignalID());
                    SiddhiAppService.getServiceInstance().sendBroadcast(i);
                } else if (url.contains("rob")){
                    Intent i = new Intent(CopsNRobbersApp.EV_ROBBERDETECTED);
                    i.putExtra(CopsNRobbersApp.USER, c.getEmail());
                    i.putExtra(CopsNRobbersApp.TIME, Long.toString(System.currentTimeMillis()));
                    i.putExtra(CopsNRobbersApp.ONESIGNAL, c.getOneSignalID());
                    SiddhiAppService.getServiceInstance().sendBroadcast(i);
                } else if (url.contains("trs")){
                    Intent i = new Intent(CopsNRobbersApp.EV_TREASUREDETECTED);
                    i.putExtra(CopsNRobbersApp.TIME, Long.toString(System.currentTimeMillis()));
                    i.putExtra("id", onesignal);
                    SiddhiAppService.getServiceInstance().sendBroadcast(i);
                }
            }
            /**
             * If a treasure that is detected is not stolen yet, it is added to the robber profile
             * and a notification is shown
             */
            if (intent.getAction().equals(CopsNRobbersApp.EV_TREASURESTOLEN)) {
                if (!app.isCop()) {
                    if (!app.getTreasures().contains(intent.getStringExtra(CopsNRobbersApp.TREASUREID))) {
                        app.addTreasure(intent.getStringExtra(CopsNRobbersApp.TREASUREID));
                        Log.i("Digital-Avatars", "Tesoro robado con id: " + intent.getStringExtra(CopsNRobbersApp.TREASUREID));
                        Toast toast1 = Toast.makeText(context,
                                "Tesoro robado con id: " + intent.getStringExtra(CopsNRobbersApp.TREASUREID), Toast.LENGTH_LONG);
                        toast1.show();
                        Intent i = new Intent(CopsNRobbersApp.EV_STATUS);
                        i.putExtra("message", "Treasure stolen with ID "+ intent.getStringExtra(CopsNRobbersApp.TREASUREID));
                        SiddhiAppService.getServiceInstance().sendBroadcast(i);
                        if(app.getN_trs() == 0){
                            i = new Intent(CopsNRobbersApp.EV_STATUS);
                            i.putExtra("message", "Game Over. Robbers Win!");
                            SiddhiAppService.getServiceInstance().sendBroadcast(i);
                        }
                    } else {
                        Log.i("Digital-Avatars", "Este tesoro ya fue robado id: " + intent.getStringExtra(CopsNRobbersApp.TREASUREID));
                        Toast toast1 = Toast.makeText(context,
                                "Ya robaste este tesoro antes con id: " + intent.getStringExtra(CopsNRobbersApp.TREASUREID), Toast.LENGTH_LONG);
                        toast1.show();
                    }
                }
            }
            /**
             * Robber is arrested and a message is sent to him to communicate this fact
             */
            else if (intent.getAction().equals(CopsNRobbersApp.EV_ROBBERARRESTED)) {
                if (app.isCop()) {
                    //Contact c = Contact.getContactByOneSignal(intent.getStringExtra("robberid"));
                    Intent i = new Intent(CopsNRobbersApp.EV_ARREST);
                    i.putExtra(CopsNRobbersApp.RECIPIENT, intent.getStringExtra(CopsNRobbersApp.ROBBERID));
                    i.putExtra("message", "Has sido arrestado");
                    SiddhiAppService.getServiceInstance().sendBroadcast(i);
                    Log.i("Digital-Avatars", "Ladron arrestado id: " + intent.getStringExtra("robber"));
                    Toast toast1 = Toast.makeText(context,
                            "Ladron arrestado: " + intent.getStringExtra("robber"), Toast.LENGTH_LONG);
                    toast1.show();
                    i = new Intent(CopsNRobbersApp.EV_STATUS);
                    i.putExtra("message", "Ladron arrestado id: " + intent.getStringExtra("robber"));
                    SiddhiAppService.getServiceInstance().sendBroadcast(i);
                }
            }
        }
}
