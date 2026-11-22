const CN_NUMS = ["零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"]
const CN_INT_RADICE = ["", "拾", "佰", "仟"]
const CN_INT_UNITS = ["", "万", "亿", "兆"]
const CN_DEC_UNITS = ["角", "分"]

/**
 * 金额转中文人民币大写（支持负数与小数两位）
 */
export function amountToChineseUpper(money) {
  if (money === null || money === undefined || money === "" || Number.isNaN(Number(money))) {
    return "零元整"
  }
  const num = Number(money)
  if (Math.abs(num) < 0.005) {
    return "零元整"
  }
  const negative = num < 0
  const fixed = Math.abs(num).toFixed(2)
  const [integerPart, decimalPart] = fixed.split(".")

  let chineseStr = negative ? "负" : ""
  if (Number(integerPart) > 0) {
    let zeroCount = 0
    const intLen = integerPart.length
    for (let i = 0; i < intLen; i++) {
      const n = integerPart.charAt(i)
      const p = intLen - i - 1
      const q = Math.floor(p / 4)
      const m = p % 4
      if (n === "0") {
        zeroCount++
      } else {
        if (zeroCount > 0) {
          chineseStr += CN_NUMS[0]
        }
        zeroCount = 0
        chineseStr += CN_NUMS[Number(n)] + CN_INT_RADICE[m]
      }
      if (m === 0 && zeroCount < 4) {
        chineseStr += CN_INT_UNITS[q]
      }
    }
    chineseStr += "元"
  }

  if (decimalPart === "00") {
    chineseStr += "整"
  } else {
    for (let i = 0; i < 2; i++) {
      const n = decimalPart.charAt(i)
      if (n !== "0") {
        chineseStr += CN_NUMS[Number(n)] + CN_DEC_UNITS[i]
      } else if (i === 0 && Number(integerPart) > 0 && decimalPart.charAt(1) !== "0") {
        chineseStr += CN_NUMS[0]
      }
    }
  }
  return chineseStr
}
