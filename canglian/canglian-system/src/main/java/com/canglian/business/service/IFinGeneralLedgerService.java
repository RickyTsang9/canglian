package com.canglian.business.service;

import java.util.List;
import java.util.Map;

/**
 * 基础总账接口服务层
 * 
 * @author canglian
 */
public interface IFinGeneralLedgerService
{
    /**
     * 查询会计科目列表
     * 
     * @param status 状态
     * @return 会计科目集合
     */
    public List<Map<String, Object>> selectAccountSubjectList(String status);

    /**
     * 查询辅助核算列表
     * 
     * @param auxiliaryType 辅助核算类型
     * @return 辅助核算集合
     */
    public List<Map<String, Object>> selectAuxiliaryItemList(String auxiliaryType);

    /**
     * 查询会计期间列表
     * 
     * @param closeStatus 结账状态
     * @return 会计期间集合
     */
    public List<Map<String, Object>> selectFiscalPeriodList(String closeStatus);

    /**
     * 结账会计期间
     * 
     * @param periodId 会计期间id
     * @param operator 操作人
     * @return 结果
     */
    public int closeFiscalPeriod(Long periodId, String operator);

    /**
     * 反结账会计期间
     * 
     * @param periodId 会计期间id
     * @param operator 操作人
     * @return 结果
     */
    public int reopenFiscalPeriod(Long periodId, String operator);
}
