package com.baidu.flutter_bmfmap;

import android.content.Context;

import com.baidu.flutter_bmfmap.map.FlutterCommonMapView;

import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodChannel;

public class BMFHandlerHelper <ViewType>{

    private MethodChannel mMethodChannel;

    private BMFMethodHandler mBMFMethodHandler;

    private EventChannel mEventChannel;

    private BMFEventHandler mBMFEventHandler;

    public  BMFHandlerHelper(Context context
                           , FlutterCommonMapView mapView
                           , MethodChannel methodChannel
                           , EventChannel eventChannel){
          init(context, mapView, methodChannel, eventChannel);
    }

    private void init(Context context, FlutterCommonMapView mapView, MethodChannel methodChannel, EventChannel eventChannel){
        mMethodChannel = methodChannel;
        mBMFMethodHandler = new BMFMethodHandler(context, mapView, methodChannel, eventChannel);
        mMethodChannel.setMethodCallHandler(mBMFMethodHandler);

        mEventChannel = eventChannel;
        mBMFEventHandler = new BMFEventHandler(context, mapView, methodChannel, eventChannel);
        mEventChannel.setStreamHandler(mBMFEventHandler);
    }
}
