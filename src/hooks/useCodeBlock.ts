import { DependencyList, useEffect } from 'react'
import { CodeBlockCallback, runCodeBlock } from '../experiments'

/**
 * Used to run the code block function if the code block variable is activated.
 * If an empty array is passed as the last argument, the code block function will only run once (on mount).
 *
 * @param name The name of the code block variable.
 * @param codeBlock A function that will run if the code block variable is activated.
 * @param deps If present, effect will only activate if the values in the list change.
 */
function useCodeBlock(name: string, codeBlock: CodeBlockCallback, deps?: DependencyList) {
  useEffect(() => {
    runCodeBlock(name, codeBlock)
  }, deps)
}

export default useCodeBlock
