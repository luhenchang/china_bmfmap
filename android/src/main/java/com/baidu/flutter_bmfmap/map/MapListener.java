package com.baidu.flutter_bmfmap.map;

import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.flutter_bmfmap.utils.Constants;
import com.baidu.flutter_bmfmap.utils.Env;
import android.os.Message;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapBaseIndoorMapInfo;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Handler;
import javax.microedition.khronos.opengles.GL10;

import io.flutter.plugin.common.MethodChannel;
import com.baidu.flutter_bmfmap.utils.ThreadPoolUtil;
import com.baidu.mapapi.model.LatLngBounds;


@SuppressWarnings("unchecked")
public class MapListener implements BaiduMap.OnMapClickListener ,BaiduMap.OnMapLoadedCallback,
        BaiduMap.OnMapStatusChangeListener ,BaiduMap.OnMapRenderCallback,BaiduMap.OnMapDrawFrameCallback,
        BaiduMap.OnBaseIndoorMapListener ,BaiduMap.OnMarkerClickListener,BaiduMap.OnPolylineClickListener,
        BaiduMap.OnMapDoubleClickListener,BaiduMap.OnMapLongClickListener,BaiduMap.OnMarkerDragListener,
        BaiduMap.OnMapRenderValidDataListener,BaiduMap.OnMyLocationClickListener {

    private static final int DRAW_FRAME_MESSAGE = 0;
    private static final String TAG = "MapListener";
    private BaiduMap mBaiduMap;
    private MethodChannel mMethodChannel;
    private int mReason;
    private HashMap<String, HashMap> mStatusMap;

    private final Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == DRAW_FRAME_MESSAGE) {
                if (null != mStatusMap){
                    mMethodChannel.invokeMethod(
                            Constants.MethodProtocol.MapStateProtocol.sMapOnDrawMapFrameCallback,mStatusMap);
                }
            }
        }
    };


    public MapListener(FlutterCommonMapView mapView, MethodChannel methodChannel) {
        this.mMethodChannel = methodChannel;

        if (null == mapView) {
            return;
        }
        mBaiduMap = mapView.getBaiduMap();
        initListener();
    }

    private void initListener() {
        if (null == mBaiduMap) {
            return;
        }
        mBaiduMap.setOnMapClickListener(this);
        mBaiduMap.setOnMapLoadedCallback(this);
        mBaiduMap.setOnMapStatusChangeListener(this);
        mBaiduMap.setOnMapDrawFrameCallback(this);
        mBaiduMap.setOnMapRenderCallbadk(this);
        mBaiduMap.setOnBaseIndoorMapListener(this);
        mBaiduMap.setOnMarkerClickListener(this);
        mBaiduMap.setOnPolylineClickListener(this);
        mBaiduMap.setOnMapDoubleClickListener(this);
        mBaiduMap.setOnMapLongClickListener(this);
        mBaiduMap.setOnMarkerDragListener(this);
        mBaiduMap.setOnMapRenderValidDataListener(this);
        mBaiduMap.setOnMyLocationClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (null == latLng || mMethodChannel == null) {
            return;
        }
        HashMap<String, HashMap> coordinateMap = new HashMap<>();
        HashMap<String, Double> coord = new HashMap<>();
        coord.put("latitude",latLng.latitude);
        coord.put("longitude",latLng.longitude);
        coordinateMap.put("coord",coord);
        mMethodChannel.invokeMethod(Constants.MethodProtocol.MapStateProtocol.sMapOnClickedMapBlankCallback,coordinateMap);
    }

    @Override
    public void onMapPoiClick(MapPoi mapPoi) {
        if (null == mapPoi || mMethodChannel == null) {
            return;
        }
        HashMap<String, Double> pt = new HashMap<>();
        LatLng position = mapPoi.getPosition();
        if (null != position) {
            pt.put("latitude",mapPoi.getPosition().latitude);
            pt.put("longitude",mapPoi.getPosition().longitude);
        }
        HashMap<String, HashMap> poiMap = new HashMap<>();
        HashMap poi = new HashMap();
        poi.put("text",mapPoi.getName());
        poi.put("uid",mapPoi.getUid());
        poi.put("pt",pt);
        poiMap.put("poi",poi);
        mMethodChannel.invokeMethod(Constants.MethodProtocol.MapStateProtocol.sMapOnClickedMapPoiCallback,poiMap);
    }

    @Override
    public void onMapLoaded() {
        mMethodChannel.invokeMethod(Constants.MethodProtocol.MapStateProtocol.sMapDidLoadCallback,"");
    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {
        if (null == mapStatus || mMethodChannel == null) {
            return;
        }
        HashMap<String, Double> targetScreenMap = new HashMap<>();
        Point targetScreen = mapStatus.targetScreen;
        if (null == targetScreen) {
            return;
        }
        targetScreenMap.put("x", (double) targetScreen.x);
        targetScreenMap.put("y", (double) targetScreen.y);
        HashMap<String, Double> targetMap = new HashMap<>();
        LatLng latLng = mapStatus.target;
        if (null == latLng){
            return;
        }
        targetMap.put("latitude", latLng.latitude);
        targetMap.put("longitude", latLng.longitude);

        LatLngBounds bound = mapStatus.bound;
        if (null == bound) {
            return;
        }
        HashMap latLngBoundMap = latLngBounds(bound);
        if (null == latLngBoundMap) {
            return;
        }
        HashMap statusMap = new HashMap<>();
        HashMap status = new HashMap();
        status.put("fLevel",((double)mapStatus.zoom));
        double rotate = mapStatus.rotate;
        if (rotate > 180) {
            rotate = rotate - 360;
        }
        status.put("fRotation", rotate);
        status.put("fOverlooking",((double) mapStatus.overlook));
        status.put("targetScreenPt",targetScreenMap);
        status.put("targetGeoPt",targetMap);
        status.put("visibleMapBounds",latLngBoundMap);
        statusMap.put("mapStatus",status);
        mMethodChannel.invokeMethod(Constants.MethodProtocol.MapStateProtocol.sMapRegionWillChangeCallback,statusMap);
    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus, int reason) {
        if (null == mapStatus || mMethodChannel == null) {
            return;
        }
        mReason = reason;
        HashMap<String, Double> targetScreenMap = new HashMap<>();
        Point targetScreen = mapStatus.targetScreen;
        if (null == targetScreen) {
            return;
        }
        targetScreenMap.put("x", (double) targetScreen.x);
        targetScreenMap.put("y", (double) targetScreen.y);
        HashMap<String, Double> targetMap = new HashMap<>();
        LatLng latLng = mapStatus.target;
        if (null == latLng){
            return;
        }
        targetMap.put("latitude", latLng.latitude);
        targetMap.put("longitude", latLng.longitude);

        LatLngBounds bound = mapStatus.bound;
        if (null == bound) {
            return;
        }
        HashMap latLngBoundMap = latLngBounds(bound);
        if (null == latLngBoundMap) {
            return;
        }
        HashMap statusMap = new HashMap<>();
        HashMap status = new HashMap();
        status.put("fLevel",((double)mapStatus.zoom));
        double rotate = mapStatus.rotate;
        if (rotate > 180) {
            rotate = rotate - 360;
        }
        status.put("fRotation", rotate);
        status.put("fOverlooking",((double) mapStatus.overlook));
        status.put("targetScreenPt",targetScreenMap);
        status.put("targetGeoPt",targetMap);
        status.put("visibleMapBounds",latLngBoundMap);
        statusMap.put("mapStatus",status);
        statusMap.put("reason",mReason);
        mMethodChannel.invokeMethod(Constants.MethodProtocol.MapStateProtocol.
                sMapRegionWillChangeWithReasonCallback,statusMap);
    }

    @Override
    public void onMapStatusChange(MapStatus mapStatus) {
        if (null == mapStatus || mMethodChannel == null) {
            return;
        }
        HashMap<String, Double> targetScreenMap = new HashMap<>();
        Point targetScreen = mapStatus.targetScreen;
        if (null == targetScreen) {
            return;
        }
        targetScreenMap.put("x", (double) targetScreen.x);
        targetScreenMap.put("y", (double) targetScreen.y);
        HashMap<String, Double> targetMap = new HashMap<>();
        LatLng latLng = mapStatus.target;
        if (null == latLng){
            return;
        }
        targetMap.put("latitude", latLng.latitude);
        targetMap.put("longitude", latLng.longitude);

        LatLngBounds bound = mapStatus.bound;
        if (null == bound) {
            return;
        }
        HashMap latLngBoundMap = latLngBounds(bound);
        if (null == latLngBoundMap) {
            return;
        }
        HashMap statusMap = new HashMap<>();
        HashMap status = new HashMap();
        status.put("fLevel",((double)mapStatus.zoom));
        double rotate = mapStatus.rotate;
        if (rotate > 180) {
            rotate = rotate - 360;
        }
        status.put("fRotation", rotate);
        status.put("fOverlooking",((double) mapStatus.overlook));
        status.put("targetScreenPt",targetScreenMap);
        status.put("targetGeoPt",targetMap);
        status.put("visibleMapBounds",latLngBoundMap);
        statusMap.put("mapStatus",status);

        mMethodChannel.invokeMethod(
                Constants.MethodProtocol.MapStateProtocol.sMapRegionDidChangeCallback,statusMap);
    }

    @Override
    public void onMapStatusChangeFinish(MapStatus mapStatus) {
        if (null == mapStatus || mMethodChannel == null) {
            return;
        }
        HashMap<String, Double> targetScreenMap = new HashMap<>();
        Point targetScreen = mapStatus.targetScreen;
        if (null == targetScreen) {
            return;
        }
        targetScreenMap.put("x", (double) targetScreen.x);
        targetScreenMap.put("y", (double) targetScreen.y);
        HashMap<String, Double> targetMap = new HashMap<>();
        LatLng latLng = mapStatus.target;
        if (null == latLng){
            return;
        }
        targetMap.put("latitude", latLng.latitude);
        targetMap.put("longitude", latLng.longitude);

        LatLngBounds bound = mapStatus.bound;
        if (null == bound) {
            return;
        }
        HashMap latLngBoundMap = latLngBounds(bound);
        if (null == latLngBoundMap) {
            return;
        }
        HashMap statusMap = new HashMap<>();
        HashMap status = new HashMap();
        status.put("fLevel",((double)mapStatus.zoom));
        double rotate = mapStatus.rotate;
        if (rotate > 180) {
            rotate = rotate - 360;
        }
        status.put("fRotation", rotate);
        status.put("fOverlooking",((double) mapStatus.overlook));
        status.put("targetScreenPt",targetScreenMap);
        status.put("targetGeoPt",targetMap);
        status.put("visibleMapBounds",latLngBoundMap);
        statusMap.put("mapStatus",status);
        statusMap.put("reason",mReason);
        mMethodChannel.invokeMethod(Constants.MethodProtocol.MapStateProtocol.sMapRegionDidChangeWithReasonCallback,statusMap);
        mMethodChannel.invokeMethod(Constants.MethodProtocol.MapStateProtocol.sMapStatusDidChangedCallback,"");
    }

    @Override
    public void onMapRenderFinished() {
        HashMap hashMap = new HashMap();
        hashMap.put("success",true);
        mMethodChannel.invokeMethod(Constants.MethodProtocol.MapStateProtocol.sMapDidFinishRenderCallback,hashMap);
    }

    @Override
    public void onMapDrawFrame(GL10 gl10, MapStatus mapStatus) {

    }

    @Override
    public void onMapDrawFrame(MapStatus mapStatus) {
        if (null == mapStatus || mMethodChannel == null) {
            return;
        }
        HashMap<String, Double> targetScreenMap = new HashMap<>();
        Point targetScreen = mapStatus.targetScreen;
        if (null == targetScreen) {
            return;
        }
        targetScreenMap.put("x", (double) targetScreen.x);
        targetScreenMap.put("y", (double) targetScreen.y);
        HashMap<String, Double> targetMap = new HashMap<>();
        LatLng latLng = mapStatus.target;
        if (null == latLng){
          return;
        }
        targetMap.put("latitude", latLng.latitude);
        targetMap.put("longitude", latLng.longitude);

        LatLngBounds bound = mapStatus.bound;
        if (null == bound) {
            return;
        }

        HashMap latLngBoundMap = latLngBounds(bound);
        if (null == latLngBoundMap) {
            return;
        }
        mStatusMap = new HashMap<>();
        HashMap status = new HashMap();
        status.put("fLevel",((double)mapStatus.zoom));
        double rotate = mapStatus.rotate;
        if (rotate > 180) {
            rotate = rotate - 360;
        }
        status.put("fRotation", rotate);
        status.put("fOverlooking",((double) mapStatus.overlook));
        status.put("targetScreenPt",targetScreenMap);
        status.put("targetGeoPt",targetMap);
        status.put("visibleMapBounds",latLngBoundMap);
        mStatusMap.put("mapStatus",status);

        ThreadPoolUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.arg1 = DRAW_FRAME_MESSAGE;
                mHandler.sendMessage(msg);
            }
        });

    }

    @Override
    public void onBaseIndoorMapMode(boolean isIndoorMap, MapBaseIndoorMapInfo mapBaseIndoorMapInfo) {
        if (mMethodChannel == null) {
            return;
        }
        HashMap indoorHashMap = new HashMap();
        indoorHashMap.put("flag",isIndoorMap);
        HashMap indoorMap = new HashMap();
        if (isIndoorMap) {
            if (null == mapBaseIndoorMapInfo) {
                return;
            }
            String curFloor = mapBaseIndoorMapInfo.getCurFloor();
            String id = mapBaseIndoorMapInfo.getID();
            ArrayList<String> floors = mapBaseIndoorMapInfo.getFloors();
            indoorMap.put("strFloor", curFloor);
            indoorMap.put("strID", id);
            indoorMap.put("listStrFloors", floors);
        }
        indoorHashMap.put("info",indoorMap);
        mMethodChannel.invokeMethod(Constants.MethodProtocol.MapStateProtocol.sMapInOrOutBaseIndoorMapCallback
                ,indoorHashMap);
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        if(Env.DEBUG){
            Log.d(TAG, "onMarkerClick");
        }
        if(null == mMethodChannel){
            return false;
        }

        Bundle bundle = marker.getExtraInfo();
        if(null == bundle){
            if(Env.DEBUG){
                Log.d(TAG, "bundle is null");
            }
            return false;
        }

        String id = bundle.getString("id");
        if(TextUtils.isEmpty(id)){
            if(Env.DEBUG){
                Log.d(TAG, "marker id is null ");
            }
            return false;
        }


        Map<String, Object> clickMap = new HashMap<>();
        clickMap.put("id", id);

        mMethodChannel.invokeMethod(Constants.MethodProtocol.MarkerProtocol.sMapClickedmarkedMethod, clickMap);

        return true;
    }

    @Override
    public boolean onPolylineClick(Polyline polyline) {

        Log.d("polyline", "polyline click");

        HashMap hashMap = polylineClick(polyline);
        HashMap<String, Object> polyLineMap = new HashMap<>();
        polyLineMap.put("polyline", hashMap);
        mMethodChannel.invokeMethod(Constants.MethodProtocol.PolylineProtocol.sMapOnClickedOverlayCallback, polyLineMap);

        return true;
    }

    private HashMap polylineClick(Polyline polyline) {
        if (null == polyline) {
            return null;
        }

        Bundle bundle = polyline.getExtraInfo();
        String id = bundle.getString("id");

        HashMap polylineMap = new HashMap();

        List<LatLng> points = polyline.getPoints();
        List<Object> latlngLists = new ArrayList<>();
        if (null != points){
            for (int i = 0; i < points.size(); i++) {
                HashMap<String, Double> latlngHashMap = new HashMap<>();
                latlngHashMap.put("latitude",points.get(i).latitude);
                latlngHashMap.put("longitude",points.get(i).longitude);
                latlngLists.add(latlngHashMap);
            }
        }
        polylineMap.put("id", id);
        polylineMap.put("coordinates",latlngLists);

        ArrayList<String> colorList = new ArrayList<>();
        int[] colors = polyline.getColorList();
        if(null != colors){
            for(int i = 0; i < colors.length; i++){
                colorList.add(Integer.toHexString(colors[i]));
            }
        }


        polylineMap.put("colors", colorList);

        polylineMap.put("color", polyline.getColor());
        polylineMap.put("lineDashType", polyline.getDottedLineType());
        polylineMap.put("lineCapType", 0);
        polylineMap.put("lineJoinType", 0);
        polylineMap.put("width", polyline.getWidth());
        polylineMap.put("zIndex", polyline.getZIndex());

        return polylineMap;
    }

    @Override
    public void onMapDoubleClick(LatLng latLng) {
        if (null == latLng || mMethodChannel == null) {
            return;
        }
        HashMap<String, HashMap> coordinateMap = new HashMap<>();
        HashMap<String, Double> coord = new HashMap<>();
        coord.put("latitude",latLng.latitude);
        coord.put("longitude",latLng.longitude);
        coordinateMap.put("coord",coord);
        mMethodChannel.invokeMethod(Constants.MethodProtocol.MapStateProtocol.sMapOnDoubleClickCallback,coordinateMap);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (null == latLng || mMethodChannel == null) {
            return;
        }
        HashMap<String, HashMap> coordinateMap = new HashMap<>();
        HashMap<String, Double> coord = new HashMap<>();
        coord.put("latitude",latLng.latitude);
        coord.put("longitude",latLng.longitude);
        coordinateMap.put("coord",coord);
        mMethodChannel.invokeMethod(Constants.MethodProtocol.MapStateProtocol.sMapOnLongClickCallback,coordinateMap);
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        if(Env.DEBUG){
            Log.d(TAG, "onMarkerDrag");
        }
        if(null == mMethodChannel){
            return;
        }

        Bundle bundle = marker.getExtraInfo();
        if(null == bundle){
            return;
        }

        String id = bundle.getString("id");

        if(null == mMethodChannel){
            return;
        }

        if(TextUtils.isEmpty(id)){
            if(Env.DEBUG){
                Log.d(TAG, "id is null");
            }
            return;
        }

        if(TextUtils.isEmpty(id)){
            if(Env.DEBUG){
                Log.d(TAG, "id is null");
            }
            return;
        }


        Map<String, Object> dragMap = new HashMap<>();
        dragMap.put("id", id);
        Map<String, Object> extraInfoMap = new HashMap<>();
        extraInfoMap.put("state", Constants.MethodProtocol.MarkerProtocol.MarkerDragState.sDragging);
        dragMap.put("extra", extraInfoMap);

        mMethodChannel.invokeMethod(Constants.MethodProtocol.MarkerProtocol.sMapDragMarkerMethod, dragMap);
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        if(Env.DEBUG){
            Log.d(TAG, "onMarkerDrag");
        }
        if(null == mMethodChannel){
            return;
        }

        Bundle bundle = marker.getExtraInfo();
        if(null == bundle){
            return;
        }

        String id = bundle.getString("id");

        if(null == mMethodChannel){
            return;
        }

        if(TextUtils.isEmpty(id)){
            if(Env.DEBUG){
                Log.d(TAG, "id is null");
            }
            return;
        }

        LatLng center = marker.getPosition();
        if(null == center){
            return;
        }

        Map<String, Object> dragMap = new HashMap<>();
        dragMap.put("id", id);

        Map<String, Double> centerMap = new HashMap<>();
        centerMap.put("latitude", center.latitude);
        centerMap.put("longitude", center.longitude);

        Map<String, Object> extraInfoMap = new HashMap<>();
        extraInfoMap.put("center", centerMap);
        extraInfoMap.put("state", Constants.MethodProtocol.MarkerProtocol.MarkerDragState.sDragEnd);
        dragMap.put("extra", extraInfoMap);

        mMethodChannel.invokeMethod(Constants.MethodProtocol.MarkerProtocol.sMapDragMarkerMethod, dragMap);
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        if(Env.DEBUG){
            Log.d(TAG, "onMarkerDrag");
        }

        Bundle bundle = marker.getExtraInfo();
        if(null == bundle){
            return;
        }

        String id = bundle.getString("id");

        if(null == mMethodChannel){
            return;
        }

        if(TextUtils.isEmpty(id)){
            if(Env.DEBUG){
                Log.d(TAG, "id is null");
            }
            return;
        }

        LatLng center = marker.getPosition();
        if(null == center){
            return;
        }

        Map<String, Object> dragMap = new HashMap<>();
        dragMap.put("id", id);

        Map<String, Double> centerMap = new HashMap<>();
        centerMap.put("latitude", center.latitude);
        centerMap.put("longitude", center.longitude);

        Map<String, Object> extraInfoMap = new HashMap<>();
        extraInfoMap.put("center", centerMap);
        extraInfoMap.put("state", Constants.MethodProtocol.MarkerProtocol.MarkerDragState.sDragStart);
        dragMap.put("extra", extraInfoMap);

        mMethodChannel.invokeMethod(Constants.MethodProtocol.MarkerProtocol.sMapDragMarkerMethod, dragMap);
    }

    @Override
    public void onMapRenderValidData(boolean isValid, int errorCode, String errorMessage) {
        HashMap hashMap = new HashMap();
        hashMap.put("isValid",isValid);
        hashMap.put("errorCode",errorCode);
        hashMap.put("errorMessage",errorMessage);
        mMethodChannel.invokeMethod(Constants.MethodProtocol.MapStateProtocol.sMapRenderValidDataCallback,hashMap);
    }


    @Override
    public boolean onMyLocationClick() {
        return false;
    }

    private HashMap latLngBounds(LatLngBounds latLngBounds) {
        if (null == latLngBounds) {
            return null;
        }
        // 该地理范围东北坐标
        LatLng northeast = latLngBounds.northeast;
        // 该地理范围西南坐标
        LatLng southwest = latLngBounds.southwest;

        HashMap boundsMap = new HashMap();
        HashMap northeastMap = new HashMap<String,Double>();
        if (null == northeast){
           return null;
        }
        northeastMap.put("latitude", northeast.latitude);
        northeastMap.put("longitude",northeast.longitude);
        HashMap southwestMap = new HashMap<String,Double>();
        if (null == southwest) {
            return null;
        }
        southwestMap.put("latitude",southwest.latitude);
        southwestMap.put("longitude", southwest.longitude);
        boundsMap.put("northeast",northeastMap);
        boundsMap.put("southwest",southwestMap);
        return boundsMap;
    }
}

