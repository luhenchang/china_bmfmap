library bmfmap_map.models.bmf_mapstatus;

import 'package:flutter_bmfbase/BaiduMap/bmfmap_base.dart';

/// 此类表示地图状态信息
class BMFMapStatus implements BMFModel {
  static BMFMapStatus mapStatus() => BMFMapStatus();

  /// 缩放级别:(4-21)
  double fLevel;

  /// 旋转角度
  double fRotation;

  /// 俯视角度:(-45~0)
  double fOverlooking;

  /// 屏幕中心点坐标:在屏幕内，超过无效
  BMFPoint targetScreenPt;

  /// 地理中心点坐标:经纬度
  BMFCoordinate targetGeoPt;

  /// 当前屏幕显示范围内的地理范围
  BMFCoordinateBounds coordinateBounds;

  /// BMFMapStatus构造方法
  BMFMapStatus(
      {this.fLevel,
      this.fRotation,
      this.fOverlooking,
      this.targetScreenPt,
      this.targetGeoPt,
      this.coordinateBounds});
  @override
  fromMap(Map map) {
    return new BMFMapStatus(
        fLevel: map['fLevel'],
        fRotation: map['fRotation'],
        fOverlooking: map['fOverlooking'],
        targetScreenPt: BMFPoint.point().fromMap(map['targetScreenPt']),
        targetGeoPt: BMFCoordinate.coordinate().fromMap(map['targetGeoPt']),
        coordinateBounds: BMFCoordinateBounds.coordinateBounds()
            .fromMap(map['visibleMapBounds']));
  }

  @override
  Map<String, Object> toMap() {
    return {
      'fLevel': this.fLevel,
      'fRotation': this.fRotation,
      'fOverlooking': this.fOverlooking,
      'targetScreenPt': this.targetScreenPt?.toMap(),
      'targetGeoPt': this.targetGeoPt?.toMap(),
      'visibleMapBounds': this.coordinateBounds?.toMap()
    };
  }
}
