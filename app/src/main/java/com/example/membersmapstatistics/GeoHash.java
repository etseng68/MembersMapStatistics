package com.example.membersmapstatistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class
GeoHash {
    private static int DEFAULT_LEVEL = 12;

    public static String toGeoHash(double lat,double lng) {
        return toGeoHash(lat, lng, DEFAULT_LEVEL);
    }

    public static String toGeoHash(double lat,double lng, int level) {
        int len = level * 5;

        double lngMax = 180;
        double lngMin = -180;
        double latMax = 90;
        double latMin = -90;

        int[] geoBin = new int[len];

        for (int i = 0; i < len; i++) {
            //偶数 经度
            if ((i | 1) != i) {
                double lngMid = (lngMax + lngMin) / 2;
                if (lng > lngMid) {
                    geoBin[i] = 1;
                    lngMin = lngMid;
                } else {
                    geoBin[i] = 0;
                    lngMax = lngMid;
                }
                //奇数 纬度
            } else {
                double latMid = (latMax + latMin) / 2;
                if (lat > latMid) {
                    geoBin[i] = 1;
                    latMin = latMid;
                } else {
                    geoBin[i] = 0;
                    latMax = latMid;
                }
            }
        }
        return Base32.encode(geoBin);
    }

    public static GPS toGPS(String geohash) {
        int[] geoBin = Base32.decode(geohash);
        int len = geoBin.length;

        double lngMax = 180;
        double lngMin = -180;
        double latMax = 90;
        double latMin = -90;

        int[] lngBin = new int[len / 2];
        int[] latBin = new int[len / 2];

        for (int i = 0; i < len; i++) {
            //偶数 经度
            if (i % 2 == 0) {
                if (geoBin[i] == 1) {
                    lngMin = (lngMax + lngMin) / 2;
                } else {
                    lngMax = (lngMax + lngMin) / 2;
                }
                lngBin[i / 2] = geoBin[i];
                //奇数 纬度
            } else {
                if (geoBin[i] == 1) {
                    latMin = (latMax + latMin) / 2;
                } else {
                    latMax = (latMax + latMin) / 2;
                }
                latBin[i / 2] = geoBin[i];
            }
        }
        return new GPS((lngMax + lngMin) / 2, (latMax + latMin) / 2, lngBin, latBin);
    }

    public static List<String> getRound8(String geohash) {
        List<String> l = new ArrayList<>();
        GPS gps = toGPS(geohash);

        int[] midLatBin = gps.latBin;
        int[] lowLatBin = decrBin(gps.latBin);
        int[] highLatBin = incrBin(gps.latBin);

        int[] midLngBin = gps.lngBin;
        int[] lowLngBin = decrBin(gps.lngBin);
        int[] highLngBin = incrBin(gps.lngBin);


        l.add(Base32.encode(mergeLngLat(lowLngBin, lowLatBin)));
        l.add(Base32.encode(mergeLngLat(lowLngBin, midLatBin)));
        l.add(Base32.encode(mergeLngLat(lowLngBin, highLatBin)));

        l.add(Base32.encode(mergeLngLat(midLngBin, lowLatBin)));
        l.add(Base32.encode(mergeLngLat(midLngBin, highLatBin)));

        l.add(Base32.encode(mergeLngLat(highLngBin, lowLatBin)));
        l.add(Base32.encode(mergeLngLat(highLngBin, midLatBin)));
        l.add(Base32.encode(mergeLngLat(highLngBin, highLatBin)));


        return l;
    }

    private static int[] mergeLngLat(int[] lngBin, int[] latBin) {
        int[] geoBin = new int[latBin.length + lngBin.length];
        for (int i = 0; i < geoBin.length; i++) {
            if (i % 2 == 0) {
                geoBin[i] = lngBin[i / 2];
            } else {
                geoBin[i] = latBin[i / 2];
            }
        }
        return geoBin;
    }

    private static int[] decrBin(int[] bin) {
        int[] newBin = bin.clone();
        for (int i = bin.length - 1; i >= 0; i--) {
            if (newBin[i] == 1) {
                newBin[i] = 0;
                break;
            } else {
                newBin[i] = 1;
            }
        }
        return newBin;
    }

    private static int[] incrBin(int[] bin) {
        int[] newBin = bin.clone();
        for (int i = bin.length - 1; i >= 0; i--) {
            if (newBin[i] == 0) {
                newBin[i] = 1;
                break;
            } else {
                newBin[i] = 0;
            }
        }
        return newBin;
    }

    public static class GPS {
        double lat;
        double lng;
        private int[] latBin;
        private int[] lngBin;

        GPS(double lng, double lat, int[] lngBin, int[] latBin) {
            this.lng = lng;
            this.lat = lat;
            this.lngBin = lngBin;
            this.latBin = latBin;
        }

        public double getLat() {
            return lat;
        }

        public double getLng() {
            return lng;
        }

        @Override
        public String toString() {
            return "lng:" + lng + ", lat:" + lat + ", lngBin:" + Arrays.toString(lngBin).replace(", ", "") + ", latBin:" + Arrays.toString(latBin).replace(", ", "");
        }
    }

    private static final class Base32 {

        private static final char[] base32Table = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        private static final Map<Character, Integer> base32Map = new HashMap<>();

        static {
            for (int i = 0; i < 32; i++) {
                base32Map.put(base32Table[i], i);
            }
        }

        public static int[] decode(String str) {
            int len = str.length();
            int[] bin = new int[len * 5];

            for (int i = 0, j = 0; i < len; i++, j += 5) {
                int idx = base32Map.get(str.charAt(i));

                String binStr = Integer.toBinaryString(idx);
                int binLen = binStr.length();
                for (int k = 0; k < 5; k++) {
                    bin[j + k] = k < (5 - binLen) ? 0 : binStr.charAt(k - (5 - binLen)) - '0';
                }
            }

            return bin;
        }

        public static String encode(int[] bin) {

            int len = bin.length;

            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < len; i += 5) {

                int idx = (bin[i] << 4)
                        + ((i + 1 < len) ? bin[i + 1] << 3 : 0)
                        + ((i + 2 < len) ? bin[i + 2] << 2 : 0)
                        + ((i + 3 < len) ? bin[i + 3] << 1 : 0)
                        + ((i + 4 < len) ? bin[i + 4] : 0);
                sb.append(base32Table[idx]);
            }

            return sb.toString();
        }
    }

    public static int getPrecision(float widthMeter,float heightMeter){
        int chrNum=0;
        double area=widthMeter * heightMeter;
        if( area >(1250000*625000) && area <=(5000000*5000000)){  //width * height
            chrNum=1;
        } else if(area>(156000*156000) && area <=(1250000*625000)){
            chrNum=2;
        } else if(area>(39100*19500) && area <=(156000*156000)){
            chrNum=3;
        } else if(area>(4890*4890) && area <=(39100*19500)){
            chrNum=4;
        }else if(area>(1220*610) && area <=(4890*4890)){
            chrNum=5;
        }else if(area>(153*153) && area <=(1220*610)){
            chrNum=6;
        }else if(area>(38.2*19.1) && area <=(153*153)){
            chrNum=7;
        }else if(area>(4.77*4.77) && area <=(38.2*19.1)){
            chrNum=8;
        }else if(area>(1.19*0.596) && area <=(4.77*4.77)){
            chrNum=9;
        }else if(area>(0.149*0.149) && area <=(1.19*0.596)){
            chrNum=10;
        }else if(area>(0.0372*0.0186) && area <=(0.149*0.149)){
            chrNum=11;
        }else if(area <=(0.0372*0.0186)){
            chrNum=12;
        }
        return chrNum;
    }
    public static int getHeightPrecision(float heightMeter){
        int chrNum=0;
        if(heightMeter > 624100 && heightMeter <= 4992600){  //width * height
            chrNum=1;
        } else if(heightMeter > 156000 && heightMeter <=624100){
            chrNum=2;
        } else if(heightMeter > 19500 && heightMeter <= 156000){
            chrNum=3;
        } else if(heightMeter > 4890 && heightMeter <= 19500){
            chrNum=4;
        }else if(heightMeter >610 && heightMeter <=4890){
            chrNum=5;
        }else if(heightMeter >153 && heightMeter <= 610){
            chrNum=6;
        }else if(heightMeter >19.1 && heightMeter <= 153){
            chrNum=7;
        }else if(heightMeter > 4.77 && heightMeter <= 19.1){
            chrNum=8;
        }else if(heightMeter > 0.596 && heightMeter <= 4.77){
            chrNum=9;
        }else if(heightMeter > 0.149 && heightMeter <= 0.596){
            chrNum=10;
        }else if(heightMeter > 0.0186 && heightMeter <= 0.149){
            chrNum=11;
        }else if(heightMeter <= 0.0186){
            chrNum=12;
        }
        return chrNum;
    }
}
