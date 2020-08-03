library bmfmap_map.models.overlays.bmf_ground;

import 'package:flutter/material.dart';
import 'package:flutter_bmfbase/BaiduMap/bmfmap_base.dart'
    show BMFCoordinate, BMFCoordinateBounds;

import 'bmf_overlay.dart';

/// 该类用于定义一个图片图层
class BMFGround extends BMFOverlay {
  /// 两种绘制GroundOverlay的方式之一：绘制的位置地理坐标，与anchor配对使用
  BMFCoordinate position;

  /// 用位置绘制时图片的锚点x，图片左上角为(0.0f,0.0f),向右向下为正
  ///
  /// 使用groundOverlayWithPosition初始化时生效
  double anchorX;

  /// 用位置绘制时图片的锚点y，图片左上角为(0.0f,0.0f),向右向下为正
  ///
  /// 使用groundOverlayWithPosition初始化时生效
  double anchorY;

  /// 宽
  double width;

  /// 高
  double height;

  /// 缩放级别(仅ios支持)
  int zoomLevel;

  /// 两种绘制GroundOverlay的方式之二：绘制的地理区域范围，图片在此区域内合理缩放
  BMFCoordinateBounds bounds;

  /// 绘制图片
  String image;

  /// 图片纹理透明度,最终透明度 = 纹理透明度 * alpha,取值范围为【0.0f, 1.0f】，默认为1.0f
  double transparency;

  /// BMFGround构造方法
  BMFGround(
      {@required this.image,
      this.width,
      this.height,
      this.anchorX,
      this.anchorY,
      this.zoomLevel,
      this.bounds,
      this.position,
      this.transparency: 1.0,
      int zIndex: 0,
      bool visible: true})
      : super(zIndex: zIndex, visible: visible);

  BMFGround.withGround(Map map) {
    if (null == map) {
      return;
    }

    super.fromMap(map);

    this.image = map['image'];
    this.width = map['width'];
    this.height = map['height'];
    this.anchorX = map['anchorX'];
    this.anchorY = map['anchorY'];
    this.zoomLevel = map['zoomLevel'];
    this.position = BMFCoordinate.coordinate().fromMap(map['position']);
    this.bounds = BMFCoordinateBounds.coordinateBounds().fromMap(map['bounds']);
    this.transparency = map['transparency'];
  }

  @override
  Map<String, Object> toMap() {
    return {
      'id': this.getId(),
      'image': this.image,
      'width': this.width,
      'height': this.height,
      'anchorX': this.anchorX,
      'anchorY': this.anchorY,
      'zoomLevel': this.zoomLevel,
      'position': this.position?.toMap(),
      'bounds': this.bounds?.toMap(),
      'transparency': this.transparency,
      'zIndex': this.zIndex,
      'visible': this.visible
    };
  }

  @override
  fromMap(Map map) {
    if (null == map) {
      return;
    }

    return BMFGround.withGround(map);
  }
}
