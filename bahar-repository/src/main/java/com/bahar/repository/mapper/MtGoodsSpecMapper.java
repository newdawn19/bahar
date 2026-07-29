package com.bahar.repository.mapper;

import com.bahar.repository.model.MtGoodsSpec;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 规格表 Mapper 接口
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
public interface MtGoodsSpecMapper extends BaseMapper<MtGoodsSpec> {

    List<MtGoodsSpec> getGoodsSpecCountList(@Param("goodsId") Integer goodsId);

}
