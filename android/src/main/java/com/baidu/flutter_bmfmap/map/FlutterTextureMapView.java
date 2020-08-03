package com.baidu.flutter_bmfmap.map;

import static com.baidu.flutter_bmfmap.utils.Constants.MAX_GET_VIEW_CNT_BY_FLUTTER_RESIZE;

import java.util.Map;

import com.baidu.flutter_bmfmap.BMFHandlerHelper;
import com.baidu.flutter_bmfmap.utils.Constants;
import com.baidu.flutter_bmfmap.utils.Env;
import com.baidu.mapapi.map.TextureMapView;

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

public class FlutterTextureMapView extends FlutterBaseMapView implements PlatformView {

    private static final String TAG = "FlutterMapView";

    private TextureMapView mTextureMapView;

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

    public FlutterTextureMapView(Context context,
                                 BinaryMessenger messenger,
                                 int viewId,
                                 Object args,
                                 String viewType) {
        Log.d(TAG, "FlutterMapView");
        mContext = context;
        mMessager = messenger;
        mViewType = viewType;
        init(viewId, args);
    }

    protected void init(int viewId, Object args) {
        if (Env.DEBUG) {
            Log.d(TAG, "init");
        }

        mTextureMapView = new TextureMapView(mContext);
        FlutterCommonMapView flutterCommonMapView =
                new TextureMapViewWrapper(mTextureMapView, mViewType);
        initMapView(args, flutterCommonMapView);

        mMethodChannel = new MethodChannel(mMessager,
                Constants.VIEW_METHOD_CHANNEL_PREFIX + (char) (viewId + 97));

        mEventChannel = new EventChannel(mMessager,
                Constants.VIEW_EVENT_CHANNEL_PREFIX + (char) (viewId + 97));

        mBMFHandlerHelper =
                new BMFHandlerHelper(mContext, flutterCommonMapView, mMethodChannel, mEventChannel);

        new MapListener(new TextureMapViewWrapper(mTextureMapView, mViewType), mMethodChannel);

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
            mTextureMapView.onResume();
            mResume = false;
            mGetViewCount = 0;
        }

        return mTextureMapView;
    }

    @Override
    public void onFlutterViewAttached(@NonNull View flutterView) {
        if (null != mTextureMapView) {
            mTextureMapView.onResume();
        }

    }

    @Override
    public void onFlutterViewDetached() {
        if (null != mTextureMapView) {
            mTextureMapView.onPause();
        }

    }

    @Override
    public void dispose() {
        if (Env.DEBUG) {
            Log.d(TAG, "dispose");
        }

        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mReceiver);

        if (null != mTextureMapView) {
            mTextureMapView.onDestroy();
        }
    }
}
