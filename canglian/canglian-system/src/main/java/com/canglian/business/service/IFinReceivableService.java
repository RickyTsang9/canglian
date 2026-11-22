package com.canglian.business.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.canglian.business.domain.FinReceivable;

/**
 * 应收单 服务层
 * 
 * @author canglian
 */
public interface IFinReceivableService
{
    public FinReceivable selectFinReceivableById(Long receivableId);

    public List<FinReceivable> selectFinReceivableList(FinReceivable finReceivable);

    public int insertFinReceivable(FinReceivable finReceivable);

    public int updateFinReceivable(FinReceivable finReceivable);

    public int deleteFinReceivableById(Long receivableId);

    public int deleteFinReceivableByIds(Long[] receivableIds);

    public int auditFinReceivable(Long receivableId, String operator);

    /** 反确认应收单（开放账期、未发生收款/对冲且非业务单据自动生成） */
    public int unconfirmFinReceivable(Long receivableId, String operator);

    /**
     * 销售退货/出库退货自动生成已确认的红字应收冲减单（幂等）
     */
    public FinReceivable createRedReceivableForReturn(Long customerId, String sourceBillType, Long sourceBillId,
        String sourceBillNo, Date businessDate, BigDecimal returnAmount, String operator, String remark);

    /**
     * 红蓝应收单互冲核销（将红字待抵扣余额冲抵指定正向应收单未收余额）
     */
    public int offsetRedReceivable(Long redReceivableId, Long targetReceivableId, BigDecimal offsetAmount, String operator);

    /** 查询应收红蓝对冲台账 */
    public List<com.canglian.business.domain.FinOffsetLog> selectOffsetLogs(Long receivableId);

    /** 撤销应收红蓝对冲 */
    public int unoffsetRedReceivable(Long offsetId, String operator);
}
