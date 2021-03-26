import { useContext } from 'react'
import { TaplyticsFeatureFlags } from '../experiments'
import { TaplyticsContext, TaplyticsHookMetaData } from '../TaplyticsProvider'

/**
 * Returns all available feature flags, and a meta data object.
 *
 */
function useFeatureFlag(): [TaplyticsFeatureFlags, TaplyticsHookMetaData]

/**
 * Returns a boolean indicating if the feature flag is turned on, and a meta data object.
 *
 * @param name The name of the feature flag
 *
 */
function useFeatureFlag(name: string): [boolean, TaplyticsHookMetaData]

function useFeatureFlag(name?: any) {
  const { runningFeatureFlags, loading, error } = useContext(TaplyticsContext)

  const metaData: TaplyticsHookMetaData = { loading, error }

  if (name === undefined) return [runningFeatureFlags, metaData]

  const featureFlags = Object.values(runningFeatureFlags)
  const isFeatureFlagActive = featureFlags.includes(name)

  return [!!isFeatureFlagActive, metaData]
}

export default useFeatureFlag
