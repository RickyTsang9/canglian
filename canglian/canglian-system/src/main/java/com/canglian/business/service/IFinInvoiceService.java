package com.canglian.business.service;

import java.math.BigDecimal;
import java.util.List;
import com.canglian.business.domain.FinInvoice;

/**
 * 发票登记服务接口
 *
 * @author canglian
 */
public interface IFinInvoiceService
{
    public FinInvoice selectFinInvoiceById(Long invoiceId);

    public List<FinInvoice> selectFinInvoiceList(FinInvoice finInvoice);

    public int insertFinInvoice(FinInvoice finInvoice);

    public int updateFinInvoice(FinInvoice finInvoice);

    public int confirmFinInvoice(Long invoiceId, String operator);

    /** 开具红字发票冲销原已确认蓝票（支持全额或部分红冲） */
    public FinInvoice issueRedInvoice(Long originInvoiceId, BigDecimal redAmount, String remark, String operator);

    public int deleteFinInvoiceById(Long invoiceId);

    public int deleteFinInvoiceByIds(Long[] invoiceIds);
}
