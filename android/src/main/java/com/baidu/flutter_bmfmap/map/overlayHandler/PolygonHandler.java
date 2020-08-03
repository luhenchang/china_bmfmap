package com.baidu.flutter_bmfmap.map.overlayHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.flutter_bmfmap.utils.Env;
import com.baidu.flutter_bmfmap.utils.converter.FlutterDataConveter;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;

import android.text.TextUtils;
import android.util.Log;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class PolygonHandler extends OverlayHandler {
    private static final String TAG = "PolygonHandler";

    public PolygonHandler(BaiduMap baiduMap) {
        super(baiduMap);
    }

    @Override
    public Map<String, Overlay> handlerMethodCall(MethodCall call, MethodChannel.Result result) {
        if (Env.DEBUG) {
            Log.d(TAG, "handlerMethodCall enter0");
        }
        Map<String, Object> argument = call.arguments();
        if (null == argument) {
            if (Env.DEBUG) {
                Log.d(TAG, "argument is null");
            }
            return null;
        }

        if (!argument.containsKey("id")
                || !argument.containsKey("coordinates")) {
            if (Env.DEBUG) {
                Log.d(TAG, "argument does not contain");
            }
            return null;
        }

        final String id = (String) argument.get("id");
        if (TextUtils.isEmpty(id)) {
            return null;
        }
        List<Map<String, Double>> coordinates =
                (List<Map<String, Double>>) argument.get("coordinates");

        if (coordinates.size() < 1) {
            if (Env.DEBUG) {
                Log.d(TAG, "coordinates.size() < 1");
            }
            return null;
        }

        PolygonOptions polygonOptions = new PolygonOptions();

        List<LatLng> coordinatesList = FlutterDataConveter.mapToLatlngs(coordinates);
        if (null == coordinatesList) {
            if (Env.DEBUG) {
                Log.d(TAG, "coordinatesList is null");
            }
            return null;
        }

        polygonOptions.points(coordinatesList);

        if (argument.containsKey("width") && argument
                .containsKey("strokeColor")) {
            int width = (Integer) argument.get("width");
            String strokeColorStr = (String) argument.get("strokeColor");
            if (Env.DEBUG) {
                Log.d(TAG, "strokeColorStr:" + strokeColorStr);
            }
            if (!TextUtils.isEmpty(strokeColorStr)) {
                int strokeColor = FlutterDataConveter.strColorToInteger(strokeColorStr);
                Stroke stroke = new Stroke(width, strokeColor);
                polygonOptions.stroke(stroke);
            }
        }

        if (argument.containsKey("fillColor")) {
            String fillColorStr = (String) argument.get("fillColor");
            if (Env.DEBUG) {
                Log.d(TAG, "fillColorStr:" + fillColorStr);
            }
            if (!TextUtils.isEmpty(fillColorStr)) {
                int fillColor = FlutterDataConveter.strColorToInteger(fillColorStr);
                polygonOptions.fillColor(fillColor);
            }
        }

        if (argument.containsKey("zIndex")) {
            int zIndex = (Integer) argument.get("zIndex");
            polygonOptions.zIndex(zIndex);
        }

        if (argument.containsKey("visible")) {
            boolean visible = (Boolean) argument.get("visible");
            polygonOptions.visible(visible);
        }

        final Overlay overlay = mBaiduMap.addOverlay(polygonOptions);

        return new HashMap<String, Overlay>() {
            {
                put(id, overlay);
            }
        };
    }
}
