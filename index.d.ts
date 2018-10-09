declare module 'taplytics-react-native' {
  namespace Taplytics {
    export interface TaplyticsUserAttributes extends object {
      email?: string;
      name?: string;
      age?: number;
      gender?: string;
      user_id: string;
    }

    /**
     * User Attributes set before startTaplytics is called will be used for experiment
     * segmentation on the first session of your app. Any attributes that are set after
     * startTaplytics is called will not be used for experiment segmentation until the
     * next session of your app.
     */
    export function setUserAttributes(attributes: TaplyticsUserAttributes): void;

    /**
     * Once a user logs out of your app, their User Attributes are no longer valid.
     * You can reset their data by calling resetAppUser, make sure you do not set
     * any new user attributes until you receive the callback.
     */
    export function resetAppUser(): void;

    export interface TaplyticsSessionInfo {
      appUser_id: string;
      session_id: string;
    }

    export function getSessionInfo(): Promise<TaplyticsSessionInfo>;
    export function logEvent(eventName: string, value: number, customAttributes: object): void;
    export function logRevenue(eventName: string, value: number, customAttributes: object): void;

    /**
     * Synchronous variables are guaranteed to have the same value for the
     * entire session and will have that value immediately after construction.
     * Synchronous variables take two parameters in its constructor:
     *   - Variable name
     *   - Default Value
     *   - Promise
     * The type of the variable will be inferred from the type of value passed
     * in as the default. This method returns a promise which resolves with the
     * value of the synchronous variable:
     */
    export function newSyncVariable<T>(variableName: string, defaultValue: T): Promise<T>;

    /**
     * IMPORTANT: The value of these variables will be determined immediately,
     * ie. the SDK will not wait for properties to be loaded from the server.
     * Thus, if you want to ensure that the variables have their correct variables
     * based on your experiment segmentation, you must initialize them after the
     * properties have been loaded from the server. This module provides a callback to
     * achieve this:
     */
    export function propertiesLoadedCallback(): Promise<void>;

    /**
     * Asynchronous variables take care of insuring that the experiments have been loaded
     * before returning a value. This removes any danger of tainting the results of your
     * experiment with bad data. What comes with the insurance of using the correct value
     * is the possibility that the value will not be set immediately. If the variable is
     * constructed before the experiments are loaded, you won't have the correct value until
     * the experiments have finished loading. If the experiments fail to load, then you will
     * be given the default value, as specified in the variables constructor.
     */
    export function newAsyncVariable<T>(name: string, defaultValue: T, variableChangedCallback: (v: T) => void): void;

    /**
     * Synchronous feature flags are guaranteed to have the same value for the entire session
     * and will have that value immediately after construction.
     */
    export function featureFlagEnabled(featureFlagKey: string): Promise<boolean>;

    /**
     * IMPORTANT: The value of featureFlagEnabled will be determined immediately, ie. the SDK
     * will not wait for properties to be loaded from the server. Thus, if you want to ensure
     * that the variables have their correct variables based on your experiment segmentation,
     * you must initialize them after the properties have been loaded from the server.
     * This module provides a callback to achieve this: propertiesLoadedCallback
     */

    /**
     * If you would like to see which feature flags are running on a given device,
     * there exists a getRunningFeatureFlags() function which provides a callback with the
     * current feature flags' names and their associated key. An example:
     */
    export function getRunningFeatureFlags(): Promise<object>;

    /**
     * To make it easier to keep track of your variables, this module also provides a
     * method to retrieve an object map of their names and values:
     */
    export interface TaplyticsVariableMap {[key: string]: string | number | boolean | object}
    export function getVariables(): Promise<TaplyticsVariableMap>;

    /**
     * You can also register a function to be called whenever this object changes:
     */
    export function registerVariablesChangedListener(callback: (v: TaplyticsVariableMap) => void): void;

    /**
     * If you would like to see which variations and experiments are running on a given device,
     * there exists a getRunningExperimentsAndVariations function which provides a callback with
     * a map of the current experiments and their running variation. An example:
     */
    export function getRunningExperimentsAndVariations(): Promise<TaplyticsVariableMap>;

    /**
     * To manually force a new user session (ex: A user has logged in / out), there exists
     */
    export function startNewSession(): Promise<void>;

    /**
     * You can also register a callback to be run when Taplytics creates a new session:
     */
    export function setTaplyticsNewSessionListener(callback: () => void): void;

  }
  export = Taplytics;
}
