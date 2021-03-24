export interface ApsPayload {
  alert?: string | AlertPayload
  badge?: number
  sound?: string
  category?: string
  'content-available'?: string
  'mutable-content'?: string
  tl_id?: string
}
export interface AlertPayload {
  title: string
  body: string
}

type TaplyticsNotificationCustomKeys = {
  [key: string]: string
}

export type TaplyticsiOSNotification = {
  aps: ApsPayload
} & TaplyticsNotificationCustomKeys

export type TaplyticsAndroidNotification = {
  tl_id: string
  custom_keys: {
    tl_title: string
  } & TaplyticsNotificationCustomKeys
}

export type TaplyticsNotificationListener = (notification: TaplyticsAndroidNotification | TaplyticsiOSNotification) => void
