package com.baidu.flutter_bmfmap.map;

import static com.baidu.flutter_bmfmap.utils.Constants.MAX_GET_VIEW_CNT_BY_FLUTTER_RESIZE;

import java.util.Map;

import com.baidu.flutter_bmfmap.BMFHandlerHelper;
import com.baidu.flutter_bmfmap.map.mapHandler.BMapHandlerFactory;
import com.baidu.flutter_bmfmap.map.overlayHandler.OverlayHandlerFactory;
import com.baidu.flutter_bmfmap.utils.Constants;
import com.baidu.flutter_bmfmap.utils.Env;
import com.baidu.mapapi.map.MapView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.platform.PlatformView;

public class FlutterMapView extends FlutterBaseMapView implements PlatformView {

    private static final String TAG = "FlutterMapView";

    private MapView mMapView;

    private Context mContext;

    private BinaryMessenger mMessager;

    private BMFHandlerHelper mBMFHandlerHelper;

    private MethodChannel mMethodChannel;

    private EventChannel mEventChannel;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constants.sConfigChangedAction.equals(action) && !mResume) {
                mResume = true;
            }
        }
    };

    public FlutterMapView(Context context,
                          BinaryMessenger messenger,
                          int viewId,
                          Object args,
                          String viewType) {
        if (Env.DEBUG) {
            Log.d(TAG, "FlutterMapView");
        }
        mContext = context;
        mMessager = messenger;
        mViewType = viewType;
        init(viewId, args);
    }

    protected void init(int viewId, Object args) {
        if (Env.DEBUG) {
            Log.d(TAG, "init");
        }

        mMapView = new MapView(mContext);
        FlutterCommonMapView mapViewWrapper = new MapViewWrapper(this, mViewType);
        initMapView(args, mapViewWrapper);

        mMethodChannel = new MethodChannel(mMessager,
                Constants.VIEW_METHOD_CHANNEL_PREFIX + (char) (viewId + 97));

        mEventChannel = new EventChannel(mMessager,
                Constants.VIEW_EVENT_CHANNEL_PREFIX + (char) (viewId + 97));

        mBMFHandlerHelper =
                new BMFHandlerHelper(mContext, mapViewWrapper, mMethodChannel, mEventChannel);

        new MapListener(new MapViewWrapper(this, mViewType), mMethodChannel);

        IntentFilter intentFilter = new IntentFilter(Constants.sConfigChangedAction);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mReceiver, intentFilter);

        if (Env.DEBUG) {
            Log.d(TAG, "init success");
        }
    }

    protected void initMapView(Object args, FlutterCommonMapView flutterCommonMapView) {
        if (null == mContext) {
            return;
        }

        Map<String, Object> mapOptionsMap = (Map<String, Object>) args;
        if (null == mapOptionsMap) {
            return;
        }

        initMapStatus(mapOptionsMap, flutterCommonMapView);
    }

    @Override
    public View getView() {
        if (Env.DEBUG) {
            Log.d(TAG, "getView");
        }

        if (mResume) {
            mGetViewCount++;
        }

        if (mGetViewCount >= MAX_GET_VIEW_CNT_BY_FLUTTER_RESIZE - 1) {
            mMapView.onResume();
            mResume = false;
            mGetViewCount = 0;
        }

        return mMapView;
    }

    @Override
    public void onFlutterViewAttached(@NonNull View flutterView) {
        if (Env.DEBUG) {
            Log.d(TAG, "onFlutterViewAttached");
        }
        if (null != mMapView) {
            mMapView.onResume();
        }
    }

    @Override
    public void onFlutterViewDetached() {
        if (Env.DEBUG) {
            Log.d(TAG, "onFlutterViewDetached");
        }
        if (null != mMapView) {
            mMapView.onPause();
        }
    }

    @Override
    public void dispose() {
        if (Env.DEBUG) {
            Log.d(TAG, "dispose");
        }

        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mReceiver);

        BMapHandlerFactory.getInstance(null).clean();
        OverlayHandlerFactory.getInstance(null).clean();

        if (null != mMapView) {
            mMapView.onDestroy();
        }
    }

    public void setResumeState(boolean resume) {
        mResume = true;
    }

    public MapView getMapView() {
        return mMapView;
    }
}
