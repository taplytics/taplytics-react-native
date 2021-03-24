export interface TaplyticsSessionInfo {
  appUser_id: string
  session_id: string
}

export interface TaplyticsUserAttributes extends Object {
  email?: string
  name?: string
  age?: number
  gender?: string
  user_id: string
}
