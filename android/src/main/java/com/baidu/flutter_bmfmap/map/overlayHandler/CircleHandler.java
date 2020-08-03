package com.baidu.flutter_bmfmap.map.overlayHandler;

import java.util.HashMap;
import java.util.Map;

import com.baidu.flutter_bmfmap.utils.Env;
import com.baidu.flutter_bmfmap.utils.converter.FlutterDataConveter;
import com.baidu.flutter_bmfmap.utils.converter.TypeConverter;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.CircleDottedStrokeType;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;

import android.text.TextUtils;
import android.util.Log;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class CircleHandler extends OverlayHandler {

    private static final String TAG = "CircleHandler";

    public CircleHandler(BaiduMap baiduMap) {
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

        if (!argument.containsKey("id")
                || !argument.containsKey("center")
                || !argument.containsKey("radius")) {
            if (Env.DEBUG) {
                Log.d(TAG, "argument does not contain");
            }
            return null;
        }

        CircleOptions circleOptions = new CircleOptions();

        final String id = new TypeConverter<String>().getValue(argument, "id");
        if (TextUtils.isEmpty(id)) {
            return null;
        }

        Map<String, Object> centerMap = (Map<String, Object>) argument.get("center");
        LatLng center = FlutterDataConveter.mapToLatlng(centerMap);
        if (null != center) {
            circleOptions.center(center);
        }

        double radius = (Double) argument.get("radius");
        circleOptions.radius((int) radius);


        if (argument.containsKey("width") && argument.containsKey("strokeColor")) {
            int width = (Integer) argument.get("width");
            String strokeColorStr = (String) argument.get("strokeColor");
            if (!TextUtils.isEmpty(strokeColorStr)) {
                int strokeColor = FlutterDataConveter.strColorToInteger(strokeColorStr);
                Stroke stroke = new Stroke(width, strokeColor);
                circleOptions.stroke(stroke);
            }

        }

        if (argument.containsKey("fillColor")) {
            String fillColorStr = (String) argument.get("fillColor");
            int fillColor = FlutterDataConveter.strColorToInteger(fillColorStr);
            circleOptions.fillColor(fillColor);
        }

        if (argument.containsKey("zIndex")) {
            int zIndex = (Integer) argument.get("zIndex");
            circleOptions.zIndex(zIndex);
        }

        if (argument.containsKey("visible")) {
            boolean visible = (Boolean) argument.get("visible");
            circleOptions.visible(visible);
        }

        setLineDashType(argument, circleOptions);

        final Overlay overlay = mBaiduMap.addOverlay(circleOptions);
        return new HashMap<String, Overlay>() {
            {
                put(id, overlay);
            }
        };
    }

    private void setLineDashType(Map<String, Object> circleOptionsMap,
                                 CircleOptions circleOptions) {
        if (null == circleOptionsMap || null == circleOptions) {
            return;
        }

        Integer lineDashType =
                new TypeConverter<Integer>().getValue(circleOptionsMap, "lineDashType");
        if (null == lineDashType) {
            return;
        }

        switch (lineDashType) {
            case OverlayCommon.LineDashType.sLineDashTypeNone:
                circleOptions.dottedStroke(false);
                break;
            case OverlayCommon.LineDashType.sLineDashTypeSquare:
                circleOptions.dottedStroke(true);
                circleOptions.dottedStrokeType(CircleDottedStrokeType.DOTTED_LINE_SQUARE);
                break;
            case OverlayCommon.LineDashType.sLineDashTypeDot:
                circleOptions.dottedStroke(true);
                circleOptions.dottedStrokeType(CircleDottedStrokeType.DOTTED_LINE_CIRCLE);
                break;
            default:
                break;
        }
    }
}
