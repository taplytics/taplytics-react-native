export type CodeBlockCallback = () => void | Promise<void>

export type PropertiesLoadedCallback = (loaded: boolean) => void | Promise<void>

export type TaplyticsExperiments = {
  [key: string]: string
}

export type TaplyticsFeatureFlags = {
  [key: string]: string
}

export type TaplyticsVariable = boolean | string | object | number

export type TaplyticsVariableMap = {
  [key: string]: TaplyticsVariable
}
