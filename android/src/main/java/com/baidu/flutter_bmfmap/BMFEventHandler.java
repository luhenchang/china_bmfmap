package com.baidu.flutter_bmfmap;

import android.content.Context;

import com.baidu.mapapi.map.MapView;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodChannel;

public class BMFEventHandler<ViewType> implements EventChannel.StreamHandler {

    private Context mContext;

    private ViewType mMapView;

    private BinaryMessenger mMessager;

    private MethodChannel mMethodChannel;
    private EventChannel mEventChannel;

    public BMFEventHandler(Context context, ViewType mapView, MethodChannel methodChannel, EventChannel eventChannel){
        mContext = context;
        mMapView = mapView;
        mMethodChannel = methodChannel;
        mEventChannel = eventChannel;
    }

    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {

    }

    @Override
    public void onCancel(Object arguments) {

    }
}
