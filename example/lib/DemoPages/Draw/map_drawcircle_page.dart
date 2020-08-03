import 'package:flutter/material.dart';
import 'package:flutter_bmfmap_example/CustomWidgets/map_appbar.dart';
import 'package:flutter_bmfbase/BaiduMap/bmfmap_base.dart';
import 'package:flutter_bmfmap/BaiduMap/bmfmap_map.dart';
import 'package:flutter_bmfmap_example/CustomWidgets/map_base_page_state.dart';
import 'package:flutter_bmfmap_example/constants.dart';

/// circle圆形绘制示例
class DrawCirclePage extends StatefulWidget {
  DrawCirclePage({Key key}) : super(key: key);

  @override
  _DrawCirclePageState createState() => _DrawCirclePageState();
}

class _DrawCirclePageState extends BMFBaseMapState<DrawCirclePage> {
  BMFCircle _circle0;
  BMFCircle _circle1;
  BMFCircle _circle2;

  bool _addState = false;
  String _btnText = "删除";

  /// 创建完成回调
  @override
  void onBMFMapCreated(BMFMapController controller) {
    super.onBMFMapCreated(controller);
    if (!_addState) {
      addCircle();
    }
  }

  @override
  Widget build(BuildContext context) {
    super.build(context);
    return MaterialApp(
      home: Scaffold(
        appBar: BMFAppBar(
          title: 'circle示例',
          onBack: () {
            Navigator.pop(context);
          },
        ),
        body: Stack(children: <Widget>[generateMap(), generateControlBar()]),
      ),
    );
  }

  @override
  Widget generateControlBar() {
    return Container(
      width: screenSize.width,
      height: 60,
      color: Color(int.parse(Constants.controlBarColor)),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: <Widget>[
          RaisedButton(
              shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.all(Radius.circular(10))),
              color: Color(int.parse(Constants.btnColor)),
              textColor: Colors.white,
              child: Text(
                _btnText,
                style: TextStyle(color: Colors.white),
              ),
              onPressed: () {
                onBtnPress();
              })
        ],
      ),
    );
  }

  void onBtnPress() {
    if (_addState) {
      addCircle();
    } else {
      removeCircle();
    }

    _addState = !_addState;
    setState(() {
      _btnText = _addState == true ? "添加" : "删除";
    });
  }

  void addCircle() {
    addCircle0();
    addCircle1();
    addCircle2();
  }

  void addCircle0() {
    _circle0 = BMFCircle(
        center: BMFCoordinate(40.048, 116.404),
        radius: 5000,
        width: 6,
        strokeColor: Colors.green,
        fillColor: Colors.amber,
        lineDashType: BMFLineDashType.LineDashTypeSquare);
    myMapController?.addCircle(_circle0);
  }

  void addCircle1() {
    _circle1 = BMFCircle(
        center: BMFCoordinate(39.965, 116.404),
        radius: 4000,
         width: 6000,
        strokeColor: Colors.blue,
        fillColor: Colors.deepPurpleAccent,
        lineDashType: BMFLineDashType.LineDashTypeDot);
    myMapController?.addCircle(_circle1);
  }

  void addCircle2() {
    _circle2 = BMFCircle(
        center: BMFCoordinate(39.835, 116.404),
        radius: 7000,
         width: 14,
        strokeColor: Colors.blue,
        fillColor: Colors.brown,
        lineDashType: BMFLineDashType.LineDashTypeNone);
    myMapController?.addCircle(_circle2);
  }

  void removeCircle() {
    myMapController?.removeOverlay(_circle0?.getId());
    myMapController?.removeOverlay(_circle1?.getId());
    myMapController?.removeOverlay(_circle2?.getId());
  }
}

/// 设置地图参数
BMFMapOptions initMapOptions() {
  BMFMapOptions mapOptions = BMFMapOptions(
      mapType: BMFMapType.Standard,
      zoomLevel: 13,
      center: BMFCoordinate(39.915, 116.404));
  return mapOptions;
}
