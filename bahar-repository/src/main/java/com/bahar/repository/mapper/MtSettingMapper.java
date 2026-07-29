package com.bahar.repository.mapper;

import com.bahar.repository.model.MtSetting;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 全局设置表 Mapper 接口
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
public interface MtSettingMapper extends BaseMapper<MtSetting> {

    List<MtSetting> querySettingByType(@Param("merchantId") Integer merchantId, @Param("type") String type);

    MtSetting querySettingByName(@Param("merchantId") Integer merchantId, @Param("storeId") Integer storeId, @Param("type") String type, @Param("name") String name);

}
