library bmfmap_map.models.bmf_mappoi;

import 'package:flutter_bmfbase/BaiduMap/bmfmap_base.dart'
    show BMFModel, BMFCoordinate;

/// 点击地图标注返回数据结构
class BMFMapPoi implements BMFModel {
  static BMFMapPoi mapPoi() => BMFMapPoi();

  /// 点标注的名称
  String text;

  /// 点标注的经纬度坐标
  BMFCoordinate pt;

  /// 点标注的uid，可能为空
  String uid;

  /// BMFMapPoi构造方法
  BMFMapPoi({this.text, this.pt, this.uid});
  @override
  fromMap(Map map) {
    return new BMFMapPoi(
        text: map['text'],
        pt: BMFCoordinate.coordinate().fromMap(map['pt']),
        uid: map['uid']);
  }

  @override
  Map<String, Object> toMap() {
    return {'text': this.text, 'pt': this.pt?.toMap(), 'uid': this.uid};
  }
}
