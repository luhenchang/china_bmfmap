//
//  BMFPolylineModel.m
//  flutter_bmfmap
//
//  Created by zbj on 2020/2/11.
//

#import "BMFPolylineModel.h"
#import "BMFMapModels.h"

@implementation BMFPolylineModel

+ (NSDictionary *)bmf_setupObjectClassInArray{
    return @{@"coordinates" : @"BMFCoordinate",
             @"indexs" : @"NSNumber"
    };
}
+ (NSDictionary *)bmf_setupReplacedKeyFromPropertyName{
    return @{@"Id" : @"id"};
}

@end


@implementation BMFPolylineViewOptions

+ (NSDictionary *)bmf_setupObjectClassInArray{
    return @{@"colors" : @"NSString",
             @"textures" : @"NSString"
    };
}

@end
