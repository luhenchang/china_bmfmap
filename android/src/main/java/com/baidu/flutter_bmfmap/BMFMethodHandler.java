package com.baidu.flutter_bmfmap;

import android.content.Context;
import android.util.Log;

import com.baidu.flutter_bmfmap.map.mapHandler.BMapHandlerFactory;
import com.baidu.flutter_bmfmap.map.FlutterCommonMapView;
import com.baidu.flutter_bmfmap.map.overlayHandler.OverlayHandlerFactory;
import com.baidu.flutter_bmfmap.utils.Env;
import com.baidu.mapapi.map.BaiduMap;

import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class BMFMethodHandler implements MethodChannel.MethodCallHandler {
    private static final String TAG = "BMFMethodHandler";

    private Context mContext;
    private FlutterCommonMapView mMapView;
    private final BaiduMap mBaiduMap;
    private MethodChannel mMethodChannel;
    private EventChannel mEventChannel;

    public BMFMethodHandler(Context context
                            ,FlutterCommonMapView mapView
                            ,MethodChannel methodChannel
                            ,EventChannel eventChannel){
        mContext = context;
        mMapView = mapView;
        mBaiduMap = mapView.getBaiduMap();
        mMethodChannel = methodChannel;
        mEventChannel = eventChannel;
    }



    @Override
    public void onMethodCall(MethodCall call, MethodChannel.Result result) {
        if(Env.DEBUG){
            Log.d(TAG,"onMethodCall enter");
        }

        if(null == call || null == result){
            Log.d(TAG,"null == call || null == result");
            return;
        }

        if (null == mMapView || null == mBaiduMap) {
            Log.d(TAG,"mMapView == call || mBaiduMap == result");
            return;
        }

        boolean ret = OverlayHandlerFactory.getInstance(mBaiduMap).dispatchMethodHandler(call,
                result);

        if (!ret) {
            BMapHandlerFactory.getInstance(mMapView).dispatchMethodHandler(mContext,call,
                    result, mMethodChannel);
        }
    }
}
