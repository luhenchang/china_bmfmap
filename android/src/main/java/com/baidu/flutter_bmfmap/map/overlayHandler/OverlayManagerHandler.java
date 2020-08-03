package com.baidu.flutter_bmfmap.map.overlayHandler;

import java.util.HashMap;
import java.util.Map;

import com.baidu.flutter_bmfmap.utils.Constants.MethodProtocol.OverlayProtocol;
import com.baidu.flutter_bmfmap.utils.Env;
import com.baidu.flutter_bmfmap.utils.converter.TypeConverter;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Overlay;

import android.text.TextUtils;
import android.util.Log;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class OverlayManagerHandler extends OverlayHandler {
    private static final String TAG = "OverlayManagerHandler";

    private HashMap<String, Overlay> mOverlayMap = new HashMap<>();

    private OverlayHandler mCurrentOverlayHandler;

    public OverlayManagerHandler(BaiduMap baiduMap) {
        super(baiduMap);
    }

    @Override
    public Map<String, Overlay> handlerMethodCall(MethodCall call, MethodChannel.Result result) {
        if (Env.DEBUG) {
            Log.d(TAG, "handlerMethodCall enter");
            //result.success(false);
        }

        Map<String, Object> argument = call.arguments();
        if (null == argument) {
            if (Env.DEBUG) {
                Log.d(TAG, "argument is null");
            }
            result.success(false);
            return null;
        }

        boolean ret = false;
        String methodId = call.method;
        switch (methodId) {
            case OverlayProtocol.sMapRemoveOverlayMethod:
                ret = removeOverlay(argument);
                break;
            default:
                break;
        }

        result.success(ret);

        return null;
    }

    public void addOverlay(Map<String, Overlay> overlayMap) {
        mOverlayMap.putAll(overlayMap);
    }

    public Overlay getOverlay(String id) {
        return mOverlayMap.get(id);
    }

    public void setCurrentOverlayHandler(OverlayHandler overlayHandler) {
        mCurrentOverlayHandler = overlayHandler;
    }

    /**
     * 移除overlay
     *
     * @param argument
     * @return
     */
    private boolean removeOverlay(Map<String, Object> argument) {
        String id = new TypeConverter<String>().getValue(argument, "id");
        if (TextUtils.isEmpty(id)) {
            return false;
        }

        Overlay overlay = mOverlayMap.get(id);
        if (null == overlay) {
            if (Env.DEBUG) {
                Log.d(TAG, "not found overlay with id:" + id);
            }
            return false;
        }

        overlay.remove();
        mOverlayMap.remove(id);
        if(null != mCurrentOverlayHandler) {
            mCurrentOverlayHandler.clean(id);
            mCurrentOverlayHandler = null;
        }

        if (Env.DEBUG) {
            Log.d(TAG, "remove Overlay success");
        }
        return true;
    }
}