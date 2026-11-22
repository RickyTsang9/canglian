package com.canglian.business.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.canglian.business.domain.FinVoucherEntry;
import com.canglian.business.domain.FinVoucherTemplate;

/**
 * 凭证分录与凭证模板数据层
 *
 * @author canglian
 */
public interface FinVoucherEntryMapper
{
    public List<FinVoucherTemplate> selectTemplateList(FinVoucherTemplate template);

    public FinVoucherTemplate selectTemplateById(@Param("templateId") Long templateId);

    public List<FinVoucherTemplate> selectEnabledTemplatesByEventType(@Param("eventType") String eventType);

    public int countTemplateByEventAndSeq(@Param("eventType") String eventType, @Param("entrySeq") Integer entrySeq, @Param("excludeId") Long excludeId);

    public int countTemplateBySubjectCode(@Param("subjectCode") String subjectCode);

    public int insertTemplate(FinVoucherTemplate template);

    public int updateTemplate(FinVoucherTemplate template);

    public int deleteTemplateById(@Param("templateId") Long templateId);

    public List<FinVoucherEntry> selectEntriesByEventId(@Param("voucherEventId") Long voucherEventId);

    public List<FinVoucherEntry> selectEntriesByEventAndVoucherNo(@Param("voucherEventId") Long voucherEventId, @Param("voucherNo") String voucherNo);

    public List<FinVoucherEntry> selectEntriesForExport(@Param("billType") String billType,
                                                        @Param("eventType") String eventType,
                                                        @Param("status") String status,
                                                        @Param("voucherNo") String voucherNo);

    public int countEntriesBySubjectCode(@Param("subjectCode") String subjectCode);

    public int insertVoucherEntry(FinVoucherEntry entry);

    public int deleteEntriesByEventId(@Param("voucherEventId") Long voucherEventId);
}
