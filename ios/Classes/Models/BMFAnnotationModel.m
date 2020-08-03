//
//  BMFAnnotationModel.m
//  flutter_bmfmap
//
//  Created by zbj on 2020/2/11.
//

#import "BMFAnnotationModel.h"
#import "BMFMapModels.h"

@implementation BMFAnnotationModel

+ (NSDictionary *)bmf_setupReplacedKeyFromPropertyName{
    return @{@"Id" : @"id",
             @"annotationViewOptions" : @"markerOptions"
    };
}

@end


@implementation BMFAnnotationViewOptions

@end
