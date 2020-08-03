library bmfmap_map.models.overlays.bmf_polygon;

import 'package:flutter/material.dart';
import 'package:flutter_bmfmap/BaiduMap/map/bmf_map_linedraw_types.dart';
import 'package:flutter_bmfbase/BaiduMap/bmfmap_base.dart'
    show BMFCoordinate, ColorUtil;

import 'bmf_overlay.dart';

/// 多边形
class BMFPolygon extends BMFOverlay {
  /// 经纬度数组
  List<BMFCoordinate> coordinates;

  /// 设置polygonView的线宽度
  int width;

  /// 设置polygonView的边框颜色
  Color strokeColor;

  /// 设置polygonView的填充色
  Color fillColor;

  /// 设置polygonView为虚线样式
  ///
  /// iOS独有
  BMFLineDashType lineDashType;

  BMFPolygon.withMap(Map map) {
    if (null == map) {
      return;
    }

    super.fromMap(map);

    List list = map['coordinates'] as List;
    this.coordinates = list
        ?.map((c) => BMFCoordinate.coordinate().fromMap(c) as BMFCoordinate)
        ?.toList();

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

  /// BMFPolygon构造方法
  BMFPolygon(
      {@required this.coordinates,
      this.width: 5,
      this.strokeColor: Colors.blue,
      this.fillColor: Colors.red,
      this.lineDashType: BMFLineDashType.LineDashTypeNone,
      int zIndex: 0,
      bool visible: true})
      : super(zIndex: zIndex, visible: visible);

  @override
  fromMap(Map map) {
    if (null == map) {
      return;
    }

    return BMFPolygon.withMap(map);
  }

  @override
  Map<String, Object> toMap() {
    return {
      'id': this.getId(),
      'coordinates': this.coordinates?.map((coord) => coord?.toMap())?.toList(),
      'width': this.width,
      'strokeColor': this.strokeColor?.value?.toRadixString(16),
      'fillColor': this.fillColor?.value?.toRadixString(16),
      'lineDashType': this.lineDashType?.index,
      'zIndex': this.zIndex,
      'visible': this.visible
    };
  }
}
