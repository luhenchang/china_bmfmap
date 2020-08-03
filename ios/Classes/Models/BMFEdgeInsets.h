//
//  BMFEdgeInsets.h
//  flutter_bmfmap
//
//  Created by zhangbaojin on 2020/3/3.
//

#import "BMFModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface BMFEdgeInsets : BMFModel

/// top
@property (nonatomic, assign) CGFloat top;

/// left
@property (nonatomic, assign) CGFloat left;

/// bottom
@property (nonatomic, assign) CGFloat bottom;

/// right
@property (nonatomic, assign) CGFloat right;

- (UIEdgeInsets)toUIEdgeInsets;

@end

NS_ASSUME_NONNULL_END
