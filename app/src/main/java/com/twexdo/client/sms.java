package com.twexdo.client;

import android.util.Log;

public class sms {

    public String arrived;
    public String to;
    public String from;
    public String content;
    public String oraStab;
    public Double x, y;
    public Integer time;
    public Integer type;
    public Boolean clientConfirm ;

    public String getArrived() {
        return arrived;
    }

    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }

    public String getContent() {
        return content;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public Integer getTime() {
        return time;
    }

    public Integer getType() {
        return type;
    }

    public Boolean getClientConfirm() {
        return clientConfirm;
    }

    public sms(String to, String from, String content, double x, double y) {
        oraStab=null;
        this.to = to;
        this.from = from;
        this.content = content;
        this.x = x;
        this.y = y;
        type=0;
        clientConfirm=null;
        arrived=null;
    }

    public sms(String to, String from, int time) {
        oraStab=null;
        type=1;
        this.to = to;
        this.from = from;
        this.time = time;
        this.x=null;
        this.y=null;
        clientConfirm=null;
        arrived=null;
    }
    public sms(String to, String from, boolean b,int timp) {
        oraStab=null;
        type=2;
        this.time=timp;
        this.to = to;
        this.from = from;
        this.x=null;
        this.y=null;
        clientConfirm=b;
        arrived=null;
        Log.d("newSms:type2: "," to:"+to+" from:"+from+" bool:"+b+" time:"+time);
    }
    public sms(String to, String from, boolean b,int timp,String oraStab) {
        this.oraStab=oraStab;
        type=2;
        this.time=timp;
        this.to = to;
        this.from = from;
        this.x=null;
        this.y=null;
        clientConfirm=b;
        arrived=null;
        Log.d("newSms:type2: "," to:"+to+" from:"+from+" bool:"+b+" time:"+time);
    }

    public sms(String myid, String from, String s) {
        oraStab=null;
        this.from=myid;
        this.to=from;
        x=null;
        y=null;
        clientConfirm=null;
        time=null;
        type=3;
        arrived=s;
    }


}
