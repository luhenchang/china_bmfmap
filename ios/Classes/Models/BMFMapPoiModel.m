//
//  BMFMapPoiModel.m
//  flutter_bmfmap
//
//  Created by zhangbaojin on 2020/3/6.
//

#import "BMFMapPoiModel.h"
#import "BMFMapModels.h"
#import <BaiduMapAPI_Map/BMKMapView.h>

@implementation BMFMapPoiModel
+ (BMFMapPoiModel *)fromBMKMapPoi:(BMKMapPoi *)poi{
    BMFMapPoiModel *model = [BMFMapPoiModel new];
    model.text = poi.text;
    model.pt = [BMFCoordinate fromCLLocationCoordinate2D:poi.pt];
    model.uid = poi.uid;
    return model;
}
@end
