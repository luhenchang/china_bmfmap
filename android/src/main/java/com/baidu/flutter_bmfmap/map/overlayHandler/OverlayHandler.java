package com.baidu.flutter_bmfmap.map.overlayHandler;

import java.util.Map;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Overlay;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public abstract class OverlayHandler {
    protected BaiduMap mBaiduMap;

    protected Overlay mCurrentOverlay;

    public OverlayHandler(BaiduMap baiduMap) {
        this.mBaiduMap = baiduMap;
    }

    public abstract Map<String, Overlay> handlerMethodCall(MethodCall call,
                                                           MethodChannel.Result result);

    public void updateBaiduMap(BaiduMap baiduMap) {
        mBaiduMap = baiduMap;
    }

    public void setCurrentOverlay(Overlay overlay){
        mCurrentOverlay = overlay;
    }

    /**
     * 清理所有
     */
    public void clean(){}

    /**
     * 清理指定id的overlay
     * @param id
     */
    public void clean(String id) {

    }
}
