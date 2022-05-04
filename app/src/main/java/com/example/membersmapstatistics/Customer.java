package com.example.membersmapstatistics;

//import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Customer implements Serializable {
    //private static final String TIMESTAMP_NAME = "timestamp";

    private Auth auth;
    //@Expose private Coupon coupon;
    private Info info;
    private Share share;
    private Gps gps;
    //@Expose private Map<String, Boolean> coupon= new HashMap<>();
    //@Expose private Map<String, String> exchange= new HashMap<>();  //get,return
    //@Expose private Map<String, Boolean> transaction= new HashMap<>();

    public Customer() {
        auth = new Auth();
        info = new Info();
        share = new Share();
        gps = new Gps();
    }
    public Customer(Info info, Gps gps){
        this.auth = new Auth();
        //info = new Info(getRName(i),getRBirthday(),getRGender());
        this.info = info;
        this.share = new Share();
        //gps= new Gps(getRLatitude(),getRLongitude());
        this.gps=gps;
    }
    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public Share getShare() {
        return share;
    }

    public void setShare(Share share) {
        this.share = share;
    }

    public Gps getGps() {
        return gps;
    }

    public void setGps(Gps gps) {
        this.gps = gps;
    }

//    public Map<String, String> getExchange() {
//        return exchange;
//    }
//
//    public void setExchange(Map<String, String> exchange) {
//        this.exchange = exchange;
//    }
//
//    public Map<String, Boolean> getTransaction() {
//        return transaction;
//    }
//
//    public void setTransaction(Map<String, Boolean> transaction) {
//        this.transaction = transaction;
//    }
//
//    public Map<String, Boolean> getCoupon() {
//        return coupon;
//    }
//
//    public void setCoupon(Map<String, Boolean> coupon) {
//        this.coupon = coupon;
//    }

    //-------------------------------------------------------------------
    public static class Auth implements Serializable {

        private String fcm;
        //private Map<String, Object> modify;

        public Auth() {
            //modify = Server.getTimestamp();
        }

        public String getFcm() {
            return fcm;
        }

        public void setFcm(String fcm) {
            this.fcm = fcm;
        }


//        public Map<String, Object> getModify() {
//            if (modify == null)
//                modify = Server.getTimestamp();
//            return modify;
//        }

//        public void setModify(Map<String, Object> modify) {
//            this.modify = modify;
//        }
//
//        @Exclude
//        public long getModifyLong() {
//            return Server.getTiemLong(modify);
//        }
    }

    //-------------------------------------------------------------------
    public static class Info implements Serializable {
        private String name;
        private long birthday;
        private int gender = 3; //0:female 1:male 2:other 3:unknown
        private String mail;
        private String phone;
        private int status; //0:normal 1:NotShare 2:suspend 3:terminate 4:LegalIssue 5:other
        //private Map<String, Object> created;
        //private Map<String, Object> modify;
        private boolean share;

        public Info() {
            //created = Server.getTimestamp();
            //modify = Server.getTimestamp();
        }

        public Info(String name, long birthday, int gender) {
            this.name = name;
            this.birthday = birthday;
            this.gender = gender;
            this.status=0;
            this.share =true;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getBirthday() {
            return birthday;
        }

        public void setBirthday(long birthday) {
            this.birthday = birthday;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public String getMail() {
            return mail;
        }

        public void setMail(String mail) {
            this.mail = mail;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

//        public Map<String, Object> getCreated() {
//            if (created == null)
//                created = Server.getTimestamp();
//            return created;
//        }
//
//        public void setCreated(Map<String, Object> created) {
//            this.created = created;
//        }
//
//        public Map<String, Object> getModify() {
//            if (modify == null)
//                modify = Server.getTimestamp();
//            return modify;
//        }

        public boolean isShare() {
            return share;
        }

        public void setShare(boolean share) {
            if (share) status = 0;
            else status = 1;
            this.share = share;
        }

//        public void setModify(Map<String, Object> modify) {
//            this.modify = modify;
//        }

//        @Exclude
//        public long getCreatedLong() {
//            return Server.getTiemLong(created);
//        }
//
//        @Exclude
//        public long getModifyLong() {
//            return Server.getTiemLong(modify);
//        }
//
//        @Exclude
        public void setGenderStr(String genderStr) {
            if (genderStr.equals("male"))
                this.gender = 1;
            else if (genderStr.equals("female"))
                this.gender = 0;
            else if (genderStr.equals("other"))
                this.gender = 2;
            else if (genderStr.equals("unknown"))
                this.gender = 3;
        }
    }

    //-------------------------------------------------------------------
    public static class Share implements Serializable {
        private int type = 0;
        private Map<String, Boolean> infos;

        public Share() {
            infos = new HashMap<>();
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public Map<String, Boolean> getInfos() {
            return infos;
        }

        //        public void setInfos(Map<String, Boolean> infos) {
//            this.infos = infos;
//        }
        public void setInfos(Map<String, Boolean> infos) {
            boolean gender = false, age = false;
            for (Map.Entry entry : infos.entrySet()) {
                String type = entry.getKey().toString();
                boolean value = Boolean.parseBoolean(entry.getKey().toString());
                if (type.equals("gender")) gender = value;
                if (type.equals("age")) age = value;
            }
            if (gender && age) this.type = 3;
            else if (gender) this.type = 1;
            else if (age) this.type = 2;
            else this.type = 0;
            this.infos = infos;
        }
    }
}
    //-------------------------------------------------------------------
//    public static class Coupon implements Serializable{
//        @Expose private long couponCount;
//        @Expose private Map<String,CouponInfo> couponInfos;
//
//        public Coupon() {
//            couponInfos = new HashMap<>();
//        }
//
//        public long getCouponCount() {
//            return couponCount;
//        }
//
//        public void setCouponCount(long couponCount) {
//            this.couponCount = couponCount;
//        }
//
//        public Map<String, CouponInfo> getCouponInfos() {
//            return couponInfos;
//        }
//
//        public void setCouponInfos(Map<String, CouponInfo> couponInfos) {
//            this.couponInfos = couponInfos;
//        }
//    }
    //----------------------------------------------------------------------
//    public static class CouponInfo implements Serializable{
//        @Expose private String geo;
//        @Expose private long startDate;
//        @Expose private long endDate;
//        @Expose private int status;  //0:show 1:hide
//        @Expose private String seekWebLink;
//
//        public CouponInfo() {
//        }
//
//        public CouponInfo(String geo, long startDate, long endDate) {
//            this.geo = geo;
//            this.startDate = startDate;
//            this.endDate = endDate;
//            this.status=0;
//        }
//
//        public CouponInfo(String geo, long startDate, long endDate, String seekWebLink) {
//            this.geo = geo;
//            this.startDate = startDate;
//            this.endDate = endDate;
//            this.status=0;
//            this.seekWebLink = seekWebLink;
//        }
//
//        public String getGeo() {
//            return geo;
//        }
//
//        public void setGeo(String geo) {
//            this.geo = geo;
//        }
//
//        public long getStartDate() {
//            return startDate;
//        }
//
//        public void setStartDate(long startDate) {
//            this.startDate = startDate;
//        }
//
//        public long getEndDate() {
//            return endDate;
//        }
//
//        public void setEndDate(long endDate) {
//            this.endDate = endDate;
//        }
//
//        public int getStatus() {
//            return status;
//        }
//
//        public void setStatus(int status) {
//            this.status = status;
//        }
//
//        public String getSeekWebLink() {
//            return seekWebLink;
//        }
//
//        public void setSeekWebLink(String seekWebLink) {
//            this.seekWebLink = seekWebLink;
//        }
//    }
    //-------------------------------------------------------------------
//    public static class Point implements Serializable{
//        @Expose private long total;
//        @Expose private long seekPoint;
//        @Expose private long pushPoint;
//        @Expose private long spendPoint;
//
//        public Point() {
//            this.total=0;
//            this.pushPoint=0;
//            this.seekPoint=0;
//            this.spendPoint=0;
//        }
//
//        public long getTotal() {
//            return total;
//        }
//
//        public void setTotal(long total) {
//            this.total = total;
//        }
//
//        public long getPushPoint() {
//            return pushPoint;
//        }
//
//        public void setPushPoint(long pushPoint) {
//            this.pushPoint = pushPoint;
//            setTotalCount();
//        }
//
//        public long getSeekPoint() {
//            return seekPoint;
//        }
//
//        public void setSeekPoint(long seekPoint) {
//            this.seekPoint = seekPoint;
//            setTotalCount();
//        }
//        public long getSpendPoint() {
//            return spendPoint;
//        }
//
//        public void setSpendPoint(long spendPoint) {
//            this.spendPoint = spendPoint;
//            setTotalCount();
//        }
//        @Exclude
//        public void setAddSpendPoint(long spendPoint){
//            this.spendPoint = this.spendPoint +spendPoint;
//            setTotalCount();
//        }
//        @Exclude
//        public void setAddPushPoint(long pushPoint){
//            this.pushPoint = this.pushPoint +pushPoint;
//            setTotalCount();
//        }
//        @Exclude
//        public void setAddSeekPoint(long seekPoint){
//            this.seekPoint = this.seekPoint +seekPoint;
//            setTotalCount();
//        }
//        private void setTotalCount(){
//            this.total=pushPoint+seekPoint-spendPoint;
//        }
//    }
    //--------------------------------------------------------------------------------
//    public static class Push implements Serializable{
//        @Expose private long pushCount;
//        @Expose private Map<String,FlyerInfo> info;
//
//        public Push() {this.info = new HashMap<>();}
//
//        public long getPushCount() {
//            return pushCount;
//        }
//
//        public void setPushCount(long pushCount) {
//            this.pushCount = pushCount;
//        }
//
//        public Map<String, FlyerInfo> getInfo() {
//            return info;
//        }
//
//        public void setInfo(Map<String, FlyerInfo> info) {
//            this.info = info;
//        }
//    }
    //------------------------------------------------------------------------------------
//    public static class FlyerInfo implements Serializable{ //orderKey
//        @Expose private String fileName;
//        @Expose private String webLink;
//        @Expose private boolean open;
//        @Expose private Gps location;
//        @Expose private int point;
//        @Expose private int status; //0:normal 1:disable
//
//        public FlyerInfo() {
//            this.location = new Gps();
//            //this.location = new Location();
//            this.open = false;
//            this.status =0;
//        }
//
//        public String getFileName() {
//            return fileName;
//        }
//
//        public void setFileName(String fileName) {
//            this.fileName = fileName;
//        }
//
//        public String getWebLink() {
//            return webLink;
//        }
//
//        public void setWebLink(String webLink) {
//            this.webLink = webLink;
//        }
//
//        public int getPoint() {
//            return point;
//        }
//
//        public void setPoint(int point) {
//            this.point = point;
//        }
//
//        public boolean isOpen() {
//            return open;
//        }
//
//        public void setOpen(boolean open) {
//            this.open = open;
//        }
//
//        public Gps getLocation() {
//            return location;
//        }
//
//        public void setLocation(Gps location) {
//            this.location = new Gps(
//                    location.getGeo(),location.getAddress(),
//                    location.getLatitude(),location.getLongitude(),location.getModifyLong());
//        }
//
////        public Location getLocation() {
////            return location;
////        }
////
////        public void setLocation(Location location) {
////            this.location = new Location(
////                    location.getGeo(),location.getAddress(),
////                    location.getLatitude(),location.getLongitude(),
////                    location.getModify());
////        }
//
//        public int getStatus() {
//            return status;
//        }
//
//        public void setStatus(int status) {
//            this.status = status;
//        }
//    }
    //-----------------------------------------------------------------------------
//    public static class Location{
//        @Expose private String geo;
//        @Expose private String address;
//        @Expose private double latitude;
//        @Expose private double longitude;
//        @Expose private Map<String,Object> modify;
//
//        public Location() {}
//
//        public Location(String geo, String address, double latitude, double longitude,
//                        Map<String,Object> modify) {
//            this.geo = geo;
//            this.address = address;
//            this.latitude = latitude;
//            this.longitude = longitude;
//            this.modify = modify;
//            //modify = Server.getTimestamp();
//        }
//
//        public String getGeo() {
//            return geo;
//        }
//
//        public void setGeo(String geo) {
//            this.geo = geo;
//        }
//
//        public String getAddress() {
//            return address;
//        }
//
//        public void setAddress(String address) {
//            this.address = address;
//        }
//
//        public double getLatitude() {
//            return latitude;
//        }
//
//        public void setLatitude(double latitude) {
//            this.latitude = latitude;
//        }
//
//        public double getLongitude() {
//            return longitude;
//        }
//
//        public void setLongitude(double longitude) {
//            this.longitude = longitude;
//        }
//
//        public Map<String, Object> getModify() {
//            if(modify == null)
//                modify = Server.getTimestamp();
//            return modify;
//        }
//
//        public void setModify(Map<String, Object> modify) {
//            this.modify = modify;
//        }
//        @Exclude
//        public long getModifyLong (){
//            return  Server.getTiemLong(modify);
//        }
//    }
//}
