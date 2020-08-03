#import "FlutterBmfmapPlugin.h"
#import <BaiduMapAPI_Base/BMKBaseComponent.h>
#import <BaiduMapAPI_Map/BMKMapComponent.h>
#import "BMFMapViewController.h"
#import "BMFFileManager.h"
#import "BMFOfflineMapManager.h"



static NSString *kBMFMapIdentifier = @"flutter_bmfmap/map/BMKMapView";
@interface FlutterBmfmapPlugin()<BMKGeneralDelegate>


@end

@implementation FlutterBmfmapPlugin
/// 注册
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
    // 初始化BMFFileManagerCenter
    [BMFFileManager defaultCenter].registar = registrar;
    
    // mapView
    [registrar registerViewFactory:[[FlutterMapViewFactory alloc] initWithMessenger:registrar.messenger] withId:kBMFMapIdentifier];
    
    // 离线地图
    [BMFOfflineMapManager registerWithRegistrar:registrar];

}

@end
