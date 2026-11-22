package com.canglian.business.service;

import java.util.List;
import com.canglian.business.domain.WmsInventoryCheck;

/**
 * 盘点单 服务层
 *
 * @author canglian
 */
public interface IWmsInventoryCheckService
{
    public WmsInventoryCheck selectWmsInventoryCheckById(Long checkId);

    public List<WmsInventoryCheck> selectWmsInventoryCheckList(WmsInventoryCheck wmsInventoryCheck);

    public int insertWmsInventoryCheck(WmsInventoryCheck wmsInventoryCheck);

    public int updateWmsInventoryCheck(WmsInventoryCheck wmsInventoryCheck);

    public int deleteWmsInventoryCheckById(Long checkId);

    public int deleteWmsInventoryCheckByIds(Long[] checkIds);

    public int auditWmsInventoryCheck(Long checkId, String operator);

    /** 反审核盘点单（反向冲回盘盈盘亏差异并恢复草稿） */
    public int unauditWmsInventoryCheck(Long checkId, String operator);

    /** 一键红冲盘点单（生成反向红冲盘点单） */
    public WmsInventoryCheck reverseWmsInventoryCheck(Long checkId, String operator);
}
