package com.example.membersmapstatistics;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class DBHelper extends SQLiteOpenHelper{
    private static final String DataBaseName = "Customer.db";
    private static final int DataBaseVersion = 1;
    //private SQLiteDatabase mDb;

    public DBHelper(@Nullable Context context) {
        super(context, DataBaseName, null, DataBaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
/*        String SqlTable = "CREATE TABLE IF NOT EXISTS Customer (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT not null," +
                "birthday INTEGER," +
                "gender INTEGER," +
                "latitude REAL," +
                "longitude REAL," +
                "geo TEXT" +
                ")";
        db.execSQL(SqlTable);*/
        //createCustomerTable();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //final String SQL = "DROP TABLE Customer";
        //db.execSQL(SQL);
    }
    public void createCustomerTable(){
        String SqlTable = "CREATE TABLE IF NOT EXISTS Customer (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT not null," +
                "birthday INTEGER," +
                "gender INTEGER," +
                "latitude REAL," +
                "longitude REAL," +
                "geo TEXT" +
                ")";
        getWritableDatabase().execSQL(SqlTable);
    }
    public void dropTable(String tableName){
        final String SQL = "DROP TABLE "+tableName;
        getWritableDatabase().execSQL(SQL);
    }
    // @param db, readable database from SQLiteOpenHelper
    public boolean checkTableExist(String tableName) {
        Cursor cursor = getReadableDatabase().rawQuery(
                "select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'",
                null);

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }
    public void fillFakeData(int number){
        //if(mDb != null) {
        LatLng point = new LatLng(25.033408d,121.564099d);
            for (int i = 0; i < number; i++) {
                ContentValues values = new ContentValues();
                values.put("name", getRName(i));
                values.put("birthday", getRBirthday());
                values.put("gender", getRGender());
                LatLng latLng = getRandomLocation(point,3000);
                values.put("latitude", latLng.latitude);
                values.put("longitude", latLng.longitude);
                values.put("geo", GeoHash.toGeoHash(latLng.latitude,latLng.longitude));
                this.getWritableDatabase().insert("Customer",null,values);
            }
        //}
    }

    public List<Customer> queryCustomers(String qCode){
        String[] projection={"_id","name","birthday","gender","latitude","longitude","geo"};
        String selection = "geo LIKE ?";
        //String[] selectionArgs = {"'"+qCode+"%'"};
        String[] selectionArgs = {qCode+"%"};
        String sortOrder ="_id DESC";
        Cursor cursor = getReadableDatabase().query(
                "Customer",
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
        //Cursor cursor = getReadableDatabase().rawQuery(
        //        "SELECT * FROM Customer WHERE geo LIKE '"+ qCode +"%'",null);
        //cursor.moveToFirst();
        //if(cursor.getCount()>0) {
            List<Customer> customers = new ArrayList<>();
            //for (int i = 0; i < cursor.getCount(); i++) {
            //}
            while(cursor.moveToNext()){
                Customer.Info info = new Customer.Info(
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("birthday")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("gender")));
                Gps gps = new Gps(
                        cursor.getString(cursor.getColumnIndexOrThrow("geo")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("latitude")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("longitude")));
                customers.add(new Customer(info,gps));
            }
            return customers;
        //}else
        //    cursor.close();
        //    return null;
    }
    private long getRBirthday(){
        Random random = new Random();
        int minDay = (int) LocalDate.of(1952, 1, 1).toEpochDay();
        int maxDay = (int) LocalDate.of(2004, 1, 1).toEpochDay();
        long randomDay = minDay + random.nextInt(maxDay - minDay);
        LocalDate randomBirthDate = LocalDate.ofEpochDay(randomDay);
        Instant instant=randomBirthDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        return instant.toEpochMilli();
    }
    private String getRName(int i){
        Random random = new Random();
        return Character.toString((char) (65+random.nextInt(26)))+i;
    }
    private int getRGender(){
        return new Random().nextInt(2);
    }
    private double getRLatitude(){
        //臺北市 緯度 24.9605027 - 25.21017521
        //101 緯度 24.9605027 - 25.21017521
        double l= 25.033408;
        double x=0.00001;
        return l+randomAndCut(x);
    }
    private double getRLongitude(){
        //臺北市 經度 121.4571409 - 121.6659343
        //double ls= 121.4571409, le = 121.6659343;
        double l= 121.564099;
        double x=0.00001;
        return l+randomAndCut(x);
    }
    private double randomAndCut(double base){
        DecimalFormat decimalFormat = new DecimalFormat(".00000000");
        double mx;
        int m = new Random().nextInt(2);
        if(m == 0)
            mx = ThreadLocalRandom.current().nextDouble(base);
        else
            mx = -ThreadLocalRandom.current().nextDouble(base);
        return Double.parseDouble(decimalFormat.format(mx));
    }

    public LatLng getRandomLocation(LatLng point, int radius) {

        List<LatLng> randomPoints = new ArrayList<>();
        List<Float> randomDistances = new ArrayList<>();
        Location myLocation = new Location("");
        myLocation.setLatitude(point.latitude);
        myLocation.setLongitude(point.longitude);

        //This is to generate 10 random points
        for(int i = 0; i<10; i++) {
            double x0 = point.latitude;
            double y0 = point.longitude;

            Random random = new Random();

            // Convert radius from meters to degrees
            double radiusInDegrees = radius / 111000f;

            double u = random.nextDouble();
            double v = random.nextDouble();
            double w = radiusInDegrees * Math.sqrt(u);
            double t = 2 * Math.PI * v;
            double x = w * Math.cos(t);
            double y = w * Math.sin(t);

            // Adjust the x-coordinate for the shrinking of the east-west distances
            double new_x = x / Math.cos(y0);

            double foundLatitude = new_x + x0;
            double foundLongitude = y + y0;
            LatLng randomLatLng = new LatLng(foundLatitude, foundLongitude);
            randomPoints.add(randomLatLng);
            Location l1 = new Location("");
            l1.setLatitude(randomLatLng.latitude);
            l1.setLongitude(randomLatLng.longitude);
            randomDistances.add(l1.distanceTo(myLocation));
        }
        //Get nearest point to the centre
        int indexOfNearestPointToCentre =
                randomDistances.indexOf(Collections.min(randomDistances));
        return randomPoints.get(indexOfNearestPointToCentre);
    }
}
