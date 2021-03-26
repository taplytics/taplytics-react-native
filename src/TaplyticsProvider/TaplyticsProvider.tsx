import React, { FC, createContext, useState } from 'react'

import { TaplyticsFeatureFlags, TaplyticsExperiments } from '../experiments'

import { useAppStateListener, useAppStartAndNewSessionListener } from './TaplyticsProvider.hooks'
import { ITaplyticsContext, TaplyticsProviderHooksArgs } from './TaplyticsProvider.types'

export const TaplyticsContext = createContext<ITaplyticsContext>({
  loading: false,
  error: null,
  runningFeatureFlags: {},
  experiments: {},
})

/**
 * A provider component that is used to wrap an application/component. This allows you to be able to
 * utilize hooks provided by Taplytics.
 *
 */
const TaplyticsProvider: FC = ({ children }) => {
  const [loading, setIsLoading] = useState<boolean>(false)
  const [error, setError] = useState<Error | null>(null)

  const [runningFeatureFlags, setRunningFeatureFlags] = useState<TaplyticsFeatureFlags>({})
  const [experiments, setExperiments] = useState<TaplyticsExperiments>({})

  const hooksArgs: TaplyticsProviderHooksArgs = {
    setError,
    setIsLoading,
    setRunningFeatureFlags,
    setExperiments,
  }

  useAppStartAndNewSessionListener(hooksArgs)
  useAppStateListener(hooksArgs)

  return (
    <TaplyticsContext.Provider
      value={{
        loading,
        error,
        runningFeatureFlags,
        experiments,
      }}
    >
      {children}
    </TaplyticsContext.Provider>
  )
}

export default TaplyticsProvider
