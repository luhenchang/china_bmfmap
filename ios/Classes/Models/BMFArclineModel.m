//
//  BMFArclineModel.m
//  flutter_bmfmap
//
//  Created by zbj on 2020/2/15.
//

#import "BMFArclineModel.h"
#import "BMFMapModels.h"
@implementation BMFArclineModel

+ (NSDictionary *)bmf_setupObjectClassInArray{
    return @{@"coordinates" : @"BMFCoordinate"};
}

+ (NSDictionary *)bmf_setupReplacedKeyFromPropertyName{
    return @{@"Id" : @"id"};
}

@end

@implementation BMFArclineViewOptions


@end
