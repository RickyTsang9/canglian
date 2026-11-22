import { getConfigKey } from "@/api/system/config"
import { amountToChineseUpper } from "./currencyUpper"

export { amountToChineseUpper }

function escapeHtml(val) {
  if (val === null || val === undefined) return ""
  return String(val)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#39;")
}

let cachedCompanyInfo = null

async function loadCompanyPrintConfig() {
  if (cachedCompanyInfo) return cachedCompanyInfo
  const defaults = {
    companyName: "仓链商贸有限公司",
    companyPhone: "400-800-8888",
    companyAddress: "上海市浦东新区仓链智慧产业园"
  }
  try {
    const [nameRes, phoneRes, addrRes] = await Promise.all([
      getConfigKey("canglian.print.companyName").catch(() => null),
      getConfigKey("canglian.print.companyPhone").catch(() => null),
      getConfigKey("canglian.print.companyAddress").catch(() => null)
    ])
    cachedCompanyInfo = {
      companyName: (nameRes && nameRes.msg) || defaults.companyName,
      companyPhone: (phoneRes && phoneRes.msg) || defaults.companyPhone,
      companyAddress: (addrRes && addrRes.msg) || defaults.companyAddress
    }
  } catch (e) {
    cachedCompanyInfo = defaults
  }
  return cachedCompanyInfo
}

/**
 * 使用内置模态预览层 + 隐藏 iframe 输出打印，彻底摆脱 window.open 弹窗拦截
 */
function openInPagePrintPreview(html, title) {
  const existing = document.getElementById("canglian-print-preview-mask")
  if (existing) existing.remove()

  const mask = document.createElement("div")
  mask.id = "canglian-print-preview-mask"
  mask.style.cssText = "position:fixed;inset:0;z-index:9999;background:rgba(15,23,42,0.6);display:flex;align-items:center;justify-content:center;padding:24px;"

  const box = document.createElement("div")
  box.style.cssText = "width:min(1040px,96vw);height:min(820px,92vh);background:#fff;border-radius:8px;box-shadow:0 20px 50px rgba(0,0,0,0.35);display:flex;flex-direction:column;overflow:hidden;"

  const bar = document.createElement("div")
  bar.style.cssText = "display:flex;align-items:center;justify-content:space-between;padding:12px 18px;border-bottom:1px solid #e5e7eb;background:#f8fafc;"
  bar.innerHTML = `<div style="font-size:15px;font-weight:600;color:#1e293b;">打印预览 - ${escapeHtml(title)}</div>`

  const btnGroup = document.createElement("div")
  btnGroup.style.cssText = "display:flex;gap:10px;"

  const printBtn = document.createElement("button")
  printBtn.type = "button"
  printBtn.textContent = "立即打印 / 导出 PDF"
  printBtn.style.cssText = "padding:7px 16px;background:#409eff;color:#fff;border:none;border-radius:4px;cursor:pointer;font-size:13px;font-weight:500;"

  const closeBtn = document.createElement("button")
  closeBtn.type = "button"
  closeBtn.textContent = "关闭预览"
  closeBtn.style.cssText = "padding:7px 14px;background:#fff;color:#475569;border:1px solid #cbd5e1;border-radius:4px;cursor:pointer;font-size:13px;"

  const iframe = document.createElement("iframe")
  iframe.style.cssText = "flex:1;width:100%;border:none;background:#fff;"

  printBtn.onclick = () => {
    try {
      iframe.contentWindow.focus()
      iframe.contentWindow.print()
    } catch (e) {
      window.print()
    }
  }
  const closeOverlay = () => {
    mask.remove()
    document.removeEventListener("keydown", onKeyDown)
  }
  const onKeyDown = (e) => {
    if (e.key === "Escape") closeOverlay()
  }
  closeBtn.onclick = closeOverlay
  mask.onclick = (e) => {
    if (e.target === mask) closeOverlay()
  }
  document.addEventListener("keydown", onKeyDown)

  btnGroup.appendChild(printBtn)
  btnGroup.appendChild(closeBtn)
  bar.appendChild(btnGroup)
  box.appendChild(bar)
  box.appendChild(iframe)
  mask.appendChild(box)
  document.body.appendChild(mask)

  const doc = iframe.contentWindow.document
  doc.open()
  doc.write(html)
  doc.close()
  return true
}

function buildAlignedSummaryRow(columns, totalQty, totalAmount) {
  const qtyIdx = columns.findIndex(c => c.summary === "qty" || ["quantity", "diffQty", "actualQty"].includes(c.prop))
  const amtIdx = columns.findIndex(c => c.summary === "amount" || ["amount", "diffAmount", "debitAmount", "creditAmount"].includes(c.prop))

  const cells = [`<td style="text-align:center">合计</td>`]
  columns.forEach((col, idx) => {
    if (idx === qtyIdx) {
      cells.push(`<td style="text-align:right">${Number(totalQty || 0).toFixed(2)}</td>`)
    } else if (idx === amtIdx) {
      cells.push(`<td style="text-align:right">￥${Number(totalAmount || 0).toFixed(2)}</td>`)
    } else {
      cells.push(`<td></td>`)
    }
  })
  return `<tr class="summary-row">${cells.join("")}</tr>`
}

const PRINT_STYLE = `
  @page { size: A4; margin: 12mm; }
  body { font-family: "Microsoft YaHei", "PingFang SC", sans-serif; color: #1f2329; font-size: 12px; margin: 0; padding: 14px; }
  .sheet { max-width: 960px; margin: 0 auto; }
  .header { text-align: center; border-bottom: 2px solid #1f2329; padding-bottom: 8px; margin-bottom: 10px; }
  .company { font-size: 19px; font-weight: 700; letter-spacing: 2px; }
  .sub-company { font-size: 11px; color: #555; margin-top: 3px; }
  .doc-title { font-size: 16px; font-weight: 700; margin-top: 6px; letter-spacing: 4px; }
  .meta-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 6px 14px; margin-bottom: 10px; line-height: 1.7; }
  table { width: 100%; border-collapse: collapse; margin-top: 6px; }
  th, td { border: 1px solid #333; padding: 6px 8px; font-size: 12px; word-break: break-all; }
  th { background: #f3f5f8; font-weight: 600; }
  .summary-row td { font-weight: 700; background: #fafafa; }
  .upper-line { margin-top: 8px; padding: 6px 8px; border: 1px solid #333; background: #fcfcfc; display: flex; justify-content: space-between; }
  .remark-line { margin-top: 8px; line-height: 1.6; }
  .sign-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; margin-top: 24px; padding-top: 8px; border-top: 1px dashed #999; }
  @media print { body { padding: 0; } }
`

/**
 * 标准中文商贸 A4 / 三联凭证业务单据打印引擎
 */
export async function printBusinessDocument(options = {}) {
  const company = await loadCompanyPrintConfig()
  const title = options.title || "业务单据"
  const companyName = options.companyName || company.companyName
  const companyPhone = options.companyPhone || company.companyPhone
  const companyAddress = options.companyAddress || company.companyAddress
  const docNo = options.docNo || "-"
  const bizDate = options.bizDate || "-"
  const displayNames = options.displayNames || {}
  const counterpartyLabel = options.counterpartyLabel || "往来单位"
  const counterpartyName = options.counterpartyName || displayNames.counterpartyName || "-"
  const warehouseName = options.warehouseName || displayNames.warehouseName || "-"
  const columns = options.columns || [
    { label: "商品", prop: "productLabel" },
    { label: "单位", prop: "unitName", align: "center" },
    { label: "批次号", prop: "batchNo", align: "center" },
    { label: "数量", prop: "quantity", align: "right", summary: "qty" },
    { label: "单价", prop: "unitPrice", align: "right" },
    { label: "金额", prop: "amount", align: "right", summary: "amount" }
  ]
  const items = Array.isArray(options.items) ? options.items : []
  const totalQty = options.totalQty !== undefined ? options.totalQty : items.reduce((acc, it) => acc + Number(it.quantity ?? it.diffQty ?? 0), 0)
  const totalAmount = options.totalAmount !== undefined ? options.totalAmount : items.reduce((acc, it) => acc + Number(it.amount ?? it.diffAmount ?? 0), 0)
  const extraMeta = Array.isArray(options.extraMeta) ? options.extraMeta : []

  const headerCells = columns.map(c => `<th style="text-align:${c.align || "left"}">${escapeHtml(c.label)}</th>`).join("")
  const bodyRows = items.length > 0
    ? items.map((row, idx) => {
      const cells = columns.map(c => {
        const raw = typeof c.formatter === "function" ? c.formatter(row, idx) : row[c.prop]
        return `<td style="text-align:${c.align || "left"}">${escapeHtml(raw === null || raw === undefined || raw === "" ? "-" : raw)}</td>`
      }).join("")
      return `<tr><td style="text-align:center">${idx + 1}</td>${cells}</tr>`
    }).join("")
    : `<tr><td colspan="${columns.length + 1}" style="text-align:center;color:#888">无明细记录</td></tr>`

  const summaryRowHtml = buildAlignedSummaryRow(columns, totalQty, totalAmount)
  const extraMetaHtml = extraMeta.map(m => `<div><strong>${escapeHtml(m.label)}：</strong>${escapeHtml(m.value || "-")}</div>`).join("")

  const html = `<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="UTF-8" />
<title>${escapeHtml(title)} - ${escapeHtml(docNo)}</title>
<style>${PRINT_STYLE}</style>
</head>
<body>
  <div class="sheet">
    <div class="header">
      <div class="company">${escapeHtml(companyName)}</div>
      <div class="sub-company">地址：${escapeHtml(companyAddress)} &nbsp;|&nbsp; 电话：${escapeHtml(companyPhone)}</div>
      <div class="doc-title">${escapeHtml(title)}</div>
    </div>
    <div class="meta-grid">
      <div><strong>单据编号：</strong>${escapeHtml(docNo)}</div>
      <div><strong>业务日期：</strong>${escapeHtml(bizDate)}</div>
      <div><strong>${escapeHtml(counterpartyLabel)}：</strong>${escapeHtml(counterpartyName)}</div>
      <div><strong>仓库：</strong>${escapeHtml(warehouseName)}</div>
      ${extraMetaHtml}
    </div>
    <table>
      <thead>
        <tr>
          <th style="width:42px;text-align:center">序号</th>
          ${headerCells}
        </tr>
      </thead>
      <tbody>
        ${bodyRows}
        ${summaryRowHtml}
      </tbody>
    </table>
    <div class="upper-line">
      <span><strong>金额大写（人民币）：</strong>${escapeHtml(amountToChineseUpper(totalAmount))}</span>
      <span><strong>小写：</strong>￥${Number(totalAmount || 0).toFixed(2)}</span>
    </div>
    <div class="remark-line"><strong>单据备注：</strong>${escapeHtml(options.remark || "无")}</div>
    <div class="sign-grid">
      <div>制单人：${escapeHtml(options.createBy || "__________")}</div>
      <div>审核人：${escapeHtml(options.auditBy || "__________")}</div>
      <div>仓管/经办：__________</div>
      <div>客户/供应商签收：__________</div>
    </div>
  </div>
</body>
</html>`

  return openInPagePrintPreview(html, `${title} ${docNo}`)
}

/**
 * 财务单据通用 A4 打印引擎（支持收款/付款/应收/应付/费用/发票/核销/坏账/资金互转/会计凭证）
 */
export async function printFinancialDocument(options = {}) {
  const company = await loadCompanyPrintConfig()
  const title = options.title || "财务单据"
  const docNo = options.docNo || "-"
  const metaList = Array.isArray(options.metaList) ? options.metaList : []
  const columns = Array.isArray(options.columns) ? options.columns : []
  const rows = Array.isArray(options.rows) ? options.rows : []
  const amount = options.amount !== undefined ? Number(options.amount || 0) : 0
  const showUpper = options.showUpper !== false

  const metaHtml = metaList.map(m => `<div><strong>${escapeHtml(m.label)}：</strong>${escapeHtml(m.value ?? "-")}</div>`).join("")

  let tableHtml = ""
  if (columns.length > 0) {
    const headerCells = columns.map(c => `<th style="text-align:${c.align || "left"}">${escapeHtml(c.label)}</th>`).join("")
    const bodyRows = rows.length > 0
      ? rows.map((r, idx) => {
        const cells = columns.map(c => {
          const val = typeof c.formatter === "function" ? c.formatter(r, idx) : r[c.prop]
          return `<td style="text-align:${c.align || "left"}">${escapeHtml(val === null || val === undefined || val === "" ? "-" : val)}</td>`
        }).join("")
        return `<tr><td style="text-align:center">${idx + 1}</td>${cells}</tr>`
      }).join("")
      : `<tr><td colspan="${columns.length + 1}" style="text-align:center;color:#888">无明细</td></tr>`
    tableHtml = `<table><thead><tr><th style="width:42px;text-align:center">序号</th>${headerCells}</tr></thead><tbody>${bodyRows}</tbody></table>`
  }

  const upperHtml = showUpper ? `
    <div class="upper-line">
      <span><strong>金额大写（人民币）：</strong>${escapeHtml(amountToChineseUpper(amount))}</span>
      <span><strong>小写：</strong>￥${amount.toFixed(2)}</span>
    </div>` : ""

  const html = `<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="UTF-8" />
<title>${escapeHtml(title)} - ${escapeHtml(docNo)}</title>
<style>${PRINT_STYLE}</style>
</head>
<body>
  <div class="sheet">
    <div class="header">
      <div class="company">${escapeHtml(company.companyName)}</div>
      <div class="sub-company">地址：${escapeHtml(company.companyAddress)} &nbsp;|&nbsp; 电话：${escapeHtml(company.companyPhone)}</div>
      <div class="doc-title">${escapeHtml(title)}</div>
    </div>
    <div class="meta-grid">${metaHtml}</div>
    ${tableHtml}
    ${upperHtml}
    <div class="remark-line"><strong>备注：</strong>${escapeHtml(options.remark || "无")}</div>
    <div class="sign-grid">
      <div>制单：${escapeHtml(options.createBy || "__________")}</div>
      <div>复核：${escapeHtml(options.auditBy || "__________")}</div>
      <div>出纳/会计：__________</div>
      <div>审批主管：__________</div>
    </div>
  </div>
</body>
</html>`

  return openInPagePrintPreview(html, `${title} ${docNo}`)
}
