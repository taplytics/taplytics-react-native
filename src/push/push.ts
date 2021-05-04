import { EventSubscription, NativeEventEmitter, NativeModules, Platform } from 'react-native'

import { TaplyticsAndroidNotification, TaplyticsiOSNotification, TaplyticsNotificationListener } from './push.types'

const { Taplytics } = NativeModules
const TaplyticsEventEmitter = new NativeEventEmitter(Taplytics)

/**
 * This method is used on iOS to register for push notifications from Taplytics.
 */
export const registerPushNotifications = () => {
  if (Platform.OS === 'ios') {
    Taplytics._registerPushNotifications()
  }
}

/**
 * Use this method to subscriber to when Taplytics' push notifications are received.
 *
 * @param listener A method that gets triggered whenever a push notification is received.
 * The method receives an argument which is a notification object with a `tl_id` property,
 * and a `custom_data` object.
 *
 * @returns An event subscriber object is returned. Use the `remove` function to clean up the event listener.
 */
export const registerPushReceivedListener = (listener: TaplyticsNotificationListener): EventSubscription => {
  const subscriber = TaplyticsEventEmitter.addListener('pushReceived', (notification) => {
    if (Platform.OS === 'android') {
      listener(notification?.value as TaplyticsAndroidNotification)
    }

    if (Platform.OS === 'ios') {
      listener(notification as TaplyticsiOSNotification)
    }
  })

  if (Platform.OS === 'ios') {
    Taplytics._registerPushReceivedListener()
  }

  return subscriber
}

/**
 * Use this method to subscriber to when Taplytics' push notifications are opened.
 *
 * @param listener A method that gets triggered whenever a push notification is opened.
 * The method receives an argument which is a notification object with a `tl_id` property,
 * and a `custom_data` object.
 *
 * @returns An event subscriber object is returned. Use the `remove` function to clean up the event listener.
 */
export const registerPushOpenedListener = (listener: TaplyticsNotificationListener): EventSubscription => {
  const subscriber = TaplyticsEventEmitter.addListener('pushOpened', (notification) => {
    if (Platform.OS === 'android') {
      listener(notification?.value as TaplyticsAndroidNotification)
    }

    if (Platform.OS === 'ios') {
      listener(notification as TaplyticsiOSNotification)
    }
  })

  if (Platform.OS === 'ios') {
    Taplytics._registerPushOpenedListener()
  }

  return subscriber
}

/**
 * Use this method to subscriber to when Taplytics' push notifications are dismissed.
 * This method is only available on `android` devices.
 *
 * @param listener A method that gets triggered whenever a push notification is dismissed.
 * The method receives an argument which is a notification object with a `tl_id` property,
 * and a `custom_data` object.
 *
 * @returns An event subscriber object is returned. Use the `remove` function to clean up the event listener.
 */
export const registerPushDismissedListener = (listener: TaplyticsNotificationListener): EventSubscription | undefined => {
  if (Platform.OS === 'android') {
    const subscriber = TaplyticsEventEmitter.addListener('pushDismissed', ({ value }: { value: TaplyticsAndroidNotification }) => {
      listener(value)
    })

    return subscriber
  }
}
