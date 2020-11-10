#import "RNTaplyticsReact.h"
#import <Taplytics/Taplytics.h>

@implementation RNTaplyticsReact

+ (id)allocWithZone:(NSZone *)zone {
    static RNTaplyticsReact *sharedInstance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [super allocWithZone:zone];
    });
    return sharedInstance;
}

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

- (NSArray<NSString *> *)supportedEvents
{
    return @[@"asyncVariable", @"propertiesLoadedCallback",@"pushOpened",@"pushReceived"];
}

- (void)sendEvent:(NSString *)name withValue:(id)value
{
    [self sendEventWithName:name body:value];
}

- (void)sendPushEvent:(id)name withData:(NSDictionary *)userInfo
{
    if ([name isEqualToString:@"pushReceived"] || [name isEqualToString:@"pushOpened"]) {
        [self sendEvent:name withValue:userInfo];
    } else {
        NSLog(@"Invalid push event sent to React Native");
    }
}

RCT_EXPORT_MODULE(Taplytics);

RCT_REMAP_METHOD(_newSyncBool, name:(NSString *)name defaultBoolValue:(BOOL)defaultValue resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    TaplyticsVar* variable = [TaplyticsVar taplyticsSyncVarWithName:name defaultValue:@(defaultValue)];
    resolve((NSNumber *)variable.value);
}

RCT_REMAP_METHOD(_newSyncString, name:(NSString *)name defaultStringValue:(NSString *)defaultValue resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    TaplyticsVar* variable = [TaplyticsVar taplyticsSyncVarWithName:name defaultValue:defaultValue];
    resolve((NSString *)variable.value);
}

RCT_REMAP_METHOD(_newSyncNumber, name:(NSString *)name defaultNumValue:(nonnull NSNumber *)defaultValue resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    TaplyticsVar* variable = [TaplyticsVar taplyticsSyncVarWithName:name defaultValue:defaultValue];
    resolve((NSNumber *)variable.value);
}

RCT_REMAP_METHOD(_newSyncObject, name:(NSString *)name defaultValue:(NSString *)defaultValue resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
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


RCT_EXPORT_METHOD(_newAsyncBool:(NSString *)name defaultValue:(BOOL)defaultValue callbackId:(nonnull NSNumber *)_id)
{
    [TaplyticsVar taplyticsVarWithName:name defaultValue:@(defaultValue) updatedBlock:^(NSObject* value) {
        [self sendEvent:@"asyncVariable" withValue:@{@"id": _id, @"value": value}];
    }];
}

RCT_EXPORT_METHOD(_newAsyncString:(NSString *)name defaultValue:(NSString *)defaultValue callbackId:(nonnull NSNumber *)_id)
{
    [TaplyticsVar taplyticsVarWithName:name defaultValue:defaultValue updatedBlock:^(NSObject* value) {
        [self sendEvent:@"asyncVariable" withValue:@{@"id": _id, @"value": value}];
    }];
}

RCT_EXPORT_METHOD(_newAsyncNumber:(NSString *)name defaultValue:(nonnull NSNumber *)defaultValue callbackId:(nonnull NSNumber *)_id)
{
    [TaplyticsVar taplyticsVarWithName:name defaultValue:defaultValue updatedBlock:^(NSObject* value) {
        [self sendEvent:@"asyncVariable" withValue:@{@"id": _id, @"value": value}];
    }];
}

RCT_EXPORT_METHOD(_newAsyncObject:(NSString *)name defaultValue:(NSString *)defaultValue callbackId:(nonnull NSNumber *)_id)
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
                    NSLog(@"%@", err.description);
                }
                NSString *stringifiedJSON = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
                [self sendEvent:@"asyncVariable" withValue:@{@"id": _id, @"value": value}];
            }
        }];
    } @catch (NSException* e) {
        NSLog(@"%@", e.reason);
    }
    if (err) {
        NSLog(@"%@", err.description);
    }
}

RCT_REMAP_METHOD(_featureFlagEnabled, key:(NSString *)key resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    BOOL isEnabled = [Taplytics featureFlagEnabled:key];
    resolve([NSNumber numberWithBool:isEnabled]);
}

RCT_EXPORT_METHOD(runCodeBlock:(NSString *)name codeBlock:(RCTResponseSenderBlock)codeBlock)
{
    [Taplytics runCodeBlock:name forBlock:^{
        codeBlock(@[[NSNull null]]);
    }];
}

RCT_EXPORT_METHOD(_propertiesLoadedCallback)
{
    [Taplytics propertiesLoadedCallback:^(BOOL loaded) {
        [self sendEvent:@"propertiesLoadedCallback" withValue:@(loaded)];
    }];
}

RCT_EXPORT_METHOD(_registerPushOpenedListener)
{
    if ([[Taplytics class] respondsToSelector:@selector(registerPushOpenedListener:)]) {
        [[Taplytics class] performSelector:@selector(registerPushOpenedListener:) withObject:^(NSDictionary* userInfo) {
            [self sendPushEvent:@"pushOpened" withData:userInfo];
        }];
    }
}

RCT_EXPORT_METHOD(_registerPushReceivedListener)
{
    if ([[Taplytics class] respondsToSelector:@selector(registerPushReceivedListener:)]) {
        [[Taplytics class] performSelector:@selector(registerPushReceivedListener:) withObject:^(NSDictionary* userInfo) {
            [self sendPushEvent:@"pushReceived" withData:userInfo];
        }];
    }
}

RCT_EXPORT_METHOD(_registerPushNotifications)
{
    [Taplytics registerPushNotifications];
}

RCT_EXPORT_METHOD(registerPushNotificationsWithTypes:(NSInteger)types categories:(nullable NSSet*)categories)
{
    [Taplytics registerPushNotificationsWithTypes:types categories:categories];
}

RCT_EXPORT_METHOD(registerLocationAccess)
{
    [Taplytics registerLocationAccess];
}

RCT_REMAP_METHOD(resetAppUser, resetAppUserResolver:(RCTPromiseResolveBlock)resolve resetAppUserRejecter:(RCTPromiseRejectBlock)reject)
{
    [Taplytics resetUser:^{
        resolve(nil);
    }];
}

RCT_EXPORT_METHOD(_setUserAttributes:(NSString*)attributes resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    NSData* data = [attributes dataUsingEncoding:NSUTF8StringEncoding];
    NSError* err;
    @try {
        id object = [NSJSONSerialization JSONObjectWithData:data options:0 error:&err];
        [Taplytics setUserAttributes:object withCallback:^{
            resolve(nil);
        }];
    } @catch (NSException* e) {
        NSLog(@"%@", e.reason);
    }
    if (err) {
        NSLog(@"%@", err.description);
    }
}

RCT_EXPORT_METHOD(logEvent:(NSString *)eventName value:(nonnull NSNumber*)value metaData:(NSDictionary*)metaData)
{
    [Taplytics logEvent:eventName value:value metaData:metaData];
}


RCT_EXPORT_METHOD(logRevenue:(NSString *)eventName value:(nonnull NSNumber*)value metaData:(NSDictionary*)metaData)
{
    [Taplytics logRevenue:eventName revenue:value metaData:metaData];
}


RCT_REMAP_METHOD(isUserRegisteredForPushNotifications, isUserRegisteredForPushNotificationsResolver:(RCTPromiseResolveBlock)resolve rejectIsUserRegisteredForPushNotifications:(RCTPromiseRejectBlock)reject)
{
    resolve([NSNumber numberWithBool:[Taplytics isUserRegisteredForPushNotifications]]);
}

RCT_REMAP_METHOD(isLoadingPropertiesFromServer, propertiesLoadingResolver:(RCTPromiseResolveBlock)resolve rejectPropertiesLoading:(RCTPromiseRejectBlock)reject)
{
    resolve([NSNumber numberWithBool:[Taplytics isLoadingPropertiesFromServer]]);
}

RCT_REMAP_METHOD(getRunningExperimentsAndVariations, experimentsAndVariationsResolver:(RCTPromiseResolveBlock)resolve rejectExperimentsAndVariations:(RCTPromiseRejectBlock)reject)
{
    [Taplytics getRunningExperimentsAndVariations:^(NSDictionary * _Nullable experimentsAndVariations) {
        resolve(experimentsAndVariations);
        
    }];
}

RCT_REMAP_METHOD(getRunningFeatureFlags, featureFlagsResolver:(RCTPromiseResolveBlock)resolve rejectFeatureFlags:(RCTPromiseRejectBlock)reject)
{
  [Taplytics getRunningFeatureFlags:^(NSDictionary * _Nullable featureFlags) {
    resolve(featureFlags);
  }];
}
     
RCT_REMAP_METHOD(startNewSession, startNewSessionResolver:(RCTPromiseResolveBlock)resolve rejectStartNewSession:(RCTPromiseRejectBlock)reject)
{
    [Taplytics startNewSession:^(BOOL success) {
        resolve([NSNumber numberWithBool:success]);
    }];
}

RCT_REMAP_METHOD(getSessionInfo, resolveSessionInfo:(RCTPromiseResolveBlock)resolve resolveGetSessionInfo:(RCTPromiseRejectBlock)reject)
{
    [Taplytics getSessionInfo:^(NSDictionary * _Nullable sessionInfo) {
        resolve(sessionInfo);
    }];
}

RCT_REMAP_METHOD(performBackgroundFetch, resolveFetch:(RCTPromiseResolveBlock)resolve rejectFetch:(RCTPromiseRejectBlock)reject)
{
    [Taplytics performBackgroundFetch:^(UIBackgroundFetchResult result) {
        resolve([NSNumber numberWithUnsignedInteger:result]);
    }];
}

@end
