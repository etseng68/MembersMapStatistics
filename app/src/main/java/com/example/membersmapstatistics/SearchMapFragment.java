package com.example.membersmapstatistics;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

//import com.ai2app.spolesflyer.androidlib.comm.FirebaseApi;
//import com.ai2app.spolesflyer.androidlib.comm.data.Customer;
//import com.ai2app.spolesflyer.androidlib.comm.data.Store;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.ui.IconGenerator;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SearchMapFragment extends Fragment {
    private static final String TAG = "SearchMapFragment";
    private static final String ARG_UID = "uid";
    private static final String DB_TABLE_NAME = "Customer";

    public static final int RANGE_TYPE_NONE=0;
    public static final int RANGE_TYPE_GENDER=1;
    public static final int RANGE_TYPE_AGE=2;
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private LatLng mLocation = new LatLng(25.033408, 121.564099);
    private String mLocationGeoCode;
    private int mDefaultZoom=16;
    //private DatabaseReference membersRef;
    public MapRangData mMapRangData;
    private boolean mMapLoadReady =false;
    private String mUid;
    private List<Customer> mCustomers;
    private DBHelper mdb;

    public SearchMapFragment() {

    }
/*    public static SearchMapFragment newInstance(String uid) {
        SearchMapFragment fragment = new SearchMapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_UID, uid);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*        if (getArguments() != null) {
            mUid =getArguments().getString(ARG_UID);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
        mdb = new DBHelper(getContext());
        if (mdb.checkTableExist(DB_TABLE_NAME)) {
            mdb.dropTable(DB_TABLE_NAME);
            mdb.createCustomerTable();
        }
        mdb.fillFakeData(100);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_seach_map, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) view.findViewById(R.id.search_map);
        mMapView.onCreate(savedInstanceState);
        mMapView.setClickable(false);
        mMapView.getMapAsync(new OnMapReadyCallback(){

            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                mGoogleMap.setOnCameraMoveListener(cameraMoveListener);
                mGoogleMap.setOnCameraMoveStartedListener(cameraMoveStartedListener);
                mGoogleMap.setOnCameraIdleListener(onCameraIdleListener);
                //mGoogleMap.setOnCameraChangeListener(cameraChangeListener);
                if (ActivityCompat.checkSelfPermission(
                        getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED
                        &&
                        ActivityCompat.checkSelfPermission(
                                getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                PackageManager.PERMISSION_GRANTED) {
                    //Log.i(TAG,"checkSelfPermission error");
                }
                else {
                    //Log.i(TAG, "checkSelfPermission ok");
                    mGoogleMap.setMyLocationEnabled(true);
                    UiSettings settings = mGoogleMap.getUiSettings();
                    settings.setAllGesturesEnabled(true);
                    settings.setMyLocationButtonEnabled(true);
                }
                //moveMap(mLocation,mDefaultZoom);
                mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        mMapLoadReady =true;
                        //int w = mMapView.getWidth();
                        //int h = mMapView.getHeight();
                        mMapRangData.setPointRange(mMapView.getWidth(),mMapView.getHeight());
                        Projection projection = mGoogleMap.getProjection();
                        //showDefaultTage(projection);
                        moveMap(mLocation,mDefaultZoom);
                    }
                });
            }
        });
        //mLocationGeoCode=GeoHash.toGeoHash(mLocation.longitude,mLocation.latitude,10);
        mMapRangData = new MapRangData(getActivity());
    }
/*    private void getStore(){
        FirebaseApi.getStore(mUid, new FirebaseApi.StoreListener() {
            @Override
            public void onComplete(Store store) {
                if(store != null && store.getShop() != null
                        && store.getShop().getLatitude() >0
                        && store.getShop().getLongitude() >0)
                    mLocation = new LatLng(store.getShop().getLatitude(),
                            store.getShop().getLongitude());
                    //mLocationGeoCode=GeoHash.toGeoHash(mLocation.longitude,mLocation.latitude,10);

            }
        });
    }*/
    private GoogleMap.OnCameraMoveStartedListener cameraMoveStartedListener =
            new GoogleMap.OnCameraMoveStartedListener() {
                @Override
                public void onCameraMoveStarted(int i) {
                    Log.i(TAG,"onCameraMoveStarted");
                }
            };
    private GoogleMap.OnCameraMoveListener cameraMoveListener = new GoogleMap.OnCameraMoveListener() {
        @Override
        public void onCameraMove() {
            Log.i(TAG,"onCameraMove");
        }
    };
    private GoogleMap.OnCameraIdleListener onCameraIdleListener = new GoogleMap.OnCameraIdleListener() {
        @Override
        public void onCameraIdle() {
            Log.i(TAG,"onCameraIdle");
            if(mMapLoadReady) {
                CameraPosition cameraPosition = mGoogleMap.getCameraPosition();
                LatLng latLng = cameraPosition.target;
                String centerCode = GeoHash.toGeoHash(latLng.latitude,latLng.longitude,  10);
                if (!getLocationGeoCode().equals(centerCode)) {
                    moveMap(cameraPosition.target, cameraPosition.zoom);
                    mLocation = latLng;
                    //mLocationGeoCode = centerCode;
                }
                //else
                //getFireData(getGeohashToFindFireData());
                getMemberGPO(getGeohashToFindFireData());
            }
        }
    };
    public String getLocationGeoCode(){
        if(mLocation != null)
        mLocationGeoCode=GeoHash.toGeoHash(mLocation.latitude,mLocation.longitude,10);
        return mLocationGeoCode;
    }
//    private GoogleMap.OnCameraChangeListener cameraChangeListener= new GoogleMap.OnCameraChangeListener() {
//        @Override
//        public void onCameraChange(CameraPosition cameraPosition) {
//            LatLng latLng=cameraPosition.target;
//            String centerCode = GeoHash.toGeoHash(latLng.longitude,latLng.latitude,10);
//            if(!mLocationGeoCode.equals(centerCode)) {
//                moveMap(cameraPosition.target, cameraPosition.zoom);
//                mLocation=latLng;
//                mLocationGeoCode=centerCode;
//            }
//        }
//    };
    private void moveMap(final LatLng place,float zoom) {
        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(place)
                        .zoom(zoom)
                        .build();
        //mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), cancelableCallback);
    }

    private GoogleMap.CancelableCallback cancelableCallback = new GoogleMap.CancelableCallback() {
        @Override
        public void onFinish() {
            Log.i(TAG,"onFinish");
            //mMapRangData.removeMarker();
//            getFireData(getGeohashToFindFireData());
        }

        @Override
        public void onCancel() {

        }
    };
    private String getGeohashToFindFireData(){
        //String[] areas=new String[9];
        int precision=0;
        String geoCode = null;

        Projection projection= mGoogleMap.getProjection();
        VisibleRegion visibleRegion = projection.getVisibleRegion();

        LatLngBounds latLngBounds= visibleRegion.latLngBounds;
        LatLng center = latLngBounds.getCenter();
        //Log.i(TAG,mLocation.toString());
        mMapRangData.setCenter(center);
        LatLng northeast = latLngBounds.northeast;
        LatLng southwest =latLngBounds.southwest;

        LatLng northwest = projection.fromScreenLocation(new Point(0,0));

        float[] heightResults = new float[3];
        Location.distanceBetween(northwest.latitude,northwest.longitude,
                southwest.latitude,southwest.longitude,heightResults);

        float[] widthResults = new float[3];
        Location.distanceBetween(northwest.latitude,northwest.longitude,
                northeast.latitude,northeast.longitude,widthResults);

        float[] radiusResult = new float[3];
        Location.distanceBetween(northwest.latitude,northwest.longitude,
                center.latitude,center.longitude,radiusResult);
        mMapRangData.setRadius((int)radiusResult[0]);

        precision = GeoHash.getHeightPrecision(heightResults[0]);
        geoCode = GeoHash.toGeoHash(center.latitude,center.longitude,precision);

        return geoCode;
    }
/*    private void getFireData(String qCode){
        if(qCode != null && !qCode.isEmpty()) {
            mMapRangData.setLocationGeoCode(qCode);
            FirebaseDatabase.getInstance().getReference().child("customer")
                    .orderByChild("gps/geo").startAt(qCode)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        LatLngBounds latLngBounds =mGoogleMap.getProjection().getVisibleRegion().latLngBounds;
                        int female=0,male=0;
                        Map<String,Integer> ageMap = new HashMap<>();
                        for(DataSnapshot customerShot:dataSnapshot.getChildren()) {
                            Customer customer = customerShot.getValue(Customer.class);
                            LatLng latLng = new LatLng(customer.getGps().getLatitude(),
                                    customer.getGps().getLongitude());
                            if (latLngBounds.contains(latLng)) {
                                if (customer.getInfo().getGender() == 0)
                                    female++;
                                else if (customer.getInfo().getGender() == 1)
                                    male++;
                                ageMap = setAgeMap(customer.getInfo().getBirthday(), ageMap);
                            }
                        }
                        saveRangData(male,female,ageMap);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }*/
    private void getMemberGPO(String qCode){
        if(qCode != null && !qCode.isEmpty()) {
            //List<Customer> customers = fillRandomRangeData();
            List<Customer> customers = mdb.queryCustomers(qCode);
            if(customers != null && customers.size()>0) {
                mMapRangData.setLocationGeoCode(qCode);
                LatLngBounds latLngBounds =mGoogleMap.getProjection().getVisibleRegion().latLngBounds;
                int female=0,male=0;
                Map<String,Integer> ageMap = new HashMap<>();
                for (Customer customer : customers) {
                    LatLng latLng = new LatLng(customer.getGps().getLatitude(),
                            customer.getGps().getLongitude());
                    //addMapMark(latLng,customer.getInfo().getName());
                    //moveMap(latLng,mDefaultZoom);
                    //Log.i(TAG, "getMemberGPO: "+customer.getInfo().getName());
                    if (latLngBounds.contains(latLng)) {
                        if (customer.getInfo().getGender() == 0)
                            female++;
                        else if (customer.getInfo().getGender() == 1)
                            male++;
                        ageMap = setAgeMap(customer.getInfo().getBirthday(), ageMap);
                    }
                }
                saveRangData(male,female,ageMap);
            }

        }
    }
    private void addMapMark(LatLng latLng,String title){
        mGoogleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(title));
    }


    private Map<String,Integer> setAgeMap(long birthdayStamp,Map<String,Integer> ageMap){
        Calendar birthday = Calendar.getInstance();
        birthday.setTimeInMillis(birthdayStamp);
        Calendar now = Calendar.getInstance();
        int age = now.get(Calendar.YEAR)-birthday.get(Calendar.YEAR);
        if(now.get(Calendar.DAY_OF_YEAR)<birthday.get(Calendar.DAY_OF_YEAR))
            age--;
        if(age<=12){ ageMap=setAgeCount("0-12",ageMap);
        }else if(age > 12 && age <=18){ ageMap=setAgeCount("13-18",ageMap);
        }else if(age > 18 && age <=25){ ageMap=setAgeCount("19-25",ageMap);
        }else if(age > 25 && age <=35){ ageMap=setAgeCount("26-35",ageMap);
        }else if(age > 35 && age <=45){ ageMap=setAgeCount("36-45",ageMap);
        }else if(age > 45 && age <=55){ ageMap=setAgeCount("46-55",ageMap);
        }else if(age > 55 && age <=65){ ageMap=setAgeCount("56-65",ageMap);
        }else if(age > 65){ ageMap=setAgeCount("66-99",ageMap);}
        return ageMap;
    }
    private Map<String,Integer> setAgeCount(String range,Map<String,Integer> ageMap){
        if(ageMap.get(range) != null) {
            int count = ageMap.get(range)+1;
            ageMap.put(range,count);
        }
        else
            ageMap.put(range,1);

        return ageMap;
    }
    public void showRangeType(int rangeType){
        //int reType=RANGE_TYPE_NONE;
        mMapRangData.setIconType(rangeType);
/*        switch (rangeType){
            case RANGE_TYPE_GENDER:
                reType=RANGE_TYPE_AGE;
                break;
            case RANGE_TYPE_AGE:
                reType=RANGE_TYPE_GENDER;
                break;
        }*/
        //getFireData(getGeohashToFindFireData());
        getMemberGPO(getGeohashToFindFireData());
        //return reType;
    }
    public MapRangData getMapRangData(){
        return mMapRangData;
    }


    private void setMapRangTag(){
        //IconGenerator iconFactory = new IconGenerator(getActivity());
        //iconFactory.setBackground(getResources().getDrawable(R.drawable.ic_home_black_48px,null));
        Projection projection = mGoogleMap.getProjection();
        //mMapRangData.setPointRange(mMapView.getWidth(),mMapView.getHeight());
        switch (mMapRangData.getIconType()){
            case MapRangData.RANGE_TYPE_GENDER:
                hideMarkerTitles(MapRangData.ageTitle);
//                for(String name:MapRangData.ageTitle){
//                    if (mMapRangData.getMarker(name)!= null)
//                        mMapRangData.getMarker(name).setVisible(false);
//                }
                for(String name:MapRangData.genderTitle){
                    if(mMapRangData.getGender(name)>0)
                        showTag(name,projection);
                    else
                        //mMapRangData.getMarker(name).setVisible(false);
                        hidMarketTitle(name);
                }
                break;
            case MapRangData.RANGE_TYPE_AGE:
                hideMarkerTitles(MapRangData.genderTitle);
//                for(String name:MapRangData.genderTitle){
//                    if (mMapRangData.getMarker(name)!= null)
//                        mMapRangData.getMarker(name).setVisible(false);
//                }
                for(String name:MapRangData.ageTitle) {
                    if(mMapRangData.getAge(name) >0)
                        showTag(name, projection);
                    else
                        hidMarketTitle(name);
//                        if(mMapRangData.getMarker(name) != null)
//                            mMapRangData.getMarker(name).setVisible(false);
                }
                break;
        }
    }

    private void hideMarkerTitles(String[] names){
        for(String name:names) {
            hidMarketTitle(name);
        }
    }
    private void hidMarketTitle(String name){
        Marker marker = mMapRangData.getMarker(name);
        if (marker != null)
            marker.setVisible(false);
    }
    private void showTag(String name,Projection projection){
        mMapRangData.setMarkerOptions(name,
                getMarkerOptions(
                        mMapRangData.getIconGenerator(name),
                        name,
                        projection.fromScreenLocation(mMapRangData.getPoint(name))));
        mMapRangData.setMarker(name, mGoogleMap.addMarker(mMapRangData.getMarkerOptions(name)));
        mMapRangData.getMarker(name).showInfoWindow();
    }

    public void saveRangData(int male, int female, Map<String, Integer> ageMap){
        mMapRangData.setMapData(male,female,ageMap);
        setMapRangTag();
    }

    private MarkerOptions getMarkerOptions(IconGenerator iconFactory, String text, LatLng position) {
        MarkerOptions markerOptions = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(text)))
                .position(position)
                .anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
        return markerOptions;
    }


    @Override
    public void onResume() {
        super.onResume();
        //getStore();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
    //-------------------------------------------------------------------------

}
