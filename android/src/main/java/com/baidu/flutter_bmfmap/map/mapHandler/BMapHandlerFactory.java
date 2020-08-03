package com.baidu.flutter_bmfmap.map.mapHandler;

import android.content.Context;
import android.util.Log;

import com.baidu.flutter_bmfmap.map.FlutterCommonMapView;
import com.baidu.flutter_bmfmap.utils.Constants;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import com.baidu.flutter_bmfmap.utils.Constants.MethodProtocol.CustomMapProtocol;
import com.baidu.flutter_bmfmap.utils.Constants.MethodProtocol.MapStateProtocol;
import com.baidu.flutter_bmfmap.utils.Constants.MethodProtocol.HeatMapProtocol;
import com.baidu.flutter_bmfmap.utils.Constants.MethodProtocol.TileMapProtocol;
import com.baidu.flutter_bmfmap.utils.Constants.MethodProtocol.MarkerProtocol;
import com.baidu.flutter_bmfmap.utils.Constants.MethodProtocol.InfoWindowProtocol;
import com.baidu.flutter_bmfmap.utils.Constants.MethodProtocol.ProjectionMethodId;
import com.baidu.flutter_bmfmap.utils.Env;

public class BMapHandlerFactory{

    private static volatile BMapHandlerFactory sInstance;
    private HashMap<Integer, BMapHandler> mMapHandlerHashMap;

    public static BMapHandlerFactory getInstance(FlutterCommonMapView mapView) {

        if (null == sInstance) {
            synchronized (BMapHandlerFactory.class) {
                if (null == sInstance) {
                    sInstance = new BMapHandlerFactory(mapView);
                } else {
                    sInstance.updateMapView(mapView);
                }
            }
        } else {
            sInstance.updateMapView(mapView);
        }

        return sInstance;
    }

    private void updateMapView(FlutterCommonMapView mapView) {
        if (null == mapView) {
            return;
        }

        if(null == mMapHandlerHashMap || mMapHandlerHashMap.isEmpty()){
            init(mapView);
        }

        Iterator it = mMapHandlerHashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, BMapHandler> entry = (Map.Entry<Integer, BMapHandler>) it.next();
            BMapHandler bMapHandler = entry.getValue();
            if (null != bMapHandler) {
                bMapHandler.updateMapView(mapView);
            }
        }
    }

    private BMapHandlerFactory(FlutterCommonMapView mapView) {
        init(mapView);
    }

    private void init(FlutterCommonMapView mapView) {
        if (null == mapView) {
            return;
        }

        mMapHandlerHashMap = new HashMap<>();

        mMapHandlerHashMap.put(Constants.BMapHandlerType.CUSTOM_MAP,new CustomMapHandler(mapView));
        mMapHandlerHashMap.put(Constants.BMapHandlerType.MAP_STATE,new MapStateHandler(mapView));
        mMapHandlerHashMap.put(Constants.BMapHandlerType.INDOOR_MAP, new IndoorMapHandler(mapView));
        mMapHandlerHashMap.put(Constants.BMapHandlerType.MAP_UPDATE, new MapUpdateHandler(mapView));
        mMapHandlerHashMap.put(Constants.BMapHandlerType.HEAT_MAP, new HeatMapHandler(mapView));
        mMapHandlerHashMap.put(Constants.BMapHandlerType.TILE_MAP, new TileMapHandler(mapView));
        mMapHandlerHashMap.put(Constants.BMapHandlerType.INFOWINDOW_HANDLER, new InfoWindowHandler(mapView));
        mMapHandlerHashMap.put(Constants.BMapHandlerType.MARKER_HANDLER, new MarkerHandler(mapView));
        mMapHandlerHashMap.put(Constants.BMapHandlerType.LOCATION_LAYER, new LocationLayerHandler(mapView));
        mMapHandlerHashMap.put(Constants.BMapHandlerType.PROJECTION, new ProjectionHandler(mapView));
    }

    public boolean dispatchMethodHandler(Context context, MethodCall call, MethodChannel.Result result,
                                   MethodChannel methodChannel) {
        if (null == call) {
            return false;
        }

        String methodId = call.method;
        if(Env.DEBUG){
            Log.d("BMapHandlerFactory", "dispatchMethodHandler: " + methodId);
        }
        BMapHandler bMapHandler = null;
        switch (methodId) {
            case CustomMapProtocol.sMapSetCustomMapStyleEnableMethod:
            case CustomMapProtocol.sMapSetCustomMapStylePathMethod:
            case CustomMapProtocol.sMapSetCustomMapStyleWithOptionMethod:
                bMapHandler = mMapHandlerHashMap.get(Constants.BMapHandlerType.CUSTOM_MAP);
                break;
            case Constants.MethodProtocol.IndoorMapProtocol.sShowBaseIndoorMapMethod:
            case Constants.MethodProtocol.IndoorMapProtocol.sShowBaseIndoorMapPoiMethod:
            case Constants.MethodProtocol.IndoorMapProtocol.sSwitchBaseIndoorMapFloorMethod:
            case Constants.MethodProtocol.IndoorMapProtocol.sGetFocusedBaseIndoorMapInfoMethod:
                bMapHandler = mMapHandlerHashMap.get(Constants.BMapHandlerType.INDOOR_MAP);
                break;
            case MapStateProtocol.sMapUpdateMethod:
            case  MapStateProtocol.sMapSetVisibleMapBoundsMethod:
            case  MapStateProtocol.sMapSetVisibleMapBoundsWithPaddingMethod:
            case MapStateProtocol.sMapSetCompassImageMethod:
            case MapStateProtocol.sMapSetCustomTrafficColorMethod:
            case MapStateProtocol.sMapTakeSnapshotMethod:
            case MapStateProtocol.sMapTakeSnapshotWithRectMethod:
            case MapStateProtocol.sMapDidUpdateWidget:
            case MapStateProtocol.sMapReassemble:
                bMapHandler = mMapHandlerHashMap.get(Constants.BMapHandlerType.MAP_STATE);
                break;
            case MapStateProtocol.sMapZoomInMethod:
            case MapStateProtocol.sMapZoomOutMethod:
            case MapStateProtocol.sMapSetCenterCoordinateMethod:
            case MapStateProtocol.sMapSetCenterZoomMethod:
            case MapStateProtocol.sMapSetMapStatusMethod:
            case MapStateProtocol.sMapSetScrollByMethod:
            case MapStateProtocol.sMapSetZoomByMethod:
            case MapStateProtocol.sMapSetZoomPointByMethod:
            case MapStateProtocol.sMapSetZoomToMethod:
            case MapStateProtocol.sMapGetMapStatusMethod:
                bMapHandler = mMapHandlerHashMap.get(Constants.BMapHandlerType.MAP_UPDATE);
                break;
            case HeatMapProtocol.sMapAddHeatMapMethod:
            case HeatMapProtocol.sMapRemoveHeatMapMethod:
            case HeatMapProtocol.sShowHeatMapMethod:
                bMapHandler = mMapHandlerHashMap.get(Constants.BMapHandlerType.HEAT_MAP);
                break;
            case TileMapProtocol.sAddTileMapMethod:
            case TileMapProtocol.sRemoveTileMapMethod:
                bMapHandler = mMapHandlerHashMap.get(Constants.BMapHandlerType.TILE_MAP);
                break;
            case MarkerProtocol.sMapAddMarkerMethod:
            case MarkerProtocol.sMapAddMarkersMethod:
            case MarkerProtocol.sMapRemoveMarkerMethod:
            case MarkerProtocol.sMapRemoveMarkersMethod:
            case MarkerProtocol.sMapDidSelectMarkerMethod:
            case MarkerProtocol.sMapDidDeselectMarkerMethod:
            case MarkerProtocol.sMapCleanAllMarkersMethod:
            case MarkerProtocol.sMapUpdateMarkerMemberMethod:
                bMapHandler = mMapHandlerHashMap.get(Constants.BMapHandlerType.MARKER_HANDLER);
                break;
            case InfoWindowProtocol.sAddInfoWindowMapMethod:
            case InfoWindowProtocol.sRemoveInfoWindowMapMethod:
            case InfoWindowProtocol.sAddInfoWindowsMapMethod:
                bMapHandler = mMapHandlerHashMap.get(Constants.BMapHandlerType.INFOWINDOW_HANDLER);
                break;
            case Constants.LocationLayerMethodId.sMapShowUserLocationMethod:
            case Constants.LocationLayerMethodId.sMapUpdateLocationDataMethod:
            case Constants.LocationLayerMethodId.sMapUserTrackingModeMethod:
            case Constants.LocationLayerMethodId.sMapUpdateLocationDisplayParamMethod:
                bMapHandler = mMapHandlerHashMap.get(Constants.BMapHandlerType.LOCATION_LAYER);
                break;
            case ProjectionMethodId.sFromScreenLocation:
            case ProjectionMethodId.sToScreenLocation:
                bMapHandler = mMapHandlerHashMap.get(Constants.BMapHandlerType.PROJECTION);
                break;
            default:
                if(methodId.startsWith("flutter_bmfmap/map/get")){
                    bMapHandler = mMapHandlerHashMap.get(Constants.BMapHandlerType.MAP_UPDATE);
                }
                break;
        }

        if (null == bMapHandler) {
            return false;
        }

        bMapHandler.handlerMethodCallResult(context,call, result);

        return true;
    }

    public void clean(){
        if (null == mMapHandlerHashMap || mMapHandlerHashMap.size() == 0) {
            return;
        }

        BMapHandler bMapHandler = null;
        Iterator iterator = mMapHandlerHashMap.values().iterator();
        while (iterator.hasNext()){
            bMapHandler = (BMapHandler) iterator.next();
            if(null == bMapHandler){
                continue;
            }

            bMapHandler.clean();
        }
    }
}
