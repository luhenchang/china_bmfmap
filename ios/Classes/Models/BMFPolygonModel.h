//
//  BMFPolygonModel.h
//  flutter_bmfmap
//
//  Created by zhangbaojin on 2020/2/27.
//

#import "BMFModel.h"

@class BMFCoordinate;
@class BMFPolygonViewOptions;
NS_ASSUME_NONNULL_BEGIN

@interface BMFPolygonModel : BMFModel

/// flutter层polygon的唯一id(用于区别哪个polygon)
@property (nonatomic, copy) NSString *Id;

/// 经纬度数组
@property (nonatomic, strong) NSArray<BMFCoordinate *> *coordinates;

/// polygonView属性model
@property (nonatomic,strong) BMFPolygonViewOptions *polygonOptions;

@end


@interface BMFPolygonViewOptions : BMFModel

/// 线宽
@property (nonatomic, assign) int width;

/// 颜色16进制strokeColor
@property (nonatomic, copy) NSString *strokeColor;

/// 颜色16进制fillColor
@property (nonatomic, copy) NSString *fillColor;

/// 虚线类型
@property (nonatomic, assign) int lineDashType;

@end



NS_ASSUME_NONNULL_END