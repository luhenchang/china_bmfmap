//
//  BMFPolygon.h
//  flutter_bmfmap
//
//  Created by zhangbaojin on 2020/2/27.
//

#ifndef __BMFPolygon__H__
#define __BMFPolygon__H__
#ifdef __OBJC__
#import <BaiduMapAPI_Map/BMKMapComponent.h>
#endif
#endif
#import "BMFPolygonModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface BMKPolygon (BMFPolygon)

/// polylgonView唯一id
@property (nonatomic, copy, readonly) NSString *Id;

/// BMKPolygonView参数集合
@property (nonatomic, strong, readonly) BMFPolygonViewOptions *polygonViewOptions;

/// BMKPolygon
+ (BMKPolygon *)polygonWith:(NSDictionary *)dic;

@end

NS_ASSUME_NONNULL_END
