package com.canglian.business.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.canglian.business.domain.WmsSerialNo;
import com.canglian.business.domain.WmsSerialNoLog;

/**
 * 商品序列号(SN) Mapper
 *
 * @author canglian
 */
public interface WmsSerialNoMapper
{
    public WmsSerialNo selectWmsSerialNoById(Long serialId);

    public WmsSerialNo selectWmsSerialNoByCode(@Param("snCode") String snCode);

    public List<WmsSerialNo> selectWmsSerialNoList(WmsSerialNo query);

    public int insertWmsSerialNo(WmsSerialNo serialNo);

    public int updateWmsSerialNo(WmsSerialNo serialNo);

    public int deleteWmsSerialNoByIds(Long[] serialIds);

    public int insertWmsSerialNoLog(WmsSerialNoLog log);

    public List<WmsSerialNoLog> selectLogsBySnCode(@Param("snCode") String snCode);
}
