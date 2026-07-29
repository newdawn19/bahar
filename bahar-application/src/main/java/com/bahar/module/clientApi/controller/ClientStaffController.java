package com.bahar.module.clientApi.controller;

import com.bahar.common.dto.member.UserInfo;
import com.bahar.common.dto.system.AccountInfo;
import com.bahar.common.enums.StatusEnum;
import com.bahar.common.param.StaffParam;
import com.bahar.common.service.MemberService;
import com.bahar.common.service.StaffService;
import com.bahar.common.util.TokenUtil;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.web.BaseController;
import com.bahar.framework.web.ResponseObject;
import com.bahar.repository.model.MtStaff;
import com.bahar.repository.model.MtUser;
import com.bahar.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 会员端-员工相关接口controller
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
@Api(tags="会员端-员工相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/staff")
public class ClientStaffController extends BaseController {

    /**
     * 员工服务接口
     */
    private StaffService staffService;

    /**
     * 会员服务接口
     */
    private MemberService memberService;

    /**
     * 申请成为员工
     */
    @ApiOperation(value = "申请成为员工")
    @RequestMapping(value = "/apply", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject apply(@RequestBody StaffParam params) throws BusinessCheckException {
        UserInfo loginInfo = TokenUtil.getUserInfo();
        MtUser userInfo = memberService.queryMemberById(loginInfo.getId());
        // 参数校验
        if (StringUtil.isBlank(params.getRealName())) {
            return getFailureResult(201, "姓名不能为空");
        }
        if (StringUtil.isBlank(params.getMobile())) {
            return getFailureResult(201, "手机号不能为空");
        }

        // 检查该手机号是否已经是员工
        MtStaff existStaff = staffService.queryStaffByMobile(params.getMobile());
        if (existStaff != null) {
            if (StatusEnum.ENABLED.getKey().equals(existStaff.getAuditedStatus())) {
                return getFailureResult(201, "您已经是员工，无需重复申请");
            } else if (StatusEnum.UnAudited.getKey().equals(existStaff.getAuditedStatus())) {
                return getFailureResult(201, "您的申请正在审核中，请耐心等待");
            } else {
                return getFailureResult(201, "您已经申请过，无法再申请");
            }
        }

        // 创建员工申请
        MtStaff newStaff = new MtStaff();
        newStaff.setRealName(params.getRealName());
        newStaff.setMobile(params.getMobile());
        newStaff.setStoreId(params.getStoreId());
        newStaff.setMerchantId(userInfo.getMerchantId());
        newStaff.setAuditedStatus(StatusEnum.UnAudited.getKey());
        newStaff.setCreateTime(new Date());
        newStaff.setCategory(1);
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setAccountName(userInfo.getName());
        accountInfo.setMerchantId(userInfo.getMerchantId());
        staffService.saveStaff(newStaff, accountInfo);

        return getSuccessResult("申请提交成功，请等待审核", null);
    }
}
