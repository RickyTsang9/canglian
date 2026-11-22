package com.canglian.business.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.WmsStockLog;
import com.canglian.business.mapper.WmsStockLogMapper;
import com.canglian.business.service.IWmsStockLogService;
import com.canglian.common.annotation.WarehouseScope;

@Service
public class WmsStockLogServiceImpl implements IWmsStockLogService
{
    @Autowired
    private WmsStockLogMapper wmsStockLogMapper;

    @Autowired(required = false)
    private BizScopeHelper bizScopeHelper;

    @Override
    public WmsStockLog selectWmsStockLogById(Long stockLogId)
    {
        WmsStockLog log = wmsStockLogMapper.selectWmsStockLogById(stockLogId);
        if (log != null && bizScopeHelper != null)
        {
            bizScopeHelper.assertWarehouseAccess(log.getWarehouseId());
        }
        return log;
    }

    @Override
    @WarehouseScope(column = "warehouse_id")
    public List<WmsStockLog> selectWmsStockLogList(WmsStockLog wmsStockLog)
    {
        return wmsStockLogMapper.selectWmsStockLogList(wmsStockLog);
    }

    @Override
    public int insertWmsStockLog(WmsStockLog wmsStockLog)
    {
        return wmsStockLogMapper.insertWmsStockLog(wmsStockLog);
    }

    @Override
    public int updateWmsStockLog(WmsStockLog wmsStockLog)
    {
        return wmsStockLogMapper.updateWmsStockLog(wmsStockLog);
    }

    @Override
    public int deleteWmsStockLogById(Long stockLogId)
    {
        return wmsStockLogMapper.deleteWmsStockLogById(stockLogId);
    }

    @Override
    public int deleteWmsStockLogByIds(Long[] stockLogIds)
    {
        return wmsStockLogMapper.deleteWmsStockLogByIds(stockLogIds);
    }
}
