import { useContext } from 'react'
import { TaplyticsExperiments } from '../experiments'
import { TaplyticsContext, TaplyticsHookMetaData } from '../TaplyticsProvider'

/**
 * Return all available experiments as well as a meta data object.
 */
function useRunningExperiments(): [TaplyticsExperiments, TaplyticsHookMetaData] {
  const { experiments, loading, error } = useContext(TaplyticsContext)
  const metaData: TaplyticsHookMetaData = { loading, error }

  return [experiments, metaData]
}

export default useRunningExperiments
