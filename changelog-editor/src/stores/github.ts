import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export interface GithubUser {
  login: string
  avatarUrl: string
}

export interface GithubFork {
  fullName: string
  defaultBranch: string
}

export const useGithubStore = defineStore('github', () => {
  const token = ref<string | null>(null)
  const user = ref<GithubUser | null>(null)
  const forks = ref<GithubFork[]>([])
  const selectedFork = ref<string>('')

  const isLoggedIn = computed(() => !!token.value)
  const username = computed(() => user.value?.login ?? '')

  function setToken(t: string) { token.value = t }
  function setUser(u: GithubUser) { user.value = u }
  function setForks(f: GithubFork[]) { forks.value = f }
  function clearAuth() { token.value = null; user.value = null; forks.value = []; selectedFork.value = '' }

  return { token, user, forks, selectedFork, isLoggedIn, username, setToken, setUser, setForks, clearAuth }
})