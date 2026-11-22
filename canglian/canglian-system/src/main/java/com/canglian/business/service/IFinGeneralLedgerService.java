package com.canglian.business.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import com.canglian.business.domain.FinVoucherTemplate;

/**
 * 基础总账接口服务层
 * 
 * @author canglian
 */
public interface IFinGeneralLedgerService
{
    public List<Map<String, Object>> selectAccountSubjectList(String status);

    public int insertAccountSubject(Map<String, Object> subject, String operator);

    public int updateAccountSubject(Map<String, Object> subject, String operator);

    public int deleteAccountSubjectById(Long subjectId);

    public List<Map<String, Object>> selectAuxiliaryItemList(String auxiliaryType);

    public Map<String, Object> syncAuxiliaryItems(String operator);

    public List<Map<String, Object>> selectFiscalPeriodList(String closeStatus);

    public Map<String, Object> initFiscalPeriodsByYear(Integer year, String operator);

    public Map<String, Object> checkPeriodCloseReadiness(Long periodId);

    public int closeFiscalPeriod(Long periodId, String operator);

    public int reopenFiscalPeriod(Long periodId, String operator);

    public void assertPeriodOpen(Date businessDate);

    public List<FinVoucherTemplate> selectVoucherTemplateList(FinVoucherTemplate template);

    public int insertVoucherTemplate(FinVoucherTemplate template, String operator);

    public int updateVoucherTemplate(FinVoucherTemplate template);

    public int deleteVoucherTemplateById(Long templateId);
}
