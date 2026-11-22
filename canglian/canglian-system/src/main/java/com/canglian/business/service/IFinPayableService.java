package com.canglian.business.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.canglian.business.domain.FinPayable;

/**
 * 应付单 服务层
 * 
 * @author canglian
 */
public interface IFinPayableService
{
    public FinPayable selectFinPayableById(Long payableId);

    public List<FinPayable> selectFinPayableList(FinPayable finPayable);

    public int insertFinPayable(FinPayable finPayable);

    public int updateFinPayable(FinPayable finPayable);

    public int deleteFinPayableById(Long payableId);

    public int deleteFinPayableByIds(Long[] payableIds);

    public int auditFinPayable(Long payableId, String operator);

    /** 反确认应付单（开放账期、未发生付款/对冲且非业务单据自动生成） */
    public int unconfirmFinPayable(Long payableId, String operator);

    /**
     * 采购退货自动生成已确认的红字应付冲减单（幂等）
     */
    public FinPayable createRedPayableForReturn(Long supplierId, String sourceBillType, Long sourceBillId,
        String sourceBillNo, Date businessDate, BigDecimal returnAmount, String operator, String remark);

    /**
     * 红蓝应付单互冲核销（将红字待抵扣余额冲抵指定正向应付单未付余额）
     */
    public int offsetRedPayable(Long redPayableId, Long targetPayableId, BigDecimal offsetAmount, String operator);

    /** 查询应付红蓝对冲台账 */
    public List<com.canglian.business.domain.FinOffsetLog> selectOffsetLogs(Long payableId);

    /** 撤销应付红蓝对冲 */
    public int unoffsetRedPayable(Long offsetId, String operator);
}
