
#import "React/RCTBridgeModule.h"
#import "React/RCTEventDispatcher.h"
#import <React/RCTEventEmitter.h>

@interface RNTaplyticsReact : RCTEventEmitter <RCTBridgeModule>

- (void)sendPushEvent:(NSString *)name withData:(NSDictionary *)userInfo;

@end
  
