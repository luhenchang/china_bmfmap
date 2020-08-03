package com.baidu.flutter_bmfmap.map;

import java.util.Map;

public abstract class FlutterBaseMapView {

    protected String mViewType;

    protected boolean mResume = false;

    protected int mGetViewCount = 0;

    protected abstract void init(int viewId, Object args);

    protected abstract void initMapView(Object args, FlutterCommonMapView flutterCommonMapView);

    protected void initMapStatus(Map<String, Object> mapOptionsMap,
                                 FlutterCommonMapView flutterCommonMapView) {
        if (null == mapOptionsMap) {
            return;
        }

        MapStateUpdateImp.getInstance()
                .setCommView(flutterCommonMapView)
                .updateMapState(mapOptionsMap);
    }
}