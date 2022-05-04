package com.example.membersmapstatistics;



//import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.Map;

public class Gps implements Serializable{
     
      private String geo;
      private String address;
      private double latitude;
      private double longitude;
      //private Map<String,Object> modify;

    public Gps() {
        //modify = Server.getTimestamp();
    }
    public Gps(String geo,double latitude, double longitude){
        this.geo = geo;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public Gps(String geo, String address, double latitude, double longitude,
               long modifyLong) {
        this.geo = geo;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        //this.modify = Server.getTimeMap(modifyLong);
    }
    public Gps(String geo, String address, double latitude, double longitude) {
        this.geo = geo;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        //this.modify = Server.getTimestamp();
    }
    public String getGeo() {
        return geo;
    }

    public void setGeo(String geo) {
        this.geo = geo;
    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

//    public Map<String, Object> getModify() {
//        if(modify == null)
//            modify = Server.getTimestamp();
//        return modify;
//    }

    //public void setModify(Map<String, Object> modify) {
    //    this.modify = modify;
    //}

    //@Exclude
    //public long getModifyLong (){
    //    return  Server.getTiemLong(modify);
    //}
}
