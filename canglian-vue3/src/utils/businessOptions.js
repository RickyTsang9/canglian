import { listWarehouse } from "@/api/business/warehouse"
import { listProduct } from "@/api/business/product"
import { listCustomer } from "@/api/business/customer"
import { listSupplier } from "@/api/business/supplier"
import { listFundAccount } from "@/api/business/fundAccount"
import { listLocation } from "@/api/business/location"
import { listReceivable } from "@/api/business/receivable"
import { listPayable } from "@/api/business/payable"
import { listReceipt } from "@/api/business/receipt"
import { listPayment } from "@/api/business/payment"
import { listInbound } from "@/api/business/inbound"
import { listOutbound } from "@/api/business/outbound"
import { listExpenseCategory } from "@/api/business/expenseCategory"
import { listUnitsByProduct, resolveLivePrice } from "@/api/business/productUnit"
import request from "@/utils/request"

const LARGE_PAGE = { pageNum: 1, pageSize: 1000 }

/**
 * 业务主数据下拉选项与名称映射 Composable
 */
export function useBusinessOptions() {
  const warehouseOptions = ref([])
  const productOptions = ref([])
  const customerOptions = ref([])
  const supplierOptions = ref([])
  const fundAccountOptions = ref([])
  const locationOptions = ref([])

  // 仅启用的主数据选项（用于单据新增/编辑下拉，停用记录仍保留在全量列表中用于历史名称翻译）
  const activeWarehouseOptions = computed(() => warehouseOptions.value.filter(w => String(w.status ?? "0") === "0"))
  const activeProductOptions = computed(() => productOptions.value.filter(p => String(p.status ?? "0") === "0"))
  const activeCustomerOptions = computed(() => customerOptions.value.filter(c => String(c.status ?? "0") === "0"))
  const activeSupplierOptions = computed(() => supplierOptions.value.filter(s => String(s.status ?? "0") === "0"))
  const activeFundAccountOptions = computed(() => fundAccountOptions.value.filter(a => String(a.status ?? "0") === "0"))

  function loadWarehouses() {
    return request({ url: "/business/options/warehouses", method: "get", headers: { hideError: true } }).then(res => {
      warehouseOptions.value = res.data || []
      return warehouseOptions.value
    }).catch(() => [])
  }

  function loadProducts() {
    return request({ url: "/business/options/products", method: "get", headers: { hideError: true } }).then(res => {
      productOptions.value = res.data || []
      return productOptions.value
    }).catch(() => [])
  }

  function loadCustomers() {
    return request({ url: "/business/options/customers", method: "get", headers: { hideError: true } }).then(res => {
      customerOptions.value = res.data || []
      return customerOptions.value
    }).catch(() => [])
  }

  function loadSuppliers() {
    return request({ url: "/business/options/suppliers", method: "get", headers: { hideError: true } }).then(res => {
      supplierOptions.value = res.data || []
      return supplierOptions.value
    }).catch(() => [])
  }

  function loadFundAccounts() {
    return request({ url: "/business/options/fundAccounts", method: "get", headers: { hideError: true } }).then(res => {
      fundAccountOptions.value = res.data || []
      return fundAccountOptions.value
    }).catch(() => [])
  }

  function loadLocations(warehouseId) {
    const params = warehouseId ? { warehouseId } : {}
    return request({ url: "/business/options/locations", method: "get", params, headers: { hideError: true } }).then(res => {
      locationOptions.value = res.data || []
      return locationOptions.value
    }).catch(() => [])
  }

  function fetchLocationsByWarehouse(warehouseId) {
    if (!warehouseId) return Promise.resolve([])
    return request({ url: "/business/options/locations", method: "get", params: { warehouseId }, headers: { hideError: true } })
      .then(res => (res.data || []).filter(l => String(l.status ?? "0") === "0"))
      .catch(() => [])
  }

  function fetchOpenReceivables(customerId, includePaid = false) {
    const query = { ...LARGE_PAGE }
    if (customerId) query.customerId = customerId
    return listReceivable(query).then(res => {
      return (res.rows || [])
        .map(r => {
          const unreceivedAmount = Number((Number(r.amount || 0) - Number(r.receivedAmount || 0) - Number(r.badDebtAmount || 0)).toFixed(2))
          return { ...r, unreceivedAmount }
        })
        .filter(r => r.bizStatus === "confirmed" && String(r.billDirection || "1") !== "2" && (includePaid ? true : r.unreceivedAmount > 0.005))
    }).catch(() => [])
  }

  function fetchOpenPayables(supplierId) {
    const query = { ...LARGE_PAGE }
    if (supplierId) query.supplierId = supplierId
    return listPayable(query).then(res => {
      return (res.rows || [])
        .map(p => {
          const unpaidAmount = Number((Number(p.amount || 0) - Number(p.paidAmount || 0)).toFixed(2))
          return { ...p, unpaidAmount }
        })
        .filter(p => p.bizStatus === "confirmed" && String(p.billDirection || "1") !== "2" && p.unpaidAmount > 0.005)
    }).catch(() => [])
  }

  function fetchConfirmedReceipts(customerId, unlinkedOnly = true) {
    const query = { ...LARGE_PAGE, status: "1" }
    if (customerId) query.customerId = customerId
    return listReceipt(query).then(res => (res.rows || []).filter(r => Number(r.amount || 0) > 0 && (!unlinkedOnly || !r.receivableId))).catch(() => [])
  }

  function fetchConfirmedPayments(supplierId, unlinkedOnly = true) {
    const query = { ...LARGE_PAGE, status: "1" }
    if (supplierId) query.supplierId = supplierId
    return listPayment(query).then(res => (res.rows || []).filter(p => Number(p.amount || 0) > 0 && (!unlinkedOnly || !p.payableId))).catch(() => [])
  }

  function fetchExpenseCategoryOptions() {
    return request({ url: "/business/options/expenseCategories", method: "get", headers: { hideError: true } }).then(res => {
      const list = Array.isArray(res.data) ? res.data : (res.rows || [])
      return list.filter(c => String(c.status ?? "0") === "0")
    }).catch(() => [])
  }

  function fetchBillNoMap() {
    return request({ url: "/business/options/billNos", method: "get", headers: { hideError: true } })
      .then(res => res.data || {})
      .catch(() => ({}))
  }

  function fetchAuditedInboundOptions(supplierId) {
    const query = { ...LARGE_PAGE, status: "1" }
    if (supplierId) query.supplierId = supplierId
    return listInbound(query).then(res => (res.rows || []).filter(r => String(r.isReversal || "0") !== "1" && String(r.reversedFlag || "0") !== "1")).catch(() => [])
  }

  function fetchSignedOutboundOptions(customerId) {
    const query = { ...LARGE_PAGE, status: "1" }
    if (customerId) query.customerId = customerId
    return listOutbound(query).then(res => (res.rows || []).filter(r => String(r.deliveryStatus) === "2" && String(r.isReversal || "0") !== "1" && String(r.reversedFlag || "0") !== "1")).catch(() => [])
  }

  function fetchProductUnits(productId) {
    if (!productId) return Promise.resolve([])
    return listUnitsByProduct(productId).then(res => res.data || []).catch(() => [])
  }

  function resolveLiveSalePrice(customerId, productId, unitName) {
    if (!productId) return Promise.resolve(null)
    return resolveLivePrice({ customerId, productId, unitName }).then(res => res.data || null).catch(() => null)
  }

  function fetchFefoBatches(warehouseId, productId) {
    if (!warehouseId || !productId) return Promise.resolve([])
    return request({
      url: "/business/stock/fefo",
      method: "get",
      params: { warehouseId, productId }
    }).then(res => res.data || []).catch(() => [])
  }

  function isProductSerialEnabled(productId) {
    if (!productId) return false
    const item = productOptions.value.find(p => Number(p.productId) === Number(productId))
    return item ? String(item.enableSerial) === "1" : false
  }

  function getWarehouseName(id) {
    if (id === null || id === undefined || id === "") return "-"
    const item = warehouseOptions.value.find(w => Number(w.warehouseId) === Number(id))
    return item ? item.warehouseName : `#${id}`
  }

  function getProductName(id) {
    if (id === null || id === undefined || id === "") return "-"
    const item = productOptions.value.find(p => Number(p.productId) === Number(id))
    return item ? `${item.productCode ? item.productCode + " - " : ""}${item.productName}` : `#${id}`
  }

  function getCustomerName(id) {
    if (id === null || id === undefined || id === "") return "-"
    const item = customerOptions.value.find(c => Number(c.customerId) === Number(id))
    return item ? item.customerName : `#${id}`
  }

  function getSupplierName(id) {
    if (id === null || id === undefined || id === "") return "-"
    const item = supplierOptions.value.find(s => Number(s.supplierId) === Number(id))
    return item ? item.supplierName : `#${id}`
  }

  function getFundAccountName(id) {
    if (id === null || id === undefined || id === "") return "-"
    const item = fundAccountOptions.value.find(a => Number(a.accountId) === Number(id))
    return item ? item.accountName : `#${id}`
  }

  function getLocationName(id) {
    if (id === null || id === undefined || id === "" || Number(id) === 0) return "默认库位"
    const item = locationOptions.value.find(l => Number(l.locationId) === Number(id))
    return item ? `${item.locationCode} - ${item.locationName}` : `#${id}`
  }

  return {
    warehouseOptions,
    productOptions,
    customerOptions,
    supplierOptions,
    fundAccountOptions,
    locationOptions,
    activeWarehouseOptions,
    activeProductOptions,
    activeCustomerOptions,
    activeSupplierOptions,
    activeFundAccountOptions,
    loadWarehouses,
    loadProducts,
    loadCustomers,
    loadSuppliers,
    loadFundAccounts,
    loadLocations,
    fetchLocationsByWarehouse,
    fetchOpenReceivables,
    fetchOpenPayables,
    fetchConfirmedReceipts,
    fetchConfirmedPayments,
    fetchExpenseCategoryOptions,
    fetchBillNoMap,
    fetchAuditedInboundOptions,
    fetchSignedOutboundOptions,
    fetchProductUnits,
    resolveLiveSalePrice,
    fetchFefoBatches,
    isProductSerialEnabled,
    getWarehouseName,
    getProductName,
    getCustomerName,
    getSupplierName,
    getFundAccountName,
    getLocationName
  }
}
