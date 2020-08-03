import 'package:flutter_bmfmap/BaiduMap/private/mapdispatcher/bmf_map_get_state_dispacther.dart';
import 'package:flutter_bmfmap/BaiduMap/private/mapdispatcher/bmf_map_state_dispacther.dart';
import 'package:flutter_bmfmap/BaiduMap/private/mapdispatcher/bmf_map_widget_state_dispather.dart';
import 'package:flutter_bmfmap/BaiduMap/private/mapdispatcher/bmf_marker_dispatcher.dart';
import 'package:flutter_bmfmap/BaiduMap/private/mapdispatcher/bmf_offline_map_dispatcher.dart';
import 'package:flutter_bmfmap/BaiduMap/private/mapdispatcher/bmf_overlay_dispatcher.dart';
import 'package:flutter_bmfmap/BaiduMap/private/mapdispatcher/bmf_userlocation_dispatcher.dart';

class BMFMapDispatcherFactory {
  // 工厂模式
  factory BMFMapDispatcherFactory() => _getInstance();
  static BMFMapDispatcherFactory get instance => _getInstance();
  static BMFMapDispatcherFactory _instance;

  BMFMapStateDispatcher _mapStateDispatcher;
  BMFMapGetStateDispatcher _mapGetStateDispatcher;
  BMFMapUserLocationDispatcher _mapUserLocationDispatcher;
  BMFMarkerDispatcher _markerDispatcher;
  BMFOverlayDispatcher _overlayDispatcher;
  BMFOfflineMapDispatcher _offlineMapDispatcher;
  BMFMapWidgetStateDispatcher _mapWidgetStateDispatcher;

  BMFMapDispatcherFactory._internal() {
    _mapStateDispatcher = new BMFMapStateDispatcher();
    _mapGetStateDispatcher = new BMFMapGetStateDispatcher();
    _mapUserLocationDispatcher = new BMFMapUserLocationDispatcher();
    _markerDispatcher = new BMFMarkerDispatcher();
    _overlayDispatcher = new BMFOverlayDispatcher();
    _offlineMapDispatcher = new BMFOfflineMapDispatcher();
    _mapWidgetStateDispatcher = new BMFMapWidgetStateDispatcher();
  }
  static BMFMapDispatcherFactory _getInstance() {
    if (_instance == null) {
      _instance = new BMFMapDispatcherFactory._internal();
    }
    return _instance;
  }

  /// mapStateDispatcher
  BMFMapStateDispatcher getMapStateDispatcher() => _mapStateDispatcher;

  // mapGetStateDispatcher
  BMFMapGetStateDispatcher getMapGetStateDispatcher() => _mapGetStateDispatcher;

  /// mapUserLocationDispatcher
  BMFMapUserLocationDispatcher getMapUserLocationDispatcher() =>
      _mapUserLocationDispatcher;

  /// markerDispatcher
  BMFMarkerDispatcher getMarkerDispatcher() => _markerDispatcher;

  /// overlayDispatcher
  BMFOverlayDispatcher getOverlayDispatcher() => _overlayDispatcher;

  /// offlineMapDispatcher
  BMFOfflineMapDispatcher getOfflineMapDispatcher() => _offlineMapDispatcher;

  /// widgetStateDispatcher
  BMFMapWidgetStateDispatcher getMapWidgetStateDispatcher() =>
      _mapWidgetStateDispatcher;
}
