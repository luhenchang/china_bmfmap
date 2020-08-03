package com.baidu.flutter_bmfmap.map.overlayHandler;

import java.util.HashMap;
import java.util.Map;

import com.baidu.flutter_bmfmap.utils.Env;
import com.baidu.flutter_bmfmap.utils.converter.FlutterDataConveter;
import com.baidu.flutter_bmfmap.utils.converter.TypeConverter;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;

import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class TextHandler extends OverlayHandler {
    private static final String TAG = "TextHandler";

    public TextHandler(BaiduMap baiduMap) {
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
                || !argument.containsKey("text")
                || !argument.containsKey("position")) {
            if (Env.DEBUG) {
                Log.d(TAG, "argument does not contain" + argument.toString());

            }
            return null;
        }

        final String id = new TypeConverter<String>().getValue(argument, "id");
        if (TextUtils.isEmpty(id)) {
            return null;
        }

        TextOptions textOptions = new TextOptions();

        Object posObj = (argument.get("position"));
        if (null != posObj) {
            Map<String, Object> posMap = (Map<String, Object>) posObj;
            LatLng pos = FlutterDataConveter.mapToLatlng(posMap);
            if (null != pos) {
                if (Env.DEBUG) {
                    Log.d(TAG, "pos");
                }
                textOptions.position(pos);
            }
        }

        String text = new TypeConverter<String>().getValue(argument, "text");
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        textOptions.text(text);

        setTextOptions(argument, textOptions);

        final Overlay overlay = mBaiduMap.addOverlay(textOptions);
        return new HashMap<String, Overlay>() {
            {
                put(id, overlay);
            }
        };
    }

    private void setTextOptions(Map<String, Object> textOptionsMap, TextOptions textOptions) {
        if (null == textOptionsMap || null == textOptions) {
            return;
        }

        String bgColorStr = new TypeConverter<String>().getValue(textOptionsMap, "bgColor");
        if (!TextUtils.isEmpty(bgColorStr)) {
            int bgColor = FlutterDataConveter.strColorToInteger(bgColorStr);
            textOptions.bgColor(bgColor);
        }

        String fongColorStr = new TypeConverter<String>().getValue(textOptionsMap, "fontColor");
        if (!TextUtils.isEmpty(fongColorStr)) {
            int fontColor = FlutterDataConveter.strColorToInteger(fongColorStr);
            textOptions.fontColor(fontColor);
        }

        Integer fontSize = new TypeConverter<Integer>().getValue(textOptionsMap, "fontSize");
        if (null != fontSize) {
            textOptions.fontSize(fontSize);
        }

        Integer alignx = new TypeConverter<Integer>().getValue(textOptionsMap, "alignX");
        Integer aligny = new TypeConverter<Integer>().getValue(textOptionsMap, "alignY");
        if (null != alignx && null != aligny) {
            textOptions.align(alignx, aligny);
        }

        Double roate = new TypeConverter<Double>().getValue(textOptionsMap, "rotate");
        if (null != roate) {
            textOptions.rotate(roate.floatValue());
        }

        Integer zIndex = new TypeConverter<Integer>().getValue(textOptionsMap, "zIndex");
        if (null != zIndex) {
            textOptions.zIndex(zIndex);
        }

        Boolean visible = new TypeConverter<Boolean>().getValue(textOptionsMap, "visible");
        if (null != visible) {
            textOptions.visible(visible);
        }

        Map<String, Object> typeFaceMap =
                new TypeConverter<Map<String, Object>>().getValue(textOptionsMap, "typeFace");
        if (null != typeFaceMap) {
            String familyName = new TypeConverter<String>().getValue(typeFaceMap, "familyName");
            Integer textStype = new TypeConverter<Integer>().getValue(typeFaceMap, "textStype");
            if (!TextUtils.isEmpty(familyName) && textStype >= 0 && textStype <= 4) {
                Typeface typeface = Typeface.create(familyName, textStype);
                textOptions.typeface(typeface);
            }
        }
    }
}
