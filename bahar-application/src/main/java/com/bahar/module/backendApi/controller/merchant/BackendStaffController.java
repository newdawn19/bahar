package com.bahar.module.backendApi.controller.merchant;

import com.bahar.common.dto.merchant.StaffDto;
import com.bahar.common.dto.system.AccountInfo;
import com.bahar.common.dto.common.ParamDto;
import com.bahar.common.enums.StaffCategoryEnum;
import com.bahar.common.enums.StatusEnum;
import com.bahar.common.param.StaffPage;
import com.bahar.common.param.StaffParam;
import com.bahar.common.param.StatusParam;
import com.bahar.common.service.MemberService;
import com.bahar.common.service.StaffService;
import com.bahar.common.util.CommonUtil;
import com.bahar.common.util.PhoneFormatCheckUtils;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 店铺员工管理
 *
 * CopyRight https://www.bahar.cn
 */
@Api(tags="管理端-店铺员工相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/staff")
public class BackendStaffController extends BaseController {

    /**
     * 员工接口
     */
    private StaffService staffService;

    /**
     * 会员接口（用于把已有会员账号关联成员工）
     */
    private MemberService memberService;

    /**
     * 获取员工列表
     */
    @ApiOperation(value = "获取员工列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('staff:list')")
    public ResponseObject list(@ModelAttribute StaffPage staffPage) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            staffPage.setStoreId(accountInfo.getStoreId());
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            staffPage.setMerchantId(accountInfo.getMerchantId());
        }
        PaginationResponse<StaffDto> paginationResponse = staffService.queryStaffListByPagination(staffPage);

        // 员工类别列表
        List<ParamDto> categoryList = StaffCategoryEnum.getStaffCategoryList();

        Map<String, Object> result = new HashMap<>();
        result.put("paginationResponse", paginationResponse);
        result.put("categoryList", categoryList);

        return getSuccessResult(result);
    }

    /**
     * 更新员工状态
     */
    @ApiOperation(value = "更新员工状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('staff:list')")
    public ResponseObject updateStatus(@RequestBody StatusParam params) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        staffService.updateAuditedStatus(params.getId(), params.getStatus(), accountInfo);
        return getSuccessResult(true);
    }

    /**
     * 保存员工信息
     */
    @ApiOperation(value = "保存员工信息")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('staff:list')")
    public ResponseObject saveHandler(@RequestBody StaffParam staffParam) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        if (accountInfo.getMerchantId() == null || accountInfo.getMerchantId() <= 0) {
            return getFailureResult(5002);
        }

        MtStaff mtStaff = new MtStaff();
        Integer storeId = staffParam.getStoreId();
        if (staffParam.getId() != null) {
            mtStaff = staffService.queryStaffById(staffParam.getId());
        }
        if (staffParam.getId() != null && mtStaff == null) {
            return getFailureResult(201, "员工信息不存在");
        }
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            mtStaff.setMerchantId(accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            storeId = accountInfo.getStoreId();
        }
        mtStaff.setStoreId(storeId);
        mtStaff.setRealName(staffParam.getRealName());
        if (PhoneFormatCheckUtils.isChinaPhoneLegal(staffParam.getMobile())) {
            mtStaff.setMobile(staffParam.getMobile());
        }
        mtStaff.setAuditedStatus(staffParam.getAuditedStatus() == null ? StatusEnum.FORBIDDEN.getKey() : staffParam.getAuditedStatus());
        mtStaff.setDescription(staffParam.getDescription());
        mtStaff.setCategory(staffParam.getCategory());
        // 关联已有会员：会员号（如 U00000001）优先，其次会员ID；
        // 命中则直接把该会员挂成员工（saveStaff 会将其 isStaff 置为 Y），
        // 都没传才沿用原逻辑——由系统自动注册一个新会员账号再回填。
        Integer bindUserId = staffParam.getUserId();
        if (StringUtil.isNotEmpty(staffParam.getUserNo())) {
            MtUser bindUser = memberService.queryMemberByUserNo(accountInfo.getMerchantId(), staffParam.getUserNo().trim());
            if (bindUser == null) {
                return getFailureResult(201, "会员号不存在：" + staffParam.getUserNo());
            }
            bindUserId = bindUser.getId();
        }
        if (bindUserId != null && bindUserId > 0) {
            mtStaff.setUserId(bindUserId);
        }

        if (StringUtil.isEmpty(mtStaff.getMobile())) {
            return getFailureResult(201, "手机号码不能为空");
        } else {
            MtStaff staff = staffService.queryStaffByMobile(mtStaff.getMobile());
            if (staff != null && !staff.getId().equals(mtStaff.getId())) {
                return getFailureResult(201, "该手机号码已经存在");
            }
        }
        staffService.saveStaff(mtStaff, accountInfo);
        return getSuccessResult(true);
    }

    /**
     * 查询员工详情
     */
    @ApiOperation(value = "查询员工详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('staff:list')")
    public ResponseObject getStaffInfo(@PathVariable("id") Integer id) {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        MtStaff staffInfo = staffService.queryStaffById(id);
        Map<String, Object> result = new HashMap<>();
        if (staffInfo != null) {
            if (accountInfo.getMerchantId() > 0 && !accountInfo.getMerchantId().equals(staffInfo.getMerchantId())) {
                return getFailureResult(1004);
            }
            // 用 StaffDto 返回，除了脱敏手机号，还带上已绑定的会员号，
            // 这样编辑员工时「关联会员号」一栏能回填，不会看着像没绑定。
            StaffDto staffDto = new StaffDto();
            BeanUtils.copyProperties(staffInfo, staffDto);
            staffDto.setMobile(CommonUtil.hidePhone(staffInfo.getMobile()));
            if (staffInfo.getUserId() != null && staffInfo.getUserId() > 0) {
                MtUser member = memberService.queryMemberById(staffInfo.getUserId());
                if (member != null) {
                    staffDto.setUserNo(member.getUserNo());
                    staffDto.setUserName(member.getName());
                }
            }
            result.put("staffInfo", staffDto);
        }

        return getSuccessResult(result);
    }

    /**
     * 店铺员工列表
     */
    @ApiOperation(value = "店铺员工列表")
    @RequestMapping(value = "/storeStaffList/{storeId}", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject storeStaffList(@PathVariable("storeId") Integer storeId) {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        Map<String, Object> params = new HashMap<>();
        if (accountInfo.getMerchantId() != null && accountInfo.getMerchantId() > 0) {
            params.put("MERCHANT_ID", accountInfo.getMerchantId());
        }
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            storeId = accountInfo.getStoreId();
        }
        params.put("AUDITED_STATUS", StatusEnum.ENABLED.getKey());
        if (storeId != null && storeId > 0) {
            params.put("STORE_ID", storeId);
        }
        List<MtStaff> staffList = staffService.queryStaffByParams(params);

        Map<String, Object> result = new HashMap<>();
        result.put("staffList", staffList);

        return getSuccessResult(result);
    }

    /**
     * 删除员工
     */
    @ApiOperation(value = "删除员工")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @CrossOrigin
    @PreAuthorize("@pms.hasPermission('staff:list')")
    public ResponseObject deleteStaff(@PathVariable("id") Integer id) throws BusinessCheckException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        staffService.updateAuditedStatus(id, StatusEnum.DISABLE.getKey(), accountInfo);
        return getSuccessResult(true);
    }
}
