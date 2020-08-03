package com.baidu.flutter_bmfmap.map.overlayHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.flutter_bmfmap.utils.Env;
import com.baidu.flutter_bmfmap.utils.converter.FlutterDataConveter;
import com.baidu.mapapi.map.ArcOptions;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.model.LatLng;

import android.text.TextUtils;
import android.util.Log;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class ArcLineHandler extends OverlayHandler {
    private static final String TAG = "ArcLineHandler";

    public ArcLineHandler(BaiduMap baiduMap) {
        super(baiduMap);
    }

    @Override
    public Map<String, Overlay> handlerMethodCall(MethodCall call, MethodChannel.Result result) {
        Map<String, Object> argument = call.arguments();
        if (null == argument) {
            return null;
        }

        if (!argument.containsKey("id")
                || !argument.containsKey("coordinates")) {
            if (Env.DEBUG) {
                Log.d(TAG, "argument does not contain");
            }
            return null;
        }

        ArcOptions arcOptions = new ArcOptions();

        final String id = (String) argument.get("id");
        if (TextUtils.isEmpty(id)) {
            return null;
        }
        List<Map<String, Object>> coordinates =
                (List<Map<String, Object>>) argument.get("coordinates");

        if (coordinates.size() < 3) {
            if (Env.DEBUG) {
                Log.d(TAG, "atlngs.size() < 3");
            }
            return null;
        }

        LatLng latLngStart = FlutterDataConveter.mapToLatlng(coordinates.get(0));
        LatLng latLngMiddle = FlutterDataConveter.mapToLatlng(coordinates.get(1));
        LatLng latLngEnd = FlutterDataConveter.mapToLatlng(coordinates.get(2));

        if (null == latLngStart
                || null == latLngMiddle
                || null == latLngEnd) {
            if (Env.DEBUG) {
                Log.d(TAG, "null == latLngStart\n" +
                        "        || null == latLngMiddle\n" +
                        "        || null == latLngEnd");
            }
            return null;
        }

        arcOptions.points(latLngStart, latLngMiddle, latLngEnd);

        if (argument.containsKey("width")) {
            int width = (Integer) argument.get("width");
            arcOptions.width(width);
        }

        if (argument.containsKey("color")) {
            String strokeColorStr = (String) argument.get("color");
            int strokeColor = FlutterDataConveter.strColorToInteger(strokeColorStr);
            arcOptions.color(strokeColor);
        }

        if (argument.containsKey("zIndex")) {
            int zIndex = (Integer) argument.get("zIndex");
            arcOptions.zIndex(zIndex);
        }

        if (argument.containsKey("visible")) {
            boolean visible = (Boolean) argument.get("visible");
            arcOptions.visible(visible);
        }

        final Overlay overlay = mBaiduMap.addOverlay(arcOptions);
        return new HashMap<String, Overlay>() {
            {
                put(id, overlay);
            }
        };
    }
}
