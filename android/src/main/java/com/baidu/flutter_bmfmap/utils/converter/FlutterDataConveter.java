package com.baidu.flutter_bmfmap.utils.converter;

import android.graphics.Color;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.Size;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.map.WeightedLatLng;
import com.baidu.mapapi.map.WinRound;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FlutterDataConveter {

    /**
     * 将map形式的经纬度信息转换为结构化的经纬度数据
     * @param latlngMap
     * @return
     */
    public static LatLng mapToLatlng(Map<String, Object> latlngMap){
        if(null == latlngMap){
            return null;
        }

        if(!latlngMap.containsKey("latitude")
                || !latlngMap.containsKey("longitude")){
            return null;
        }

        Object latitudeObj = latlngMap.get("latitude");
        Object longitudeObj = latlngMap.get("longitude");
        if(null == latitudeObj || null == longitudeObj){
            return null;
        }
        LatLng latLng = new LatLng((double)latitudeObj, (double)longitudeObj);
        return latLng;
    }

    /**
     * 将多个map形式的经纬度信息转换为结构化的经纬度数据
     * @param latlngList
     * @return
     */
    public static List<LatLng> mapToLatlngs(List<Map<String, Double> > latlngList) {
        if (null == latlngList) {
            return null;
        }

        Iterator itr = latlngList.iterator();
        ArrayList<LatLng> latLngs = new ArrayList<>();
        while (itr.hasNext()){
            Map<String, Object> latlngMap = (Map<String, Object>)itr.next();
            LatLng latLng = mapToLatlng(latlngMap);
            if(null == latLng){
                break;
            }

            latLngs.add(latLng);
        }

        if(latLngs.size() != latlngList.size()){
            return null;
        }

        return latLngs;
    }


    /**
     * 将整形转换为16进制字符串
     * @param number
     * @return
     */
    private static String intToHexValue(int number) {
        String result = Integer.toHexString(number & 0xff);
        while (result.length() < 2) {
            result = "0" + result;
        }
        return result.toUpperCase();
    }

    /**
     * 将16进制颜色转换为整形颜色值
     * @param str 16进制颜色值
     * @return
     */
    public static int strColorToInteger(String str) {
        if(TextUtils.isEmpty(str) || str.length() < 8){
            return 0;
        }
        String str1 = str.substring(0, 2);
        String str2 = str.substring(2, 4);
        String str3 = str.substring(4, 6);
        String str4 = str.substring(6, 8);
        int alpha = Integer.parseInt(str1, 16);
        int red = Integer.parseInt(str2, 16);
        int green = Integer.parseInt(str3, 16);
        int blue = Integer.parseInt(str4, 16);


        return Color.argb(alpha, red, green, blue);
    }

    /**
     * 批量根据icon名称获取BitmapDescriptor
     * @param icons
     * @return
     */
    public static List<BitmapDescriptor> getIcons(List<String> icons){
        if(null == icons){
            return null;
        }

        List<BitmapDescriptor> bitmapIcons = new ArrayList<>();
        Iterator itr = icons.iterator();
        while (itr.hasNext()){
            String icon = (String) itr.next();
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromAsset("flutter_assets/" + icon);
            bitmapIcons.add(bitmapDescriptor);
        }

        return bitmapIcons;
    }

    /**
     * 批量将16进制字符串颜色值转换为整形颜色值
     * @param colors
     * @return
     */
    public static List<Integer> getColors(List<String> colors){
        if(null == colors || colors.size() <= 0){
            return null;
        }

        List<Integer> intColors = new ArrayList<>();
        Iterator iterator = colors.iterator();
        while (iterator.hasNext()){
            String colorStr = (String)iterator.next();
            if(TextUtils.isEmpty(colorStr)){
                return null;
            }

            int color = FlutterDataConveter.strColorToInteger(colorStr);
            intColors.add(color);
        }

        return intColors;
    }

    /**
     * 将map形式的bounds转换为LatLngBounds
     * @param boundsMap
     * @return
     */
    public static LatLngBounds mapToLatlngBounds(Map<String, Object> boundsMap){
        if(null == boundsMap){
            return null;
        }
        if(!boundsMap.containsKey("northeast") || !boundsMap.containsKey("southwest")){
            return null;
        }

        Map<String, Object> northeastMap = (Map<String, Object>)boundsMap.get("northeast");
        Map<String, Object> southwestMap = (Map<String, Object>)boundsMap.get("southwest");
        if(null == northeastMap || null == southwestMap){
            return null;
        }

        LatLng northeast = mapToLatlng(northeastMap);
        LatLng southwest = mapToLatlng(southwestMap);
        return new LatLngBounds.Builder().include(northeast).include(southwest).build();
    }

    /**
     * 将LatLngBounds转换为map
     * @param latLngBounds
     * @return
     */
    public static Map<String, Object> latlngBoundsToMap(LatLngBounds latLngBounds){
        if(null == latLngBounds){
            return null;
        }

        LatLng southwest = latLngBounds.southwest;
        LatLng northeast = latLngBounds.northeast;

        Map<String, Double> southwestMap = FlutterDataConveter.latLngToMap(southwest);
        Map<String, Double> northeastMap = FlutterDataConveter.latLngToMap(northeast);

        HashMap<String, Object> latLngBoundsMap = new HashMap<>();
        latLngBoundsMap.put("southwest", southwestMap);
        latLngBoundsMap.put("northeast", northeastMap);

        return latLngBoundsMap;
    }

    /**
     * 将map形式的带权值经纬度数据转换为结构化的带权值的经纬度数据
     * @param dataList
     * @return
     */
    public static List<WeightedLatLng> mapToWeightedLatLngList(List<Map<String, Object> > dataList) {
        if(null == dataList){
            return null;
        }

        List<WeightedLatLng> weightedLatLngList = new ArrayList<WeightedLatLng>();
        Iterator itr = dataList.iterator();
        while (itr.hasNext()){
            Map<String, Object> data = ( Map<String, Object> )itr.next();
            if(null == data){
                return null;
            }

            if(!data.containsKey("pt")
                 || !data.containsKey("intensity")){
                return null;
            }

            Object intensityObj = data.get("intensity");
            if(null == intensityObj){
                return null;
            }

            double intensity = (double)intensityObj;

            Object ptObj = data.get("pt");
            if(null == ptObj){
                return null;
            }

            Map<String, Object> ptMap = (Map<String, Object>)ptObj;
            if(null == ptMap){
                return null;
            }

            LatLng latLng = FlutterDataConveter.mapToLatlng(ptMap);

            WeightedLatLng weightedLatLng = new WeightedLatLng(latLng, intensity);
            weightedLatLngList.add(weightedLatLng);
        }

        return weightedLatLngList;
    }

    /**
     * 将map形式的屏幕点坐标转换为Point
     * @param pointMap
     * @return
     */
    public static Point mapToPoint(Map<String, Object> pointMap){
       if(null == pointMap){
           return null;
       }

       if(!pointMap.containsKey("x") || !pointMap.containsKey("y")){
           return null;
       }

       Object xObj = pointMap.get("x");
       Object yObj = pointMap.get("y");
       if(null == xObj || null == yObj){
           return null;
       }

       double x = (double)xObj;
       double y = (double)yObj;

       Point point = new Point((int)x, (int)y);

       return point;
    }

    /**
     * 将LatLng转成map存储
     * @param latLng
     * @return
     */
    public static Map<String, Double>  latLngToMap(LatLng latLng){
        if(null == latLng){
            return null;
        }

        Map<String, Double> resultMap = new HashMap<String, Double>();
        resultMap.put("latitude", latLng.latitude);
        resultMap.put("longitude", latLng.longitude);
        resultMap.put("latitudeE6", latLng.latitudeE6);
        resultMap.put("longitudeE6", latLng.longitudeE6);

        return resultMap;
    }

    /**
     * 将Point转成map存储
     * @param point
     * @return
     */
    public static Map<String, Double>  pointToMap(Point point){
        if(null == point){
            return null;
        }

        Map<String, Double> resultMap = new HashMap<String, Double>();
        resultMap.put("x", (double)point.x);
        resultMap.put("y", (double)point.y);

        return resultMap;
    }

    /**
     * 将flutter传过来的BMFRect转换为WinRound
     * BMFRect结构：
     *      /// 屏幕左上点对应的直角地理坐标
     *      final BMFPoint origin;
     *
     *      /// 坐标范围
     *      final BMFSize size;
     *
     * WinRound结构:
     *     public int left = 0;
     *     public int right = 0;
     *     public int top = 0;
     *     public int bottom = 0;
     */
    public static WinRound BMFRectToWinRound(Map<String, Object> bmfRect){
        if(null == bmfRect){
            return null;
        }

        if(!bmfRect.containsKey("origin") || !bmfRect.containsKey("size")){
            return null;
        }

        Map<String, Object> pointMap = (Map<String, Object>)bmfRect.get("origin");
        Point point = FlutterDataConveter.mapToPoint(pointMap);
        if(null == point){
            return null;
        }

        Map<String, Object> sizeMap = (Map<String, Object>)bmfRect.get("size");
        if(null == sizeMap){
            return null;
        }

        if(null == sizeMap){
            return null;
        }

        Double width = new TypeConverter<Double>().getValue(sizeMap, "width");
        Double height = new TypeConverter<Double>().getValue(sizeMap, "height");

        if(null == width || null == height){
            return null;
        }

        WinRound winRound = new WinRound();
        winRound.left = point.x;
        winRound.top = point.y;
        winRound.right = point.x + width.intValue();
        winRound.bottom = point.y + height.intValue();
        return winRound;

    }


    public static WinRound insetsToWinRound(Map<String, Object> insets){
        if(null == insets){
            return null;
        }

        if(!insets.containsKey("top")
                ||!insets.containsKey("left")
                || !insets.containsKey("bottom")
                || !insets.containsKey("right")){
            return null;
        }

        Double top = new TypeConverter<Double>().getValue(insets, "top");
        Double left = new TypeConverter<Double>().getValue(insets, "left");
        Double bottom = new TypeConverter<Double>().getValue(insets, "bottom");
        Double right = new TypeConverter<Double>().getValue(insets, "right");
        if(null == top
        || null == left
        || null == bottom
        || null == right){
            return null;
        }

        WinRound winRound = new WinRound();
        winRound.left = left.intValue();
        winRound.top = top.intValue();
        winRound.right = right.intValue();
        winRound.bottom = bottom.intValue();
        return winRound;

    }

    public static LatLngBounds BMFRectToLatLngBounds(BaiduMap baiduMap, Map<String, Object> bmfRect){
        if(null == baiduMap || null == bmfRect){
            return null;
        }

        WinRound winRound = FlutterDataConveter.BMFRectToWinRound(bmfRect);
        if(null == bmfRect){
            return null;
        }

        Point notrhEastPoint = new Point();
        notrhEastPoint.x = winRound.left;
        notrhEastPoint.y = winRound.top;
        Point southWestPoint = new Point();
        southWestPoint.x = winRound.right;
        southWestPoint.y = winRound.bottom;

        Projection projection = baiduMap.getProjection();
        LatLng northEast = projection.fromScreenLocation(notrhEastPoint);
        LatLng southWest = projection.fromScreenLocation(southWestPoint);


        LatLngBounds latLngBounds = new LatLngBounds.Builder().include(northEast).include(southWest).build();

        return latLngBounds;

    }
}
