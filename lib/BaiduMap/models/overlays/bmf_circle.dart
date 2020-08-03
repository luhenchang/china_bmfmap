library bmfmap_map.models.overlays.bmf_circle;

import 'package:flutter/material.dart';
import 'package:flutter_bmfmap/BaiduMap/map/bmf_map_linedraw_types.dart';
import 'package:flutter_bmfbase/BaiduMap/bmfmap_base.dart'
    show BMFCoordinate, ColorUtil;

import 'bmf_overlay.dart';

/// 圆
class BMFCircle extends BMFOverlay {
  /// 圆心点经纬度
  BMFCoordinate center;

  /// 圆的半径(单位米)
  double radius;

  /// 设置circleView的线宽度
  int width;

  /// 设置circleView的边框颜色
  Color strokeColor;

  /// 设置circleView的填充色
  Color fillColor;

  /// 设置circleView为虚线样式
  BMFLineDashType lineDashType;

  /// BMFCircle构造方法
  BMFCircle(
      {@required this.center,
      @required this.radius,
      this.width: 5,
      this.strokeColor: Colors.blue,
      this.fillColor: Colors.red,
      this.lineDashType: BMFLineDashType.LineDashTypeNone,
      int zIndex: 0,
      bool visible: true})
      : super(zIndex: zIndex, visible: visible);

  BMFCircle.withMap(Map map) {
    if (null == map) {
      return;
    }

    super.fromMap(map);

    this.center = BMFCoordinate.coordinate().fromMap(map['center']);
    this.radius = map['radius'];
    this.width = map['width'];
    this.strokeColor = ColorUtil.hexToColor(map['strokeColor']);
    this.fillColor = ColorUtil.hexToColor(map['fillColor']);

    int lineType = map['lineDashType'] as int;
    if (null != lineType &&
        lineType >= 0 &&
        lineType < BMFLineDashType.values.length) {
      this.lineDashType = BMFLineDashType.values[lineType];
    }
  }

  @override
  fromMap(Map map) {
    if (null == map) {
      return;
    }

    return BMFCircle.withMap(map);
  }

  @override
  Map<String, Object> toMap() {
    return {
      'id': this.getId(),
      'center': this.center?.toMap(),
      'radius': this.radius,
      'width': this.width,
      'strokeColor': this.strokeColor?.value?.toRadixString(16),
      'fillColor': this.fillColor?.value?.toRadixString(16),
      'lineDashType': this.lineDashType?.index,
      'zIndex': this.zIndex,
      'visible': this.visible
    };
  }
}
