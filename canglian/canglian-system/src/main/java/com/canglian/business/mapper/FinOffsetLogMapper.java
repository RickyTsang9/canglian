package com.canglian.business.mapper;

import java.util.List;
import com.canglian.business.domain.FinOffsetLog;

/**
 * 往来红蓝对冲台账 数据层
 *
 * @author canglian
 */
public interface FinOffsetLogMapper
{
    public FinOffsetLog selectFinOffsetLogById(Long offsetId);

    public List<FinOffsetLog> selectFinOffsetLogList(FinOffsetLog query);

    public int insertFinOffsetLog(FinOffsetLog log);

    public int updateFinOffsetLog(FinOffsetLog log);
}
