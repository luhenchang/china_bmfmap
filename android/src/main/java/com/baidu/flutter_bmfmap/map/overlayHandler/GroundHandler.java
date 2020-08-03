package com.baidu.flutter_bmfmap.map.overlayHandler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.baidu.flutter_bmfmap.utils.Env;
import com.baidu.flutter_bmfmap.utils.converter.FlutterDataConveter;
import com.baidu.flutter_bmfmap.utils.converter.TypeConverter;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.GroundOverlay;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

import android.text.TextUtils;
import android.util.Log;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

class GroundHandler extends OverlayHandler {

    private static final String TAG = "GroundHandler";

    private HashMap<String, BitmapDescriptor> mBitmapMap = new HashMap<>();

    public GroundHandler(BaiduMap baiduMap) {
        super(baiduMap);
    }

    @Override
    public Map<String, Overlay> handlerMethodCall(MethodCall call, MethodChannel.Result result) {
        if (Env.DEBUG) {
            Log.d(TAG, "handlerMethodCall enter");
        }
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

        GroundOverlayOptions groundOverlayOptions = new GroundOverlayOptions();

        setGroundOptions(id, argument, groundOverlayOptions);

        final Overlay overlay = mBaiduMap.addOverlay(groundOverlayOptions);

        return new HashMap<String, Overlay>() {
            {
                put(id, overlay);
            }
        };
    }

    /**
     *
     */
    private void setGroundOptions(String id, Map<String, Object> groundOptionsMap,
                                  GroundOverlayOptions groundOverlayOptions) {
        if (null == groundOptionsMap) {
            return;
        }

        String image = new TypeConverter<String>().getValue(groundOptionsMap, "image");
        if (!TextUtils.isEmpty(image)) {
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromAsset("flutter_assets/" + image);
            if (null != bitmap) {
                if (Env.DEBUG) {
                    Log.d(TAG, "image");

                }
                groundOverlayOptions.image(bitmap);
                mBitmapMap.put(id, bitmap);
            }

        }

        Double anchorX = new TypeConverter<Double>().getValue(groundOptionsMap, "anchorX");
        Double anchorY = new TypeConverter<Double>().getValue(groundOptionsMap, "anchorY");
        if (null != anchorX && null != anchorY) {
            groundOverlayOptions.anchor(anchorX.floatValue(), anchorY.floatValue());
        }

        Map<String, Object> centerMap =
                new TypeConverter<Map<String, Object>>().getValue(groundOptionsMap, "position");
        if (null != centerMap) {
            LatLng center = FlutterDataConveter.mapToLatlng(centerMap);
            if (null != center) {
                if (Env.DEBUG) {
                    Log.d(TAG, "position");

                }
                groundOverlayOptions.position(center);
            }
        }

        Double width = new TypeConverter<Double>().getValue(groundOptionsMap, "width");
        Double height = new TypeConverter<Double>().getValue(groundOptionsMap, "height");
        if(null != width && null != height){
            groundOverlayOptions.dimensions(width.intValue(), height.intValue());
        }

        Map<String, Object> boundsMap =
                new TypeConverter<Map<String, Object>>().getValue(groundOptionsMap, "bounds");
        LatLngBounds latLngBounds = FlutterDataConveter.mapToLatlngBounds(boundsMap);
        if (null != latLngBounds) {
            if (Env.DEBUG) {
                Log.d(TAG, "bounds");

            }
            groundOverlayOptions.positionFromBounds(latLngBounds);
        }

        Double transparency =
                new TypeConverter<Double>().getValue(groundOptionsMap, "transparency");
        if (null != transparency) {
            groundOverlayOptions.transparency(transparency.floatValue());
        }

        Integer zIndex = new TypeConverter<Integer>().getValue(groundOptionsMap, "zIndex");
        if (null != zIndex) {
            groundOverlayOptions.zIndex(zIndex);
        }

        Boolean visible = new TypeConverter<Boolean>().getValue(groundOptionsMap, "visible");
        if (null != visible) {
            groundOverlayOptions.visible(visible);
        }

    }


    public void clean(){
        super.clean();
        Iterator iterator = mBitmapMap.values().iterator();
        BitmapDescriptor bitmapDescriptor;
        while (iterator.hasNext()){
            bitmapDescriptor = (BitmapDescriptor)iterator.next();
            if(null != bitmapDescriptor){
                bitmapDescriptor.recycle();
            }
        }

        mBitmapMap.clear();
    }

    public void clean(String id) {
        if (TextUtils.isEmpty(id)) {
            return;
        }

        BitmapDescriptor bitmapDescriptor = mBitmapMap.get(id);
        if (null != bitmapDescriptor) {
            bitmapDescriptor.recycle();
        }

        mBitmapMap.remove(id);
    }
}