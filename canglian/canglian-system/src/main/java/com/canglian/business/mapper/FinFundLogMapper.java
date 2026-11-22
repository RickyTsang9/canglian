package com.canglian.business.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.canglian.business.domain.FinFundLog;
import com.canglian.business.domain.FinFundTransfer;

/**
 * 资金流水与内部互转 Mapper
 *
 * @author canglian
 */
public interface FinFundLogMapper
{
    public List<FinFundLog> selectFinFundLogList(FinFundLog query);

    public int countLogsByAccountId(@Param("accountId") Long accountId);

    public int insertFinFundLog(FinFundLog log);

    public FinFundTransfer selectFinFundTransferById(Long transferId);

    public List<FinFundTransfer> selectFinFundTransferList(FinFundTransfer query);

    public int insertFinFundTransfer(FinFundTransfer transfer);

    public int updateFinFundTransfer(FinFundTransfer transfer);

    public int deleteFinFundTransferById(Long transferId);

    public String selectMaxTransferNoByPrefix(@Param("prefix") String prefix);
}
