import test from 'node:test'
import assert from 'node:assert/strict'
import { amountToChineseUpper } from '../src/utils/currencyUpper.js'

test('amountToChineseUpper handles zero, null, and invalid inputs', () => {
  assert.equal(amountToChineseUpper(0), '零元整')
  assert.equal(amountToChineseUpper(null), '零元整')
  assert.equal(amountToChineseUpper(''), '零元整')
  assert.equal(amountToChineseUpper('abc'), '零元整')
})

test('amountToChineseUpper formats standard positive integer and decimal amounts', () => {
  assert.equal(amountToChineseUpper(100), '壹佰元整')
  assert.equal(amountToChineseUpper(1005.5), '壹仟零伍元伍角')
  assert.equal(amountToChineseUpper(12345.67), '壹万贰仟叁佰肆拾伍元陆角柒分')
  assert.equal(amountToChineseUpper(100000.05), '壹拾万元零伍分')
})

test('amountToChineseUpper formats negative red-letter amounts', () => {
  assert.equal(amountToChineseUpper(-88.25), '负捌拾捌元贰角伍分')
  assert.equal(amountToChineseUpper(-500), '负伍佰元整')
})
