
#import "React/RCTBridgeModule.h"
#import "React/RCTEventDispatcher.h"

@interface RNTaplyticsReact : NSObject <RCTBridgeModule>

- (void)sendPushEvent:(NSString *)name;

@end
  
