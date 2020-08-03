library bmfmap_map.models.bmf_heatmap;

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bmfbase/BaiduMap/bmfmap_base.dart'
    show BMFModel, BMFCoordinate, ColorUtil;

/// 热力图瓦片提供者
class BMFHeatMap implements BMFModel {
  /// 用户传入的热力图数据,数组,成员类型为BMFHeatMapNode
  List<BMFHeatMapNode> data;

  /// 设置热力图点半径
  int radius;

  /// 设置热力图层透明度，默认 0.6
  double opacity;

  /// 设置热力图渐变
  BMFGradient gradient;

  /// BMFHeatMap构造方法
  BMFHeatMap(
      {@required this.data, this.radius, this.opacity: 0.6, this.gradient});

  @override
  Map<String, Object> toMap() {
    return {
      'data': this
          .data
          ?.map((weightedCoordinate) => weightedCoordinate?.toMap())
          ?.toList(),
      'radius': this.radius,
      'opacity': this.opacity,
      'gradient': this.gradient?.toMap()
    };
  }

  /// map -> dynamic
  @override
  dynamic fromMap(Map map) {
    if (null == map) {
      return null;
    }
    List data = map['data'] as List;
    return new BMFHeatMap(
        data: data
            ?.map((node) => BMFHeatMapNode.heatMapNode().fromMap(node))
            ?.toList(),
        radius: map['radius'],
        opacity: map['opacity'],
        gradient: BMFGradient.gradient().fromMap(map['gradient']));
  }
}

/// 热力图节点信息
class BMFHeatMapNode implements BMFModel {
  static BMFHeatMapNode heatMapNode() =>
      BMFHeatMapNode(intensity: null, pt: null);

  /// 点的强度权值
  double intensity;

  /// 点的位置坐标
  BMFCoordinate pt;

  BMFHeatMapNode({@required this.intensity, @required this.pt}) {
    this.intensity = this.intensity > 1 ? this.intensity : 1.0;
  }
  @override
  fromMap(Map map) {
    return new BMFHeatMapNode(
        intensity: map['intensity'],
        pt: BMFCoordinate.coordinate().fromMap(map['pt']));
  }

  @override
  Map<String, Object> toMap() {
    return {'intensity': this.intensity, 'pt': this.pt?.toMap()};
  }
}

/// 热力图渐变色定义类
class BMFGradient implements BMFModel {
  static BMFGradient gradient() => BMFGradient(colors: null, startPoints: null);

  /// 渐变色用到的所有颜色数组 colors与startPoints必须对应
  List<Color> colors;

  /// 每一个颜色的起始点数组,数组成员类型为 【0,1】的double值,
  /// 个数和mColors的个数必须相同，
  /// 数组内元素必须时递增的 例 【0.1, 0.5, 1】;
  List<double> startPoints;

  BMFGradient({@required this.colors, @required this.startPoints});

  @override
  dynamic fromMap(Map map) {
    if (null == map) {
      return null;
    }

    List colors = map['colors'] as List;
    List startPoints = map['startPoints'] as List;
    return new BMFGradient(
        colors: colors?.map((color) => ColorUtil.hexToColor(color))?.toList(),
        startPoints: startPoints?.map((p) => p as double)?.toList());
  }

  @override
  Map<String, Object> toMap() {
    return {
      'colors':
          this.colors?.map((color) => color.value.toRadixString(16))?.toList(),
      'startPoints': this.startPoints?.map((p) => p)?.toList()
    };
  }
}
