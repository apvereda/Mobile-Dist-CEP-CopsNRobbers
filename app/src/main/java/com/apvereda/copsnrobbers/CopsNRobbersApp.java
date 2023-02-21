package com.apvereda.copsnrobbers;

import java.util.ArrayList;
import java.util.List;

public class CopsNRobbersApp {
    public static final String DATE ="date";
    public static final String TIME ="time";
    public static final String SENDER ="sender";
    public static final String ONESIGNAL ="onesignalid";
    public static final String RECIPIENT ="recipient";
    public static final String USER ="user";
    public static final String TREASUREID = "treasureId";
    public static final String ROBBERID = "robberId";
    public static final String EV_BEACONRECEIVED ="CopsNRobbers_BeaconReceived"; //User enters new trip
    public static final String EV_TREASURESTOLEN ="CopsNRobbers_TreasureStolen"; //Contact receives trip query
    public static final String EV_ROBBERARRESTED ="CopsNRobbers_RobberArrested"; //Contact responds with a proposal
    public static final String EV_ARREST ="CopsNRobbers_Arrest"; //Contact responds with a proposal
    public static final String EV_COPDETECTED ="CopsNRobbers_CopDetected"; //User receives proposal from a contact
    public static final String EV_ROBBERDETECTED ="CopsNRobbers_RobberDetected"; //User receives proposal from a contact
    public static final String EV_TREASUREDETECTED ="CopsNRobbers_TreasureDetected"; //User receives proposal from a contact
    public static final String EV_STATUS ="CopsNRobbers_inStatus"; //User receives status of the game

    public static final int N_TREASURES = 5;


    private List<String> treasures = new ArrayList<>();
    private boolean iscop;
    private int n_trs;
    private static CopsNRobbersApp app;

    public static CopsNRobbersApp getApp(boolean iscop){
        if (app == null){
            app = new CopsNRobbersApp(iscop);
        }
        return app;
    }

    private CopsNRobbersApp(boolean iscop){
        treasures = new ArrayList<>();
        this.iscop = iscop;
        n_trs = N_TREASURES;
    }

    public boolean isCop() { return iscop; }

    public int getN_trs() {
        return n_trs;
    }

    public List<String> getTreasures() {
        return treasures;
    }

    public void addTreasure(String treasure){
        treasures.add(treasure);
    }

    public void setTreasures(List<String> treasures) {
        this.treasures = treasures;
    }

    public void setIsCop(boolean iscop) {
        app = new CopsNRobbersApp(iscop);
    }
}
