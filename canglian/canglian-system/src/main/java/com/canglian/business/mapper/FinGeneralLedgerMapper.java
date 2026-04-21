package com.canglian.business.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 * 基础总账接口数据层
 * 
 * @author canglian
 */
public interface FinGeneralLedgerMapper
{
    /**
     * 查询会计科目列表
     * 
     * @param status 状态
     * @return 会计科目集合
     */
    public List<Map<String, Object>> selectAccountSubjectList(@Param("status") String status);

    /**
     * 查询辅助核算列表
     * 
     * @param auxiliaryType 辅助核算类型
     * @return 辅助核算集合
     */
    public List<Map<String, Object>> selectAuxiliaryItemList(@Param("auxiliaryType") String auxiliaryType);

    /**
     * 查询会计期间列表
     * 
     * @param closeStatus 结账状态
     * @return 会计期间集合
     */
    public List<Map<String, Object>> selectFiscalPeriodList(@Param("closeStatus") String closeStatus);

    /**
     * 更新会计期间结账状态
     * 
     * @param periodId 会计期间id
     * @param closeStatus 结账状态
     * @param operator 操作人
     * @return 结果
     */
    public int updateFiscalPeriodCloseStatus(@Param("periodId") Long periodId, @Param("closeStatus") String closeStatus, @Param("operator") String operator);
}
