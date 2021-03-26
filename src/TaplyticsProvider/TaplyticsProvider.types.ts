import { TaplyticsFeatureFlags, TaplyticsExperiments } from '../experiments'

export interface ITaplyticsContext {
  loading: boolean
  error: Error | null
  runningFeatureFlags: TaplyticsFeatureFlags
  experiments: TaplyticsExperiments
}

export type TaplyticsHookMetaData = {
  loading: boolean
  error: Error | null
}

export type TaplyticsProviderHooksArgs = {
  setIsLoading: (isLoading: boolean) => void
  setError: (error: Error) => void
  setRunningFeatureFlags: (runningFeatureFlags: TaplyticsFeatureFlags) => void
  setExperiments: (experiments: TaplyticsExperiments) => void
}
