library bmfmap_map.models.overlays.bmf_arcline;

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bmfmap/BaiduMap/map/bmf_map_linedraw_types.dart';
import 'package:flutter_bmfbase/BaiduMap/bmfmap_base.dart'
    show BMFCoordinate, ColorUtil;

import 'bmf_overlay.dart';

/// 弧线
class BMFArcline extends BMFOverlay {
  /// 经纬度数组三个点确定一条弧线
  List<BMFCoordinate> coordinates;

  /// 设置arclineView的线宽度
  int width;

  /// 设置arclineView的画笔颜色
  Color color;

  /// 虚线类型
  ///
  /// iOS独有
  BMFLineDashType lineDashType;

  /// BMFArcline构造方法
  BMFArcline(
      {@required this.coordinates,
      this.width: 5,
      this.color: Colors.blue,
      this.lineDashType: BMFLineDashType.LineDashTypeNone,
      int zIndex: 0,
      bool visible: true})
      : super(zIndex: zIndex, visible: visible);

  BMFArcline.withMap(Map map) {
    if (null == map) {
      return;
    }

    List list = map['coordinates'] as List;

    super.fromMap(map);
    this.coordinates = list
        ?.map((c) => BMFCoordinate.coordinate().fromMap(c) as BMFCoordinate)
        ?.toList();
    this.width = map['width'];
    this.color = ColorUtil.hexToColor(map['color']);

    int lineType = map['lineDashType'] as int;
    if (null != lineType &&
        lineType >= 0 &&
        lineType < BMFLineDashType.values.length) {
      this.lineDashType = BMFLineDashType.values[lineType];
    }
  }

  @override
  fromMap(Map map) {
    return BMFArcline.withMap(map);
  }

  @override
  Map<String, Object> toMap() {
    return {
      'id': this.getId(),
      'coordinates': this.coordinates?.map((coord) => coord?.toMap())?.toList(),
      'width': this.width,
      'color': this.color?.value?.toRadixString(16),
      'lineDashType': this.lineDashType?.index,
      'zIndex': this.zIndex,
      'visible': this.visible
    };
  }
}
