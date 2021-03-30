import { useEffect, useState } from 'react'
import { newAsyncVariable } from '../experiments'
import { TaplyticsHookMetaData } from '../TaplyticsProvider'

/**
 * Returns an array containing the variable value and a meta data object.
 * This hook will run whenever the value of the variable is updated.
 *
 * @param name The name of the variable.
 * @param defaultValue The default value of the variable.
 *
 * @returns An array containing the variable value and a meta data object.
 */
function useVariable<T>(name: string, defaultValue: T): [T, TaplyticsHookMetaData] {
  const [variable, setVariable] = useState<T>(defaultValue)

  // This hook utilizes it's own meta data, as the context meta data do not apply.
  const [loading, setIsLoading] = useState<boolean>(false)
  const [error, setError] = useState<Error | null>(null)

  const metaData: TaplyticsHookMetaData = { loading, error }

  useEffect(() => {
    try {
      setIsLoading(true)

      const subscriber = newAsyncVariable(name, defaultValue, (variableValue) => {
        setIsLoading(false)
        setVariable(variableValue)
      })

      return () => {
        subscriber && subscriber?.remove()
      }
    } catch (error) {
      setIsLoading(false)
      setError(error)
    }
  }, [])

  return [variable, metaData]
}

export default useVariable
