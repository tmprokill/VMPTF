import { useState, useEffect } from 'react'

export interface ApiUser {
  id: number
  name: string
  email: string
  company: { name: string }
  address: { city: string }
}

interface State {
  data: ApiUser[]
  loading: boolean
  error: string | null
}

export function useJsonPlaceholderUsers() {
  const [state, setState] = useState<State>({ data: [], loading: true, error: null })

  useEffect(() => {
    let cancelled = false
    setState({ data: [], loading: true, error: null })

    fetch('https://jsonplaceholder.typicode.com/users')
      .then(r => {
        if (!r.ok) throw new Error(`HTTP ${r.status}`)
        return r.json() as Promise<ApiUser[]>
      })
      .then(data => {
        if (!cancelled) setState({ data, loading: false, error: null })
      })
      .catch(err => {
        if (!cancelled) setState({ data: [], loading: false, error: err.message })
      })

    return () => { cancelled = true }
  }, [])

  return state
}
