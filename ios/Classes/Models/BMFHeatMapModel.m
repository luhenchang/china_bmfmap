//
//  BMFHeatMapModel.m
//  flutter_bmfmap
//
//  Created by zhangbaojin on 2020/3/26.
//

#import "BMFHeatMapModel.h"
#import <BaiduMapAPI_Map/BMKHeatMap.h>
#import "BMFMapModels.h"
#import "UIColor+BMFString.h"
@implementation BMFHeatMapModel

+ (NSDictionary *)bmf_setupObjectClassInArray{
    return @{@"data" : @"BMFHeatMapModelNode"};
}

- (BMKHeatMap *)toBMKHeatMap{
    BMKHeatMap *heatMap = [BMKHeatMap new];
    heatMap.mRadius = self.radius;
    heatMap.mGradient = [self.gradient toBMKGradient];
    heatMap.mOpacity = self.opacity;
    NSMutableArray *mut = [NSMutableArray array];
    for (BMFHeatMapModelNode *node in self.data) {
        [mut addObject:[node toBMKHeatMapNode]];
    }
    heatMap.mData = mut;
    return heatMap;
}

@end

@implementation BMFGradient

+ (NSDictionary *)bmf_setupObjectClassInArray{
    return @{@"colors" : @"NSString",
             @"startPoints" : @"NSNumber"
    };
}

- (BMKGradient *)toBMKGradient{
    BMKGradient *gradient = [BMKGradient new];
    NSMutableArray *colors = [NSMutableArray array];
    for (NSString *color in self.colors) {
        [colors addObject:[UIColor fromColorString:color]];
    }
    gradient.mColors = colors;
    gradient.mStartPoints = self.startPoints;
    return gradient;
}

@end


@implementation  BMFHeatMapModelNode

- (BMKHeatMapNode *)toBMKHeatMapNode{
    BMKHeatMapNode *node = [BMKHeatMapNode new];
    node.intensity = self.intensity;
    node.pt = [self.pt toCLLocationCoordinate2D];
    return node;
}

@end
