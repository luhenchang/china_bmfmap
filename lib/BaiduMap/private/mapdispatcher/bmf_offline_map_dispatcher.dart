import 'package:flutter/services.dart';
import 'package:flutter_bmfmap/BaiduMap/models/bmf_offline_models.dart';
import 'bmf_map_method_id.dart' show BMFOfflineMethodId;

/// 离线地图处理类
class BMFOfflineMapDispatcher {
  /// 初始化
  ///
  /// 调用离线接口之前必须先初始化
  Future<bool> initOfflineMapDispatch(MethodChannel _mapChannel) async {
    if (null == _mapChannel) {
      return false;
    }
    bool result = false;
    try {
      result = (await _mapChannel
          .invokeMethod(BMFOfflineMethodId.kMapInitOfflineMethod)) as bool;
    } on PlatformException catch (e) {
      print(e.toString());
    }
    return result;
  }

  /// 启动下载指定城市ID的离线地图，或在暂停更新某城市后继续更新下载某城市离线地图
  ///
  /// cityID  指定的城市ID
  /// 成功返回true，否则返回false
  Future<bool> startOfflineMapDispatch(
      MethodChannel _mapChannel, int cityID) async {
    if (null == _mapChannel || null == cityID) {
      return false;
    }
    bool result = false;
    try {
      result = (await _mapChannel.invokeMethod(
              BMFOfflineMethodId.kMapStartOfflineMethod, {"cityID": cityID}))
          as bool;
    } on PlatformException catch (e) {
      print(e.toString());
    }
    return result;
  }

  /// 启动更新指定城市ID的离线地图
  ///
  /// cityID  指定的城市ID
  /// 成功返回true，否则返回false
  Future<bool> updateOfflineMapDispatch(
      MethodChannel _mapChannel, int cityID) async {
    if (null == _mapChannel || null == cityID) {
      return false;
    }
    bool result = false;
    try {
      result = (await _mapChannel.invokeMethod(
              BMFOfflineMethodId.kMapUpdateOfflineMethod, {"cityID": cityID}))
          as bool;
    } on PlatformException catch (e) {
      print(e.toString());
    }
    return result;
  }

  /// 暂停下载或更新指定城市ID的离线地图
  ///
  /// cityID 指定的城市ID
  /// 成功返回true，否则返回false
  Future<bool> pauseOfflineMapDispatch(
      MethodChannel _mapChannel, int cityID) async {
    if (null == _mapChannel || null == cityID) {
      return false;
    }
    bool result = false;
    try {
      result = (await _mapChannel.invokeMethod(
              BMFOfflineMethodId.kMapPauseOfflineMethod, {"cityID": cityID}))
          as bool;
    } on PlatformException catch (e) {
      print(e.toString());
    }
    return result;
  }

  /// 删除指定城市ID的离线地图
  ///
  /// cityID 指定的城市ID
  /// 成功返回true，否则返回false
  Future<bool> removeOfflineMapDispatch(
      MethodChannel _mapChannel, int cityID) async {
    if (null == _mapChannel || null == cityID) {
      return false;
    }
    bool result = false;
    try {
      result = (await _mapChannel.invokeMethod(
              BMFOfflineMethodId.kMapRemoveOfflineMethod, {"cityID": cityID}))
          as bool;
    } on PlatformException catch (e) {
      print(e.toString());
    }
    return result;
  }

  /// 返回热门城市列表
  Future<List<BMFOfflineCityRecord>> getHotCityListDispatch(
      MethodChannel _mapChannel) async {
    if (null == _mapChannel) {
      return null;
    }
    List<BMFOfflineCityRecord> result;
    try {
      Map map = (await _mapChannel
          .invokeMethod(BMFOfflineMethodId.kMapGetHotCityListMethod) as Map);
      if (null == map) {
        return null;
      }
      List list = map["searchCityRecord"] as List;
      result = list
          ?.map((city) => BMFOfflineCityRecord.offlineCityRecord().fromMap(city)
              as BMFOfflineCityRecord)
          ?.toList();
    } on PlatformException catch (e) {
      print(e.toString());
    }
    return result;
  }

  /// 返回支持离线地图城市列表
  Future<List<BMFOfflineCityRecord>> getOfflineCityListDispatch(
      MethodChannel _mapChannel) async {
    if (null == _mapChannel) {
      return null;
    }
    List<BMFOfflineCityRecord> result;
    try {
      Map map = (await _mapChannel.invokeMethod(
          BMFOfflineMethodId.kMapGetOfflineCityListMethod) as Map);
      if (null == map) {
        return null;
      }
      List list = map["searchCityRecord"] as List;
      result = list
          ?.map((city) => BMFOfflineCityRecord.offlineCityRecord().fromMap(city)
              as BMFOfflineCityRecord)
          ?.toList();
    } on PlatformException catch (e) {
      print(e.toString());
    }
    return result;
  }

  /// 根据城市名搜索该城市离线地图记录
  ///
  /// cityName 城市名
  Future<List<BMFOfflineCityRecord>> searchCityDispatch(
      MethodChannel _mapChannel, String cityName) async {
    if (null == _mapChannel || null == cityName) {
      return null;
    }
    List<BMFOfflineCityRecord> result;
    try {
      Map map = (await _mapChannel.invokeMethod(
              BMFOfflineMethodId.kMapSearchCityMethod, {'cityName': cityName})
          as Map);
      if (null == map) {
        return null;
      }
      List list = map["searchCityRecord"] as List;
      result = list
          ?.map((city) => BMFOfflineCityRecord.offlineCityRecord().fromMap(city)
              as BMFOfflineCityRecord)
          ?.toList();
    } on PlatformException catch (e) {
      print(e.toString());
    }
    return result;
  }

  /// 返回各城市离线地图更新信息
  Future<List<BMFUpdateElement>> getAllUpdateInfoDispatch(
      MethodChannel _mapChannel) async {
    if (null == _mapChannel) {
      return null;
    }
    List<BMFUpdateElement> result;
    try {
      Map map = (await _mapChannel
          .invokeMethod(BMFOfflineMethodId.kMapGetAllUpdateInfoMethod) as Map);
      if (null == map) {
        return null;
      }
      List list = map["updateElements"] as List;
      result = list
          ?.map((element) => BMFUpdateElement.bmfUpdateElement()
              .fromMap(element) as BMFUpdateElement)
          ?.toList();
    } on PlatformException catch (e) {
      print(e.toString());
    }
    return result;
  }

  /// 返回指定城市ID离线地图更新信息
  ///
  /// id 城市id
  Future<BMFUpdateElement> getUpdateInfoDispatch(
      MethodChannel _mapChannel, int id) async {
    if (null == _mapChannel) {
      return null;
    }
    BMFUpdateElement result;
    try {
      Map map = (await _mapChannel.invokeMethod(
          BMFOfflineMethodId.kMapGetUpdateInfoMethod, {"cityID": id}) as Map);
      if (null == map) {
        return null;
      }
      result = BMFUpdateElement.bmfUpdateElement().fromMap(map);
    } on PlatformException catch (e) {
      print(e.toString());
    }
    return result;
  }

  /// 销毁离线地图管理模块，不用时调用
  Future<bool> destroyOfflineMapDispatch(MethodChannel _mapChannel) async {
    if (null == _mapChannel) {
      return false;
    }
    bool result = false;
    try {
      result = (await _mapChannel
          .invokeMethod(BMFOfflineMethodId.kMapDestroyOfflineMethod)) as bool;
    } on PlatformException catch (e) {
      print(e.toString());
    }
    return result;
  }
}
