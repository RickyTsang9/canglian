import request from "@/utils/request"

export function getWarehouseStatisticsOverview(query) {
  return request({
    url: "/business/warehouseStatistics/overview",
    method: "get",
    params: query
  })
}
