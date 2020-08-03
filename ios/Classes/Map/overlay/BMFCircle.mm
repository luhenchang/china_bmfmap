//
//  BMFCircle.m
//  flutter_bmfmap
//
//  Created by zbj on 2020/2/15.
//

#import "BMFCircle.h"
#import <objc/runtime.h>
#import "BMFMapModels.h"

static const void *IDKey = &IDKey;
static const void *circleViewOptionsKey = &circleViewOptionsKey;
@implementation  BMKCircle (BMFCircle)

+ (BMKCircle *)circlelineWith:(NSDictionary *)dic{
    if (dic) {
        BMFCircleModel *model = [BMFCircleModel bmf_modelWith:dic];
         model.circleOptions = [BMFCircleViewOptions bmf_modelWith:dic];
        if (model.center) {
            BMKCircle *circle = [BMKCircle circleWithCenterCoordinate:[model.center toCLLocationCoordinate2D] radius:model.radius];
            circle.circleViewOptions = model.circleOptions;
            circle.Id = model.Id;
            return circle;
        }
      
    }
    return nil;
}

- (NSString *)Id{
    return objc_getAssociatedObject(self, IDKey);
}
- (void)setId:(NSString * _Nonnull)Id{
    objc_setAssociatedObject(self, IDKey, Id, OBJC_ASSOCIATION_COPY);
}

- (BMFCircleViewOptions *)circleViewOptions{
    return objc_getAssociatedObject(self, circleViewOptionsKey);
}
- (void)setCircleViewOptions:(BMFCircleViewOptions * _Nonnull)circleViewOptions{
    objc_setAssociatedObject(self, circleViewOptionsKey, circleViewOptions, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

@end
