package com.canglian.business.mapper;

import java.util.List;
import com.canglian.business.domain.FinInvoice;

/**
 * 发票登记数据层
 * 
 * @author canglian
 */
public interface FinInvoiceMapper
{
    public FinInvoice selectFinInvoiceById(Long invoiceId);

    public List<FinInvoice> selectFinInvoiceList(FinInvoice finInvoice);

    public int insertFinInvoice(FinInvoice finInvoice);

    public int updateFinInvoice(FinInvoice finInvoice);

    public int deleteFinInvoiceById(Long invoiceId);

    public int deleteFinInvoiceByIds(Long[] invoiceIds);

    public String selectMaxInvoiceNoByPrefix(String invoiceNoPrefix);
}
