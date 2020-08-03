//
//  BMFArcline.m
//  flutter_bmfmap
//
//  Created by zbj on 2020/2/15.
//

#import "BMFArcline.h"
#import <objc/runtime.h>
#import "BMFMapModels.h"

static const void *IDKey = &IDKey;
static const void *arclineViewOptionsKey = &arclineViewOptionsKey;

@implementation BMKArcline (BMFArcline)
+ (BMKArcline *)arclineWith:(NSDictionary *)dic{
    if (dic) {
        BMFArclineModel *model = [BMFArclineModel bmf_modelWith:dic];
        model.arclineOptions = [BMFArclineViewOptions bmf_modelWith:dic];
        int _coordsCount = (int)model.coordinates.count;
        if (_coordsCount < 3) {
            return nil;
        }
        
        CLLocationCoordinate2D *coords = new CLLocationCoordinate2D[_coordsCount];
        for (int i = 0; i < _coordsCount; i++) {
            coords[i] = [model.coordinates[i] toCLLocationCoordinate2D];
        }
        BMKArcline *arcline = [BMKArcline arclineWithCoordinates:coords];
        arcline.Id = model.Id;
        arcline.arclineViewOptions = model.arclineOptions;
        return arcline;
    }
    return nil;
}

- (NSString *)Id{
    return objc_getAssociatedObject(self, IDKey);
}
- (void)setId:(NSString * _Nonnull)Id{
    objc_setAssociatedObject(self, IDKey, Id, OBJC_ASSOCIATION_COPY);
}

- (BMFArclineViewOptions *)arclineViewOptions{
    return objc_getAssociatedObject(self, arclineViewOptionsKey);
}
- (void)setArclineViewOptions:(BMFArclineViewOptions * _Nonnull)arclineViewOptions{
    objc_setAssociatedObject(self, arclineViewOptionsKey, arclineViewOptions, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

@end
