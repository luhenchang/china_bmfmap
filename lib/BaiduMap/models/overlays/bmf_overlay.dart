library bmfmap_map.models.overlays.bmf_overlay;

import 'package:flutter/services.dart';
import 'package:flutter_bmfbase/BaiduMap/bmfmap_base.dart' show BMFModel;

/// 地图覆盖物基类
class BMFOverlay extends BMFModel {
  /// overlay id
  String _id;

  /// overlay是否可见
  ///
  /// Android独有
  bool visible;

  /// 元素的堆叠顺序
  ///
  /// Android独有
  int zIndex;

  MethodChannel _methodChannel;

  BMFOverlay({this.visible, this.zIndex}) {
    this._id = this.hashCode.toString();
  }

  BMFOverlay.withMap(Map map) {
    if (null == map) {
      return;
    }

    this._id = map['id'];
    this.visible = map['visible'];
    this.zIndex = map['zIndex'];
  }

  String getId() {
    return _id;
  }

  void setMethodChannel(MethodChannel methodChannel) {
    this._methodChannel = methodChannel;
  }

  MethodChannel getMethodChannel() {
    return this._methodChannel;
  }

  @override
  fromMap(Map map) {
    if (null == map) {
      return null;
    }

    return BMFOverlay.withMap(map);
  }

  @override
  Map<String, Object> toMap() {
    return {'id': this.getId(), 'visible': visible, 'zIndex': zIndex};
  }
}
