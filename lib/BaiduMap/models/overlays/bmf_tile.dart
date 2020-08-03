library bmfmap_map.models.overlays.bmf_tile;

import 'package:flutter/cupertino.dart';
import 'package:flutter_bmfbase/BaiduMap/bmfmap_base.dart'
    show BMFCoordinateBounds;

import 'bmf_overlay.dart';

/// 枚举：瓦片图加载方式
enum BMFTileLoadType {
  /// 网络加载,取该值时BMFTile必须传url
  LoadUrlTile,

  /// 本地异步加载
  LoadLocalAsyncTile,

  /// 本地同步加载
  ///
  /// Android没有该选项
  LoadLocalSyncTile,
}

/// 瓦片图
class BMFTile extends BMFOverlay {
  static BMFTile tile() => BMFTile(
      visibleMapBounds: null, tileLoadType: BMFTileLoadType.LoadLocalAsyncTile);

  /// 瓦片图最大放大级别,android平台默认为20，其它平台默认为21
  int maxZoom;

  /// 瓦片图最小缩放级别,默认3
  int minZoom;

  /// tileOverlay的可渲染区域，默认世界范围
  BMFCoordinateBounds visibleMapBounds;

  /// 瓦片图缓存大小,android端需要，ios端暂时不需要
  int maxTileTmp;

  /// 瓦片图加载类型
  BMFTileLoadType tileLoadType;

  /// 可选的参数，只有tileLoadType为LoadUrlTile时才有效
  String url;

  /// BMFTile构造方法
  BMFTile(
      {@required this.visibleMapBounds,
      @required this.tileLoadType,
      this.maxZoom,
      this.minZoom,
      this.maxTileTmp,
      this.url});

  BMFTile.withMap(Map map) {
    if (null == map) {
      return;
    }

    super.fromMap(map);

    this.maxZoom = map['maxZoom'];
    this.minZoom = map['minZoom'];
    this.visibleMapBounds =
        BMFCoordinateBounds.coordinateBounds().fromMap(map['visibleMapBounds']);
    this.maxTileTmp = map['maxTileTmp'];

    int tileLoadType = map['tileLoadType'] as int;

    if (null != tileLoadType &&
        tileLoadType >= 0 &&
        tileLoadType < BMFTileLoadType.values.length) {
      this.tileLoadType = BMFTileLoadType.values[tileLoadType];
    }

    this.url = map['url'];
  }

  @override
  fromMap(Map map) {
    if (null == map) {
      return null;
    }

    return BMFTile.withMap(map);
  }

  @override
  Map<String, Object> toMap() {
    return {
      'id': this.getId(),
      'maxZoom': this.maxZoom,
      'minZoom': this.minZoom,
      'visibleMapBounds': this.visibleMapBounds?.toMap(),
      'maxTileTmp': this.maxTileTmp,
      'tileLoadType': this.tileLoadType?.index,
      'url': this.url
    };
  }
}
