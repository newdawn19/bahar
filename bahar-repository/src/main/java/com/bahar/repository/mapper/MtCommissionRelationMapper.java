package com.bahar.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bahar.repository.model.MtCommissionRelation;
import org.apache.ibatis.annotations.Param;

/**
 * 会员分销关系 Mapper 接口
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
public interface MtCommissionRelationMapper extends BaseMapper<MtCommissionRelation> {

    Integer getCommissionUserId(@Param("merchantId") Integer merchantId, @Param("userId") Integer userId);

    Integer getSecondLevelCommissionUserId(@Param("merchantId") Integer merchantId, @Param("userId") Integer userId);

    Long getInvitedUserCount(@Param("userId") Integer userId);

}
