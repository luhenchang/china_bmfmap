library bmfmap_map.models.overlays.bmf_text;

import 'package:flutter/material.dart';
import 'package:flutter_bmfbase/BaiduMap/bmfmap_base.dart'
    show BMFModel, BMFCoordinate, ColorUtil;

import 'bmf_overlay.dart';

/// 文本
///
/// Android独有
class BMFText extends BMFOverlay {
  /// 文本
  String text;

  /// text经纬度
  BMFCoordinate position;

  /// 背景色
  Color bgColor;

  /// 字体颜色
  Color fontColor;

  /// 字体大小
  int fontSize;

  /// typeface
  BMFTypeFace typeFace;

  /// 文字覆盖物水平对齐方式 ALIGN_LEFT | ALIGN_RIGHT | ALIGN_CENTER_HORIZONTAL
  int alignX;

  /// 文字覆盖物垂直对齐方式  ALIGN_TOP | ALIGN_BOTTOM | ALIGN_CENTER_VERTICAL
  int alignY;

  /// 旋转角度
  double rotate;

  BMFText.withMap(Map map) {
    if (null == map) {
      return;
    }

    super.fromMap(map);

    this.text = map['text'];
    this.position = BMFCoordinate.coordinate().fromMap(map['position']);
    this.bgColor = ColorUtil.hexToColor(map['bgColor']);
    this.fontColor = ColorUtil.hexToColor(map['fontColor']);
    this.fontSize = map['fontSize'];
    this.typeFace = BMFTypeFace.bmfTypeFace().fromMap(map['typeFace']);
    this.alignX = map['alignX'];
    this.alignY = map['alignY'];
    this.rotate = map['rotate'];
  }

  /// BMFText构造方法
  BMFText(
      {@required this.text,
      @required this.position,
      this.bgColor,
      this.fontColor: Colors.blue,
      this.fontSize: 12,
      this.typeFace,
      this.alignY: BMFHorizontalAlign.ALIGN_CENTER_HORIZONTAL,
      this.alignX: BMFVerticalAlign.ALIGN_CENTER_VERTICAL,
      this.rotate: 0,
      int zIndex: 0,
      bool visible: true})
      : super(zIndex: zIndex, visible: visible);

  @override
  fromMap(Map map) {
    if (null == map) {
      return null;
    }

    return BMFText.withMap(map);
  }

  @override
  Map<String, Object> toMap() {
    return {
      'id': this.getId(),
      'text': this.text,
      'position': this.position?.toMap(),
      "bgColor": this.bgColor?.value?.toRadixString(16),
      "fontColor": this.fontColor?.value?.toRadixString(16),
      "fontSize": this.fontSize,
      "typeFace": this.typeFace?.toMap(),
      "alignX": this.alignX,
      "alignY": this.alignY,
      "rotate": this.rotate,
      "zIndex": this.zIndex,
      'visible': this.visible,
    };
  }
}

/// Text水平方向上围绕position的对齐方式
class BMFHorizontalAlign {
  /// 文字覆盖物水平对齐方式:左对齐
  static const int ALIGN_LEFT = 1;

  /// 文字覆盖物水平对齐方式:右对齐
  static const int ALIGN_RIGHT = 2;

  /// 文字覆盖物水平对齐方式:水平居中对齐
  static const int ALIGN_CENTER_HORIZONTAL = 4;
}

/// Text垂直方向上围绕position的对齐方式
class BMFVerticalAlign {
  /// 文字覆盖物垂直对齐方式:上对齐
  static const int ALIGN_TOP = 8;

  /// 文字覆盖物垂直对齐方式:下对齐
  static const int ALIGN_BOTTOM = 16;

  /// 文字覆盖物垂直对齐方式:居中对齐
  static const int ALIGN_CENTER_VERTICAL = 32;
}

enum BMFTextStyle {
  NORMAL,
  BOLD,
  ITALIC,
  BOLD_ITALIC,
}

class BMFFamilyName {
  static const String sDefault = "";
  static const String sSansSerif = "sans-serif";
  static const String sSerif = "serif";
  static const String sMonospace = "monospace";
}

/// typeFace
class BMFTypeFace implements BMFModel {
  static BMFTypeFace bmfTypeFace() =>
      BMFTypeFace(familyName: null, textStype: BMFTextStyle.NORMAL);

  String familyName;
  BMFTextStyle textStype;

  BMFTypeFace({@required this.familyName, @required this.textStype});

  @override
  fromMap(Map map) {
    if (null == map) {
      return null;
    }
    return new BMFTypeFace(
        familyName: map['familyName'],
        textStype: BMFTextStyle.values[map['textStype'] as int]);
  }

  @override
  Map<String, Object> toMap() {
    return {"familyName": this.familyName, "textStype": this.textStype.index};
  }
}
