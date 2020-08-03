//
//  BMFAnnotation.m
//  flutter_bmfmap
//
//  Created by zbj on 2020/2/11.
//

#import "BMFAnnotation.h"
#import <objc/runtime.h>
#import "BMFMapModels.h"

static const void *IDKey = &IDKey;
static const void *annotationViewOptionsKey = &annotationViewOptionsKey;

@implementation BMKPointAnnotation (BMFAnnotation)
- (NSString *)Id{
    return objc_getAssociatedObject(self, IDKey);
}
- (void)setId:(NSString * _Nonnull)Id{
    objc_setAssociatedObject(self, IDKey, Id, OBJC_ASSOCIATION_COPY);
}

- (BMFAnnotationViewOptions *)annotationViewOptions{
    return objc_getAssociatedObject(self, annotationViewOptionsKey);
}
- (void)setAnnotationViewOptions:(BMFAnnotationViewOptions * _Nonnull)annotationViewOptions {
    objc_setAssociatedObject(self, annotationViewOptionsKey, annotationViewOptions, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

+ (instancetype)annotationWith:(NSDictionary *)dic{
    return [[self alloc] initWith:dic];
}
- (instancetype)initWith:(NSDictionary *)dic{
    if ([super init]) {
        if (dic) {
            BMFAnnotationModel *model = [BMFAnnotationModel bmf_modelWith:dic];
            model.annotationViewOptions = [BMFAnnotationViewOptions bmf_modelWith:dic];
            [self configAnnotation:model];
        }
    }
    return self;
}

- (void)configAnnotation:(BMFAnnotationModel*)model{
    if (model.Id) {
        self.Id = model.Id;
    }
    if (model.title) {
        self.title = model.title;
    }
   if (model.subtitle) {
       self.subtitle = model.subtitle;
   }
   if (model.position) {
       self.coordinate = [model.position toCLLocationCoordinate2D];
   }
    self.isLockedToScreen = model.isLockedToScreen;
   if (model.screenPointToLock) {
     self.screenPointToLock = [model.screenPointToLock toCGPoint];
   }
   
    self.annotationViewOptions = model.annotationViewOptions;
}

@end
