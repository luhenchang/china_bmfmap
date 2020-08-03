//
//  BMFCircle.h
//  flutter_bmfmap
//
//  Created by zbj on 2020/2/15.
//

#ifndef __BMFCircle__H__
#define __BMFCircle__H__
#ifdef __OBJC__
#import <BaiduMapAPI_Map/BMKMapComponent.h>
#endif
#endif
#import "BMFCircleModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface BMKCircle (BMFCircle)

/// circleView唯一id
@property (nonatomic, copy, readonly) NSString *Id;

/// circleViewOptions
@property (nonatomic, strong, readonly) BMFCircleViewOptions *circleViewOptions;

+ (BMKCircle *)circlelineWith:(NSDictionary *)dic;

@end

NS_ASSUME_NONNULL_END
