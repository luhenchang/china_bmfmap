package com.baidu.flutter_bmfmap.map.overlayHandler;

import java.util.HashMap;
import java.util.Map;

import com.baidu.flutter_bmfmap.utils.Env;
import com.baidu.flutter_bmfmap.utils.converter.FlutterDataConveter;
import com.baidu.flutter_bmfmap.utils.converter.TypeConverter;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.DotOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.model.LatLng;

import android.text.TextUtils;
import android.util.Log;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class DotHandler extends OverlayHandler {

    public static final String TAG = "DotHandler";

    public DotHandler(BaiduMap baiduMap) {
        super(baiduMap);
    }

    @Override
    public Map<String, Overlay> handlerMethodCall(MethodCall call, MethodChannel.Result result) {
        Map<String, Object> argument = call.arguments();
        if (null == argument) {
            if (Env.DEBUG) {
                Log.d(TAG, "argument is null");
            }
            return null;
        }

        if (!argument.containsKey("id")) {
            if (Env.DEBUG) {
                Log.d(TAG, "argument does not contain" + argument.toString());

            }
            return null;
        }

        final String id = new TypeConverter<String>().getValue(argument, "id");
        if (TextUtils.isEmpty(id)) {
            return null;
        }

        DotOptions dotOptions = new DotOptions();

        Map<String, Object> centerMap =
                new TypeConverter<Map<String, Object>>().getValue(argument, "center");
        LatLng center = FlutterDataConveter.mapToLatlng(centerMap);
        if (null == center) {
            if (Env.DEBUG) {
                Log.d(TAG, "center is null");
            }
            return null;
        }
        dotOptions.center(center);

        Double radius = new TypeConverter<Double>().getValue(argument, "radius");
        if (null == radius) {
            if (Env.DEBUG) {
                Log.d(TAG, "radius is null");
            }
            return null;
        }
        dotOptions.radius(radius.intValue());

        String colorStr = new TypeConverter<String>().getValue(argument, "color");
        if (TextUtils.isEmpty(colorStr)) {
            if (Env.DEBUG) {
                Log.d(TAG, "colorStr is null");
            }
            return null;
        }
        int color = FlutterDataConveter.strColorToInteger(colorStr);
        dotOptions.color(color);

        Integer zIndex = new TypeConverter<Integer>().getValue(argument, "zIndex");
        if (null != zIndex) {
            dotOptions.zIndex(zIndex);
        }

        Boolean visible = new TypeConverter<Boolean>().getValue(argument, "visible");
        if (null != visible) {
            dotOptions.visible(visible);
        }

        final Overlay overlay = mBaiduMap.addOverlay(dotOptions);
        return new HashMap<String, Overlay>() {
            {
                put(id, overlay);
            }
        };
    }
}
