package com.baidu.flutter_bmfmap.map.mapHandler;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.flutter_bmfmap.map.FlutterCommonMapView;
import com.baidu.flutter_bmfmap.utils.Constants;
import com.baidu.mapapi.map.CustomMapStyleCallBack;
import com.baidu.mapapi.map.MapCustomStyleOptions;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.TextureMapView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.view.FlutterMain;

public class CustomMapHandler extends BMapHandler {

    private  MapViewCustomMapHandler mMapViewCustomMapHandler;
    private   TextureMapViewCustomMapHandler mTextureMapViewCustomMapHandler;

    public CustomMapHandler(FlutterCommonMapView mapView) {
        super(mapView);
        mMapViewCustomMapHandler = new MapViewCustomMapHandler(mMapView);
        mTextureMapViewCustomMapHandler = new TextureMapViewCustomMapHandler(mMapView);
    }

    @Override
    public void updateMapView(FlutterCommonMapView mapView){
        mMapView = mapView;
        mMapViewCustomMapHandler.updateMapView(mapView);
        mTextureMapViewCustomMapHandler.updateMapView(mapView);
    }

    @Override
    public void handlerMethodCallResult(Context context,MethodCall call, MethodChannel.Result result) {
       switch (mMapView.getViewType()){
           case Constants.ViewType.sMapView:
               mMapViewCustomMapHandler.handlerMethodCallResult(context, call, result);
               break;
           case Constants.ViewType.sTextureMapView:
               mTextureMapViewCustomMapHandler.handlerMethodCallResult(context, call, result);
               break;
           default:
               break;
       }
    }

    class MapViewCustomMapHandler extends BMapHandler {

        private MapView mRealMapView;

        public MapViewCustomMapHandler(FlutterCommonMapView mapView) {
            super(mapView);
            mRealMapView = mMapView.getMapView();
        }

        @Override
        public void updateMapView(FlutterCommonMapView mapView){
            mMapView = mapView;
            if(null != mMapView){
                mRealMapView = mMapView.getMapView();
            }
        }

        @Override
        public void handlerMethodCallResult(Context context, MethodCall call, MethodChannel.Result result) {
            if (null == call) {
                result.success(false);
                return;
            }

            String methodId = call.method;
            if (TextUtils.isEmpty(methodId)) {
                result.success(false);
                return;
            }

            switch (methodId) {
                case Constants.MethodProtocol.CustomMapProtocol.sMapSetCustomMapStyleEnableMethod:
                    setCustomMapStyleEnable(call, result);
                    break;
                case Constants.MethodProtocol.CustomMapProtocol.sMapSetCustomMapStylePathMethod:
                    setCustomMapStylePath(context,call,result);
                    break;
                case Constants.MethodProtocol.CustomMapProtocol.sMapSetCustomMapStyleWithOptionMethod:
                    setMapCustomStyle(call,result);
                    break;
                default:
                    break;
            }
        }

        /**
         * 个性化地图开关
         */
        private void setCustomMapStyleEnable(MethodCall call, MethodChannel.Result result) {
            Map<String, Object> argument = call.arguments();
            if (null == argument || null == mMapView) {
                result.success(false);
                return;
            }
            if (!argument.containsKey("enable")) {
                result.success(false);
                return;
            }
            boolean enable = (boolean) argument.get("enable");
            mRealMapView.setMapCustomStyleEnable(enable);
            result.success(true);
        }

        /**
         * 设置个性化地图样式文件的路径
         */
        private void setCustomMapStylePath(Context context, MethodCall call, MethodChannel.Result result) {
            Map<String, Object> argument = call.arguments();
            if (null == argument || null == mMapView || context == null) {
                result.success(false);
                return;
            }

            if (!argument.containsKey("path") || !argument.containsKey("mode")) {
                result.success(false);
                return;
            }

            String path = (String) argument.get("path");
            if (path.isEmpty()) {
                result.success(false);
                return;
            }

            String customStyleFilePath = getCustomStyleFilePath(context, path);
            if(TextUtils.isEmpty(customStyleFilePath)){
                result.success(false);
                return;
            }
            mRealMapView.setMapCustomStylePath(customStyleFilePath);
            result.success(true);
        }

        private String getCustomStyleFilePath(Context context, String customStyleFilePath) {
            if (customStyleFilePath.isEmpty()) {
                return null;
            }

            FileOutputStream outputStream = null;
            InputStream inputStream = null;
            String parentPath = null;
            String customStyleFileName = null;
            try {
                customStyleFileName = FlutterMain.getLookupKeyForAsset(customStyleFilePath);
                inputStream = context.getAssets().open(customStyleFileName);
                byte[] buffer = new byte[inputStream.available()];
                inputStream.read(buffer);

                parentPath = context.getCacheDir().getAbsolutePath();
                String substr = customStyleFileName.substring(0, customStyleFileName.lastIndexOf("/"));
                File customStyleFile = new File(parentPath + "/" + customStyleFileName);
                if (customStyleFile.exists()) {
                    customStyleFile.delete();
                }
                File dirFile = new File(parentPath + "/" + substr);
                if (!dirFile.exists()) {
                    dirFile.mkdirs();
                }
                customStyleFile.createNewFile();
                outputStream = new FileOutputStream(customStyleFile);
                outputStream.write(buffer);
            } catch (IOException e) {
                Log.e("TAG", "Copy file failed", e);
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (IOException e) {
                    Log.e("TAG", "Close stream failed", e);
                }
            }
            return parentPath + "/" + customStyleFileName;
        }

        /**
         * 在线个性化样式加载状态回调接口
         */
        private void setMapCustomStyle(MethodCall call, final MethodChannel.Result result) {
            Map<String, Object> argument = call.arguments();
            if (null == argument || null == mMapView) {
                result.success(false);
                return;
            }
            if (!argument.containsKey("customMapStyleOption")) {
                return;
            }
            Map<String, Object> customMapStyleOption = (Map<String, Object>) argument.get("customMapStyleOption");
            if (!customMapStyleOption.containsKey("customMapStyleID")
                    || !customMapStyleOption.containsKey("customMapStyleFilePath")) {
                return;
            }

            String customMapStyleID = (String) customMapStyleOption.get("customMapStyleID");
            String customMapStyleFilePath = (String) customMapStyleOption.get("customMapStyleFilePath");
            if (customMapStyleID.isEmpty() && customMapStyleFilePath.isEmpty()) {
                return;
            }
            MapCustomStyleOptions mapCustomStyleOptions = new MapCustomStyleOptions();
            mapCustomStyleOptions.customStyleId(customMapStyleID);
            mapCustomStyleOptions.localCustomStylePath(customMapStyleFilePath);
            final HashMap<String, String> reslutMap = new HashMap<>();
            mRealMapView.setMapCustomStyle(mapCustomStyleOptions, new CustomMapStyleCallBack() {
                @Override
                public boolean onPreLoadLastCustomMapStyle(String path) {
                    reslutMap.put("preloadPath", path);
                    result.success(reslutMap);
                    return false;
                }

                @Override
                public boolean onCustomMapStyleLoadSuccess(boolean b, String path) {
                    // TODO: 2020-03-05  回调的 boolean 类型没有返回之后补齐
                    reslutMap.put("successPath", path);
                    result.success(reslutMap);
                    return false;
                }

                @Override
                public boolean onCustomMapStyleLoadFailed(int status, String message, String path) {
                    String sStatus = String.valueOf(status);
                    reslutMap.put("errorCode", sStatus);
                    reslutMap.put("successPath", path);
                    result.success(reslutMap);
                    return false;
                }
            });
        }
    }

    class TextureMapViewCustomMapHandler extends BMapHandler {

        private TextureMapView mTextureMapView;

        public TextureMapViewCustomMapHandler(FlutterCommonMapView mapView) {
            super(mapView);
            mTextureMapView = mMapView.getTextureMapView();
        }

        @Override
        public void updateMapView(FlutterCommonMapView mapView){
            mMapView = mapView;
            if(null != mMapView){
                mTextureMapView = mMapView.getTextureMapView();
            }
        }

        @Override
        public void handlerMethodCallResult(Context context, MethodCall call, MethodChannel.Result result) {
            if (null == call) {
                return;
            }

            String methodId = call.method;
            if (TextUtils.isEmpty(methodId)) {
                return;
            }

            switch (methodId) {
                case Constants.MethodProtocol.CustomMapProtocol.sMapSetCustomMapStyleEnableMethod:
                    setCustomMapStyleEnable(call, result);
                    break;
                case Constants.MethodProtocol.CustomMapProtocol.sMapSetCustomMapStylePathMethod:
                    setCustomMapStylePath(context,call,result);
                    break;
                case Constants.MethodProtocol.CustomMapProtocol.sMapSetCustomMapStyleWithOptionMethod:
                    setMapCustomStyle(context, call,result);
                    break;
                default:
                    break;
            }
        }

        /**
         * 个性化地图开关
         */
        private void setCustomMapStyleEnable(MethodCall call, MethodChannel.Result result) {
            Map<String, Object> argument = call.arguments();
            if (null == argument || null == mMapView) {
                result.success(false);
                return;
            }
            if (!argument.containsKey("enable")) {
                result.success(false);
                return;
            }
            boolean enable = (boolean) argument.get("enable");
            mTextureMapView.setMapCustomStyleEnable(enable);
            result.success(true);
        }

        /**
         * 设置个性化地图样式文件的路径
         */
        private void setCustomMapStylePath(Context context, MethodCall call, MethodChannel.Result result) {
            Map<String, Object> argument = call.arguments();
            if (null == argument || null == mMapView || context == null) {
                result.success(false);
                return;
            }

            if (!argument.containsKey("path") || !argument.containsKey("mode")) {
                result.success(false);
                return;
            }

            String path = (String) argument.get("path");
            if (path.isEmpty()) {
                result.success(false);
                return;
            }
            String customStyleFilePath = getCustomStyleFilePath(context, path);
            mTextureMapView.setMapCustomStylePath(customStyleFilePath);
            result.success(true);
        }

        private String getCustomStyleFilePath(Context context, String customStyleFilePath) {
            if (customStyleFilePath.isEmpty()) {
                return null;
            }

            FileOutputStream outputStream = null;
            InputStream inputStream = null;
            String parentPath = null;
            String customStyleFileName = null;
            try {
                customStyleFileName = FlutterMain.getLookupKeyForAsset(customStyleFilePath);
                inputStream = context.getAssets().open(customStyleFileName);
                byte[] buffer = new byte[inputStream.available()];
                inputStream.read(buffer);

                parentPath = context.getCacheDir().getAbsolutePath();
                String substr = customStyleFileName.substring(0, customStyleFileName.lastIndexOf("/"));
                File customStyleFile = new File(parentPath + "/" + customStyleFileName);
                if (customStyleFile.exists()) {
                    customStyleFile.delete();
                }
                File dirFile = new File(parentPath + "/" + substr);
                if (!dirFile.exists()) {
                    dirFile.mkdirs();
                }
                customStyleFile.createNewFile();
                outputStream = new FileOutputStream(customStyleFile);
                outputStream.write(buffer);
            } catch (IOException e) {
                Log.e("TAG", "Copy file failed", e);
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (IOException e) {
                    Log.e("TAG", "Close stream failed", e);
                }
            }
            return parentPath + "/" + customStyleFileName;
        }

        /**
         * 在线个性化样式加载状态回调接口
         */
        private void setMapCustomStyle(Context context, MethodCall call,
                                       final MethodChannel.Result result) {
            Map<String, Object> argument = call.arguments();
            if (null == argument || null == mMapView) {
                result.success(false);
                return;
            }
            if (!argument.containsKey("customMapStyleOption")) {
                return;
            }
            Map<String, Object> customMapStyleOption = (Map<String, Object>) argument.get("customMapStyleOption");
            if (!customMapStyleOption.containsKey("customMapStyleID")
                    || !customMapStyleOption.containsKey("customMapStyleFilePath")) {
                return;
            }

            String customMapStyleID = (String) customMapStyleOption.get("customMapStyleID");
            String customMapStyleFilePath = (String) customMapStyleOption.get("customMapStyleFilePath");
            customMapStyleFilePath = getCustomStyleFilePath(context, customMapStyleFilePath);
            if (customMapStyleID.isEmpty() && customMapStyleFilePath.isEmpty()) {
                return;
            }
            MapCustomStyleOptions mapCustomStyleOptions = new MapCustomStyleOptions();
            mapCustomStyleOptions.customStyleId(customMapStyleID);
            mapCustomStyleOptions.localCustomStylePath(customMapStyleFilePath);
            final HashMap<String, String> reslutMap = new HashMap<>();
            mTextureMapView.setMapCustomStyle(mapCustomStyleOptions, new CustomMapStyleCallBack() {
                @Override
                public boolean onPreLoadLastCustomMapStyle(String path) {
                    reslutMap.put("preloadPath", path);
                    result.success(reslutMap);
                    return false;
                }

                @Override
                public boolean onCustomMapStyleLoadSuccess(boolean b, String path) {
                    // TODO: 2020-03-05  回调的 boolean 类型没有返回之后补齐
                    reslutMap.put("successPath", path);
                    result.success(reslutMap);
                    return false;
                }

                @Override
                public boolean onCustomMapStyleLoadFailed(int status, String message, String path) {
                    String sStatus = String.valueOf(status);
                    reslutMap.put("errorCode", sStatus);
                    reslutMap.put("successPath", path);
                    result.success(reslutMap);
                    return false;
                }
            });
        }
    }
}
