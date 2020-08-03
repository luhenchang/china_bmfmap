package com.baidu.flutter_bmfmap.map;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.TextureMapView;

class TextureMapViewWrapper extends FlutterCommonMapView {

    private TextureMapView mTextureMapView;

    public TextureMapViewWrapper(TextureMapView textureMapView, String viewType){
        mTextureMapView = textureMapView;
        mViewType = viewType;
    }

    @Override
    public MapView getMapView() {
        return null;
    }

    @Override
    public TextureMapView getTextureMapView() {
        return mTextureMapView;
    }
}