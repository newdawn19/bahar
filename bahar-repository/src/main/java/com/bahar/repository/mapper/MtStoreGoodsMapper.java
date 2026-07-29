package com.bahar.repository.mapper;

import com.bahar.repository.model.MtStoreGoods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 店铺商品 Mapper 接口
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
public interface MtStoreGoodsMapper extends BaseMapper<MtStoreGoods> {

    void removeStoreGoods(@Param("storeId") Integer storeId);

}
