package com.bahar.common.dto.coupon;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 卡券分组数据DTO
 *
 * CopyRight https://www.bahar.cn
 */
@Data
public class GroupDataListDto {

    @ApiModelProperty("键值")
    private String key;

    @ApiModelProperty("数据")
    private GroupDataDto data;

}
