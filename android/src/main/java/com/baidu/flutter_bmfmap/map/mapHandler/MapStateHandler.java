package com.baidu.flutter_bmfmap.map.mapHandler;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import com.baidu.flutter_bmfmap.map.FlutterCommonMapView;
import com.baidu.flutter_bmfmap.map.FlutterMapView;
import com.baidu.flutter_bmfmap.map.MapStateUpdateImp;
import com.baidu.flutter_bmfmap.map.MapViewWrapper;
import com.baidu.flutter_bmfmap.utils.Constants;
import com.baidu.flutter_bmfmap.utils.converter.FlutterDataConveter;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.map.WinRound;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.text.TextUtils;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class MapStateHandler extends BMapHandler {
    private static final String TAG = MapStateHandler.class.getSimpleName();
    private BaiduMap mBaiduMap;

    private String mViewType;

    public MapStateHandler(FlutterCommonMapView mapView) {
        super(mapView);
        mViewType = mapView.getViewType();
        mBaiduMap = mapView.getBaiduMap();
    }

    @Override
    public void updateMapView(FlutterCommonMapView mapView) {
        super.updateMapView(mapView);
        if (null != mapView) {
            mBaiduMap = mapView.getBaiduMap();
        }
    }

    @Override
    public void handlerMethodCallResult(Context context, MethodCall call,
                                        MethodChannel.Result result) {
        if (null == call) {
            return;
        }
        String methodId = call.method;
        if (TextUtils.isEmpty(methodId)) {
            return;
        }

        switch (methodId) {
            case Constants.MethodProtocol.MapStateProtocol.sMapUpdateMethod:
                setMapUpdate(call, result);
                break;
            case Constants.MethodProtocol.MapStateProtocol.sMapTakeSnapshotMethod:
                mapSnapshot(result);
                break;
            case Constants.MethodProtocol.MapStateProtocol.sMapTakeSnapshotWithRectMethod:
                snapShotRect(call, result);
                break;
            case Constants.MethodProtocol.MapStateProtocol.sMapSetCompassImageMethod:
                setCompassImage(call, result);
                break;
            case Constants.MethodProtocol.MapStateProtocol.sMapSetCustomTrafficColorMethod:
                setCustomTrafficColor(call, result);
                break;
            case Constants.MethodProtocol.MapStateProtocol.sMapSetVisibleMapBoundsMethod:
                setNewCoordinateBounds(call, result);
                break;
            case Constants.MethodProtocol.MapStateProtocol.sMapSetVisibleMapBoundsWithPaddingMethod:
                setVisibleMapBoundsWithPaddingMethod(call, result);
                break;
            case Constants.MethodProtocol.MapStateProtocol.sMapDidUpdateWidget:
            case Constants.MethodProtocol.MapStateProtocol.sMapReassemble:
                resumeMap();
                break;
            default:
                break;
        }
    }

    /**
     * 自定义路况颜色
     */
    private void setCustomTrafficColor(MethodCall call, MethodChannel.Result result) {
        Map<String, Object> argument = call.arguments();
        if (null == argument || null == mBaiduMap) {
            result.success(false);
            return;
        }
        if (!argument.containsKey("smooth") || !argument.containsKey("slow")
                || !argument.containsKey("congestion") || !argument
                .containsKey("severeCongestion")) {
            result.success(false);
            return;
        }

        String smooth = (String) argument.get("smooth");
        String slow = (String) argument.get("slow");
        String congestion = (String) argument.get("congestion");
        String severeCongestion = (String) argument.get("severeCongestion");
        if (smooth == null || slow == null || congestion == null || severeCongestion == null) {
            result.success(false);
            return;
        }
        String color = "#";
        String severeCongestionColor = color.concat(severeCongestion);
        String congestionColor = color.concat(congestion);
        String slowColor = color.concat(slow);
        String smoothColor = color.concat(smooth);

        mBaiduMap.setCustomTrafficColor(severeCongestionColor, congestionColor, slowColor,
                smoothColor);
        result.success(true);
    }

    /**
     * 设置罗盘图片
     */
    private void setCompassImage(MethodCall call, MethodChannel.Result result) {
        Map<String, Object> argument = call.arguments();
        if (null == argument || null == mBaiduMap) {
            result.success(false);
            return;
        }

        if (!argument.containsKey("imagePath")) {
            result.success(false);
            return;
        }

        String imagePath = (String) argument.get("imagePath");
        if (imagePath == null) {
            result.success(false);
            return;
        }
        BitmapDescriptor bitmapDescriptor =
                BitmapDescriptorFactory.fromAsset("flutter_assets/" + imagePath);
        Bitmap bitmap = bitmapDescriptor.getBitmap();
        mBaiduMap.setCompassIcon(bitmap);
        result.success(true);
    }

    /**
     * 选取区域截图
     */
    private void snapShotRect(MethodCall call, final MethodChannel.Result result) {
        Map<String, Object> argument = call.arguments();
        if (null == argument || null == mBaiduMap) {
            result.success(null);
            return;
        }
        if (!argument.containsKey("rect")) {
            result.success(null);
            return;
        }
        Map<String, Object> rect = (Map<String, Object>) argument.get("rect");

        WinRound winRound = FlutterDataConveter.BMFRectToWinRound(rect);
        if (null == winRound) {
            result.success(null);
            return;
        }

        if (winRound.left > winRound.right || winRound.top > winRound.bottom) {
            result.success(null);
            return;
        }

        if (winRound.right - winRound.left > getMapViewWidth()
                || winRound.bottom - winRound.top > getMapViewHeight()) {
            result.success(null);
            return;
        }

        // 矩形区域保证left <= right top <= bottom 否则截屏失败
        Rect recta = new Rect(winRound.left, winRound.top, winRound.right, winRound.bottom);
        mBaiduMap.snapshotScope(recta, new BaiduMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {
                if (null == bitmap) {
                    result.success(null);
                    return;
                }
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                result.success(byteArrayOutputStream.toByteArray());
            }
        });
    }

    private int getMapViewWidth() {
        int width = 0;
        switch (mViewType) {
            case Constants.ViewType.sMapView:
                MapView mapView = mMapView.getMapView();
                width = null != mapView ? mapView.getWidth() : 0;
                break;
            case Constants.ViewType.sTextureMapView:
                TextureMapView textureMapView = mMapView.getTextureMapView();
                width = null != textureMapView ? textureMapView.getWidth() : 0;
                break;
            default:
                break;
        }

        return width;
    }

    private int getMapViewHeight() {
        int height = 0;
        switch (mViewType) {
            case Constants.ViewType.sMapView:
                MapView mapView = mMapView.getMapView();
                height = null != mapView ? mapView.getHeight() : 0;
                break;
            case Constants.ViewType.sTextureMapView:
                TextureMapView textureMapView = mMapView.getTextureMapView();
                height = null != textureMapView ? textureMapView.getHeight() : 0;
                break;
            default:
                break;
        }

        return height;
    }

    /**
     * 截图 全部地图展示区域
     */
    private void mapSnapshot(final MethodChannel.Result result) {
        if (null == mBaiduMap) {
            result.success(null);
            return;
        }
        mBaiduMap.snapshot(new BaiduMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {
                if (null == bitmap) {
                    result.success(null);
                    return;
                }
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                result.success(byteArrayOutputStream.toByteArray());
            }
        });
    }

    /**
     * 更新地图
     */
    private void setMapUpdate(MethodCall call, MethodChannel.Result result) {
        Map<String, Object> argument = call.arguments();
        if (null == argument || null == mBaiduMap) {
            result.success(false);
            return;
        }

        boolean ret = MapStateUpdateImp.getInstance()
                .setCommView(mMapView)
                .updateMapState(argument);

        result.success(ret);
    }

    /**
     * 设置显示在屏幕中的地图地理范围
     */
    private void setNewCoordinateBounds(MethodCall call, MethodChannel.Result result) {
        Map<String, Object> argument = call.arguments();
        if (null == argument || null == mBaiduMap) {
            result.success(false);
            return;
        }

        if (!argument.containsKey("visibleMapBounds")) {
            result.success(false);
            return;
        }

        Map<String, Object> visibleMapBounds =
                (Map<String, Object>) argument.get("visibleMapBounds");
        if (null == visibleMapBounds) {
            result.success(false);
            return;
        }

        LatLngBounds latLngBounds = visibleMapBoundsImp(visibleMapBounds);
        if (null == latLngBounds) {
            result.success(false);
            return;
        }

        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngBounds(latLngBounds));
        result.success(true);
    }

    private LatLngBounds visibleMapBoundsImp(Map<String, Object> visibleMapBounds) {
        if (!visibleMapBounds.containsKey("northeast") || !visibleMapBounds
                .containsKey("southwest")) {
            return null;
        }
        HashMap<String, Double> northeast =
                (HashMap<String, Double>) visibleMapBounds.get("northeast");
        HashMap<String, Double> southwest =
                (HashMap<String, Double>) visibleMapBounds.get("southwest");
        if (null == northeast || null == southwest) {
            return null;
        }
        if (!northeast.containsKey("latitude") || !northeast.containsKey("longitude")
                || !southwest.containsKey("latitude") || !southwest.containsKey("longitude")) {
            return null;
        }

        Double northeastLatitude = northeast.get("latitude");
        Double northeastLongitude = northeast.get("longitude");
        Double southwestLatitude = southwest.get("latitude");
        Double southwestLongitude = southwest.get("longitude");

        if (null == northeastLatitude || null == northeastLongitude
                || null == southwestLatitude || null == southwestLongitude) {
            return null;
        }

        LatLng northeastLatLng = new LatLng(northeastLatitude, northeastLongitude);
        LatLng southwestLatLng = new LatLng(southwestLatitude, southwestLongitude);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(northeastLatLng);
        builder.include(southwestLatLng);

        return builder.build();
    }

    /**
     * 根据Padding设置地理范围的合适缩放级别
     */
    private void setVisibleMapBoundsWithPaddingMethod(MethodCall call,
                                                      MethodChannel.Result result) {
        Map<String, Object> argument = call.arguments();
        if (null == argument || null == mBaiduMap) {
            result.success(false);
            return;
        }

        if (!argument.containsKey("visibleMapBounds") || !argument.containsKey("insets")) {
            result.success(false);
            return;
        }

        Map<String, Object> visibleMapBounds =
                (Map<String, Object>) argument.get("visibleMapBounds");
        Map<String, Double> insets = (Map<String, Double>) argument.get("insets");
        if (null == visibleMapBounds || null == insets) {
            result.success(false);
            return;
        }

        LatLngBounds latLngBounds = visibleMapBoundsImp(visibleMapBounds);
        if (null == latLngBounds) {
            result.success(false);
            return;
        }

        if (!insets.containsKey("left") || !insets.containsKey("top")
                || !insets.containsKey("right") || !insets.containsKey("bottom")) {
            result.success(false);
            return;
        }
        Double left = insets.get("left");
        Double top = insets.get("top");
        Double right = insets.get("right");
        Double bottom = insets.get("bottom");

        if (null == left || null == top || null == right || null == bottom) {
            result.success(false);
            return;
        }
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngBounds(latLngBounds,
                left.intValue(), top.intValue(), right.intValue(), bottom.intValue());
        mBaiduMap.setMapStatus(mapStatusUpdate);
        result.success(true);
    }

    private void updateMap() {
        MapStatus.Builder builder = new MapStatus.Builder();
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(builder.build());
        mBaiduMap.setMapStatus(mapStatusUpdate);
    }

    private void resumeMap() {
        MapViewWrapper mapViewWrapper = (MapViewWrapper) mMapView;
        FlutterMapView flutterMapView = mapViewWrapper.getFlutterMapView();
        flutterMapView.setResumeState(true);
    }

}
