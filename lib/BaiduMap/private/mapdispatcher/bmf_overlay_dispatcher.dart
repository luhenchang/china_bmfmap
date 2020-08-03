import 'package:flutter/services.dart';
import 'package:flutter_bmfbase/BaiduMap/bmfmap_base.dart';
import 'package:flutter_bmfmap/BaiduMap/models/overlays/bmf_arcline.dart';
import 'package:flutter_bmfmap/BaiduMap/models/overlays/bmf_circle.dart';
import 'package:flutter_bmfmap/BaiduMap/models/overlays/bmf_dot.dart';
import 'package:flutter_bmfmap/BaiduMap/models/overlays/bmf_ground.dart';
import 'package:flutter_bmfmap/BaiduMap/models/overlays/bmf_polygon.dart';
import 'package:flutter_bmfmap/BaiduMap/models/overlays/bmf_polyline.dart';
import 'package:flutter_bmfmap/BaiduMap/models/overlays/bmf_text.dart';
import 'package:flutter_bmfmap/BaiduMap/models/overlays/bmf_tile.dart';
import 'package:flutter_bmfmap/BaiduMap/private/mapdispatcher/bmf_map_method_id.dart'
    show BMFOverlayMethodId;

/// polyline, arcline, polygon, circle
/// Dot Text (Android)
class BMFOverlayDispatcher {
  static const _tag = 'BMFOverlayDispatcher';

  /// 地图添加Polyline
  Future<bool> addPolylineDispatch(
      MethodChannel _mapChannel, BMFPolyline polyline) async {
    if (null == _mapChannel || null == polyline) {
      return false;
    }

    polyline.setMethodChannel(_mapChannel);

    bool result = false;
    try {
      result = (await _mapChannel.invokeMethod(
          BMFOverlayMethodId.kMapAddPolylineMethod,
          polyline.toMap() as dynamic)) as bool;
    } on PlatformException catch (e) {
      print(e.toString());
    }
    return result;
  }

  /// 地图添加arcline
  Future<bool> addArclineDispatch(
      MethodChannel _mapChannel, BMFArcline arcline) async {
    if (null == _mapChannel || null == arcline) {
      return false;
    }

    bool result = false;
    try {
      result = (await _mapChannel.invokeMethod(
          BMFOverlayMethodId.kMapAddArclinelineMethod,
          arcline.toMap() as dynamic)) as bool;
    } on PlatformException catch (e) {
      print(e.toString());
    }
    return result;
  }

  /// 地图添加polygon
  Future<bool> addPolygonDispatch(
      MethodChannel _mapChannel, BMFPolygon polygon) async {
    if (null == _mapChannel || null == polygon) {
      return false;
    }
    bool result = false;
    try {
      result = (await _mapChannel.invokeMethod(
          BMFOverlayMethodId.kMapAddPolygonMethod,
          polygon.toMap() as dynamic)) as bool;
    } on PlatformException catch (e) {
      print(e.toString());
    }
    return result;
  }

  /// 地图添加circle
  Future<bool> addCircleDispatch(
      MethodChannel _mapChannel, BMFCircle circle) async {
    if (null == _mapChannel || null == circle) {
      return false;
    }
    bool result = false;
    try {
      result = (await _mapChannel.invokeMethod(
          BMFOverlayMethodId.kMapAddCircleMethod,
          circle.toMap() as dynamic)) as bool;
    } on PlatformException catch (e) {
      print(e.toString());
    }
    return result;
  }

  /// 地图添加Dot
  Future<bool> addDotDispatch(MethodChannel _mapChannel, BMFDot dot) async {
    if (null == _mapChannel || null == dot) {
      return false;
    }
    bool result = false;
    try {
      result = (await _mapChannel.invokeMethod(
              BMFOverlayMethodId.kMapAddDotMethod, dot?.toMap() as dynamic))
          as bool;
    } on PlatformException catch (e) {
      print(e.toString());
    }
    return result;
  }

  /// 地图添加Text
  Future<bool> addTextDispatch(MethodChannel _mapChannel, BMFText text) async {
    if (null == _mapChannel || null == text) {
      return false;
    }
    bool result = false;
    try {
      result = (await _mapChannel.invokeMethod(
              BMFOverlayMethodId.kMapAddTextMethod, text.toMap() as dynamic))
          as bool;
    } on PlatformException catch (e) {
      print(e.toString());
    }
    return result;
  }

  /// 地图添加Ground
  Future<bool> addGroundDispatch(
      MethodChannel _mapChannel, BMFGround ground) async {
    if (null == _mapChannel || null == ground) {
      return false;
    }
    bool result = false;
    try {
      result = (await _mapChannel.invokeMethod(
          BMFOverlayMethodId.kMapAddGroundMethod,
          ground.toMap() as dynamic)) as bool;
    } on PlatformException catch (e) {
      print(e.toString());
    }
    return result;
  }

  /// 添加瓦片图
  Future<bool> addTileDispatch(MethodChannel _mapChannel, BMFTile tile) async {
    if (tile == null || _mapChannel == null) {
      return false;
    }
    bool result = false;
    try {
      result = (await _mapChannel.invokeMethod(
              BMFOverlayMethodId.kMapAddTileMethod, tile?.toMap() as dynamic))
          as bool;
    } on PlatformException catch (e) {
      BMFLog.e(e.toString(), tag: _tag);
    }
    return result;
  }

  /// 移除瓦片图
  Future<bool> removeTileDispatch(
      MethodChannel _mapChannel, BMFTile tile) async {
    if (tile == null || _mapChannel == null) {
      return false;
    }
    bool result = false;
    try {
      result = (await _mapChannel.invokeMethod(
          BMFOverlayMethodId.kMapRemoveTileMethod,
          tile?.toMap() as dynamic)) as bool;
    } on PlatformException catch (e) {
      BMFLog.e(e.toString(), tag: _tag);
    }
    return result;
  }

  /// 指定id删除overlay
  Future<bool> removeOverlayDispatch(
      MethodChannel _mapChannel, String overlayId) async {
    if (null == _mapChannel || null == overlayId) {
      return false;
    }
    bool result = false;
    try {
      result = (await _mapChannel.invokeMethod(
          BMFOverlayMethodId.kMapRemoveOverlayMethod,
          {'id': overlayId} as dynamic)) as bool;
    } on PlatformException catch (e) {
      print(e.toString());
    }
    return result;
  }

  /// 更新overlay属性
  Future<bool> updateOverlayMemberDispatch(
      MethodChannel _mapChannel, Map map) async {
    if (null == _mapChannel || null == map) {
      return false;
    }
    bool result = false;
    try {
      result = (await _mapChannel.invokeMethod(
          BMFOverlayMethodId.kMapUpdatePolylineMemberMethod, map)) as bool;
    } on PlatformException catch (e) {
      print(e.toString());
    }
    return result;
  }
}
