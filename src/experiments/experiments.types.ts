export type CodeBlockCallback = () => void | Promise<void>

export type PropertiesLoadedCallback = (loaded: boolean) => void | Promise<void>

export type TaplyticsExperiments = {
  [key: string]: string
}

export type TaplyticsFeatureFlags = {
  [key: string]: string
}

export type TaplyticsVariables = {
  [key: string]: boolean | string | object | number
}
