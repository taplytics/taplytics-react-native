
#import "RNTaplyticsReact.h"
#import <Taplytics/Taplytics.h>

@implementation RNTaplyticsReact

@synthesize bridge = _bridge;

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

- (void)sendEvent:(NSString *)name withValue:(id)value
{
    [self.bridge.eventDispatcher sendAppEventWithName:name body:@{@"value": value} ];
}

RCT_EXPORT_MODULE(Taplytics);

RCT_EXPORT_METHOD(_newSyncBool:(NSString *)name defaultValue:(BOOL)defaultValue resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    TaplyticsVar* variable = [TaplyticsVar taplyticsSyncVarWithName:name defaultValue:@(defaultValue)];
    resolve((NSNumber *)variable.value);
}

RCT_EXPORT_METHOD(_newSyncString:(NSString *)name defaultValue:(NSString *)defaultValue resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    TaplyticsVar* variable = [TaplyticsVar taplyticsSyncVarWithName:name defaultValue:defaultValue];
    resolve((NSString *)variable.value);
}

RCT_EXPORT_METHOD(_newSyncNumber:(NSString *)name defaultValue:(NSNumber *)defaultValue resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    TaplyticsVar* variable = [TaplyticsVar taplyticsSyncVarWithName:name defaultValue:defaultValue];
    resolve((NSNumber *)variable.value);
}

RCT_EXPORT_METHOD(_newSyncObject:(NSString *)name defaultValue:(NSString *)defaultValue resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    NSData* data = [defaultValue dataUsingEncoding:NSUTF8StringEncoding];
    NSError* err;
    @try {
        id object = [NSJSONSerialization JSONObjectWithData:data options:0 error:&err];
        TaplyticsVar* variable = [TaplyticsVar taplyticsSyncVarWithName:name defaultValue:object];
        
        if ([NSJSONSerialization isValidJSONObject:variable.value]) {
            NSData* jsonData = [NSJSONSerialization dataWithJSONObject:variable.value options:0 error:&err];
            resolve(jsonData ? [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding] : nil);
            return;
        }
    } @catch (NSException* e) {
        reject(@"TaplyticsReact", e.reason, err);
    }
    if (err) {
        reject(@"TaplyticsReact", err.description, err);
    }
}

RCT_EXPORT_METHOD(_newAsyncBool:(NSString *)name defaultValue:(BOOL)defaultValue)
{
    TaplyticsVar* variable = [TaplyticsVar taplyticsVarWithName:name defaultValue:@(defaultValue) updatedBlock:^(NSObject* value) {
        [self sendEvent:name withValue:value];
    }];
}

RCT_EXPORT_METHOD(_newAsyncString:(NSString *)name defaultValue:(NSString *)defaultValue)
{
    TaplyticsVar* variable = [TaplyticsVar taplyticsVarWithName:name defaultValue:defaultValue updatedBlock:^(NSObject* value) {
        [self sendEvent:name withValue:value];
    }];
}

RCT_EXPORT_METHOD(_newAsyncNumber:(NSString *)name defaultValue:(NSNumber *)defaultValue)
{
    TaplyticsVar* variable = [TaplyticsVar taplyticsVarWithName:name defaultValue:defaultValue updatedBlock:^(NSObject* value) {
        [self sendEvent:name withValue:value];
    }];
}

RCT_EXPORT_METHOD(_newAsyncObject:(NSString *)name defaultValue:(NSString *)defaultValue)
{
    NSData* data = [defaultValue dataUsingEncoding:NSUTF8StringEncoding];
    NSError* err;
    @try {
        id object = [NSJSONSerialization JSONObjectWithData:data options:0 error:&err];
        TaplyticsVar* variable = [TaplyticsVar taplyticsVarWithName:name defaultValue:object updatedBlock:^(NSObject* value) {
            if ([NSJSONSerialization isValidJSONObject:variable.value]) {
                NSError* err;
                NSData* jsonData = [NSJSONSerialization dataWithJSONObject:variable.value options:0 error:&err];
                if (err) {
                    NSLog(err.description);
                }
                [self sendEvent:name withValue:(jsonData ? [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding] : nil)];
            }
        }];
       
    } @catch (NSException* e) {
        NSLog(e.reason);
    }
    if (err) {
        NSLog(err.description);
    }
}

RCT_EXPORT_METHOD(runCodeBlock:(NSString *)name codeBlock:(RCTResponseSenderBlock)codeBlock)
{
    codeBlock(@[[NSNull null]]);
}

RCT_EXPORT_METHOD(propertiesLoadedCallback:resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    [Taplytics propertiesLoadedCallback:^(BOOL loaded) {
        resolve(@(loaded));
    }];
}

RCT_EXPORT_METHOD(registerPushNotifications)
{
    [Taplytics registerPushNotifications];
}

RCT_EXPORT_METHOD(registerLocationAccess)
{
    [Taplytics registerLocationAccess];
}

RCT_EXPORT_METHOD(resetUser:resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    [Taplytics resetUser:^{
        resolve(nil);
    }];
}

RCT_EXPORT_METHOD(_setUserAttributes:(NSString*)attributes)
{
    NSData* data = [attributes dataUsingEncoding:NSUTF8StringEncoding];
    NSError* err;
    @try {
        id object = [NSJSONSerialization JSONObjectWithData:data options:0 error:&err];
        [Taplytics setUserAttributes:object];
    } @catch (NSException* e) {
        NSLog(e.reason);
    }
    if (err) {
        NSLog(err.description);
    }
}

@end
  
