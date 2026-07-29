package com.bahar.module.clientApi.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 我的卡券请求参数
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
@Data
public class MyCouponRequest implements Serializable {

    @ApiModelProperty(value="卡券ID", name="id")
    private Integer id;

    @ApiModelProperty(value="会员卡券ID", name="userCouponId")
    private Integer userCouponId;

}
