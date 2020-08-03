//
//  BMFMapViewController.m
//  flutter_bmfmap
//
//  Created by zbj on 2020/2/6.
//

#import "BMFMapViewController.h"
#import "BMFMapView.h"
#import "BMFMapCallBackConst.h"
#import "BMFMapViewHandles.h"
#import "BMFAnnotationHandles.h"
#import "BMFOverlayHandles.h"
#import "BMFHeatMapHandles.h"
#import "BMFUserLocationHandles.h"
#import "BMFProjectionHandles.h"
#import "BMFMapModels.h"
#import "BMFMapStatusModel.h"
#import "BMFMapPoiModel.h"
#import "BMFIndoorMapInfoModel.h"
#import "BMFAnnotation.h"
#import "BMFFileManager.h"
#import "BMFPolyline.h"
#import "BMFArcline.h"
#import "BMFCircle.h"
#import "BMFPolygon.h"
#import "UIColor+BMFString.h"

static NSString *kBMFMapChannelName = @"flutter_bmfmap/map_";
static NSString *kMapMethods = @"flutter_bmfmap/map/";
static NSString *kMarkerMethods = @"flutter_bmfmap/marker/";
static NSString *kOverlayMethods = @"flutter_bmfmap/overlay/";
static NSString *kHeatMapMethods = @"flutter_bmfmap/heatMap/";
static NSString *kUserLocationMethods = @"flutter_bmfmap/userLocation/";
static NSString *kProjectionMethods = @"flutter_bmfmap/projection/";

@interface BMFMapViewController()<BMKMapViewDelegate>
{
    FlutterMethodChannel *_channel;
    BMFMapView  *_mapView;

}

@end
@implementation BMFMapViewController

- (instancetype)initWithWithFrame:(CGRect)frame
                   viewIdentifier:(int64_t)viewId
                        arguments:(id _Nullable)args
                  binaryMessenger:(NSObject<FlutterBinaryMessenger>*)messenger{
    if ([super init]) {
        int Id = (int)(viewId + 97);
        NSString *channelName = [NSString stringWithFormat:@"%@%@", kBMFMapChannelName, [NSString stringWithFormat:@"%c", Id]];
        _channel = [FlutterMethodChannel methodChannelWithName:channelName binaryMessenger:messenger];
        _mapView = [BMFMapView viewWithFrame:frame dic:(NSDictionary*)args];
        _mapView.delegate = self;
    
#pragma mark - flutter -> ios
        __weak __typeof__(_mapView) weakMapView = _mapView;
        [_channel setMethodCallHandler:^(FlutterMethodCall * _Nonnull call, FlutterResult  _Nonnull result) {
            NSObject<BMFMapViewHandler> *handler;
            // map
            if ([call.method hasPrefix:kMapMethods]) {
                handler = [NSClassFromString([BMFMapViewHandles defalutCenter].mapViewHandles[call.method]) new];
            }
            // marker
            if ([call.method hasPrefix:kMarkerMethods]) {
                handler = [NSClassFromString([BMFAnnotationHandles defalutCenter].annotationHandles[call.method]) new];
            }
            // overlay
            if ([call.method hasPrefix:kOverlayMethods]) {
                handler = [NSClassFromString([BMFOverlayHandles defalutCenter].overlayHandles[call.method]) new];
            }
            // 热力图
            if ([call.method hasPrefix:kHeatMapMethods]) {
                handler = [NSClassFromString([BMFHeatMapHandles defalutCenter].heatMapHandles[call.method]) new];
            }
            // 定位图层
            if ([call.method hasPrefix:kUserLocationMethods]) {
                handler = [NSClassFromString([BMFUserLocationHandles defalutCenter].userLocationHandles[call.method]) new];
            }
            // 数据转换
            if ([call.method hasPrefix:kProjectionMethods]) {
                handler = [NSClassFromString([BMFProjectionHandles defalutCenter].projectionHandles[call.method]) new];
            }
            

            if (handler) {
                [[handler initWith:weakMapView] handleMethodCall:call result:result];
            } else {
                if ([call.method isEqualToString:@"flutter_bmfmap/map/didUpdateWidget"]) {
//                    NSLog(@"native - didUpdateWidget");
                    return;
                }
                if ([call.method isEqualToString:@"flutter_bmfmap/map/reassemble"]) {
//                    NSLog(@"native - reassemble");
                     return;
                }
                result(FlutterMethodNotImplemented);
            }
        }];
    }
    return self;
}
- (nonnull UIView *)view {
    return _mapView;
}
- (void)dealloc {
    _channel = nil;
    _mapView.delegate = nil;
    _mapView = nil;
//    NSLog(@"-BMFMapViewController-dealloc");
}
#pragma mark - ios -> flutter
#pragma mark - BMKMapViewDelegate
/// 地图加载完成
- (void)mapViewDidFinishLoading:(BMKMapView *)mapView{
    if (_mapView) {
        // 对初始时不生效的属性，在此再调用一次.暂时这么解决
        [_mapView updateMapOptions];
    }
    if (!_channel) return;
    [_channel invokeMethod:kBMFMapDidLoadCallback arguments:@{@"success": @YES} result:nil];
}
/// 地图渲染完成
- (void)mapViewDidFinishRendering:(BMKMapView *)mapView{
    if (!_channel) return;
    [_channel invokeMethod:kBMFMapDidRenderCallback arguments:@{@"success": @YES} result:nil];
}

/// 地图渲染每一帧画面过程中，以及每次需要重绘地图时（例如添加覆盖物）都会调用此接口
- (void)mapView:(BMKMapView *)mapView onDrawMapFrame:(BMKMapStatus*)status{
    if (!_channel) return;
    BMFMapStatusModel *mapStatus = [BMFMapStatusModel fromMapStatus:status];
     [_channel invokeMethod:kBMFMapOnDrawMapFrameCallback
                  arguments:@{@"mapStatus": [mapStatus bmf_toDictionary]}
                     result:nil];
    
}

/// 地图区域即将改变时会调用此接口
- (void)mapView:(BMKMapView *)mapView regionWillChangeAnimated:(BOOL)animated{
    if (!_channel) return;
    BMFMapStatusModel *mapStatus = [BMFMapStatusModel fromMapStatus:[_mapView getMapStatus]];
    [_channel invokeMethod:kBMFMapRegionWillChangeCallback
                 arguments:@{@"mapStatus": [mapStatus bmf_toDictionary]}
                    result:nil];
}

/// 地图区域即将改变时会调用此接口
- (void)mapView:(BMKMapView *)mapView regionWillChangeAnimated:(BOOL)animated reason:(BMKRegionChangeReason)reason{
    if (!_channel) return;
    BMFMapStatusModel *mapStatus = [BMFMapStatusModel fromMapStatus:[_mapView getMapStatus]];
    [_channel invokeMethod:kBMFMapRegionWillChangeWithReasonCallback
                 arguments:@{@"mapStatus": [mapStatus bmf_toDictionary], @"reason": @(reason)}
                    result:nil];
}

/// 地图区域改变完成后会调用此接口
- (void)mapView:(BMKMapView *)mapView regionDidChangeAnimated:(BOOL)animated{
    if (!_channel) return;
    BMFMapStatusModel *mapStatus = [BMFMapStatusModel fromMapStatus:[_mapView getMapStatus]];
    [_channel invokeMethod:kBMFMapRegionDidChangeCallback
                 arguments:@{@"mapStatus": [mapStatus bmf_toDictionary]}
                    result:nil];
}

/// 地图区域改变完成后会调用此接口
- (void)mapView:(BMKMapView *)mapView regionDidChangeAnimated:(BOOL)animated reason:(BMKRegionChangeReason)reason{
    if (!_channel) return;
    BMFMapStatusModel *mapStatus = [BMFMapStatusModel fromMapStatus:[_mapView getMapStatus]];
    [_channel invokeMethod:kBMFMapRegionDidChangeWithReasonCallback
                 arguments:@{@"mapStatus": [mapStatus bmf_toDictionary], @"reason": @(reason)}
                    result:nil];
}
/// 点中底图标注后会回调此接口
- (void)mapView:(BMKMapView *)mapView onClickedMapPoi:(BMKMapPoi *)mapPoi{
    if (!_channel) return;
    BMFMapPoiModel *model = [BMFMapPoiModel fromBMKMapPoi:mapPoi];
    [_channel invokeMethod:kBMFMapOnClickedMapPoiCallback arguments:@{@"poi": [model bmf_toDictionary]} result:nil];
}
/// 点中底图空白处会回调此接口
- (void)mapView:(BMKMapView *)mapView onClickedMapBlank:(CLLocationCoordinate2D)coordinate{
    if (!_channel) return;
    BMFCoordinate *coord = [BMFCoordinate fromCLLocationCoordinate2D:coordinate];
    [_channel invokeMethod:kBMFMapOnClickedMapBlankCallback arguments:@{@"coord": [coord bmf_toDictionary]} result:nil];
}

/// 双击地图时会回调此接口
- (void)mapview:(BMKMapView *)mapView onDoubleClick:(CLLocationCoordinate2D)coordinate{
    if (!_channel) return;
    BMFCoordinate *coord = [BMFCoordinate fromCLLocationCoordinate2D:coordinate];
    [_channel invokeMethod:kBMFMapOnDoubleClickCallback arguments:@{@"coord": [coord bmf_toDictionary]} result:nil];
}

/// 长按地图时会回调此接口
- (void)mapview:(BMKMapView *)mapView onLongClick:(CLLocationCoordinate2D)coordinate{
    if (!_channel) return;
    BMFCoordinate *coord = [BMFCoordinate fromCLLocationCoordinate2D:coordinate];
    [_channel invokeMethod:kBMFMapOnLongClickCallback arguments:@{@"coord": [coord bmf_toDictionary]} result:nil];
}

/// 3DTouch 按地图时会回调此接口（仅在支持3D Touch，且fouchTouchEnabled属性为YES时，会回调此接口）
/// force 触摸该点的力度(参考UITouch的force属性)
/// maximumPossibleForce 当前输入机制下的最大可能力度(参考UITouch的maximumPossibleForce属性)
- (void)mapview:(BMKMapView *)mapView onForceTouch:(CLLocationCoordinate2D)coordinate force:(CGFloat)force maximumPossibleForce:(CGFloat)maximumPossibleForce{
    if (!_channel) return;
    BMFCoordinate *coord = [BMFCoordinate fromCLLocationCoordinate2D:coordinate];
    [_channel invokeMethod:kBMFMapOnForceTouchCallback arguments:@{@"coord": [coord bmf_toDictionary], @"force": @(force), @"maximumPossibleForce": @(maximumPossibleForce)} result:nil];
}

///地图状态改变完成后会调用此接口
- (void)mapStatusDidChanged:(BMKMapView *)mapView{
    if (!_channel) return;
    [_channel invokeMethod:kBMFMapStatusDidChangedCallback arguments:nil result:nil];
}

- (void)mapview:(BMKMapView *)mapView baseIndoorMapWithIn:(BOOL)flag baseIndoorMapInfo:(BMKBaseIndoorMapInfo *)info{
    if (!_channel) return;
    BMFIndoorMapInfoModel *model = [BMFIndoorMapInfoModel new];
    model.strID = info.strID;
    model.strFloor = info.strFloor;
    model.listStrFloors = info.arrStrFloors;
    [_channel invokeMethod:kBMFMapInOrOutBaseIndoorMapCallback arguments:@{@"flag": @(flag), @"info": [model bmf_toDictionary]} result:nil];
}
#pragma mark - annotationView
- (BMFAnnotationModel *)annotationModelfromAnnotionView:(BMKAnnotationView *)view{
    BMFAnnotationModel *model = [BMFAnnotationModel new];
    BMKPointAnnotation *an = (BMKPointAnnotation *)view.annotation;
    model.Id = an.Id;
    model.title = an.title;
    model.subtitle = an.subtitle;
    model.position = [BMFCoordinate fromCLLocationCoordinate2D:an.coordinate];
    model.isLockedToScreen = an.isLockedToScreen;
    model.screenPointToLock = [BMFMapPoint fromCGPoint:an.screenPointToLock];
    model.annotationViewOptions = an.annotationViewOptions;
    return model;
}
/// 根据anntation生成对应的View
- (BMKAnnotationView *)mapView:(BMKMapView *)mapView viewForAnnotation:(id<BMKAnnotation>)annotation{
    if ([annotation isKindOfClass:[BMKPointAnnotation class]]) {
        BMFAnnotationViewOptions *options =((BMKPointAnnotation *)annotation).annotationViewOptions;
        NSString *identifier = options.identifier ? options.identifier : NSStringFromClass([BMKPointAnnotation class]);
        BMKPinAnnotationView *annotationView = (BMKPinAnnotationView *)[mapView dequeueReusableAnnotationViewWithIdentifier:identifier];
        
        if (!annotationView) {
            annotationView = [[BMKPinAnnotationView alloc] initWithAnnotation:annotation reuseIdentifier:identifier];
        }
        
        if (options.icon) {
            //TODO:image加入空值判断
            annotationView.image = [UIImage imageWithContentsOfFile:[[BMFFileManager defaultCenter] pathForFlutterImageName:options.icon]];
        }
        
        if (options.centerOffset) {
            annotationView.centerOffset = [options.centerOffset toCGPoint];
        }
        annotationView.selected = options.selected;
        annotationView.draggable = options.draggable;
        annotationView.enabled = options.enabled;
        annotationView.enabled3D = options.enabled3D;

       return annotationView;
    }
    return nil;
}

/// 当mapView新添加annotation views时，调用此接口
- (void)mapView:(BMKMapView *)mapView didAddAnnotationViews:(NSArray *)views{
    if (!_channel) return;
}

/// 每次点击BMKAnnotationView都会回调此接口。
- (void)mapView:(BMKMapView *)mapView clickAnnotationView:(BMKAnnotationView *)view{
    if (!_channel) return;
    if ([view isKindOfClass:NSClassFromString(@"BMKUserLocationView")]) {
        return;
    }
    // 改为回调id
    BMFAnnotationModel *model = [self annotationModelfromAnnotionView:view];
    [_channel invokeMethod:kBMFMapClickedMarkerCallback arguments:@{@"id": model.Id} result:^(id  _Nullable result) {
        
    }];
}
/// 当选中一个annotation views时，调用此接口
/// @param mapView 地图View
/// @param view 选中的annotation views
- (void)mapView:(BMKMapView *)mapView didSelectAnnotationView:(BMKAnnotationView *)view{
    if (!_channel) return;
    if ([view isKindOfClass:NSClassFromString(@"BMKUserLocationView")]) {
        return;
    }
    // 改为回调id
    BMFAnnotationModel *model = [self annotationModelfromAnnotionView:view];
    [_channel invokeMethod:kBMFMapDidSelectMarkerCallback arguments:@{@"id": model.Id} result:nil];
}

/// 当取消选中一个annotationView时，调用此接口
- (void)mapView:(BMKMapView *)mapView didDeselectAnnotationView:(BMKAnnotationView *)view{
    if (!_channel) return;
    // 改为回调id
    BMFAnnotationModel *model = [self annotationModelfromAnnotionView:view];
    [_channel invokeMethod:kBMFMapDidDeselectMarkerCallback arguments:@{@"id": model.Id} result:nil];
}

/// 拖动annotation view时，若view的状态发生变化，会调用此函数。ios3.2以后支持
- (void)mapView:(BMKMapView *)mapView annotationView:(BMKAnnotationView *)view didChangeDragState:(BMKAnnotationViewDragState)newState
   fromOldState:(BMKAnnotationViewDragState)oldState{
     if (!_channel) return;
    // 改为回调id
    BMFAnnotationModel *model = [self annotationModelfromAnnotionView:view];
    [_channel invokeMethod:kBMFMapDidDragMarkerCallback arguments:@{@"id": model.Id} result:nil];

}

/// 当点击annotationView的泡泡view时，调用此接口
- (void)mapView:(BMKMapView *)mapView annotationViewForBubble:(BMKAnnotationView *)view{
    if (!_channel) return;
    // 改为回调id
    BMFAnnotationModel *model = [self annotationModelfromAnnotionView:view];
     [_channel invokeMethod:kBMFMapDidClickedPaoPaoCallback arguments:@{@"id": model.Id} result:nil];
}
#pragma mark - overlayView

- (BMFPolylineModel *)polylineModelWith:(BMKPolylineView *)view{
    BMFPolylineModel *model = [BMFPolylineModel new];
    BMKPolyline *line = view.polyline;
    model = line.polylineModel;
    return model;
}
- (BMKPolylineView *)viewForPolyline:(BMKPolyline *)polyline{
    BMFPolylineViewOptions *options = polyline.polylineModel.polylineOptions;
    BMKPolylineView *polylineView = [[BMKPolylineView alloc] initWithPolyline:polyline];
    polylineView.lineWidth = options.width;
    polylineView.lineDashType = options.lineDashType;
    polylineView.lineCapType = options.lineCapType;
    polylineView.lineJoinType = options.lineJoinType;
    
    switch (polyline.lineType) {
        case kBMFDashLine:
        case kBMFColorLine:{
            if ([options.colors firstObject]) {
                polylineView.strokeColor = [UIColor fromColorString:[options.colors firstObject]];
            } else {
                // TODO:strokeColor 默认值
            }
            break;
        }
        case kBMFMultiDashLine:
        case kBMFColorsLine:{
            size_t colorsCount = options.colors.count;
            NSMutableArray<UIColor *> *colors = [NSMutableArray array];
            for (size_t i = 0; i < colorsCount; i++) {
                  // TODO:colors加入空值判断
                [colors addObject:[UIColor fromColorString:options.colors[i]]];
            }
            polylineView.colors = colors;
            break;
        }
        case kBMFTextureLine:{
            // TODO:iamge加入空值判断
            NSString *imagePath = [[BMFFileManager defaultCenter] pathForFlutterImageName:[options.textures firstObject]];
            [polylineView loadStrokeTextureImage:[UIImage imageWithContentsOfFile:imagePath]];
            break;
        }
        case kBMFTexturesLine:{
            NSMutableArray<UIImage *> *images = [NSMutableArray array];
            size_t imagesCount = options.textures.count;
            NSString *imagePath = nil;
            for (size_t i = 0; i < imagesCount; i++) {
                  //TODO:image加入空值判断
                imagePath = options.textures[i];
                UIImage *image = [UIImage imageWithContentsOfFile:[[BMFFileManager defaultCenter] pathForFlutterImageName:imagePath]];
                [images addObject:image];
            }
            [polylineView loadStrokeTextureImages:images];
            break;
        }
        default:
            break;
    }
          
    return polylineView;
}

- (BMKArclineView *)viewForArcline:(BMKArcline *)arcline{
    BMFArclineViewOptions *options = arcline.arclineViewOptions;
    BMKArclineView *arclineView = [[BMKArclineView alloc] initWithArcline:arcline];
    if (options.color) {
        arclineView.strokeColor = [UIColor fromColorString:options.color];
    } else {
        arclineView.strokeColor = [UIColor colorWithRed:0.f green:0.f blue:1.f alpha:1.f];
    }
    arclineView.lineWidth = options.width;
    arclineView.lineDashType = options.lineDashType;
    return arclineView;
}

- (BMKPolygonView *)viewForPolygon:(BMKPolygon *)polygon{
    BMFPolygonViewOptions *options = polygon.polygonViewOptions;
    BMKPolygonView *polygonView = [[BMKPolygonView alloc] initWithPolygon:polygon];
    if (options.strokeColor) {
        polygonView.strokeColor = [UIColor fromColorString:options.strokeColor];
    } else {
        polygonView.strokeColor = [UIColor colorWithRed:0.f green:0.f blue:1.f alpha:1.f];
    }
    if (options.fillColor) {
        polygonView.fillColor = [UIColor fromColorString:options.fillColor];
    }
    polygonView.lineWidth = options.width;
    polygonView.lineDashType = options.lineDashType;
    return polygonView;
}

- (BMKCircleView *)viewForCircleline:(BMKCircle *)circle{
    BMFCircleViewOptions *options = circle.circleViewOptions;
    BMKCircleView *circleView = [[BMKCircleView alloc] initWithCircle:circle];
    if (options.strokeColor) {
        circleView.strokeColor = [UIColor fromColorString:options.strokeColor];
    } else {
        circleView.strokeColor = [UIColor colorWithRed:0.f green:0.f blue:1.f alpha:1.f];
    }
    if (options.fillColor) {
        circleView.fillColor = [UIColor fromColorString:options.fillColor];
    }
    circleView.lineWidth = options.width;
    circleView.lineDashType = options.lineDashType;
    return circleView;
}
- (BMKOverlayView *)mapView:(BMKMapView *)mapView viewForOverlay:(id<BMKOverlay>)overlay{
    if ([overlay isKindOfClass:[BMKPolyline class]]) {
        return [self viewForPolyline:(BMKPolyline *)overlay];
        
    } else if ([overlay isKindOfClass:[BMKArcline class]]) {
        return [self viewForArcline:(BMKArcline *)overlay];
        
    } else if ([overlay isKindOfClass:[BMKPolygon class]]){
        return [self viewForPolygon:(BMKPolygon *)overlay];
        
    } else if ([overlay isKindOfClass:[BMKCircle class]]) {
        return [self viewForCircleline:(BMKCircle *)overlay];
        
    } else if ([overlay isKindOfClass:[BMKTileLayer class]]){
        return [[BMKTileLayerView alloc] initWithTileLayer:overlay];
        
    } else if ([overlay isKindOfClass:[BMKGroundOverlay class]]) {
        return [[BMKGroundOverlayView alloc] initWithGroundOverlay:overlay];
    }
    
    return nil;
}
/**
 *当mapView新添加overlay views时，调用此接口
 *@param mapView 地图View
 *@param overlayViews 新添加的overlay views
 */
- (void)mapView:(BMKMapView *)mapView didAddOverlayViews:(NSArray *)overlayViews{
    if (!_channel) return;

     //TODO:didAddOverlayViews
}
/**
*点中覆盖物后会回调此接口，目前只支持点中BMKPolylineView时回调
*@param mapView 地图View
*@param overlayView 覆盖物view信息
*/
-(void)mapView:(BMKMapView *)mapView onClickedBMKOverlayView:(BMKOverlayView *)overlayView{
    if (!_channel) return;

    if ([overlayView isKindOfClass:[BMKPolylineView class]]) {
        BMFPolylineModel *model = [self polylineModelWith:(BMKPolylineView *)overlayView];
//        NSLog(@"%@", [model bmf_toDictionary]);
        // 暂时只传id
        [_channel invokeMethod:kMapOnClickedOverlayCallback arguments:@{@"polyline": @{@"id" :model.Id}} result:nil];
    }
}
@end

@interface FlutterMapViewFactory()
{
    NSObject<FlutterBinaryMessenger> *_messenger;
}
@end
@implementation FlutterMapViewFactory
- (instancetype)initWithMessenger:(NSObject<FlutterBinaryMessenger> *)messager{
    if ([super init]) {
        _messenger = messager;
    }
    return self;
}

- (NSObject<FlutterMessageCodec> *)createArgsCodec{
    return [FlutterStandardMessageCodec sharedInstance];
}

- (NSObject<FlutterPlatformView> *)createWithFrame:(CGRect)frame viewIdentifier:(int64_t)viewId arguments:(id)args{
   BMFMapViewController *mapViewController = [[BMFMapViewController alloc] initWithWithFrame:frame viewIdentifier:viewId arguments:args binaryMessenger:_messenger];
    return mapViewController;
}


@end
