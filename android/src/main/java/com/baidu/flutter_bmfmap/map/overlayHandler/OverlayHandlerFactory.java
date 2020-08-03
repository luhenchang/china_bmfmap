package com.baidu.flutter_bmfmap.map.overlayHandler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.baidu.flutter_bmfmap.utils.Constants.MethodProtocol.ArclineProtocol;
import com.baidu.flutter_bmfmap.utils.Constants.MethodProtocol.CirclelineProtocol;
import com.baidu.flutter_bmfmap.utils.Constants.MethodProtocol.DotProtocol;
import com.baidu.flutter_bmfmap.utils.Constants.MethodProtocol.GroundProtocol;
import com.baidu.flutter_bmfmap.utils.Constants.MethodProtocol.OverlayProtocol;
import com.baidu.flutter_bmfmap.utils.Constants.MethodProtocol.PolygonProtocol;
import com.baidu.flutter_bmfmap.utils.Constants.MethodProtocol.PolylineProtocol;
import com.baidu.flutter_bmfmap.utils.Constants.MethodProtocol.TextProtocol;
import com.baidu.flutter_bmfmap.utils.Constants.OverlayHandlerType;
import com.baidu.flutter_bmfmap.utils.Env;
import com.baidu.mapapi.map.Arc;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Circle;
import com.baidu.mapapi.map.Dot;
import com.baidu.mapapi.map.GroundOverlay;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.Polygon;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.Text;

import android.util.Log;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class OverlayHandlerFactory {
    private static final String TAG = "OverlayHandlerFactory";

    private static volatile OverlayHandlerFactory sInstance;

    private HashMap<Integer, OverlayHandler> overlayHandlerHashMap;

    private OverlayManagerHandler mOverlayManagerHandler;

    private OverlayHandlerFactory(BaiduMap baiduMap) {
        init(baiduMap);
    }

    public static OverlayHandlerFactory getInstance(BaiduMap baiduMap) {

        if (null == sInstance) {
            synchronized(OverlayHandlerFactory.class) {
                if (null == sInstance) {
                    sInstance = new OverlayHandlerFactory(baiduMap);
                } else {
                    sInstance.updateBaiduMap(baiduMap);
                }
            }
        } else {
            sInstance.updateBaiduMap(baiduMap);
        }

        return sInstance;
    }

    private void updateBaiduMap(BaiduMap baiduMap) {
        if (null == baiduMap) {
            return;
        }

        if(null == overlayHandlerHashMap || overlayHandlerHashMap.isEmpty()){
            init(baiduMap);
        }

        Iterator it = overlayHandlerHashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, OverlayHandler> entry =
                    (Map.Entry<Integer, OverlayHandler>) it.next();
            OverlayHandler overlayHandler = entry.getValue();
            if (null != overlayHandler) {
                overlayHandler.updateBaiduMap(baiduMap);
            }
        }
    }

    private void init(BaiduMap baiduMap) {
        if (null == baiduMap) {
            return;
        }

        mOverlayManagerHandler = new OverlayManagerHandler(baiduMap);
        overlayHandlerHashMap = new HashMap<>();
        overlayHandlerHashMap.put(OverlayHandlerType.CIRCLE_HANDLER, new CircleHandler(baiduMap));
        overlayHandlerHashMap.put(OverlayHandlerType.DOT_HANDLER, new DotHandler(baiduMap));
        overlayHandlerHashMap.put(OverlayHandlerType.POLYGON_HANDLER, new PolygonHandler(baiduMap));
        overlayHandlerHashMap
                .put(OverlayHandlerType.POLYLINE_HANDLER, new PolylineHandler(baiduMap));
        overlayHandlerHashMap.put(OverlayHandlerType.TEXT_HANDLER, new TextHandler(baiduMap));
        overlayHandlerHashMap.put(OverlayHandlerType.ARCLINE_HANDLER, new ArcLineHandler(baiduMap));
        overlayHandlerHashMap.put(OverlayHandlerType.CIRCLE_HANDLER, new CircleHandler(baiduMap));
        overlayHandlerHashMap.put(OverlayHandlerType.GROUND_HANDLER, new GroundHandler(baiduMap));
    }

    public boolean dispatchMethodHandler(MethodCall call, MethodChannel.Result result) {
        if (null == call) {
            if (Env.DEBUG) {
                Log.d(TAG, "dispatchMethodHandler: null == call");
            }
            return false;
        }

        String methodId = call.method;
        Log.d(TAG, "dispatchMethodHandler: " + methodId);
        OverlayHandler overlayHandler = null;
        Overlay overlay;
        int handlerType = -1;
        switch (methodId) {
            case ArclineProtocol.sMapAddArclinelineMethod:
                overlayHandler = overlayHandlerHashMap.get(OverlayHandlerType.ARCLINE_HANDLER);
                break;
            case PolygonProtocol.sMapAddPolygonMethod:
                overlayHandler = overlayHandlerHashMap.get(OverlayHandlerType.POLYGON_HANDLER);
                break;
            case CirclelineProtocol.sMapAddCirclelineMethod:
                overlayHandler = overlayHandlerHashMap.get(OverlayHandlerType.CIRCLE_HANDLER);
                break;
            case PolylineProtocol.sMapAddPolylineMethod:
                overlayHandler = overlayHandlerHashMap.get(OverlayHandlerType.POLYLINE_HANDLER);
                break;
            case DotProtocol.sMapAddDotMethod:
                overlayHandler = overlayHandlerHashMap.get(OverlayHandlerType.DOT_HANDLER);
                break;
            case TextProtocol.sMapAddTextMethod:
                overlayHandler = overlayHandlerHashMap.get(OverlayHandlerType.TEXT_HANDLER);
                break;
            case GroundProtocol.sMapAddGroundMethod:
                overlayHandler = overlayHandlerHashMap.get(OverlayHandlerType.GROUND_HANDLER);
                break;
            case OverlayProtocol.sMapRemoveOverlayMethod:
                OverlayHandler specOverlayHandler = getCurrentOverlayHandler(call);
                mOverlayManagerHandler.setCurrentOverlayHandler(specOverlayHandler);
                overlayHandler = mOverlayManagerHandler;
                break;
            case PolylineProtocol.sMapUpdatePolylineMemberMethod:
                overlayHandler = getCurrentOverlayHandler(call);
                break;
            default:
                break;
        }

        if (null == overlayHandler) {
            return false;
        }

        Map<String, Overlay> overlayMap = overlayHandler.handlerMethodCall(call, result);

        if (null == overlayMap) {
            return false;
        }

        mOverlayManagerHandler.addOverlay(overlayMap);
        return true;
    }

    private Overlay getCurrentOverlay(MethodCall call) {
        Map<String, Object> argument = call.arguments();
        if (null == argument) {
            if (Env.DEBUG) {
                Log.d(TAG, "argument is null");
            }

            return null;
        }

        if (!argument.containsKey("id")) {
            return null;
        }

        String id = (String) argument.get("id");

        return mOverlayManagerHandler.getOverlay(id);
    }

    private int getHandlerType(Overlay overlay) {
        int handlerType = -1;
        if (overlay instanceof Polyline) {
            handlerType = OverlayHandlerType.POLYLINE_HANDLER;
        } else if (overlay instanceof Polygon) {
            handlerType = OverlayHandlerType.POLYGON_HANDLER;
        } else if (overlay instanceof Arc) {
            handlerType = OverlayHandlerType.ARCLINE_HANDLER;
        } else if (overlay instanceof Circle) {
            handlerType = OverlayHandlerType.CIRCLE_HANDLER;
        } else if (overlay instanceof Dot) {
            handlerType = OverlayHandlerType.DOT_HANDLER;
        } else if (overlay instanceof GroundOverlay) {
            handlerType = OverlayHandlerType.GROUND_HANDLER;
        } else if (overlay instanceof Text) {
            handlerType = OverlayHandlerType.TEXT_HANDLER;
        }

        return handlerType;
    }

    public void clean(){
        if(null == overlayHandlerHashMap || overlayHandlerHashMap.size() == 0) {
            return;
        }

        OverlayHandler overlayHandler= null;
        Iterator iterator = overlayHandlerHashMap.values().iterator();
        while (iterator.hasNext()){
            overlayHandler = (OverlayHandler) iterator.next();
            if(null == overlayHandler){
                continue;
            }

            overlayHandler.clean();
        }
    }

    private OverlayHandler getCurrentOverlayHandler(MethodCall call) {
        if (null == call) {
            return null;
        }

        Overlay overlay = getCurrentOverlay(call);
        if (null == overlay) {
            return null;
        }
        int handlerType = getHandlerType(overlay);
        OverlayHandler overlayHandler =   overlayHandlerHashMap.get(handlerType);
        if( null != overlayHandler) {
            overlayHandler.setCurrentOverlay(overlay);
        }

        return overlayHandler;
    }

}