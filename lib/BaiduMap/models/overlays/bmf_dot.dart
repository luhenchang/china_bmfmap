library bmfmap_map.models.overlays.bmf_dot;

import 'package:flutter/cupertino.dart';
import 'package:flutter_bmfbase/BaiduMap/bmfmap_base.dart'
    show BMFCoordinate, ColorUtil;

import 'bmf_overlay.dart';

/// 点
///
/// Android独有
class BMFDot extends BMFOverlay {
  /// 圆心点经纬度
  BMFCoordinate center;

  /// 圆的半径(单位米)
  double radius;

  ///园的颜色
  Color color;

  /// BMFDot构造方法
  BMFDot(
      {@required this.center,
      @required this.radius,
      @required this.color,
      int zIndex: 0,
      bool visible: true})
      : super(zIndex: zIndex, visible: visible);

  BMFDot.withMap(Map map) {
    if (null == map) {
      return;
    }

    super.fromMap(map);
    this.center = BMFCoordinate.coordinate().fromMap(map['center']);
    this.radius = map['radius'];
    this.color = ColorUtil.hexToColor(map['color']);
  }

  @override
  fromMap(Map map) {
    if (null == map) {
      return null;
    }

    return BMFDot.withMap(map);
  }

  @override
  Map<String, Object> toMap() {
    return {
      'id': this.getId(),
      'center': this.center?.toMap(),
      'radius': this.radius,
      'color': this.color?.value?.toRadixString(16),
      'zIndex': this.zIndex,
      'visible': this.visible
    };
  }
}
