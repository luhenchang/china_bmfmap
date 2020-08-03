package com.baidu.flutter_bmfmap.map.overlayHandler;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.baidu.flutter_bmfmap.utils.Constants;
import com.baidu.flutter_bmfmap.utils.Env;
import com.baidu.flutter_bmfmap.utils.converter.FlutterDataConveter;
import com.baidu.flutter_bmfmap.utils.converter.TypeConverter;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnPolylineClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineDottedLineType;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class PolylineHandler extends OverlayHandler {
    private static final String TAG = "PolylineHandler";

    private HashMap<String, List<BitmapDescriptor>> mBitmapMap = new HashMap<>();

    private OnPolylineClickListener mOnPolylineClickListener = new OnPolylineClickListener() {
        @Override
        public boolean onPolylineClick(Polyline polyline) {
            return false;
        }
    };

    public PolylineHandler(BaiduMap baiduMap) {
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

        String methodId = call.method;
        Map<String, Overlay> overlayMap = null;
        switch (methodId) {
            case Constants.MethodProtocol.PolylineProtocol.sMapAddPolylineMethod:
                overlayMap = addPolyLine(argument);
                break;
            case Constants.MethodProtocol.PolylineProtocol.sMapUpdatePolylineMemberMethod:
                overlayMap = updateMember(argument);
                break;
            default:
                break;
        }

        return overlayMap;
    }

    private Map<String, Overlay> addPolyLine(Map<String, Object> argument) {
        if (!argument.containsKey("id")
                || !argument.containsKey("coordinates")
                || !argument.containsKey("indexs")) {
            if (Env.DEBUG) {
                Log.d(TAG, "argument does not contain");
            }
            return null;
        }

        final String id = new TypeConverter<String>().getValue(argument, "id");
        if (TextUtils.isEmpty(id)) {
            if (Env.DEBUG) {
                Log.d(TAG, "id is null");
            }
            return null;
        }

        List<Map<String, Double>> coordinates =
                new TypeConverter<List<Map<String, Double>>>().getValue(argument, "coordinates");
        List<LatLng> latLngList = FlutterDataConveter.mapToLatlngs(coordinates);
        if (null == latLngList) {
            if (Env.DEBUG) {
                Log.d(TAG, "latLngList is null");
            }
            return null;
        }

        PolylineOptions polylineOptions = new PolylineOptions().points(latLngList);

        int pointNum = coordinates.size();
        List<Integer> indexs = new TypeConverter<List<Integer>>().getValue(argument, "indexs");
        setOptions(id, argument, polylineOptions, indexs, pointNum);

        if (Env.DEBUG) {
            Log.d(TAG, "addOverlay success");
        }

        final Polyline polyline = (Polyline) mBaiduMap.addOverlay(polylineOptions);
        Bundle bundle = new Bundle();
        bundle.putCharArray("id", id.toCharArray());
        polyline.setExtraInfo(bundle);

        if (null != polyline) {
            mBaiduMap.setOnPolylineClickListener(mOnPolylineClickListener);

            return new HashMap<String, Overlay>() {
                {
                    put(id, polyline);
                }
            };
        }

        return null;
    }

    private void setOptions(String id, Map<String, Object> polylineOptionsMap,
                            PolylineOptions polylineOptions,
                            List<Integer> indexs,
                            int pointNumn) {
        if (null == polylineOptionsMap || null == polylineOptions || null == indexs) {
            return;
        }

        Integer width = new TypeConverter<Integer>().getValue(polylineOptionsMap, "width");
        if (null != width) {
            polylineOptions.width(width);
        }

        Boolean clickable = new TypeConverter<Boolean>().getValue(polylineOptionsMap, "clickable");
        if (null != clickable) {
            polylineOptions.clickable(clickable);
        }

        Boolean isKeepScale =
                new TypeConverter<Boolean>().getValue(polylineOptionsMap, "isKeepScale");
        if (null != isKeepScale) {
            polylineOptions.keepScale(isKeepScale);
        }

        Boolean isFocus = new TypeConverter<Boolean>().getValue(polylineOptionsMap, "isFocus");
        if (null != isFocus) {
            polylineOptions.focus(isFocus);
        }

        Integer zIndex = new TypeConverter<Integer>().getValue(polylineOptionsMap, "zIndex");
        if (null != zIndex) {
            polylineOptions.zIndex(zIndex);
        }

        Boolean visible = new TypeConverter<Boolean>().getValue(polylineOptionsMap, "visible");
        if (null != visible) {
            polylineOptions.visible(visible);
        }

        Boolean isThined = new TypeConverter<Boolean>().getValue(polylineOptionsMap, "isThined");
        if(null != isThined){
            polylineOptions.isThined(isThined);
        }

        Boolean dottedLine = new TypeConverter<Boolean>().getValue(polylineOptionsMap, "dottedLine");
        if (null != dottedLine) {
            polylineOptions.dottedLine(dottedLine);
        }

        List<String> colors =
                new TypeConverter<List<String>>().getValue(polylineOptionsMap, "colors");
        if (null != colors && colors.size() > 0) {
            List<Integer> intColors = FlutterDataConveter.getColors(colors);

            if (null != intColors) {
                if (intColors.size() == 1) {
                    polylineOptions.color(intColors.get(0));
                } else {
                    List<Integer> correctColors = correctColors(indexs, intColors, pointNumn);
                    polylineOptions.colorsValues(correctColors);
                }
            }
        }

        /*
         *colors和icons不能共存
         */
        if (null == colors || colors.size() <= 0) {
            List<String> icons =
                    new TypeConverter<List<String>>().getValue(polylineOptionsMap, "textures");
            if (null != icons && icons.size() > 0) {
                List<BitmapDescriptor> bitmapDescriptors = FlutterDataConveter.getIcons(icons);
                if (null != bitmapDescriptors) {
                    if (bitmapDescriptors.size() == 1) {
                        polylineOptions.customTexture(bitmapDescriptors.get(0));
                    } else {
                        polylineOptions.textureIndex(indexs);
                        polylineOptions.customTextureList(bitmapDescriptors);
                    }

                    clearTextureBitMap(id);
                    mBitmapMap.put(id, bitmapDescriptors);
                }
            }
        }

        setLineDashType(polylineOptionsMap, polylineOptions);
    }

    /**
     * android polyline多颜色只需要设置colors
     * 但flutter传过来的colors只是一个颜色数组，没有索引的概念，需要根据indexs对其进行修正
     * 正常情况indexs的数目应该等于pointNum -1,如果indexs小于次值，则余下段的索引按照索引数组最后一个补齐，反之则按照poinNum - 1处理
     */
    private List<Integer> correctColors(List<Integer> indexs,
                                        List<Integer> colors,
                                        int pointNum) {

        // 通过colors的size对索引数组进行修正
        List<Integer> tmpIndexs = new ArrayList<>();
        for (Integer i : indexs) {
            if (i < colors.size()) {
                tmpIndexs.add(i);
            } else {
                tmpIndexs.add(colors.size() - 1);
            }
        }

        int tmpIndexSize = tmpIndexs.size();
        int lastIndexValue = tmpIndexs.get(tmpIndexSize - 1);
        // 通过pointNum对索引数组进行修正
        if (tmpIndexSize < pointNum - 1) {
            for (int i = tmpIndexSize; i < pointNum - 1; i++) {
                tmpIndexs.add(lastIndexValue);
            }
        }

        List<Integer> tmpColors = new ArrayList<>();
        for (int i = 0; i < pointNum - 1; i++) {
            tmpColors.add(colors.get(tmpIndexs.get(i)));
        }

        return tmpColors;
    }

    private void setLineDashType(Map<String, Object> polylineOptionsMap,
                                 PolylineOptions polylineOptions) {
        if (null == polylineOptionsMap || null == polylineOptions) {
            return;
        }

        Integer lineDashType =
                new TypeConverter<Integer>().getValue(polylineOptionsMap, "lineDashType");
        if (null == lineDashType) {
            return;
        }

        switch (lineDashType) {
            case OverlayCommon.LineDashType.sLineDashTypeSquare:
                polylineOptions.dottedLineType(PolylineDottedLineType.DOTTED_LINE_SQUARE);
                break;
            case OverlayCommon.LineDashType.sLineDashTypeDot:
                polylineOptions.dottedLineType(PolylineDottedLineType.DOTTED_LINE_CIRCLE);
                break;
            default:
                break;
        }
    }

    /**
     * 更新polyline属性
     *
     * @param argument
     * @return
     */
    private Map<String, Overlay> updateMember(Map<String, Object> argument) {
        if (null == mCurrentOverlay || !(mCurrentOverlay instanceof Polyline)) {
            return null;
        }

        final Polyline polyline = (Polyline) mCurrentOverlay;

        final String id = new TypeConverter<String>().getValue(argument, "id");
        if (TextUtils.isEmpty(id)) {
            return null;
        }

        String member = new TypeConverter<String>().getValue(argument, "member");
        if (TextUtils.isEmpty(member)) {
            return null;
        }

        switch (member) {
            case "coordinates":
                if (!updateCoordinates(argument, polyline)) {
                    return null;
                }
                break;
            case "width":
                Integer width = new TypeConverter<Integer>().getValue(argument, "value");
                if (null == width) {
                    return null;
                }

                polyline.setWidth(width);
                break;
            case "indexs":
                if (!updateIndexs(argument, polyline)) {
                    return null;
                }
                break;
            case "colors":
                if (!updateColors(argument, polyline)) {
                    return null;
                }
                break;
            case "textures":
                if (!updateTextures(argument, polyline)) {
                    return null;
                }
                break;
            case "lineDashType":
                if (!updateLinashType(argument, polyline)) {
                    return null;
                }
                break;
            case "lineCapType":
            case "lineJoinType":
                return null;
            case "clickable":
                Boolean clickable = new TypeConverter<Boolean>().getValue(argument, "value");
                if (null == clickable) {
                    return null;
                }

                polyline.setClickable(clickable);
                break;
            case "isKeepScale":
                Boolean isKeepScale = new TypeConverter<Boolean>().getValue(argument, "value");
                if (null == isKeepScale) {
                    return null;
                }

                polyline.setIsKeepScale(isKeepScale);
                break;
            case "isFocus":
                Boolean isFocus = new TypeConverter<Boolean>().getValue(argument, "value");
                if (null == isFocus) {
                    return null;
                }

                polyline.setFocus(isFocus);
                break;
            case "visible":
                Boolean visible = new TypeConverter<Boolean>().getValue(argument, "value");
                if (null == visible) {
                    return null;
                }

                polyline.setVisible(visible);
                break;
            case "zIndex":
                Integer zIndex = new TypeConverter<Integer>().getValue(argument, "value");
                if (null == zIndex) {
                    return null;
                }

                polyline.setZIndex(zIndex);
                break;
            case "isThined":
                Boolean isThined = new TypeConverter<Boolean>().getValue(argument, "value");
                if(null != isThined){
                    polyline.setThined(isThined);
                }
                break;
            case "dottedLine":
                Boolean dottedLine = new TypeConverter<Boolean>().getValue(argument, "value");
                if (null != dottedLine) {
                    polyline.setDottedLine(dottedLine);
                }
                break;
            default:
                break;
        }

        return new HashMap<String, Overlay>() {
            {
                put(id, polyline);
            }
        };
    }

    private boolean updateCoordinates(Map<String, Object> argument, Polyline polyline) {
        List<Map<String, Double>> coordinates =
                new TypeConverter<List<Map<String, Double>>>().getValue(argument,
                        "value");

        if (null == coordinates) {
            return false;
        }

        List<LatLng> latLngList = FlutterDataConveter.mapToLatlngs(coordinates);
        if (null == latLngList) {
            return false;
        }
        polyline.setPoints(latLngList);


        List<Integer> indexs = new TypeConverter<List<Integer>>().getValue(argument, "indexs");
        if (null != indexs) {
            int[] nIndexs = new int[indexs.size()];
            for (int i = 0; i < indexs.size(); i++) {
                nIndexs[i] = indexs.get(i);
            }

            polyline.setIndexs(nIndexs);
        }

        return true;
    }

    private boolean updateIndexs(Map<String, Object> argument, Polyline polyline) {
        List<Integer> indexs = new TypeConverter<List<Integer>>().getValue(argument, "value");
        if (null == indexs) {
            return false;
        }

        int[] nIndexs = new int[indexs.size()];
        for (int i = 0; i < indexs.size(); i++) {
            nIndexs[i] = indexs.get(i);
        }

        polyline.setIndexs(nIndexs);

        List<LatLng> points = polyline.getPoints();
        if (null != points) {
            polyline.setPoints(points);
        }

        return true;
    }


    private boolean updateColors(Map<String, Object> argument, Polyline polyline) {
        boolean ret = false;
        List<String> colors =
                new TypeConverter<List<String>>().getValue(argument, "value");
        List<Integer> indexs =
                new TypeConverter<List<Integer>>().getValue(argument, "indexs");

        List<LatLng> points = polyline.getPoints();

        if (null != colors &&
                colors.size() > 0 &&
                null != indexs &&
                indexs.size() > 0 &&
                null != points &&
                points.size() > 0) {
            List<Integer> intColors = FlutterDataConveter.getColors(colors);
            List<Integer> correctColors =  correctColors(indexs, intColors, points.size());

            if (null != correctColors) {
                if (correctColors.size() == 1) {
                    polyline.setColor(correctColors.get(0));
                    ret = true;
                } else {
                    int[] nColors = new int[correctColors.size()];
                    for (int i = 0; i < correctColors.size(); i++) {
                        nColors[i] = correctColors.get(i);
                    }
                    polyline.setColorList(nColors);
                    ret = true;
                }

                polyline.setPoints(points);
            }
        }

        return ret;
    }

    private boolean updateTextures(Map<String, Object> argument, Polyline polyline) {
        List<String> icons =
                new TypeConverter<List<String>>().getValue(argument, "value");

        if (null == icons) {
            return false;
        }

        boolean ret = false;
        if (null != icons && icons.size() > 0) {
            List<BitmapDescriptor> bitmapDescriptors = FlutterDataConveter.getIcons(icons);
            if (null != bitmapDescriptors) {
                if (bitmapDescriptors.size() == 1) {
                    polyline.setTexture(bitmapDescriptors.get(0));
                    ret = true;
                } else {
                    polyline.setTextureList(bitmapDescriptors);
                    ret = true;
                }

                List<LatLng> points = polyline.getPoints();
                if (null != points) {
                    polyline.setPoints(points);
                }

                Bundle bundle = polyline.getExtraInfo();
                String id = bundle.getString("id");
                clearTextureBitMap(id);
                mBitmapMap.put(id, bitmapDescriptors);
            }
        }

        return ret;
    }

    private boolean updateLinashType(Map<String, Object> argument, Polyline polyline) {
        Integer lineDashType = new TypeConverter<Integer>().getValue(argument, "value");

        if (null == lineDashType) {
            return false;
        }

        switch (lineDashType) {
            case OverlayCommon.LineDashType.sLineDashTypeNone:
                break;
            case OverlayCommon.LineDashType.sLineDashTypeSquare:
                polyline.setDottedLineType(PolylineDottedLineType.DOTTED_LINE_SQUARE);
                break;
            case OverlayCommon.LineDashType.sLineDashTypeDot:
                polyline.setDottedLineType(PolylineDottedLineType.DOTTED_LINE_CIRCLE);
                break;
            default:
                break;
        }

        return true;
    }

    private void clearTextureBitMap(String id) {
        if (TextUtils.isEmpty(id)) {
            return;
        }

        List<BitmapDescriptor> bitmapDescriptors = mBitmapMap.get(id);
        if (null == bitmapDescriptors) {
            return;
        }

        Iterator itr = bitmapDescriptors.iterator();
        BitmapDescriptor bitmapDescriptor;
        while (itr.hasNext()) {
            bitmapDescriptor = (BitmapDescriptor) itr.next();
            if (null == bitmapDescriptor) {
                continue;
            }

            bitmapDescriptor.recycle();
        }

        mBitmapMap.remove(id);
    }

    public void clean(){
        Iterator itr = mBitmapMap.values().iterator();
        List<BitmapDescriptor> bitmapDescriptors;
        BitmapDescriptor bitmapDescriptor;
        while (itr.hasNext()) {
            bitmapDescriptors = (List<BitmapDescriptor>) itr.next();
            if (null == bitmapDescriptors) {
                continue;
            }

            Iterator listItr = bitmapDescriptors.iterator();
            while (listItr.hasNext()) {
                bitmapDescriptor = (BitmapDescriptor)listItr.next();
                if (null == bitmapDescriptor) {
                    continue;
                }

                bitmapDescriptor.recycle();
            }
        }

        mBitmapMap.clear();
    }

    public void clean(String id) {
        if (TextUtils.isEmpty(id)) {
            return;
        }

        List<BitmapDescriptor> bitmapDescriptors = mBitmapMap.get(id);
        if (null == bitmapDescriptors) {
            return;
        }

        Iterator itr = bitmapDescriptors.iterator();
        BitmapDescriptor bitmapDescriptor;
        while (itr.hasNext()) {
            bitmapDescriptor = (BitmapDescriptor)itr.next();
            if (null == bitmapDescriptor) {
                continue;
            }

            bitmapDescriptor.recycle();
        }

        mBitmapMap.remove(id);
    }
}
