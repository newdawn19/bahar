package com.bahar.repository.mapper;

import com.bahar.repository.model.MtOrderAddress;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单收货地址记录表 Mapper 接口
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
public interface MtOrderAddressMapper extends BaseMapper<MtOrderAddress> {

    List<MtOrderAddress> getOrderAddress(@Param("orderId") Integer orderId);

}
