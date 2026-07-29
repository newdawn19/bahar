package com.bahar.repository.mapper;

import com.bahar.repository.model.MtRefund;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * 售后表 Mapper 接口
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
public interface MtRefundMapper extends BaseMapper<MtRefund> {

    Long getRefundCount(@Param("beginTime") Date beginTime, @Param("endTime") Date endTime);

    MtRefund findByOrderId(@Param("orderId") Integer orderId);

}
