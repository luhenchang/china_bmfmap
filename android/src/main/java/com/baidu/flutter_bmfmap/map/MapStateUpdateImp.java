package com.baidu.flutter_bmfmap.map;

import java.util.Map;

import com.baidu.flutter_bmfmap.map.mapHandler.BMFMapStatus;
import com.baidu.flutter_bmfmap.utils.Constants;
import com.baidu.flutter_bmfmap.utils.Env;
import com.baidu.flutter_bmfmap.utils.converter.FlutterDataConveter;
import com.baidu.flutter_bmfmap.utils.converter.TypeConverter;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

import android.graphics.Point;
import android.text.TextUtils;

/**
 * 地图状态更新
 */
public class MapStateUpdateImp {
    private static MapStateUpdateImp sInstance = null;

    private   String mViewType;

    private FlutterCommonMapView mFlutterCommonMapView;

    private BaiduMap mBaiduMap;

    private UiSettings mUiSettings;

    public static MapStateUpdateImp getInstance() {
        if (null == sInstance) {
            sInstance = new MapStateUpdateImp();
        }

        return sInstance;
    }


    public MapStateUpdateImp setCommView(FlutterCommonMapView commonMapView) {
        if(null == commonMapView){
            return sInstance;
        }

        if( mFlutterCommonMapView == commonMapView){
            return sInstance;
        }

        mFlutterCommonMapView = commonMapView;

        mBaiduMap = commonMapView.getBaiduMap();
        mViewType = mFlutterCommonMapView.getViewType();
        mBaiduMap = mFlutterCommonMapView.getBaiduMap();
        mUiSettings = mBaiduMap.getUiSettings();
        return sInstance;
    }

    public boolean updateMapState(Map<String, Object> mapOptionsMap) {
        if (null == mapOptionsMap) {
            return false;
        }

        if (null == mFlutterCommonMapView ||
                null == mBaiduMap ||
                null == mUiSettings ||
                 TextUtils.isEmpty(mViewType)) {
            return false;
        }

        // 设置地图类型
        Integer mapType = new TypeConverter<Integer>().getValue(mapOptionsMap, "mapType");
        if (null != mapType) {
            setMapType(mapType);
        }

        // 设置指南针显示位置
        Map<String, Object> compassPosMap = new TypeConverter<Map<String, Object>>().getValue(mapOptionsMap, "compassPosition");
        Point compassPos = FlutterDataConveter.mapToPoint(compassPosMap);
        if (null != compassPos) {
            mBaiduMap.setCompassPosition(compassPos);
        }

        // 设置地图中心点
        Map<String, Object> centerMap = new TypeConverter<Map<String, Object>>().getValue(mapOptionsMap, "center");
        LatLng center = FlutterDataConveter.mapToLatlng(centerMap);
        if (null != center) {
            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(center);
            mBaiduMap.setMapStatus(mapStatusUpdate);
        }

        // 设置地图缩放级别
        Integer zoomLevel = new TypeConverter<Integer>().getValue(mapOptionsMap, "zoomLevel");
        if (null != zoomLevel) {
            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomTo(zoomLevel.floatValue());
            mBaiduMap.setMapStatus(mapStatusUpdate);
        }

        // 设置地图最大、最小缩放级别
        Integer minZoomLevel = new TypeConverter<Integer>().getValue(mapOptionsMap, "minZoomLevel");
        Integer maxZoomLevel = new TypeConverter<Integer>().getValue(mapOptionsMap, "maxZoomLevel");
        if (null != minZoomLevel && null != maxZoomLevel) {
            mBaiduMap.setMaxAndMinZoomLevel(maxZoomLevel.floatValue(), minZoomLevel.floatValue());
        } else if (null == minZoomLevel && null != maxZoomLevel ) {
            mBaiduMap.setMaxAndMinZoomLevel(maxZoomLevel.floatValue(),mBaiduMap.getMinZoomLevel());
        } else if (null != minZoomLevel && null == maxZoomLevel) {
            mBaiduMap.setMaxAndMinZoomLevel(mBaiduMap.getMaxZoomLevel(), minZoomLevel.floatValue());
        }

        // 设置地图旋转角度
        Double rotation = new TypeConverter<Double>().getValue(mapOptionsMap, "rotation");
        if (null != rotation) {
            setRotation(rotation.floatValue());
        }

        // 设置地图俯仰角度
        if (mapOptionsMap.containsKey("overlooking")) {
            Double overlooking = (Double) mapOptionsMap.get("overlooking");
            if (overlooking != null) {
                MapStatus build = new MapStatus.Builder().overlook(overlooking.floatValue()).build();
                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(build);
                mBaiduMap.setMapStatus(mapStatusUpdate);
            }
        }


        // 是否显示3d建筑物
        Boolean buildingsEnabled = new TypeConverter<Boolean>().getValue(mapOptionsMap, "buildingsEnabled");
        if (null != buildingsEnabled) {
            mBaiduMap.setBuildingsEnabled(buildingsEnabled);
        }

        // 设置是否显示poi信息
        Boolean showMapPoi = new TypeConverter<Boolean>().getValue(mapOptionsMap, "showMapPoi");
        if (null != showMapPoi) {
            mBaiduMap.showMapPoi(showMapPoi);
        }

        // 设置是否显示路况信息
        Boolean trafficEnabled = new TypeConverter<Boolean>().getValue(mapOptionsMap, "trafficEnabled");
        if (null != trafficEnabled) {
            mBaiduMap.setTrafficEnabled(trafficEnabled);
        }

        // 限制地图的显示范围
        if (mapOptionsMap.containsKey("limitMapBounds")) {
            Map<String, Object> limitMapRegion = (Map<String, Object>) mapOptionsMap.get("limitMapBounds");
            if (null != limitMapRegion) {
                setMapLimits(limitMapRegion);
            }
        }

        // 设置是否显示百度自有热力图
        Boolean baiduHeatMapEnabled = new TypeConverter<Boolean>().getValue(mapOptionsMap, "baiduHeatMapEnabled");
        if (null != baiduHeatMapEnabled) {
            mBaiduMap.setBaiduHeatMapEnabled(baiduHeatMapEnabled);
        }

        // 设置是否启用手势
        Boolean gesturesEnabled = new TypeConverter<Boolean>().getValue(mapOptionsMap, "gesturesEnabled");
        if (null != gesturesEnabled) {
            mUiSettings.setAllGesturesEnabled(gesturesEnabled);
        }

        // 设置是否开启放大缩小
        Boolean zoomEnabled = new TypeConverter<Boolean>().getValue(mapOptionsMap, "zoomEnabled");
        if (null != zoomEnabled) {
            mUiSettings.setZoomGesturesEnabled(zoomEnabled);
        }

        // 设置地图是否可滑动
        Boolean scrollEnabled = new TypeConverter<Boolean>().getValue(mapOptionsMap, "scrollEnabled");
        if (null != scrollEnabled) {
            mUiSettings.setScrollGesturesEnabled(scrollEnabled);
        }

        // 设置是否开启俯仰角
        Boolean overlookEnabled = new TypeConverter<Boolean>().getValue(mapOptionsMap, "overlookEnabled");
        if (null != overlookEnabled) {
            mUiSettings.setOverlookingGesturesEnabled(overlookEnabled);
        }

        // 设置是否开启旋转角
        Boolean rotateEnabled = new TypeConverter<Boolean>().getValue(mapOptionsMap, "rotateEnabled");
        if (null != rotateEnabled) {
            mUiSettings.setRotateGesturesEnabled(rotateEnabled);
        }

        // 设置比例尺是否显示
        Boolean showMapScaleBar = new TypeConverter<Boolean>().getValue(mapOptionsMap, "showMapScaleBar");
        if (null != showMapScaleBar) {
            showScaleControl(showMapScaleBar);
        }

        // 设置比例尺显示位置
        Map<String, Object> mapScaleBarPosMap = new TypeConverter<Map<String, Object>>().getValue(mapOptionsMap, "mapScaleBarPosition");
        Point mapScaleBarPos = FlutterDataConveter.mapToPoint(mapScaleBarPosMap);
        if (null != mapScaleBarPos) {
            setScaleControlPosition(mapScaleBarPos);
        }

        // 设置百度logo显示位置
        Integer logoPosition = new TypeConverter<Integer>().getValue(mapOptionsMap, "logoPosition");
        if (null != logoPosition
                && logoPosition >= LogoPosition.logoPostionleftBottom.ordinal()
                && logoPosition <= LogoPosition.logoPostionRightTop.ordinal()) {
            setLogoPosition(LogoPosition.values()[logoPosition.intValue()]);
        }

        // 设置地图padding
        Map<String, Double> mapPadding = new TypeConverter<Map<String, Double>>().getValue(mapOptionsMap, "mapPadding");
        if (null != mapPadding) {
            if (mapPadding.containsKey("top") && mapPadding.containsKey("left")
                    && mapPadding.containsKey("bottom") && mapPadding.containsKey("right")) {
                Double top = mapPadding.get("top");
                Double left = mapPadding.get("left");
                Double bottom = mapPadding.get("bottom");
                Double right = mapPadding.get("right");

                if (top != null && left != null && bottom != null && right != null) {
                    int iTop = top.intValue();
                    int iLeft = left.intValue();
                    int iBottom = bottom.intValue();
                    int iRight = right.intValue();
                    mBaiduMap.setViewPadding(iLeft, iTop, iRight, iBottom);
                }
            }
        }


        // 设置双击屏幕放大地图时，是否改变地图中心点为当前点击点
        Boolean changeCenterWithDoubleTouchPointEnabled = new TypeConverter<Boolean>().getValue(mapOptionsMap, "changeCenterWithDoubleTouchPointEnabled");
        if (null != changeCenterWithDoubleTouchPointEnabled) {
            // 这个值，sdk好像取的是反的，这个设个反值
            mUiSettings.setEnlargeCenterWithDoubleClickEnable(!changeCenterWithDoubleTouchPointEnabled);
        }

        // 设置是否开启室内图
        Boolean baseIndoorMapEnabled = new TypeConverter<Boolean>().getValue(mapOptionsMap, "baseIndoorMapEnabled");
        if (null != baseIndoorMapEnabled) {
            mBaiduMap.setIndoorEnable(baseIndoorMapEnabled);
            BMFMapStatus.getsInstance().setBaseIndoorEnable(baseIndoorMapEnabled);
        }

        // 设置是否开启室内图poi
        Boolean showIndoorMapPoi = new TypeConverter<Boolean>().getValue(mapOptionsMap, "showIndoorMapPoi");
        if (null != showIndoorMapPoi) {
            mBaiduMap.showMapIndoorPoi(showIndoorMapPoi);
            BMFMapStatus.getsInstance().setIndoorMapPoiEnable(showIndoorMapPoi);
        }

        // 设置地图可视区域
        Map<String, Object> visibleMapBounds = new TypeConverter<Map<String, Object>>().getValue(mapOptionsMap, "visibleMapBounds");
        LatLngBounds latLngBounds = FlutterDataConveter.mapToLatlngBounds(visibleMapBounds);
        if (null != latLngBounds) {
            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngBounds(latLngBounds);
            mBaiduMap.setMapStatus(mapStatusUpdate);
        }

        return true;
    }

    private void setMapType(Integer mapType) {
        switch (mapType) {
            case Env.MAP_TYPE_NONE:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);
                break;
            case Env.MAP_TYPE_NORMAL:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                break;
            case Env.MAP_TYPE_SATELLITE:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                break;
            default:
                break;
        }
    }

    private void setRotation(float rotation) {
        if (rotation < 0) {
            rotation = rotation + 360;
        }
        MapStatus mapStatus = new MapStatus.Builder().rotate(rotation).build();
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
        mBaiduMap.setMapStatus(mapStatusUpdate);
    }

    private void showScaleControl(boolean showScaleControl) {
        switch(mViewType){
            case Constants.ViewType.sMapView:
                MapView mapView = mFlutterCommonMapView.getMapView();
                if (null != mapView) {
                    mapView.showScaleControl(showScaleControl);
                }
                break;
            case Constants.ViewType.sTextureMapView:
                TextureMapView textureMapView = mFlutterCommonMapView.getTextureMapView();
                if (null != textureMapView) {
                    textureMapView.showScaleControl(showScaleControl);
                }
                break;
            default:
                break;
        }
    }

    private void setScaleControlPosition(Point mapScaleBarPos) {
        switch (mViewType) {
            case Constants.ViewType.sMapView:
                MapView mapView = mFlutterCommonMapView.getMapView();
                if (null != mapView) {
                    mapView.setScaleControlPosition(mapScaleBarPos);
                }
                break;
            case Constants.ViewType.sTextureMapView:
                TextureMapView textureMapView = mFlutterCommonMapView.getTextureMapView();
                if (null != textureMapView) {
                    textureMapView.setScaleControlPosition(mapScaleBarPos);
                }
                break;
            default:
                break;
        }
    }

    private void setLogoPosition(LogoPosition logoPos) {
        switch (mViewType) {
            case Constants.ViewType.sMapView:
                MapView mapView = mFlutterCommonMapView.getMapView();
                if(null != mapView){
                    mapView.setLogoPosition(logoPos);
                }
                break;
            case Constants.ViewType.sTextureMapView:
                TextureMapView textureMapView = mFlutterCommonMapView.getTextureMapView();
                if (null != textureMapView) {
                    textureMapView.setLogoPosition(logoPos);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 限制地图的显示范围
     */
    private void setMapLimits(Map<String, Object> limitMapBounds) {
        LatLngBounds latLngBounds = FlutterDataConveter.mapToLatlngBounds(limitMapBounds);
        if (null == latLngBounds) {
            return;
        }
        mBaiduMap.setMapStatusLimits(latLngBounds);
    }


}
