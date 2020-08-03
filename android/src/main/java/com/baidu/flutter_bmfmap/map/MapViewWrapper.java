package com.baidu.flutter_bmfmap.map;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.TextureMapView;

public class MapViewWrapper extends FlutterCommonMapView {
    FlutterMapView mFlutterMapView;

    public MapViewWrapper(FlutterMapView mapView, String viewType) {
        mFlutterMapView = mapView;
        mViewType = viewType;
    }

    @Override
    public MapView getMapView() {
        return mFlutterMapView.getMapView();
    }

    public FlutterMapView getFlutterMapView() {
        return mFlutterMapView;
    }

    @Override
    public TextureMapView getTextureMapView() {
        return null;
    }
}