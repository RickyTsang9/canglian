import test from 'node:test'
import assert from 'node:assert/strict'

// Mock minimal browser globals for origin-isolated localStorage token testing
const storage = new Map()
globalThis.window = {
  localStorage: {
    getItem: (k) => (storage.has(k) ? storage.get(k) : null),
    setItem: (k, v) => storage.set(k, String(v)),
    removeItem: (k) => storage.delete(k)
  }
}
globalThis.document = { cookie: '' }

const { getToken, setToken, removeToken } = await import('../src/utils/auth.js')

test('auth token stores in origin-isolated localStorage key Canglian-Admin-Token', () => {
  removeToken()
  assert.equal(getToken(), '')

  setToken('jwt.token.sample')
  assert.equal(getToken(), 'jwt.token.sample')
  assert.equal(storage.get('Canglian-Admin-Token'), 'jwt.token.sample')

  removeToken()
  assert.equal(getToken(), '')
  assert.equal(storage.has('Canglian-Admin-Token'), false)
})
