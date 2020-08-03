package com.baidu.flutter_bmfmap.map.mapHandler;

import android.content.Context;

import com.baidu.flutter_bmfmap.map.FlutterCommonMapView;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public abstract class BMapHandler{
    protected FlutterCommonMapView mMapView;

    public BMapHandler(FlutterCommonMapView mapView){
        this.mMapView = mapView;
    }

    public abstract void handlerMethodCallResult(Context context,MethodCall call, MethodChannel.Result result);

    public void updateMapView(FlutterCommonMapView mapView){
        mMapView = mapView;
    }

    public void clean(){}
}
