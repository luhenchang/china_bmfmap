import 'package:flutter/services.dart';
import 'package:flutter_bmfbase/BaiduMap/bmfmap_base.dart'
    show BMFCoordinate, BMFPoint, BMFLog;
import 'package:flutter_bmfmap/BaiduMap/private/mapdispatcher/bmf_map_method_id.dart'
    show BMFProjectionMethodId;

/// Projection接口用于屏幕像素点坐标系统和地球表面经纬度点坐标系统之间的变换。
class BMFProjection {
  factory BMFProjection() => _getInstance();

  static BMFProjection get instance => _getInstance();

  static BMFProjection _instance;

  /// 通信
  MethodChannel _mapChannel;

  BMFProjection._internal();

  static BMFProjection _getInstance() {
    if (null == _instance) {
      _instance = new BMFProjection._internal();
    }

    return _instance;
  }

  void init(MethodChannel methodChannel) {
    _mapChannel = methodChannel;
  }

  /// 将屏幕坐标转换成地理坐标
  ///
  /// [BMFPoint] point 屏幕坐标 如果传入null 则返回null
  ///
  /// [BMFCoordinate] 地理坐标
  Future<BMFCoordinate> convertScreenPointToCoordinate(BMFPoint point) async {
    if (null == _mapChannel) {
      return null;
    }

    if (null == point) {
      return null;
    }
    BMFCoordinate result;
    try {
      Map map = (await _mapChannel.invokeMethod(
          BMFProjectionMethodId.kCoordinateFromScreenPointMethod,
          {'point': point?.toMap()} as dynamic)) as Map;
      result = BMFCoordinate.coordinate().fromMap(map['coordinate'])
          as BMFCoordinate;
    } on PlatformException catch (e) {
      BMFLog.e(e.toString());
    }

    return result;
  }

  /// 将地理坐标转换成屏幕坐标
  ///
  /// [BMFCoordinate] location 地理坐标 如果传入 null 则返回null
  ///
  /// [BMFPoint] 屏幕坐标
  Future<BMFPoint> convertCoordinateToScreenPoint(
      BMFCoordinate location) async {
    if (null == _mapChannel) {
      return null;
    }

    if (null == location) {
      return null;
    }

    BMFPoint point;
    try {
      Map map = ((await _mapChannel.invokeMethod(
          BMFProjectionMethodId.kScreenPointFromCoordinateMethod,
          {'coordinate': location?.toMap()} as dynamic))) as Map;
      point = BMFPoint.point().fromMap(map['point']) as BMFPoint;
    } on PlatformException catch (e) {
      BMFLog.e(e.toString());
    }
    return point;
  }
}
