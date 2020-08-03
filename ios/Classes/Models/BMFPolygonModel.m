//
//  BMFPolygonModel.m
//  flutter_bmfmap
//
//  Created by zhangbaojin on 2020/2/27.
//

#import "BMFPolygonModel.h"
#import "BMFMapModels.h"
@implementation BMFPolygonModel

+ (NSDictionary *)bmf_setupReplacedKeyFromPropertyName{
    return @{@"Id" : @"id"};
}

+ (NSDictionary *)bmf_setupObjectClassInArray{
    return @{@"coordinates" : @"BMFCoordinate"};
}

@end


@implementation BMFPolygonViewOptions

@end
