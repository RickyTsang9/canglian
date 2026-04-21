package com.canglian.business.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.mapper.FinGeneralLedgerMapper;
import com.canglian.business.service.IFinGeneralLedgerService;

/**
 * 基础总账接口服务实现
 * 
 * @author canglian
 */
@Service
public class FinGeneralLedgerServiceImpl implements IFinGeneralLedgerService
{
    @Autowired
    private FinGeneralLedgerMapper finGeneralLedgerMapper;

    /**
     * 查询会计科目列表
     * 
     * @param status 状态
     * @return 会计科目集合
     */
    @Override
    public List<Map<String, Object>> selectAccountSubjectList(String status)
    {
        return finGeneralLedgerMapper.selectAccountSubjectList(status);
    }

    /**
     * 查询辅助核算列表
     * 
     * @param auxiliaryType 辅助核算类型
     * @return 辅助核算集合
     */
    @Override
    public List<Map<String, Object>> selectAuxiliaryItemList(String auxiliaryType)
    {
        return finGeneralLedgerMapper.selectAuxiliaryItemList(auxiliaryType);
    }

    /**
     * 查询会计期间列表
     * 
     * @param closeStatus 结账状态
     * @return 会计期间集合
     */
    @Override
    public List<Map<String, Object>> selectFiscalPeriodList(String closeStatus)
    {
        return finGeneralLedgerMapper.selectFiscalPeriodList(closeStatus);
    }

    /**
     * 结账会计期间
     * 
     * @param periodId 会计期间id
     * @param operator 操作人
     * @return 结果
     */
    @Override
    public int closeFiscalPeriod(Long periodId, String operator)
    {
        return finGeneralLedgerMapper.updateFiscalPeriodCloseStatus(periodId, "closed", operator);
    }

    /**
     * 反结账会计期间
     * 
     * @param periodId 会计期间id
     * @param operator 操作人
     * @return 结果
     */
    @Override
    public int reopenFiscalPeriod(Long periodId, String operator)
    {
        return finGeneralLedgerMapper.updateFiscalPeriodCloseStatus(periodId, "open", operator);
    }
}
