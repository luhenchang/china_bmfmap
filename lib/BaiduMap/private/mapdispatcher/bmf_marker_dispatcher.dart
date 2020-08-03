import 'package:flutter/services.dart';
import 'package:flutter_bmfmap/BaiduMap/bmfmap_map.dart';
import 'package:flutter_bmfmap/BaiduMap/models/bmf_infowindow.dart';
import 'package:flutter_bmfmap/BaiduMap/models/overlays/bmf_marker.dart';
import 'bmf_map_method_id.dart' show BMFMarkerMethodId, BMFInfoWindowMethodId;

/// marker处理类
class BMFMarkerDispatcher {
  /// 地图添加Marker
  Future<bool> addMarkerDispatch(
      MethodChannel _mapChannel, BMFMarker marker) async {
    if (null == _mapChannel || null == marker) {
      return false;
    }

    marker.setMethodChannel(_mapChannel);

    bool result = false;
    try {
      result = (await _mapChannel.invokeMethod(
              BMFMarkerMethodId.kMapAddMarkerMethod, marker.toMap() as dynamic))
          as bool;
    } on PlatformException catch (e) {
      print(e.toString());
    }
    return result;
  }

  /// 批量添加markers
  Future<bool> addMarkersDispatch(
      MethodChannel _mapChannel, List<BMFMarker> markers) async {
    if (null == _mapChannel || null == markers) {
      return false;
    }

    markers.forEach((marker) {
      marker.setMethodChannel(_mapChannel);
    });

    bool result = false;
    try {
      result = (await _mapChannel.invokeMethod(
              BMFMarkerMethodId.kMapAddMarkersMethod,
              markers?.map((marker) => marker.toMap())?.toList() as dynamic))
          as bool;
    } on PlatformException catch (e) {
      print(e.toString());
    }
    return result;
  }

  /// 添加infowindow
  Future<bool> addInfoWindowDispatch(
      MethodChannel _mapChannel, BMFInfoWindow infoWindow) async {
    if (null == _mapChannel || null == infoWindow) {
      return false;
    }
    bool result = false;
    try {
      result = (await _mapChannel.invokeMethod(
          BMFInfoWindowMethodId.kMapAddInfoWindowMethod,
          infoWindow.toMap() as dynamic)) as bool;
    } on PlatformException catch (e) {
      print(e.toString());
    }
    return result;
  }

  /// 批量添加infowindow
  Future<bool> addInfoWindowsDispatch(
      MethodChannel _mapChannel, List<BMFInfoWindow> infoWindows) async {
    if (null == _mapChannel || null == infoWindows) {
      return false;
    }
    bool result = false;
    try {
      result = (await _mapChannel.invokeMethod(
              BMFInfoWindowMethodId.kMapAddInfoWindowsMethod,
              infoWindows?.map((infoWindow) => infoWindow.toMap())?.toList()))
          as bool;
    } on PlatformException catch (e) {
      print(e.toString());
    }
    return result;
  }

  /// 批量添加markers
  Future<bool> removeInfoWindowDispatch(
      MethodChannel _mapChannel, BMFInfoWindow infoWindow) async {
    if (null == _mapChannel || null == infoWindow) {
      return false;
    }
    bool result = false;
    try {
      result = (await _mapChannel.invokeMethod(
          BMFInfoWindowMethodId.kMapRemoveInfoWindowMethod,
          infoWindow.toMap() as dynamic)) as bool;
    } on PlatformException catch (e) {
      print(e.toString());
    }
    return result;
  }

  /// map 指定删除marker
  Future<bool> removeMarkerDispatch(
      MethodChannel _mapChannel, BMFMarker marker) async {
    if (null == _mapChannel || null == marker) {
      return false;
    }
    bool result = false;

    marker.setMethodChannel(null);

    try {
      result = (await _mapChannel.invokeMethod(
          BMFMarkerMethodId.kMapRemoveMarkerMethod, marker.toMap())) as bool;
    } on PlatformException catch (e) {
      print(e.toString());
    }
    return result;
  }

  /// 批量删除markers
  Future<bool> removeMarkersDispatch(
      MethodChannel _mapChannel, List<BMFMarker> markers) async {
    if (null == _mapChannel || null == markers) {
      return false;
    }

    markers.forEach((marker) {
      marker.setMethodChannel(null);
    });

    bool result = false;
    try {
      result = (await _mapChannel.invokeMethod(
              BMFMarkerMethodId.kMapRemoveMarkersMethod,
              markers?.map((marker) => marker.toMap())?.toList() as dynamic))
          as bool;
    } on PlatformException catch (e) {
      print(e.toString());
    }
    return result;
  }

  /// map 清除所有的markers
  Future<bool> cleanAllMarkersDispatch(MethodChannel _mapChannel) async {
    if (null == _mapChannel) {
      return false;
    }
    bool result = false;
    try {
      result = (await _mapChannel
          .invokeMethod(BMFMarkerMethodId.kMapCleanAllMarkersMethod)) as bool;
    } on PlatformException catch (e) {
      print(e.toString());
    }
    return result;
  }

  /// 更新marker属性
  Future<bool> updateMarkerMember(MethodChannel _mapChannel, Map map) async {
    if (null == _mapChannel || null == map) {
      return false;
    }
    bool result = false;
    try {
      result = (await _mapChannel.invokeMethod(
          BMFMarkerMethodId.kMapUpdateMarkerMember, map)) as bool;
    } on PlatformException catch (e) {
      print(e.toString());
    }
    return result;
  }
}
