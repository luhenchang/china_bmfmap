package com.baidu.flutter_bmfmap;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.baidu.flutter_bmfmap.map.OfflineHandler;
import com.baidu.flutter_bmfmap.utils.Constants;
import com.baidu.flutter_bmfmap.utils.Env;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.embedding.engine.plugins.shim.ShimPluginRegistry;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.plugin.platform.PlatformViewRegistry;

/** FlutterBmfmapPlugin */
public class FlutterBmfmapPlugin implements FlutterPlugin, ActivityAware,  MethodCallHandler {
  private static final String TAG = FlutterBmfmapPlugin.class.getSimpleName();

  private OfflineHandler mOfflineHandler;
  // private PluginRegistry.Registrar mRegistrar;
  private FlutterPluginBinding flutterBinding;
  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    if(null == flutterPluginBinding){
      return;
    }

    mOfflineHandler = new OfflineHandler();
    mOfflineHandler.init(flutterPluginBinding.getBinaryMessenger());

    FlutterEngine flutterEngine = flutterPluginBinding.getFlutterEngine();

    if(null == flutterEngine){
      return;
    }
    flutterBinding = flutterPluginBinding;

//    ShimPluginRegistry shimPluginRegistry = new ShimPluginRegistry(flutterEngine);
//    String key = FlutterBmfmapPlugin.class.getSimpleName();
//    if(shimPluginRegistry.hasPlugin(key)){
//      if(Env.DEBUG){
//        Log.d(TAG, "hasPlugin");
//      }
//      return;
//    }
//
//    mRegistrar = shimPluginRegistry.registrarFor(key);
  }

  // This static function is optional and equivalent to onAttachedToEngine. It supports the old
  // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
  // plugin registration via this function while apps migrate to use the new Android APIs
  // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
  //
  // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
  // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
  // depending on the user's project. onAttachedToEngine or registerWith must both be defined
  // in the same class.
  public static void registerWith(Registrar registrar) {
    OfflineHandler offlineHandler = new OfflineHandler();
    offlineHandler.init(registrar.messenger());
    registrar.platformViewRegistry().registerViewFactory(
            Constants.ViewType.sMapView,
            new MapViewFactory(registrar.activity()
                    , registrar.messenger()
                    , Constants.ViewType.sMapView));

    registrar.platformViewRegistry().registerViewFactory(
            Constants.ViewType.sTextureMapView,
            new TextureMapViewFactory(registrar.activity()
                    , registrar.messenger()
                    , Constants.ViewType.sTextureMapView));
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    }else{

    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    if(null == binding){
      return;
    }

    BinaryMessenger binaryMessenger = binding.getBinaryMessenger();
    if(null == binaryMessenger){
      return;
    }

    mOfflineHandler.unInit(binding.getBinaryMessenger());
  }

  @Override
  public void onAttachedToActivity(ActivityPluginBinding binding) {
    if(null == binding || null == flutterBinding){
      return;
    }
    PlatformViewRegistry pRegistry =  flutterBinding.getPlatformViewRegistry();

    pRegistry.registerViewFactory(
            Constants.ViewType.sMapView,
            new MapViewFactory(binding.getActivity()
                    , flutterBinding.getBinaryMessenger()
                    , Constants.ViewType.sMapView));

    pRegistry.registerViewFactory(
            Constants.ViewType.sTextureMapView,
            new TextureMapViewFactory(binding.getActivity()
                    , flutterBinding.getBinaryMessenger()
                    , Constants.ViewType.sTextureMapView));
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    Log.d(TAG, "onDetachedFromActivityForConfigChanges");
  }

  @Override
  public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {
    Log.d(TAG, "onReattachedToActivityForConfigChanges");
  }

  @Override
  public void onDetachedFromActivity() {
    Log.d(TAG, "onDetachedFromActivity");
  }
}
