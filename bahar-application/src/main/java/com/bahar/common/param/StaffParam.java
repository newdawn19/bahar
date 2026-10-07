package com.bahar.common.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * 订单列表请求参数
 *
 * CopyRight https://www.bahar.cn
 */
@Data
public class StaffParam extends PageParam implements Serializable {

    @ApiModelProperty(value="ID", name="id")
    private Integer id;

    @ApiModelProperty(value="商户ID", name="merchantId")
    private Integer merchantId;

    @ApiModelProperty(value="店铺ID", name="storeId")
    private Integer storeId;

    @ApiModelProperty(value="类别", name="category")
    private Integer category;

    @ApiModelProperty(value="手机号", name="mobile")
    private String mobile;

    @ApiModelProperty(value="真实姓名", name="realName")
    private String realName;

    @ApiModelProperty(value="备注信息", name="description")
    private String description;

    @ApiModelProperty(value="审核状态", name="auditedStatus")
    private String auditedStatus;

    @ApiModelProperty(value="关联的会员ID，传入则把该已有会员直接挂为员工；不传则由系统自动注册一个新会员账号", name="userId")
    private Integer userId;

    @ApiModelProperty(value="关联的会员号，如 U00000001，与 userId 二选一，会员号优先", name="userNo")
    private String userNo;

}
