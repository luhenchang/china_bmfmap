package com.baidu.flutter_bmfmap.map;

import com.baidu.flutter_bmfmap.utils.Constants;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.TextureMapView;

import java.util.Map;

public abstract class FlutterCommonMapView{
    protected String mViewType;
    public String getViewType(){
        return mViewType;
    }



    public void setmViewType(String viewType){
        mViewType = viewType;
    }

    abstract public MapView getMapView();
    abstract public TextureMapView getTextureMapView();

    public BaiduMap getBaiduMap(){
        BaiduMap baiduMap = null;
        switch (mViewType){
            case Constants.ViewType.sMapView:
                baiduMap = getBaiduMapFromMapView();
                break;
            case Constants.ViewType.sTextureMapView:
                baiduMap = getBaiduMapFromTextureMapView();
                break;
            default:
                break;
        }

        return baiduMap;
    }

    private BaiduMap getBaiduMapFromMapView(){
        MapView mapView = this.getMapView();
        if(null == mapView){
            return null;
        }

        return  mapView.getMap();
    }

    private BaiduMap getBaiduMapFromTextureMapView(){
        TextureMapView textureMapView = this.getTextureMapView();
        if(null == textureMapView){
            return null;
        }

        return  textureMapView.getMap();
    }
}