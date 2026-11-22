import test from 'node:test'
import assert from 'node:assert/strict'
import { isHttp, isExternal, isEmpty, validEmail } from '../src/utils/validate.js'

test('validate utility guards URLs, emails, and empty values accurately', () => {
  assert.equal(isHttp('https://erp.canglian.vip'), true)
  assert.equal(isHttp('/business/report'), false)
  assert.equal(isExternal('https://example.com'), true)
  assert.equal(isExternal('/system/user'), false)
  assert.equal(isEmpty(''), true)
  assert.equal(isEmpty(null), true)
  assert.equal(isEmpty('canglian'), false)
  assert.equal(validEmail('finance@canglian.vip'), true)
  assert.equal(validEmail('invalid-email'), false)
})
