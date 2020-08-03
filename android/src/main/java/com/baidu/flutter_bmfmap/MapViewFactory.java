package com.baidu.flutter_bmfmap;

import android.content.Context;
import android.util.Log;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

import com.baidu.flutter_bmfmap.map.FlutterMapView;
import com.baidu.flutter_bmfmap.utils.Env;

public class MapViewFactory extends PlatformViewFactory {

    private static final String TAG = "ViewFactory";
    private BinaryMessenger mMessenger;
    private Context mContext;
    private String mViewType;
    /**
     * @param messenger the codec used to decode the args parameter of {@link #create}.
     */
    public MapViewFactory(Context context, BinaryMessenger messenger, String viewType) {
        super(StandardMessageCodec.INSTANCE);
        if(Env.DEBUG){
            Log.d(TAG, "ViewFactory");
        }
        mContext = context;
        mMessenger = messenger;
        mViewType = viewType;
    }

    @Override
    public PlatformView create(Context context, int viewId, Object args) {
        if(Env.DEBUG){
            Log.d(TAG, "create");
        }
        return new FlutterMapView(mContext, mMessenger, viewId, args, mViewType);
    }
}
