package com.example.neareststationfromyou;

public class StationDetails {
    String v=null;
    String t=null;
    String d=null;

    public StationDetails(String v, String t, String d) {
        this.v = v;
        this.t = t;
        this.d = d;
    }

    public StationDetails() {
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }
}
