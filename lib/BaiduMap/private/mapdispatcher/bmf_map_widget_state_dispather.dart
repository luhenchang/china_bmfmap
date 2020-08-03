import 'package:flutter/services.dart';
import 'package:flutter_bmfmap/BaiduMap/private/mapdispatcher/bmf_map_method_id.dart';

/// flutter端widget 状态更新通知native
class BMFMapWidgetStateDispatcher {
  Map<int, MethodChannel> _methodChannelMap;

  BMFMapWidgetStateDispatcher() {
    _methodChannelMap = new Map();
  }

  void initChannel(int widgetID) {
    if (!_methodChannelMap.containsKey(widgetID)) {
      MethodChannel methodChannel = new MethodChannel(
          'flutter_bmfmap/map_' + String.fromCharCode(widgetID + 97));
      _methodChannelMap[widgetID] = methodChannel;
    }
  }

  void unInit() {
    _methodChannelMap.clear();
  }

  /// flutter 端Widget update通知native
  void sendMapWidgetDidUpdateMsg(int widgetID) {
    MethodChannel methodChannel = _methodChannelMap[widgetID];
    if (null == methodChannel) {
      return;
    }
    methodChannel.invokeMethod(BMFMapWidgetMethodId.kMapDidUpdateWidget, null);
  }

  /// flutter 端Widget热重载通知native
  void sendMapWidgetReassembleMsg(int widgetID) {
    MethodChannel methodChannel = _methodChannelMap[widgetID];
    if (null == methodChannel) {
      return;
    }
    methodChannel.invokeMethod(BMFMapWidgetMethodId.kMapReassemble, null);
  }
}
