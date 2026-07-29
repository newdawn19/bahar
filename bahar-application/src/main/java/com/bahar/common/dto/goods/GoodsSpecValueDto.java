package com.bahar.common.dto.goods;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品规格值实体
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
@Data
public class GoodsSpecValueDto implements Serializable {

    @ApiModelProperty("值ID")
    private Integer specValueId;

    @ApiModelProperty("规格名")
    private String specName;

    @ApiModelProperty("规格值")
    private String specValue;

}

