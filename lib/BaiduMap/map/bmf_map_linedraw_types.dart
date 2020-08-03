library bmfmap_map.map.bmf_map_linedraw_types;

/// line拐角处理方式（不支持虚线）
///
/// iOS独有
enum BMFLineJoinType {
  /// 平角衔接
  LineJoinBevel,

  /// 尖角衔接(尖角过长(大于线宽)按平角处理)
  LineJoinMiter,

  /// 圆⻆角衔接
  LineJoinRound
}

/// line头尾处理方式(不支持虚线)
enum BMFLineCapType {
  /// 普通头
  LineCapButt,

  /// 圆形头
  LineCapRound
}

/// 虚线绘制样式
enum BMFLineDashType {
  /// 实折线
  LineDashTypeNone,

  /// 方块样式
  LineDashTypeSquare,

  /// 圆点样式
  LineDashTypeDot,
}
