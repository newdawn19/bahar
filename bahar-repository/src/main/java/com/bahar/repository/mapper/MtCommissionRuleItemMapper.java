package com.bahar.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bahar.repository.model.MtCommissionRuleItem;
import org.apache.ibatis.annotations.Param;
import java.util.Date;
import java.util.List;

/**
 *  分佣提成规则项目 Mapper 接口
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
public interface MtCommissionRuleItemMapper extends BaseMapper<MtCommissionRuleItem> {

    Boolean deleteByRuleId(@Param("ruleId") Integer ruleId, @Param("updateTime") Date updateTime);

    List<MtCommissionRuleItem> getEffectiveCommissionList(@Param("merchantId") Integer merchantId, @Param("targetId") Integer targetId, @Param("type") String type);

}
