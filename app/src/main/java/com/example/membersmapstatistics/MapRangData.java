package com.example.membersmapstatistics;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapRangData implements Serializable{
    public static final String[] ageTitle = new String[]{"06-12","13-18","19-25","26-35","36-45","46-55","56-65","66-99"};
    public static final String[] genderTitle = new String[]{"male","female"};
    public static final int RANGE_TYPE_NONE=0;
    public static final int RANGE_TYPE_GENDER=1;
    public static final int RANGE_TYPE_AGE=2;
    private static final DecimalFormat df = new DecimalFormat("#,##0");
    private Context context;
    private boolean changeStatus;
    private int iconType=0;
    private int mWidth;
    private int mHeight;
    private Map<String,Integer> ageMap=new HashMap<>();
    private Map<String,Integer> genderMap=new HashMap<>();
    private Map<String,Marker> markerMap=new HashMap<>();
    private Map<String,MarkerOptions> markerOptionMap=new HashMap<>();
    private Map<String,IconGenerator> iconGeneratorMap=new HashMap<>();
    private Map<String,Drawable> drawableMap=new HashMap<>();
    //private Map<String,Drawable> iconMap=new HashMap<>();
    private Map<String,Point> pointMap=new HashMap<>();
    //private Map<String,String> textNameMap = new HashMap<>();
    private Map<String,View> viewMap = new HashMap<>();
    private LatLng center;
    private String centerAddress;
    private int radius;
    private String flyerId;
    //private Flyer flyer;
    //private String flyerName;
    private int total;
    private String locationGeoCode;

    private ArrayList<PieData> pieDatas = new ArrayList<>();
    private int showCount;
    private Map<String,Integer> mColorMap = new HashMap<>();
    private int choiceType; //
    private ArrayList<String> choiceItems;
    private int limitCost;
    private long limitPoint;
    private long mPointBalance=0;
    private Calendar startDate =Calendar.getInstance();
    private Calendar endDate=Calendar.getInstance();
/*    private Map<String,CouponViewData>  couponViewDataMap; //map<couponKey,new Coupon>
    private CouponViewData couponViewTemple;*/


    public MapRangData(Context context){
        this.context = context;
//        Drawable drawable=context.getResources().getDrawable(R.drawable.ic_home_black_48px,null);
//        drawable.setBounds(0, 0, drawable.getMinimumWidth()+10,drawable.getMinimumHeight()+10);
//        this.iconMap.put(genderTitle[0],drawable);

//        this.iconMap.put(genderTitle[1],context.getResources().getDrawable(R.drawable.female_icon,null));
        this.iconType=RANGE_TYPE_GENDER;
        this.drawableMap.put(genderTitle[0],context.getResources().getDrawable(R.drawable.map_icon_blue_48px,null));
        this.drawableMap.put(genderTitle[1],context.getResources().getDrawable(R.drawable.map_icon_pink_48px,null));
        this.iconGeneratorMap.put(genderTitle[0],createIconGenerator(genderTitle[0]));
        this.iconGeneratorMap.put(genderTitle[1],createIconGenerator(genderTitle[1]));

        this.drawableMap.put(ageTitle[0],context.getResources().getDrawable(R.drawable.map_icon_red_48px,null));
        this.drawableMap.put(ageTitle[1],context.getResources().getDrawable(R.drawable.map_icon_purple_48px,null));
        this.drawableMap.put(ageTitle[2],context.getResources().getDrawable(R.drawable.map_icon_orange_48px,null));
        this.drawableMap.put(ageTitle[3],context.getResources().getDrawable(R.drawable.map_icon_yellow_48px,null));
        this.drawableMap.put(ageTitle[4],context.getResources().getDrawable(R.drawable.map_icon_green_48px,null));
        this.drawableMap.put(ageTitle[5],context.getResources().getDrawable(R.drawable.map_icon_teal_48px,null));
        this.drawableMap.put(ageTitle[6],context.getResources().getDrawable(R.drawable.map_icon_brown_48px,null));
        this.drawableMap.put(ageTitle[7],context.getResources().getDrawable(R.drawable.map_icon_grey_48px,null));

        this.iconGeneratorMap.put(ageTitle[0],createIconGenerator(ageTitle[0]));
        this.iconGeneratorMap.put(ageTitle[1],createIconGenerator(ageTitle[1]));
        this.iconGeneratorMap.put(ageTitle[2],createIconGenerator(ageTitle[2]));
        this.iconGeneratorMap.put(ageTitle[3],createIconGenerator(ageTitle[3]));
        this.iconGeneratorMap.put(ageTitle[4],createIconGenerator(ageTitle[4]));
        this.iconGeneratorMap.put(ageTitle[5],createIconGenerator(ageTitle[5]));
        this.iconGeneratorMap.put(ageTitle[6],createIconGenerator(ageTitle[6]));
        this.iconGeneratorMap.put(ageTitle[7],createIconGenerator(ageTitle[7]));

        initChartColorMap();
//        startDate.set(startDate.get(Calendar.YEAR),startDate.get(Calendar.MONTH),
//                startDate.get(Calendar.DAY_OF_MONTH), startDate.get(Calendar.HOUR_OF_DAY),
//                startDate.get(Calendar.MINUTE));

        endDate.add(Calendar.HOUR_OF_DAY,2);
        //setDefaultGender();
    }
    private void initChartColorMap(){
        mColorMap.put("all",0xFF4CAF50);
        mColorMap.put(MapRangData.genderTitle[0],0xFF3F51B5);
        mColorMap.put(MapRangData.genderTitle[1],0xFFFF4081);
        mColorMap.put(MapRangData.ageTitle[0],0xFFFFC107);
        mColorMap.put(MapRangData.ageTitle[1],0xFFE91E63);
        mColorMap.put(MapRangData.ageTitle[2],0xFF9C27B0);
        mColorMap.put(MapRangData.ageTitle[3],0xFF2196F3);
        mColorMap.put(MapRangData.ageTitle[4],0xFFFF9800);
        mColorMap.put(MapRangData.ageTitle[5],0xFF795548);
        mColorMap.put(MapRangData.ageTitle[6],0xFF3F51B5);
        mColorMap.put(MapRangData.ageTitle[7],0xFF9E9E9E);
    }

    public void setPieDatas(Map<String,Integer> dataMap,String[] names){
        ArrayList<PieData> newPieDatas = new ArrayList<>();
        int number=0;
        if(names.length ==1){
            int[] values = new int[]{dataMap.get(names[0]),0};
            String name =names[0];
            for(int value:values){
                newPieDatas.add(getPieDate(name,value));
            }
            number=values[0];
        }else{
            for(String name:names){
                if(dataMap.get(name) != null && dataMap.get(name)>0) {
                    int value = dataMap.get(name);
                    newPieDatas.add(getPieDate(name,value));
                    number = number + value;
                }
            }
        }
        this.pieDatas = newPieDatas;
        this.showCount=number;
    }

    private PieData getPieDate(String name,float value){
        PieData pieData = new PieData();
        pieData.setName(name);
        pieData.setValue(value);
        pieData.setColor(getColor(name));
        return pieData;
    }

    public int getColor(String name){
        return mColorMap.get(name);
    }
    public ArrayList<PieData> getPieDatas() {
        return pieDatas;
    }

    public int getChoiceType() {
        return choiceType;
    }

    public void setChoiceType(int choiceType) {
        this.choiceType = choiceType;
    }

    public ArrayList<String> getChoiceItems() {
        return choiceItems;
    }
    public Map<String,Integer> getChoiceItemMap(){
        Map<String,Integer> itemMap = new HashMap<>();
        for(String s:choiceItems){
            itemMap.put(s.toLowerCase(),0);
        }
        return itemMap;
    }
    public void setChoiceItems(ArrayList<String> choiceItems) {
        this.choiceItems = choiceItems;
    }

    public void setStartDate(int year,int month,int day) {
        startDate.set(year,month,day);
    }
    public void setStartTime(int hour,int minute) {
        startDate.set(Calendar.HOUR_OF_DAY,hour);
        startDate.set(Calendar.MINUTE,minute);
    }
    public Calendar getStartDate() {
        return startDate;
    }
    public Long getStartDataStamp(){
        return startDate.getTimeInMillis();
    }
    public String getStartDateStr(){
        return startDate.get(Calendar.YEAR)+"-"+(startDate.get(Calendar.MONTH)+1)+"-"+
                startDate.get(Calendar.DAY_OF_MONTH);
    }
    public String getStartTimeStr(){
        return startDate.get(Calendar.HOUR_OF_DAY)+":"+startDate.get(Calendar.MINUTE);
    }

    public void setEndDate(int year,int month,int day){
        endDate.set(year,month,day);
    }
    public void setEndTime(int hour,int minute) {
        endDate.set(Calendar.HOUR_OF_DAY,hour);
        endDate.set(Calendar.MINUTE,minute);
    }
    public Calendar getEndDate() {
        return endDate;
    }
    public Long getEndDataStamp(){
        return endDate.getTimeInMillis();
    }
    public String getEndDateStr(){
        return endDate.get(Calendar.YEAR)+"-"+(endDate.get(Calendar.MONTH)+1)+"-"+
                endDate.get(Calendar.DAY_OF_MONTH);
    }
    public String getEndTimeStr(){
        return endDate.get(Calendar.HOUR_OF_DAY)+":"+endDate.get(Calendar.MINUTE);
    }

    public int getShowCount() {
        return showCount;
    }

    public void setShowCount(int showCount) {
        this.showCount = showCount;
    }



    public int getLimitCost() {
        return limitCost;
    }

    public void setLimitCost(int limitCost) {
        this.limitCost = limitCost;
        this.limitPoint = this.limitCost *3 * 10; //unit 3 price and 10 point
    }

    public long getLimitPoint() {
        return limitPoint;
    }

    public void setLimitPoint(long limitPoint) {
        this.limitPoint = limitPoint;
    }

    public IconGenerator createIconGenerator(String name){
        IconGenerator iconGenerator = new IconGenerator(context);
        iconGenerator.setBackground(drawableMap.get(name));

        View v = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.data_range_map, null, false);
        //TextView countText=(TextView) v.findViewById(R.id.count_text);
        //countText.setLayoutParams(getLayoutParams(getGender(name)));
        //this.textValueMap.put(name,v.findViewById(R.id.count_value));
        this.viewMap.put(name,v);
        //this.textNameMap.put(name,name);
        iconGenerator.setContentView(v);
        return iconGenerator;
    }

    public int dpToPixel(int dp){
        int px =(int)(dp * context.getResources().getDisplayMetrics().density);
        return px;
    }
//    public void setDefaultGender(){
//        this.genderMap.put(genderTitle[0],1);
//        this.genderMap.put(genderTitle[1],1);
//        //setGenderView();
//    }
    public void setMapData(int male, int female, Map<String, Integer> ageMap){
        this.total = male + female;
        this.genderMap.put(genderTitle[0],male);
        this.genderMap.put(genderTitle[1],female);
        this.ageMap =ageMap;
        switch (this.getIconType()){
            case RANGE_TYPE_GENDER:
                setGenderView();
                break;
            case RANGE_TYPE_AGE:
                setAgeView();
                break;
        }
    }
    private void setAgeView(){
        //this.ageMap =ageMap;
        List<Map.Entry<String,Integer>> list = new ArrayList<>(((HashMap)this.ageMap).entrySet());
        Collections.sort(list,new Comparator<Map.Entry<String,Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }

        });
        int i=0;
        for(Map.Entry<String,Integer> valueMap:list){
            String name = valueMap.getKey().toString();
            int count =(int) valueMap.getValue();
            int[] p = getAgePoints(i);
            pointMap.put(name,new Point(p[0],p[1]));
            setTextViewLayout(name);
            i++;
        }
    }

    private void setGenderView(){
        int male= this.genderMap.get(genderTitle[0]);
        int female= this.genderMap.get(genderTitle[1]);
        if((male>0 && female>0) || (male == 0 && female ==0)){
            pointMap.put(genderTitle[0],new Point(mWidth/4,mHeight/2));
            pointMap.put(genderTitle[1],new Point(mWidth*3/4,mHeight/2));
            setTextViewLayout(genderTitle[0]);
            setTextViewLayout(genderTitle[1]);
        }
        if(male == 0 && female>0) {
            pointMap.put(genderTitle[1],new Point(mWidth/2,mHeight/2));
            setTextViewLayout(genderTitle[1]);
        }
        if (female ==0 && male >0) {
            pointMap.put(genderTitle[0], new Point(mWidth / 2, mHeight / 2));
            setTextViewLayout(genderTitle[0]);
        }
    }
    private void setTextViewLayout(String name){
        View v = this.viewMap.get(name);
        int count =0;
        String contStr=null;
        switch (iconType){
            case RANGE_TYPE_GENDER:
                count=getGender(name);
                contStr =getGenderString(name);
                break;
            case RANGE_TYPE_AGE:
                count=getAge(name);
                contStr =getAgeString(name);
                break;
        }
        v.findViewById(R.id.count_layout).setLayoutParams(getLayoutParams(count));
        ((TextView)v.findViewById(R.id.count_value)).setText(contStr);
        ((TextView)v.findViewById(R.id.count_name)).setText(name);
        //textView.setCompoundDrawables(iconMap.get(name),null,null,null);
        v.invalidate();
    }
    public String getGenderString(String genderName){
        return df.format(getGender(genderName));
    }
    public int getGender(String genderName){
        return genderMap.get(genderName);
    }
    public String getAgeString(String ageName){
        return df.format(getAge(ageName));
    }
    public int getAge(String ageName){
        if(ageMap.get(ageName) == null)
            return 0;
        else
            return ageMap.get(ageName);
    }
    public void setMarker(String name,Marker marker){
        if(this.markerMap.get(name) != null)
            this.markerMap.get(name).remove();
        markerMap.put(name,marker);
    }
    public void removeMarker(){
        for(Map.Entry entry:this.markerMap.entrySet()){
            if(entry.getKey() != null) {
                Marker marker = (Marker) entry.getKey();
                marker.remove();
            }
        }
    }
    public Marker getMarker(String name){
        return markerMap.get(name);
    }
    public void setMarkerOptions(String name,MarkerOptions markerOptions){
        markerOptionMap.put(name,markerOptions);
    }
    public MarkerOptions getMarkerOptions(String name){
        return markerOptionMap.get(name);
    }
    public void setIconGenerator(String name,IconGenerator iconGenerator){
        iconGeneratorMap.put(name,iconGenerator);
    }
    public IconGenerator getIconGenerator(String name){
        return iconGeneratorMap.get(name);
    }
    public void setDrawable(String name,Drawable drawable){
        drawableMap.put(name,drawable);
    }
    public Drawable getDrawable(String name){
        return drawableMap.get(name);
    }
    public void setPointRange(int w,int h){
        this.mWidth=w;
        this.mHeight=h;
        //setGenderView();
        //pointMap.put(genderTitle[0],new Point(mWidth/4,mHeight/2));
       // pointMap.put(genderTitle[1],new Point(mWidth*3/4,mWidth/2));
    }
    public Point getPoint(String name){
        return pointMap.get(name);
    }

    public boolean isChangeStatus() {
        return changeStatus;
    }

    public void setChangeStatus(boolean changeStatus) {
        this.changeStatus = changeStatus;
    }

    public int getIconType() {
        return iconType;
    }

    public void setIconType(int iconType) {
        this.iconType = iconType;
    }

    public RelativeLayout.LayoutParams getLayoutParams(int count){
        RelativeLayout.LayoutParams layoutParams=null;
        if(count>0 && count<=50){
            layoutParams= new RelativeLayout.LayoutParams(dpToPixel(90),dpToPixel(90));
        }else if(count>50 && count<=100){
            layoutParams= new RelativeLayout.LayoutParams(dpToPixel(100),dpToPixel(100));
        }else if(count>100 && count<=500) {
            layoutParams= new RelativeLayout.LayoutParams(dpToPixel(110),dpToPixel(110));
        }else if(count>500 && count<=1000) {
            layoutParams= new RelativeLayout.LayoutParams(dpToPixel(120),dpToPixel(120));
        }else if(count>1000) {
            layoutParams= new RelativeLayout.LayoutParams(dpToPixel(130),dpToPixel(130));
        }else{
            layoutParams= new RelativeLayout.LayoutParams(0,0);
        }
        return layoutParams;
    }
    private int[] getAgePoints(int index){
        int[] points= new int[]{0,0};
        switch (index){
            case 0:
                points[0]=mWidth/2;points[1]=mHeight/2;
                break;
            case 1:
                points[0]=mWidth/4;points[1]=mHeight/2;
                break;
            case 2:
                points[0]=mWidth*3/4;points[1]=mHeight/2;
                break;
            case 3:
                points[0]=mWidth/2;points[1]=mHeight/4;
                break;
            case 4:
                points[0]=mWidth/2;points[1]=mHeight*3/4;
                break;
            case 5:
                points[0]=mWidth*3/4;points[1]=mHeight/4;
                break;
            case 6:
                points[0]=mWidth/4;points[1]=mHeight*3/4;
                break;
            case 7:
                points[0]=mWidth/4;points[1]=mHeight/4;
                break;
            case 8:
                points[0]=mWidth*3/4;points[1]=mHeight*3/4;
                break;
        }
        return points;
    }

    public String getFlyerId() {
        return flyerId;
    }

    public void setFlyerId(String flyerId) {
        this.flyerId = flyerId;
    }

//    public String getFlyerName() {
//        return flyerName;
//    }
//
//    public void setFlyerName(String flyerName) {
//        this.flyerName = flyerName;
//    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public LatLng getCenter() {
        return center;
    }
    public String getCenterStr(){return center.latitude+","+center.longitude;}
    public void setCenter(LatLng center) {
        this.center = center;
    }

    public int getTotal() {
        return total;
    }

    public Map<String, Integer> getAgeMap() {
        return ageMap;
    }

    public Map<String, Integer> getGenderMap() {
        return genderMap;
    }

    public String getLocationGeoCode() {
        return locationGeoCode;
    }

    public void setLocationGeoCode(String locationGeoCode) {
        this.locationGeoCode = locationGeoCode;
    }

    public String getCenterAddress() {
        return centerAddress;
    }

    public void setCenterAddress(String centerAddress) {
        this.centerAddress = centerAddress;
    }

/*    public Map<String, CouponViewData> getCouponViewDataMap() {
        return couponViewDataMap;
    }

    public void setCouponViewDataMap(Map<String, CouponViewData> couponViewDataMap) {
        this.couponViewDataMap = couponViewDataMap;
    }*/

/*    public Flyer getFlyer() {
        return flyer;
    }

    public void setFlyer(Flyer flyer) {
        this.flyer = flyer;
        if(flyer != null)
            convertSegmentMapToCouponViewDataMap(flyer.getCoupons());
    }*/

/*    private void convertSegmentMapToCouponViewDataMap(Map<String,Segment> segmentMap){
        if(segmentMap != null && segmentMap.size()>0){
            if(couponViewDataMap == null)
                couponViewDataMap = new HashMap<>();
            for(Map.Entry entry:segmentMap.entrySet()){
                String segmentKey = entry.getKey().toString();
                Segment segment =(Segment)entry.getValue();
                couponViewDataMap.put(segmentKey,new CouponViewData(segmentKey,segment));
            }

        }
    }*/
    //localUriMap :segmentKey,localUri | newCouponKey :segmentKey,newCouponKey
/*    public void setCouponViewFormTemple(Map<String,Uri> localUriMap,
                                 Map<String,String> newCouponKeyMap,
                                 CouponViewData couponViewTemple){
        for(Map.Entry entry:localUriMap.entrySet()){
            String segmentKey = entry.getKey().toString();
            Uri uri = (Uri)entry.getValue();
            String newCouponKey = newCouponKeyMap.get(segmentKey);
            couponViewDataMap.get(segmentKey).setLocalFile(uri);
            couponViewDataMap.get(segmentKey).setQrCodeKey(newCouponKey);
            couponViewDataMap.get(segmentKey).setTitle(couponViewTemple.getTitle());
            couponViewDataMap.get(segmentKey).setCaption(couponViewTemple.getCaption());
            couponViewDataMap.get(segmentKey).setPrice(couponViewTemple.getPrice());
            couponViewDataMap.get(segmentKey).setDiscount(couponViewTemple.getDiscount());
            couponViewDataMap.get(segmentKey).setSDate(couponViewTemple.getSDate());
            couponViewDataMap.get(segmentKey).setEDate(couponViewTemple.getEDate());
        }
        setCouponViewTemple(couponViewTemple);
    }

    public CouponViewData getCouponViewTemple() {
        return couponViewTemple;
    }

    public void setCouponViewTemple(CouponViewData couponViewTemple) {
        this.couponViewTemple = couponViewTemple;
    }*/

    public long getPointBalance() {
        return mPointBalance;
    }

    public void setPointBalance(long pointBalance) {
        this.mPointBalance = pointBalance;
    }

    //-------------------------------------------------------------------------------------------------
  /*  public static class CouponViewData implements Serializable{
        public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        private String qrCodeKey;  //newCouponKey
        //private Bitmap qrCodeImage;
        private long sDate;
        private long eDate;
        private String title;
        private long price;
        private long discount;
        private String caption;
        private Uri localFile;
        private String webLink;
        private String webFileId;
        private Segment segment;
        private String segmentKey;

        public CouponViewData() {
        }

        public CouponViewData(String segmentKey, Segment segment) {
            this.segmentKey =segmentKey;
            this.segment = segment;
            converSegmnetElement(this.segment);
        }
        private void converSegmnetElement(Segment segment){
            this.caption = segment.getElementString("coupon_card_caption");
            this.title = segment.getElementString("coupon_card_title");
            this.sDate=Long.parseLong(segment.getElementString("coupon_card_sdate"));
            this.eDate=Long.parseLong(segment.getElementString("coupon_card_edate"));
            this.price = segment.getElementLong("coupon_card_price");
            this.discount =segment.getElementLong("coupon_card_discount");
        }
        private String covertLongTime(long longTime){
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(longTime);
            return sdf.format(c.getTime());
        }
        private long convertDateString(String dateString){
            try {
                Date date= sdf.parse(dateString);
                return date.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }
        public void setValue(String fieldName,String value){
            if(fieldName.equals("title"))
                this.title = value;
            if(fieldName.equals("caption"))
                this.caption = value;
            if(fieldName.equals("price"))
                setPrice(value);
            if(fieldName.equals("discount"))
                setDiscount(value);
            if(fieldName.equals("sdate"))
                setSDate(value);
            if(fieldName.equals("edate"))
                setEDate(value);
            if(fieldName.equals("qrCodeKey"))
                this.qrCodeKey = value;
            if(fieldName.equals("localFile"))
                this.localFile = Uri.parse(value);
            if(fieldName.equals("webLink"))
                this.webLink = value;
            if(fieldName.equals("webFileId"))
                this.webFileId = value;
        }
        public String getQrCodeKey() {
            return qrCodeKey;
        }

        public void setQrCodeKey(String qrCodeKey) {
            this.qrCodeKey = qrCodeKey;
        }

//        public Bitmap getQrCodeImage() {
//            return qrCodeImage;
//        }
//
//        public void setQrCodeImage(Bitmap qrCodeImage) {
//            this.qrCodeImage = qrCodeImage;
//        }

        public Segment getSegment() {
            return segment;
        }

        public void setSegment(Segment segment) {
            this.segment = segment;
        }

        public String getSegmentKey() {
            return segmentKey;
        }

        public void setSegmentKey(String segmentKey) {
            this.segmentKey = segmentKey;
        }

        public long getSDate() {
            return sDate;
        }

        public void setSDate(long sDate) {
            this.sDate = sDate;
        }
        public void setSDate(String sDateString){
            setSDate(convertDateString(sDateString));
        }
        public long getEDate() {
            return eDate;
        }

        public void setEDate(long eDate) {
            this.eDate = eDate;
        }
        public void setEDate(String eDateString){
            setEDate(convertDateString(eDateString));
        }
        public String getSDateString(){
            return covertLongTime(this.sDate);
        }
        public String getEDateString(){
            return covertLongTime(this.eDate);
        }
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public long getPrice() {
            return price;
        }
        public String getPriceFormat() {
            return df.format(this.price);
        }

        public void setPrice(long price) {
            this.price = price;
        }
        public void setPrice(String priceFomat) {
            try {
                this.price = (long)df.parse(priceFomat);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        public long getDiscount() {
            return discount;
        }
        public String getDiscountFormat() {
            return df.format(this.discount);
        }

        public void setDiscount(long discount) {
            this.discount = discount;
        }
        public void setDiscount(String discountFomat) {
            try {
                this.discount = (long)df.parse(discountFomat);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        public String getCaption() {
            return caption;
        }

        public void setCaption(String caption) {
            this.caption = caption;
        }

        public Uri getLocalFile() {
            return localFile;
        }

        public void setLocalFile(Uri localFile) {
            this.localFile = localFile;
        }

        public String getWebLink() {
            return webLink;
        }

        public void setWebLink(String webLink) {
            this.webLink = webLink;
        }

        public String getWebFileId() {
            return webFileId;
        }

        public void setWebFileId(String webFileId) {
            this.webFileId = webFileId;
        }
    }*/
}
