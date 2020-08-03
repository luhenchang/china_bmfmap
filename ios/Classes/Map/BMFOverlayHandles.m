//
//  BMFOverlayHandles.m
//  flutter_bmfmap
//
//  Created by zhangbaojin on 2020/2/12.
//

#import "BMFOverlayHandles.h"
#import "BMFMapView.h"
#import "BMFOverlayMethodConst.h"
#import "BMFFileManager.h"
#import "UIColor+BMFString.h"
#import "BMFMapModels.h"
#import "BMFPolyline.h"
#import "BMFArcline.h"
#import "BMFPolygon.h"
#import "BMFCircle.h"
#import "BMFTileModel.h"
#import "BMFURLTileLayer.h"
#import "BMFAsyncTileLayer.h"
#import "BMFSyncTileLayer.h"
#import "BMFGroundOverlay.h"

@interface BMFOverlayHandles ()
{
    NSDictionary *_handles;
}
@end

@implementation BMFOverlayHandles

static  BMFOverlayHandles *_instance = nil;
+ (instancetype)defalutCenter{
    if (!_instance) {
        static dispatch_once_t onceToken;
        dispatch_once(&onceToken, ^{
            _instance = [[BMFOverlayHandles alloc] init];
        });
    }
    return _instance;
}

- (NSDictionary<NSString *, NSString *> *)overlayHandles{
    if (!_handles) {
        _handles = @{
            kBMFMapAddPolylineMethod: NSStringFromClass([BMFAddPolyline class]),
            kBMFMapAddArcineMethod: NSStringFromClass([BMFAddArcline class]),
            kBMFMapAddPolygonMethod: NSStringFromClass([BMFAddPolygon class]),
            kBMFMapAddCircleMethod: NSStringFromClass([BMFAddCircle class]),
            kBMFMapAddTileMethod: NSStringFromClass([BMFAddTile class]),
            kBMFMapAddGroundMethod: NSStringFromClass([BMFAddGround class]),
            kBMFMapRemoveOverlayMethod: NSStringFromClass([BMFRemoveOverlay class]),
            kBMFMapRemoveTileMethod: NSStringFromClass([BMFRemoveTileOverlay class]),
            kBMFMapUpdatePolylineMemberMethod: NSStringFromClass([BMFUpdatePolyline class])
        };
    }
    return _handles;
}
@end


#pragma mark - overlay

@implementation BMFAddPolyline

@synthesize _mapView;

- (nonnull NSObject<BMFMapViewHandler> *)initWith:(nonnull BMFMapView *)mapView {
    _mapView = mapView;
    return self;
}

- (void)handleMethodCall:(nonnull FlutterMethodCall *)call result:(nonnull FlutterResult)result {
    BMKPolyline *polyline = [BMKPolyline polylineWith:call.arguments];
    if (polyline) {
        [_mapView addOverlay:polyline];
        result(@YES);
    } else {
        result(@NO);
    }
}

@end

@implementation BMFAddArcline

@synthesize _mapView;

- (nonnull NSObject<BMFMapViewHandler> *)initWith:(nonnull BMFMapView *)mapView {
    _mapView = mapView;
    return self;
}

- (void)handleMethodCall:(nonnull FlutterMethodCall *)call result:(nonnull FlutterResult)result {
    BMKArcline *arcline = [BMKArcline arclineWith:call.arguments];
    if (arcline) {
        [_mapView addOverlay:arcline];
        result(@YES);
    } else {
        result(@NO);
    }
}

@end


@implementation BMFAddPolygon

@synthesize _mapView;

- (nonnull NSObject<BMFMapViewHandler> *)initWith:(nonnull BMFMapView *)mapView {
    _mapView = mapView;
    return self;
}

- (void)handleMethodCall:(nonnull FlutterMethodCall *)call result:(nonnull FlutterResult)result {
    BMKPolygon *polygon = [BMKPolygon polygonWith:call.arguments];
    if (polygon) {
        [_mapView addOverlay:polygon];
        result(@YES);
    } else {
        result(@NO);
    }
}

@end


@implementation BMFAddCircle

@synthesize _mapView;

- (nonnull NSObject<BMFMapViewHandler> *)initWith:(nonnull BMFMapView *)mapView {
    _mapView = mapView;
    return self;
}

- (void)handleMethodCall:(nonnull FlutterMethodCall *)call result:(nonnull FlutterResult)result {
    BMKCircle *circle = [BMKCircle circlelineWith:call.arguments];
    if (circle) {
        [_mapView addOverlay:circle];
        result(@YES);
        return;
    } else {
        result(@NO);
    }
}

@end

@implementation BMFAddTile

@synthesize _mapView;

- (nonnull NSObject<BMFMapViewHandler> *)initWith:(nonnull BMFMapView *)mapView {
    _mapView = mapView;
    return self;
}

- (void)handleMethodCall:(nonnull FlutterMethodCall *)call result:(nonnull FlutterResult)result {
    if (!call.arguments) {
        result(@NO);
        return;
    }
    BMFTileModel *model = [BMFTileModel bmf_modelWith:call.arguments];
    model.tileOptions = [BMFTileModelOptions bmf_modelWith:call.arguments];
//    NSLog(@"%@", [model bmf_toDictionary]);
    if (!model) {
        result(@NO);
        return;
    }
    if (model.tileOptions.tileLoadType == kBMFTileLoadUrl && model.tileOptions.url) {
        BMFURLTileLayer *urlTileLayer = [BMFURLTileLayer urlTileLayerWith:model];
        [_mapView addOverlay:urlTileLayer];
        result(@YES);
        return;
    } else if (model.tileOptions.tileLoadType == kBMFTileLoadLocalAsync) {
        BMFAsyncTileLayer *asyncTileLayer = [BMFAsyncTileLayer asyncTileLayerWith:model];
        [_mapView addOverlay:asyncTileLayer];
        result(@YES);
        return;
    } else if (model.tileOptions.tileLoadType == kBMFTileLoadLocalSync) {
        BMFSyncTileLayer *syncTileLayer = [BMFSyncTileLayer syncTileLayerWith:model];
        [_mapView addOverlay:syncTileLayer];
        result(@YES);
        return;
    } else {
        result(@NO);
        return;
    }
}

@end

@implementation BMFAddGround

@synthesize _mapView;

- (nonnull NSObject<BMFMapViewHandler> *)initWith:(nonnull BMFMapView *)mapView {
    _mapView = mapView;
    return self;
}

- (void)handleMethodCall:(nonnull FlutterMethodCall *)call result:(nonnull FlutterResult)result {
    BMKGroundOverlay *ground = [BMKGroundOverlay groundOverlayWith:call.arguments];
    if (ground) {
        [_mapView addOverlay:ground];
        result(@YES);
        return;
    } else {
        result(@NO);
    }
}



@end

@implementation BMFRemoveOverlay

@synthesize _mapView;

- (nonnull NSObject<BMFMapViewHandler> *)initWith:(nonnull BMFMapView *)mapView {
    _mapView = mapView;
    return self;
}

- (void)handleMethodCall:(nonnull FlutterMethodCall *)call result:(nonnull FlutterResult)result {
    if (!call.arguments || !call.arguments[@"id"]) {
        result(@NO);
        return;
    }
    NSString *ID = [call.arguments safeValueForKey:@"id"];
    __weak __typeof__(_mapView) weakMapView = _mapView;
    [_mapView.overlays enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        // 折线
        if ([obj isKindOfClass:[BMKPolyline class]]) {
            BMKPolyline *overlay = (BMKPolyline *)obj;
            if ([ID isEqualToString:overlay.Id]) {
                [weakMapView removeOverlay:obj];
                result(@YES);
                *stop = YES;
                return;
            }
        }
        // 多边形
        if ([obj isKindOfClass:[BMKPolygon class]]) {
            BMKPolygon  *overlay = (BMKPolygon *)obj;
            if ([ID isEqualToString:overlay.Id]) {
                [weakMapView removeOverlay:obj];
                result(@YES);
                *stop = YES;
                return;
            }
        }
        // 弧线
        if ([obj isKindOfClass:[BMKArcline class]]) {
            BMKArcline *overlay = (BMKArcline *)obj;
            if ([ID isEqualToString:overlay.Id]) {
                [weakMapView removeOverlay:obj];
                result(@YES);
                *stop = YES;
                return;
            }
        }
        // 圆
        if ([obj isKindOfClass:[BMKCircle class]]) {
            BMKCircle *overlay = (BMKCircle *)obj;
            if ([ID isEqualToString:overlay.Id]) {
                [weakMapView removeOverlay:obj];
                result(@YES);
                *stop = YES;
                return;
            }
        }
        // ground
        if ([obj isKindOfClass:[BMKGroundOverlay class]]) {
            BMKGroundOverlay *overlay = (BMKGroundOverlay *)obj;
            if ([ID isEqualToString:overlay.Id]) {
                [weakMapView removeOverlay:obj];
                result(@YES);
                *stop = YES;
                return;
            }
        }
        // 瓦片图
        if ([obj isKindOfClass:[BMFURLTileLayer class]]) {
            BMFURLTileLayer *overlay = (BMFURLTileLayer *)obj;
            if ([ID isEqualToString:overlay.Id]) {
                [weakMapView removeOverlay:obj];
                result(@YES);
                *stop = YES;
                return;
            }
        }
        if ([obj isKindOfClass:[BMFAsyncTileLayer class]]) {
            BMFAsyncTileLayer *overlay = (BMFAsyncTileLayer *)obj;
            if ([ID isEqualToString:overlay.Id]) {
                [weakMapView removeOverlay:obj];
                result(@YES);
                *stop = YES;
                return;
            }
        }
        if ([obj isKindOfClass:[BMFSyncTileLayer class]]) {
            BMFSyncTileLayer *overlay = (BMFSyncTileLayer *)obj;
            if ([ID isEqualToString:overlay.Id]) {
                [weakMapView removeOverlay:obj];
                result(@YES);
                *stop = YES;
                return;
            }
        }

    }];
}


@end

@implementation BMFRemoveTileOverlay

@synthesize _mapView;

- (nonnull NSObject<BMFMapViewHandler> *)initWith:(nonnull BMFMapView *)mapView {
    _mapView = mapView;
    return self;
}

- (void)handleMethodCall:(nonnull FlutterMethodCall *)call result:(nonnull FlutterResult)result {
    
    if (!call.arguments || !call.arguments[@"id"]) {
        result(@NO);
        return;
    }
    NSString *ID = [call.arguments safeValueForKey:@"id"];
    __weak __typeof__(_mapView) weakMapView = _mapView;
    [_mapView.overlays enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        // 瓦片图
        if ([obj isKindOfClass:[BMFURLTileLayer class]]) {
            BMFURLTileLayer *overlay = (BMFURLTileLayer *)obj;
            if ([ID isEqualToString:overlay.Id]) {
                [weakMapView removeOverlay:obj];
                result(@YES);
                *stop = YES;
                return;
            }
        }
        if ([obj isKindOfClass:[BMFAsyncTileLayer class]]) {
            BMFAsyncTileLayer *overlay = (BMFAsyncTileLayer *)obj;
            if ([ID isEqualToString:overlay.Id]) {
                [weakMapView removeOverlay:obj];
                result(@YES);
                *stop = YES;
                return;
            }
        }
        if ([obj isKindOfClass:[BMFSyncTileLayer class]]) {
            BMFSyncTileLayer *overlay = (BMFSyncTileLayer *)obj;
            if ([ID isEqualToString:overlay.Id]) {
                [weakMapView removeOverlay:obj];
                result(@YES);
                *stop = YES;
                return;
            }
        }

    }];
}


@end

#pragma mark - Update
@implementation BMFUpdatePolyline

@synthesize _mapView;

- (nonnull NSObject<BMFMapViewHandler> *)initWith:(nonnull BMFMapView *)mapView {
    _mapView = mapView;
    return self;
}

- (void)handleMethodCall:(nonnull FlutterMethodCall *)call result:(nonnull FlutterResult)result {
    if (!call.arguments || !call.arguments[@"id"]) {
        result(@NO);
        return;
    }
    NSString *ID = [call.arguments safeValueForKey:@"id"];
    __block  BMKPolyline *polyline;
    [_mapView.overlays enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        if ([obj isKindOfClass:[BMKPolyline class]]) {
            BMKPolyline *line = (BMKPolyline *)obj;
            if ([ID isEqualToString:line.Id]) {
                polyline = line;
                *stop = YES;
            }
        }
    }];
    if (!polyline) {
        result(@NO);
        return;
    }
    NSString *member = [call.arguments safeValueForKey:@"member"];

//    kBMFColorLine = 0,   ///< 单色折线
//    kBMFColorsLine,      ///< 多色折线
//    kBMFTextureLine,     ///< 单纹理折线
//    kBMFTexturesLine,    ///< 多纹理折线
//    kBMFDashLine,        ///< 虚线
//    kBMFMultiDashLine    ///< 多色虚线
    
    if ([member isEqualToString:@"coordinates"]) {

        NSArray<NSDictionary *> *coordinates = [call.arguments safeObjectForKey:@"value"];
        if (!coordinates || coordinates.count <= 1) {
            result(@NO);
            return;
        }
                
        CLLocationCoordinate2D coords[coordinates.count];
        for (size_t i = 0, count = coordinates.count; i < count; i++) {
            BMFCoordinate *coord = [BMFCoordinate bmf_modelWith:coordinates[i]];
            coords[i] = [coord toCLLocationCoordinate2D];
        }
        switch (polyline.lineType) {
            case kBMFColorLine:
            case kBMFTextureLine:
            case kBMFDashLine: {
                [polyline setPolylineWithCoordinates:coords count:coordinates.count];
                [_mapView setMapStatus:_mapView.getMapStatus];
                result(@YES);
            }
                break;
            case kBMFColorsLine:
            case kBMFTexturesLine:
            case kBMFMultiDashLine: {
                if (![call.arguments safeObjectForKey:@"indexs"]) {
                    result(@NO);
                    return;
                }
                NSMutableArray<NSNumber *> *indexs = [NSMutableArray array];
                for (NSNumber *value in call.arguments[@"indexs"]) {
                    [indexs addObject:value];
                }
                [polyline setPolylineWithCoordinates:coords count:coordinates.count textureIndex:indexs];
                [_mapView setMapStatus:_mapView.getMapStatus];
                result(@YES);
            }
                
                break;
            default:
                break;
        }
        
    } else if ([member isEqualToString:@"width"]) {
        BMKPolylineView *view = (BMKPolylineView *)[_mapView viewForOverlay:polyline];
        view.lineWidth = [[call.arguments safeValueForKey:@"value"] floatValue];
        [_mapView setMapStatus:_mapView.getMapStatus];
        result(@YES);
        return;
        
    } else if ([member isEqualToString:@"colors"]) {
        NSArray *colorsData = [call.arguments safeObjectForKey:@"value"];
        if (!colorsData || colorsData.count <= 0) {
            result(@NO);
            return;
        }
        BMKPolylineView *view = (BMKPolylineView *)[_mapView viewForOverlay:polyline];
        NSMutableArray<UIColor *> *colors = [NSMutableArray array];
        for (NSString *color in colorsData) {
            [colors addObject:[UIColor fromColorString:color]];
        }
        if (polyline.lineType == kBMFColorsLine || polyline.lineType == kBMFMultiDashLine) {
            view.colors = [colors copy];
            [_mapView setMapStatus:_mapView.getMapStatus];
            result(@YES);
            return;
            
        } else if (polyline.lineType ==kBMFColorLine || polyline.lineType == kBMFDashLine) {
            view.strokeColor = [colors firstObject];
            [_mapView setMapStatus:_mapView.getMapStatus];
            result(@YES);
            return;
        } else {
            NSLog(@"ios - 纹理折线不支持更新colors");
            result(@NO);
            return;
        }
        
        
    } else if ([member isEqualToString:@"lineDashType"]) {
        if (polyline.lineType == kBMFTextureLine || polyline.lineType == kBMFTexturesLine) {
            NSLog(@"ios - 纹理折线不支持虚线类型");
            result(@NO);
            return;
        }
        BMFPolylineModel *model = polyline.polylineModel;
        model.polylineOptions.lineDashType = [[call.arguments safeValueForKey:@"value"] intValue];
        [_mapView removeOverlay:polyline];
        
        BMKPolyline *dashLine = [BMKPolyline polylineWithModel:model];
        [_mapView addOverlay:dashLine];
//        BMKPolylineView *view = (BMKPolylineView *)[_mapView viewForOverlay:polyline];
//        view.lineDashType = [[call.arguments safeValueForKey:@"value"] intValue];
//        [_mapView setMapStatus:_mapView.getMapStatus];
        result(@YES);
        return;
        
    } else if ([member isEqualToString:@"lineCapType"]) {
        if (polyline.lineType == kBMFDashLine || polyline.lineType == kBMFMultiDashLine) {
            NSLog(@"ios - lineCapType不支持虚线");
            result(@NO);
            return;
        }
        BMKPolylineView *view = (BMKPolylineView *)[_mapView viewForOverlay:polyline];
        view.lineCapType = [[call.arguments safeValueForKey:@"value"] intValue];
        [_mapView setMapStatus:_mapView.getMapStatus];
        result(@YES);
        return;
        
    } else if ([member isEqualToString:@"lineJoinType"]) {
        if (polyline.lineType == kBMFDashLine || polyline.lineType == kBMFMultiDashLine) {
            NSLog(@"ios - lineJoinType不支持虚线");
            result(@NO);
            return;
        }
        BMKPolylineView *view = (BMKPolylineView *)[_mapView viewForOverlay:polyline];
        view.lineJoinType = [[call.arguments safeValueForKey:@"value"] intValue];
        [_mapView setMapStatus:_mapView.getMapStatus];
        result(@YES);
        return;
        
    } else if ([member isEqualToString:@"isThined"]) {
        polyline.isThined = [[call.arguments safeValueForKey:@"value"] boolValue];
        [_mapView setMapStatus:_mapView.getMapStatus];
        result(@YES);
        return;
        
    } else if ([member isEqualToString:@"textures"]) {
        NSLog(@"ios - 暂不支持更新textures");
        result(@YES);
        return;
           
    } else if ([member isEqualToString:@"clickable"]) {
        NSLog(@"ios - 暂不支持设置clickable");
        result(@YES);
        return;
           
    } else if ([member isEqualToString:@"isKeepScale"]) {
        NSLog(@"ios - 暂不支持设置isKeepScale");
        result(@YES);
        return;
           
    } else if ([member isEqualToString:@"isFocus"]) {
        NSLog(@"ios - 暂不支持设置isFocus");
        result(@YES);
        return;
           
    } else if ([member isEqualToString:@"visible"]) {
        NSLog(@"ios - 暂不支持设置visible");
        result(@YES);
        return;
        
    } else if ([member isEqualToString:@"zIndex"]) {
        NSLog(@"ios - 暂不支持设置zIndex");
        result(@YES);
        return;
        
    } else {
        result(@YES);
    }
    
}

@end
