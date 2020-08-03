package com.baidu.flutter_bmfmap;

import android.content.Context;
import android.util.Log;

import com.baidu.flutter_bmfmap.map.FlutterMapView;
import com.baidu.flutter_bmfmap.map.FlutterTextureMapView;
import com.baidu.flutter_bmfmap.utils.Env;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MessageCodec;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

public class TextureMapViewFactory extends PlatformViewFactory {

    private static final String TAG = "ViewFactory";
    private BinaryMessenger mMessenger;
    private Context mContext;
    private String mViewType;
    /**
     * @param messenger the codec used to decode the args parameter of {@link #create}.
     */
    public TextureMapViewFactory(Context context, BinaryMessenger messenger, String viewType) {
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
        return new FlutterTextureMapView(mContext, mMessenger, viewId, args, mViewType);
    }
}