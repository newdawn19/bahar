package com.bahar.module.merchantApi.controller;

import com.bahar.common.dto.member.UserInfo;
import com.bahar.common.dto.system.AccountInfo;
import com.bahar.common.param.StaffPage;
import com.bahar.common.param.StaffParam;
import com.bahar.common.service.MemberService;
import com.bahar.common.service.MerchantService;
import com.bahar.common.service.StaffService;
import com.bahar.common.util.TokenUtil;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.framework.web.BaseController;
import com.bahar.framework.web.ResponseObject;
import com.bahar.repository.model.MtStaff;
import com.bahar.repository.model.MtUser;
import com.bahar.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 商户员工管理接口controller
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
@Api(tags="商户端-员工相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/merchantApi/staff")
public class MerchantStaffController extends BaseController {

    /**
     * 会员服务接口
     */
    private MemberService memberService;

    /**
     * 店铺员工服务接口
     * */
    private StaffService staffService;

    /**
     * 商户服务接口
     * */
    private MerchantService merchantService;

    @ApiOperation(value = "员工列表")
    @RequestMapping(value = "/staffList", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject staffList(HttpServletRequest request, @RequestBody StaffPage staffPage) {
        UserInfo userInfo = TokenUtil.getUserInfo();
        MtUser mtUser = memberService.queryMemberById(userInfo.getId());

        if (mtUser == null || StringUtil.isBlank(mtUser.getMobile())) {
            return getFailureResult(201, "您的帐号不是商户，没有操作权限");
        }

        MtStaff staff = staffService.queryStaffByMobile(mtUser.getMobile());
        Integer merchantId = merchantService.getMerchantId(request.getHeader("merchantNo"));
        if (staff == null || !merchantId.equals(staff.getMerchantId())) {
            return getFailureResult(201, "您没有操作权限");
        }

        staffPage.setMerchantId(staff.getMerchantId());
        if (staff.getStoreId() != null && staff.getStoreId() > 0) {
            staffPage.setStoreId(staff.getStoreId());
        }

        PaginationResponse paginationResponse = staffService.queryStaffListByPagination(staffPage);

        Map<String, Object> result = new HashMap<>();
        result.put("content", paginationResponse.getContent());
        result.put("pageSize", paginationResponse.getPageSize());
        result.put("pageNumber", paginationResponse.getCurrentPage());
        result.put("totalRow", paginationResponse.getTotalElements());
        result.put("totalPage", paginationResponse.getTotalPages());

        return getSuccessResult(result);
    }

    /**
     * 查询员工详情
     */
    @ApiOperation(value = "查询员工详情")
    @RequestMapping(value = "/info", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject info(@RequestBody StaffParam params) throws BusinessCheckException {
        UserInfo userInfo = TokenUtil.getUserInfo();

        MtStaff myInfo = null;
        MtUser mtUser = memberService.queryMemberById(userInfo.getId());
        if (mtUser != null && mtUser.getMobile() != null) {
            myInfo = staffService.queryStaffByMobile(mtUser.getMobile());
        }
        if (myInfo == null) {
            return getFailureResult(201, "您的帐号不是商户，没有操作权限");
        }
        MtStaff staffInfo = staffService.queryStaffById(params.getId());
        Map<String, Object> result = new HashMap<>();
        result.put("staffInfo", staffInfo);

        return getSuccessResult(result);
    }

    @ApiOperation(value = "保存员工信息")
    @RequestMapping(value = "/saveStaff", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject saveStaff(HttpServletRequest request, @RequestBody StaffParam params) throws BusinessCheckException {
        Integer merchantId = merchantService.getMerchantId(request.getHeader("merchantNo"));
        UserInfo userInfo = TokenUtil.getUserInfo();
        MtUser mtUser = memberService.queryMemberById(userInfo.getId());
        if (mtUser == null || StringUtil.isBlank(mtUser.getMobile())) {
            return getFailureResult(201, "您的帐号不是商户，没有操作权限");
        }

        MtStaff staff = staffService.queryStaffByMobile(mtUser.getMobile());
        if (staff == null || !merchantId.equals(staff.getMerchantId())) {
            return getFailureResult(201, "您没有操作权限");
        }

        MtStaff mtStaff = new MtStaff();
        if (params.getId() != null && params.getId() > 0) {
            mtStaff = staffService.queryStaffById(params.getId());
        }
        BeanUtils.copyProperties(params, mtStaff);
        mtStaff.setMerchantId(staff.getMerchantId());
        if (staff.getStoreId() != null && staff.getStoreId() > 0) {
            mtStaff.setStoreId(staff.getStoreId());
        }
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setAccountName(staff.getRealName());
        accountInfo.setMerchantId(staff.getMerchantId());
        MtStaff staffInfo = staffService.saveStaff(mtStaff, accountInfo);
        return getSuccessResult(staffInfo);
    }
}
