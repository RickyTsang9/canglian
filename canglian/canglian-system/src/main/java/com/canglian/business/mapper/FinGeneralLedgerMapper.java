package com.canglian.business.mapper;

import java.util.Date;
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
    public List<Map<String, Object>> selectAccountSubjectList(@Param("status") String status);

    public Map<String, Object> selectAccountSubjectById(@Param("subjectId") Long subjectId);

    public int countAccountSubjectByCode(@Param("subjectCode") String subjectCode, @Param("excludeId") Long excludeId);

    public int insertAccountSubject(Map<String, Object> subject);

    public int updateAccountSubject(Map<String, Object> subject);

    public int deleteAccountSubjectById(@Param("subjectId") Long subjectId);

    public List<Map<String, Object>> selectAuxiliaryItemList(@Param("auxiliaryType") String auxiliaryType);

    public int countAuxiliaryItem(@Param("auxiliaryType") String auxiliaryType, @Param("relationId") Long relationId);

    public int insertAuxiliaryItem(@Param("auxiliaryType") String auxiliaryType,
                                   @Param("itemCode") String itemCode,
                                   @Param("itemName") String itemName,
                                   @Param("relationId") Long relationId,
                                   @Param("status") String status,
                                   @Param("operator") String operator);

    public int updateAuxiliaryItem(@Param("auxiliaryType") String auxiliaryType,
                                   @Param("itemCode") String itemCode,
                                   @Param("itemName") String itemName,
                                   @Param("relationId") Long relationId,
                                   @Param("status") String status,
                                   @Param("operator") String operator);

    public List<Map<String, Object>> selectActiveCustomersForAux();

    public List<Map<String, Object>> selectActiveSuppliersForAux();

    public List<Map<String, Object>> selectActiveWarehousesForAux();

    public List<Map<String, Object>> selectFiscalPeriodList(@Param("closeStatus") String closeStatus);

    public Map<String, Object> selectFiscalPeriodById(@Param("periodId") Long periodId);

    public int countFiscalPeriodByCode(@Param("periodCode") String periodCode);

    public int insertFiscalPeriod(@Param("periodCode") String periodCode,
                                  @Param("startDate") Date startDate,
                                  @Param("endDate") Date endDate,
                                  @Param("operator") String operator,
                                  @Param("remark") String remark);

    public String selectClosedPeriodCodeByDate(@Param("targetDate") Date targetDate);

    public int countEarlierOpenPeriods(@Param("periodCode") String periodCode);

    public int countLaterClosedPeriods(@Param("periodCode") String periodCode);

    public int updateFiscalPeriodCloseStatus(@Param("periodId") Long periodId, @Param("closeStatus") String closeStatus, @Param("operator") String operator);

    public int countDraftInboundInPeriod(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    public int countDraftOutboundInPeriod(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    public int countDraftTransferInPeriod(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    public int countDraftInventoryCheckInPeriod(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    public int countDraftPurchaseReturnInPeriod(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    public int countDraftSaleReturnInPeriod(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    public int countDraftReceiptInPeriod(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    public int countDraftPaymentInPeriod(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    public int countDraftExpenseInPeriod(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    public int countDraftInvoiceInPeriod(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    public int countDraftWriteOffInPeriod(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    public int countDraftBadDebtInPeriod(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    public int countDraftFundTransferInPeriod(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    public int countPendingVoucherEventInPeriod(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    public int countUnwrittenBackVoucherInPeriod(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
