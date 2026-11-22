package com.canglian.business.service;

import java.util.List;
import com.canglian.business.domain.WmsSaleReturn;

/**
 * 销售退货 服务层
 *
 * @author canglian
 */
public interface IWmsSaleReturnService
{
    public WmsSaleReturn selectWmsSaleReturnById(Long saleReturnId);

    public List<WmsSaleReturn> selectWmsSaleReturnList(WmsSaleReturn wmsSaleReturn);

    public int insertWmsSaleReturn(WmsSaleReturn wmsSaleReturn);

    public int updateWmsSaleReturn(WmsSaleReturn wmsSaleReturn);

    public int deleteWmsSaleReturnById(Long saleReturnId);

    public int deleteWmsSaleReturnByIds(Long[] saleReturnIds);

    public int auditWmsSaleReturn(Long saleReturnId, String operator);

    /** 反审核销售退货单 */
    public int unauditWmsSaleReturn(Long saleReturnId, String operator);

    /** 一键红冲销售退货单 */
    public WmsSaleReturn reverseWmsSaleReturn(Long saleReturnId, String operator);
}
