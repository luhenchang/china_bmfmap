//
//  BMFPolygon.m
//  flutter_bmfmap
//
//  Created by zhangbaojin on 2020/2/27.
//

#import "BMFPolygon.h"
#import <objc/runtime.h>
#import "BMFMapModels.h"

static const void *IDKey = &IDKey;
static const void *polygonViewOptionsKey = &polygonViewOptionsKey;
@implementation BMKPolygon (BMFPolygon)

+ (BMKPolygon *)polygonWith:(NSDictionary *)dic{
    if (dic) {
        BMFPolygonModel *model = [BMFPolygonModel bmf_modelWith:dic];
        model.polygonOptions = [BMFPolygonViewOptions bmf_modelWith:dic];
        size_t _coordsCount = model.coordinates.count;
        CLLocationCoordinate2D *coords = new CLLocationCoordinate2D[_coordsCount];
        for (size_t i = 0; i < _coordsCount; i++) {
            coords[i] = [model.coordinates[i] toCLLocationCoordinate2D];
        }
        
        BMKPolygon *polygon = [BMKPolygon polygonWithCoordinates:coords count:_coordsCount];
        polygon.polygonViewOptions = model.polygonOptions;
        polygon.Id = model.Id;
        return polygon;
    }
    return nil;
}

- (NSString *)Id{
    return objc_getAssociatedObject(self, IDKey);
}
- (void)setId:(NSString * _Nonnull)Id{
    objc_setAssociatedObject(self, IDKey, Id, OBJC_ASSOCIATION_COPY);
}

- (BMFPolygonViewOptions *)polygonViewOptions{
    return objc_getAssociatedObject(self, polygonViewOptionsKey);
}
- (void)setPolygonViewOptions:(BMFPolygonViewOptions * _Nonnull)polygonViewOptions{
    objc_setAssociatedObject(self, polygonViewOptionsKey, polygonViewOptions, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

@end
