library bmfmap_map.models.bmf_baseindoormap_info;

import 'package:flutter_bmfbase/BaiduMap/bmfmap_base.dart' show BMFModel;

/// 此类表示室内图基础信息
class BMFBaseIndoorMapInfo implements BMFModel {
  static BMFBaseIndoorMapInfo baseIndoorMapInfo() => BMFBaseIndoorMapInfo();

  /// 室内ID
  String strID;

  /// 当前楼层
  String strFloor;

  /// 所有楼层信息
  List<String> listStrFloors;

  /// BMFBaseIndoorMapInfo构造方法
  BMFBaseIndoorMapInfo({this.strID, this.strFloor, this.listStrFloors});
  @override
  fromMap(Map map) {
    List list = map['listStrFloors'] as List;
    return new BMFBaseIndoorMapInfo(
        strID: map['strID'],
        strFloor: map['strFloor'],
        listStrFloors: list?.map((s) => s as String)?.toList());
  }

  @override
  Map<String, Object> toMap() {
    return {
      'strID': this.strID,
      'strFloor': this.strFloor,
      'listStrFloors': this.listStrFloors?.map((s) => s?.toString())?.toList()
    };
  }
}
