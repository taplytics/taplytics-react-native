import { useEffect, useRef } from 'react'
import { AppState, AppStateStatus, EventSubscription, Platform } from 'react-native'

import { getRunningFeatureFlags, getRunningExperimentsAndVariations } from '../experiments'
import { setTaplyticsNewSessionListener } from '../user'

import { TaplyticsProviderHooksArgs } from './TaplyticsProvider.types'

/**
 * Helper function that returns a promise that resolves in an array of running feature flags,
 * and experiments and variations.
 */
const getProperties = async () => Promise.all([getRunningFeatureFlags(), getRunningExperimentsAndVariations()])

/**
 * @description This hook utilizes the `setTaplyticsNewSessionListener` listener to detect
 * when a new session has been initiated and updates the context state. The listener also
 * gets triggered when the `user_id` or `email` attribute of the user is updated through the
 * `setUserAttributes` method.
 *
 * On `android` the `setTaplyticsNewSessionListener` is not triggered upon app start.
 * The `getRunningExperimentsAndVariations` method is used instead.
 *
 * @param setStateFunctions An object of type `TaplyticsProviderHooksArgs` which contains
 *  functions to manipulate the context state.
 */
export const useAppStartAndNewSessionListener = ({
  setError,
  setIsLoading,
  setRunningFeatureFlags,
  setExperiments,
}: TaplyticsProviderHooksArgs): void => {
  useEffect(() => {
    let subscriber: EventSubscription | undefined

    try {
      setIsLoading(true)

      // If on Android, fetch properties on App Start
      if (Platform.OS === 'android') {
        ;(async () => {
          const [runningFeatureFlags, experiments] = await getProperties()
          setRunningFeatureFlags(runningFeatureFlags)
          setExperiments(experiments)

          setIsLoading(false)
        })()
      }

      subscriber = setTaplyticsNewSessionListener(async () => {
        const [runningFeatureFlags, experiments] = await getProperties()

        setRunningFeatureFlags(runningFeatureFlags)
        setExperiments(experiments)

        setIsLoading(false)
      })
    } catch (error) {
      setIsLoading(false)
      setError(error)
    }

    return () => {
      subscriber?.remove && subscriber.remove()
    }
  }, [])
}

/**
 * @description This hook utilizes the react-native `AppState` listener to detect
 * when the app goes from background to foreground and updates the context state.
 *
 * @param setStateFunctions An object of type `TaplyticsProviderHooksArgs` which contains
 *  functions to manipulate the context state.
 */
export const useAppStateListener = ({ setError, setIsLoading, setRunningFeatureFlags, setExperiments }: TaplyticsProviderHooksArgs) => {
  const appState = useRef<AppStateStatus>(AppState.currentState)

  const handleAppStateChange = async (nextAppState: AppStateStatus) => {
    if (appState.current === 'background' && nextAppState === 'active') {
      try {
        setIsLoading(true)

        const [runningFeatureFlags, experiments] = await getProperties()

        setRunningFeatureFlags(runningFeatureFlags)
        setExperiments(experiments)

        setIsLoading(false)
      } catch (error) {
        setIsLoading(false)
        setError(error)
      }
    }
    appState.current = nextAppState
  }

  useEffect(() => {
    let listener = AppState.addEventListener('change', handleAppStateChange);

    return () => {
      listener.remove();
    }
  }, [])
}
