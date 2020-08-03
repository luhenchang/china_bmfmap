library bmfmap_map.models.overlays.bmf_polyline;

import 'package:flutter/material.dart';
import 'package:flutter_bmfmap/BaiduMap/map/bmf_map_linedraw_types.dart';
import 'package:flutter_bmfbase/BaiduMap/bmfmap_base.dart';
import 'package:flutter_bmfmap/BaiduMap/private/mapdispatcher/bmf_map_dispatcher_factory.dart';
import 'bmf_overlay.dart';

/// 折线
///
class BMFPolyline extends BMFOverlay {
  static BMFPolyline polyline() => BMFPolyline(coordinates: null, indexs: null);

  /// 经纬度数组
  List<BMFCoordinate> coordinates;

  /// 分段索引(多纹理，多颜色折线请赋值)
  List<int> indexs;

  /// 线宽
  int width;

  /// 颜色折线的colors 用于颜色绘制
  ///
  /// 与textures二者必须选择其一进行传参
  List<Color> colors;

  /// 纹理折线的纹理图片路径 用于纹理绘制
  ///
  /// 注意：纹理图片宽高必须是2的n次幂
  /// 与colors二者必须选择其一进行传参
  List<String> textures;

  /// 设置折线是否虚线(Android独有)
  ///
  /// 当以纹理形式渲染polyline时，该属性为true，则显示纹理上的元素，为false不显示
  /// 当以颜色形式渲染polyline时，与BMFLineDashType配合使用;为true，则polyline可以渲染成虚线，为fasle则不能，
  bool dottedLine;

  /// 虚线绘制样式 默认实折线 BMFLineDashTypeNone
  ///
  /// 实折线 LineDashTypeNone,
  /// 方块虚线 LineDashTypeSquare,
  /// 圆点虚线 LineDashTypeDot,
  ///
  /// Android平台要想渲染成虚线，必须设置dottedLine为true
  BMFLineDashType lineDashType;

  /// line头尾处理方式(不支持虚线) 默认普通头 LineCapButt,
  ///
  /// 普通头 LineCapButt,
  /// 圆形头 LineCapRound
  ///
  /// iOS独有
  BMFLineCapType lineCapType;

  /// line拐角处理方式（不支持虚线）默认平角衔接 LineJoinBevel,
  ///
  /// 平角衔接  BMFLineJoinBevel,
  /// 尖角衔接(尖角过长(大于线宽)按平角处理) LineJoinMiter,
  /// 圆⻆角衔接 LineJoinRound
  ///
  /// iOS独有
  BMFLineJoinType lineJoinType;

  /// 是否抽稀 默认ture
  bool isThined;

  /// 是否可点击
  ///
  /// Android独有属性，iOS polyline默认可点击，目前不支持通过该属性设置可点击状态
  bool clickable;

  /// 纹理宽、高是否保持原比例渲染,默认为false
  ///
  /// Android独有
  bool isKeepScale;

  /// 是否可以被选中，获得焦点,默认true
  ///
  /// Android独有
  bool isFocus;

  /// BMFPolyline构造方法
  BMFPolyline(
      {@required this.coordinates,
      @required this.indexs,
      this.width: 5,
      this.colors: const [],
      this.textures: const [],
      this.dottedLine: true,
      this.lineDashType: BMFLineDashType.LineDashTypeNone,
      this.lineCapType: BMFLineCapType.LineCapButt,
      this.lineJoinType: BMFLineJoinType.LineJoinBevel,
      this.isThined: true,
      this.clickable: true,
      this.isKeepScale: false,
      this.isFocus: true,
      int zIndex: 0,
      bool visible: true})
      : super(zIndex: zIndex, visible: visible);

  BMFPolyline.withMap(Map map) {
    if (null == map) {
      return;
    }

    super.fromMap(map);

    List list = map['coordinates'] as List;
    List indexs = map['indexs'] as List;
    this.coordinates = list
        ?.map((c) => BMFCoordinate.coordinate().fromMap(c) as BMFCoordinate)
        ?.toList();

    this.indexs = indexs?.map((i) => i as int)?.toList();
    this.width = map['width'];
    list = map['colors'] as List;
    this.colors = list?.map((s) => ColorUtil.hexToColor(s as String))?.toList();
    this.textures = textures?.map((s) => s)?.toList();

    this.dottedLine = map['dottedLine'] as bool;

    int lineType = map['lineDashType'] as int;
    if (null != lineType &&
        lineType >= 0 &&
        lineType < BMFLineDashType.values.length) {
      this.lineDashType = BMFLineDashType.values[lineType];
    }

    lineType = map['lineCapType'] as int;
    if (null != lineType &&
        lineType >= 0 &&
        lineType < BMFLineDashType.values.length) {
      this.lineCapType = BMFLineCapType.values[lineType];
    }

    lineType = map['lineJoinType'] as int;
    if (null != lineType &&
        lineType >= 0 &&
        lineType < BMFLineDashType.values.length) {
      this.lineJoinType = BMFLineJoinType.values[lineType];
    }

    this.isThined = map['isThined'] as bool;
    this.clickable = map['clickable'] as bool;
    this.isKeepScale = map['isKeepScale'] as bool;
    this.isFocus = map['isFocus'];
  }

  @override
  fromMap(Map map) {
    if (null == map) {
      return;
    }

    return BMFPolyline.withMap(map);
  }

  @override
  Map<String, Object> toMap() {
    return {
      'id': this.getId(),
      'coordinates': this.coordinates?.map((coord) => coord?.toMap())?.toList(),
      'indexs': this.indexs?.map((index) => index)?.toList(),
      'width': this.width,
      'colors':
          this.colors?.map((color) => color.value.toRadixString(16))?.toList(),
      'textures': this.textures,
      'dottedLine': this.dottedLine,
      'lineDashType': this.lineDashType?.index,
      'lineCapType': this.lineCapType?.index,
      'lineJoinType': this.lineJoinType?.index,
      'isThined': this.isThined,
      'clickable': this.clickable,
      'isKeepScale': this.isKeepScale,
      'isFocus': this.isFocus,
      'zIndex': this.zIndex,
      'visible': this.visible
    };
  }

  /// 更新经纬度数组
  ///
  /// List<[BMFCoordinate]> coordinates polyline经纬度数组
  ///
  /// indexs 分段索引(多纹理，多颜色折线请赋值),iOS在多颜色或者多纹理渲染的情况下，更新经纬度数组的同时，必须更新indexs
  Future<bool> updateCoordinates(List<BMFCoordinate> coordinates,
      {List<int> indexs}) async {
    if (null == coordinates) {
      return false;
    }

    this.coordinates = coordinates;

    return await BMFMapDispatcherFactory.instance
        .getOverlayDispatcher()
        .updateOverlayMemberDispatch(this.getMethodChannel(), {
      'id': this.getId(),
      'member': 'coordinates',
      'value': coordinates?.map((coordinate) => coordinate?.toMap())?.toList(),
      'indexs': indexs?.map((index) => index)?.toList()
    });
  }

  /// 更新线宽
  Future<bool> updateWidth(int width) async {
    if (width < 0) {
      return false;
    }

    this.width = width;

    return await BMFMapDispatcherFactory.instance
        .getOverlayDispatcher()
        .updateOverlayMemberDispatch(this.getMethodChannel(), {
      'id': this.getId(),
      'member': 'width',
      'value': width,
    });
  }

  /// 更新索引
  ///
  /// Android在以colors渲染的情况下，更新纹理是无效的，应该使用updateColors接口
  Future<bool> updateIndexs(List<int> indexs) async {
    if (null == indexs) {
      return false;
    }

    this.indexs = indexs;

    return await BMFMapDispatcherFactory.instance
        .getOverlayDispatcher()
        .updateOverlayMemberDispatch(this.getMethodChannel(), {
      'id': this.getId(),
      'member': 'indexs',
      'value': indexs?.map((index) => index)?.toList(),
    });
  }

  /// 更新colors
  ///
  /// android更新colors的时候,必须带上indexs
  Future<bool> updateColors(List<Color> colors, {List<int> indexs}) async {
    if (null == colors) {
      return false;
    }

    this.colors = colors;

    if (null != indexs) {
      this.indexs = indexs;
    }

    return await BMFMapDispatcherFactory.instance
        .getOverlayDispatcher()
        .updateOverlayMemberDispatch(this.getMethodChannel(), {
      'id': this.getId(),
      'member': 'colors',
      'value': colors?.map((color) => color.value.toRadixString(16))?.toList(),
      'indexs': indexs?.map((index) => index)?.toList(),
    });
  }

  /// 更新纹理textures (ios暂不支持)
  Future<bool> updateTextures(List<String> textures) async {
    if (null == textures) {
      return false;
    }

    this.textures = textures;

    return await BMFMapDispatcherFactory.instance
        .getOverlayDispatcher()
        .updateOverlayMemberDispatch(this.getMethodChannel(), {
      'id': this.getId(),
      'member': 'textures',
      'value': textures?.map((e) => e)?.toList(),
    });
  }

  /// 更新是否设置虚线
  ///
  /// Android独有
  Future<bool> updateDottedLine(bool dottedLine) async {
    this.dottedLine = dottedLine;

    return await BMFMapDispatcherFactory.instance
        .getOverlayDispatcher()
        .updateOverlayMemberDispatch(this.getMethodChannel(), {
      'id': this.getId(),
      'member': 'dottedLine',
      'value': dottedLine,
    });
  }

  /// 更新折线绘制样式
  ///
  /// [BMFLineDashType] lineDashType  折线类型
  Future<bool> updateLineDashType(BMFLineDashType lineDashType) async {
    this.lineDashType = lineDashType;

    return await BMFMapDispatcherFactory.instance
        .getOverlayDispatcher()
        .updateOverlayMemberDispatch(this.getMethodChannel(), {
      'id': this.getId(),
      'member': 'lineDashType',
      'value': lineDashType?.index,
    });
  }

  /// 更新折线头尾处理方式
  ///
  /// [BMFLineCapType] lineCapType 折线头尾类型
  ///
  /// iOS独有
  Future<bool> updateLineCapType(BMFLineCapType lineCapType) async {
    this.lineCapType = lineCapType;

    return await BMFMapDispatcherFactory.instance
        .getOverlayDispatcher()
        .updateOverlayMemberDispatch(this.getMethodChannel(), {
      'id': this.getId(),
      'member': 'lineCapType',
      'value': lineCapType?.index,
    });
  }

  /// 更新折线拐角处理方式
  ///
  /// [BMFLineJoinType] lineJoinType 折线拐角处理方式
  ///
  /// iOS独有
  Future<bool> updateLineJoinType(BMFLineJoinType lineJoinType) async {
    this.lineJoinType = lineJoinType;

    return await BMFMapDispatcherFactory.instance
        .getOverlayDispatcher()
        .updateOverlayMemberDispatch(this.getMethodChannel(), {
      'id': this.getId(),
      'member': 'lineJoinType',
      'value': lineJoinType?.index,
    });
  }

  /// 更新polyLine是否可点击
  ///
  /// Android独有
  Future<bool> updateClickable(bool clickable) async {
    this.clickable = clickable;

    return await BMFMapDispatcherFactory.instance
        .getOverlayDispatcher()
        .updateOverlayMemberDispatch(this.getMethodChannel(), {
      'id': this.getId(),
      'member': 'clickable',
      'value': clickable,
    });
  }

  /// 更新纹理宽、高是否保持原比例渲染
  ///
  /// Android独有
  Future<bool> updateIsKeepScale(bool isKeepScale) async {
    this.isKeepScale = isKeepScale;
    return await BMFMapDispatcherFactory.instance
        .getOverlayDispatcher()
        .updateOverlayMemberDispatch(this.getMethodChannel(), {
      'id': this.getId(),
      'member': 'isKeepScale',
      'value': isKeepScale,
    });
  }

  /// 更新是否可以被选中，获得焦点
  ///
  /// Android独有
  Future<bool> updateIsFocus(bool isFocus) async {
    this.isFocus = isFocus;

    return await BMFMapDispatcherFactory.instance
        .getOverlayDispatcher()
        .updateOverlayMemberDispatch(this.getMethodChannel(), {
      'id': this.getId(),
      'member': 'isFocus',
      'value': isFocus,
    });
  }

  /// 更新polyline是否显示
  ///
  /// Android独有
  Future<bool> updateVisible(bool visible) async {
    this.visible = visible;

    return await BMFMapDispatcherFactory.instance
        .getOverlayDispatcher()
        .updateOverlayMemberDispatch(this.getMethodChannel(), {
      'id': this.getId(),
      'member': 'visible',
      'value': visible,
    });
  }

  /// 更新z轴方向上的堆叠顺序
  ///
  /// Android独有
  Future<bool> updateZIndex(int zIndex) async {
    this.zIndex = zIndex;

    return await BMFMapDispatcherFactory.instance
        .getOverlayDispatcher()
        .updateOverlayMemberDispatch(this.getMethodChannel(), {
      'id': this.getId(),
      'member': 'zIndex',
      'value': zIndex,
    });
  }

  /// 更新polyline是否抽稀
  Future<bool> updateThined(bool isThined) async {
    this.isThined = isThined;

    return await BMFMapDispatcherFactory.instance
        .getOverlayDispatcher()
        .updateOverlayMemberDispatch(this.getMethodChannel(), {
      'id': this.getId(),
      'member': 'isThined',
      'value': isThined,
    });
  }
}
