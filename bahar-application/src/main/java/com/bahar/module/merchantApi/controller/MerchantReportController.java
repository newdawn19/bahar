package com.bahar.module.merchantApi.controller;

import com.bahar.common.dto.goods.GoodsTopDto;
import com.bahar.common.dto.member.MemberTopDto;
import com.bahar.common.dto.member.UserInfo;
import com.bahar.common.param.StatisticParam;
import com.bahar.common.service.*;
import com.bahar.common.util.DateUtil;
import com.bahar.common.util.TokenUtil;
import com.bahar.framework.web.BaseController;
import com.bahar.framework.web.ResponseObject;
import com.bahar.repository.model.MtStaff;
import com.bahar.repository.model.MtUser;
import com.bahar.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商户报表管理接口controller
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
@Api(tags="商户端-报表相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/merchantApi/report")
public class MerchantReportController extends BaseController {

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

    /**
     * 报表服务接口
     * */
    private ReportService reportService;

    /**
     * 商品服务接口
     * */
    private GoodsService goodsService;

    @ApiOperation(value = "报表概述")
    @RequestMapping(value = "/overview", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject overview(HttpServletRequest request) throws ParseException {
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

        Integer storeId = StringUtil.isEmpty(request.getParameter("storeId")) ? 0 : Integer.parseInt(request.getParameter("storeId"));
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        if (StringUtil.isNotEmpty(startTime)) {
            startTime = startTime + " 00:00:00";
        }
        if (StringUtil.isNotEmpty(endTime)) {
            endTime = endTime + " 23:59:59";
        }

        if (staff.getStoreId() != null && staff.getStoreId() > 0) {
            storeId = staff.getStoreId();
        }

        Map<String, Object> result = reportService.getReportOverview(merchantId, storeId, startTime, endTime);
        return getSuccessResult(result);
    }

    /**
     * 图表统计数据
     */
    @ApiOperation(value = "图表统计数据")
    @RequestMapping(value = "/chart", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject chart(HttpServletRequest request) {
        String tag = request.getParameter("tag") == null ? "order,user_active" : request.getParameter("tag");
        Integer storeId = StringUtil.isEmpty(request.getParameter("storeId")) ? 0 : Integer.parseInt(request.getParameter("storeId"));
        UserInfo loginUser = TokenUtil.getUserInfo();
        MtUser userInfo = memberService.queryMemberById(loginUser.getId());
        MtStaff staff = staffService.queryStaffByMobile(userInfo.getMobile());
        Integer merchantId = merchantService.getMerchantId(request.getHeader("merchantNo"));
        if (staff == null || !merchantId.equals(staff.getMerchantId())) {
            return getFailureResult(201, "您不是员工，没有操作权限");
        }
        if (staff.getStoreId() != null && staff.getStoreId() > 0) {
            storeId = userInfo.getStoreId();
        }
        Map<String, Object> result = reportService.getChartData(tag, merchantId, storeId);
        return getSuccessResult(result);
    }

    /**
     * 排行榜数据
     */
    @ApiOperation(value = "排行榜数据")
    @RequestMapping(value = "/top", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject top(HttpServletRequest request, @RequestBody StatisticParam param) throws ParseException {
        Integer merchantId = merchantService.getMerchantId(request.getHeader("merchantNo"));
        String startTimeStr = param.getStartTime();
        String endTimeStr = param.getEndTime();
        if (StringUtil.isNotEmpty(startTimeStr)) {
            startTimeStr = startTimeStr + " 00:00:00";
        }
        if (StringUtil.isNotEmpty(endTimeStr)) {
            endTimeStr = endTimeStr + " 23:59:59";
        }

        Integer storeId = param.getStoreId();
        Date startTime = StringUtil.isNotEmpty(startTimeStr) ? DateUtil.parseDate(startTimeStr) : null;
        Date endTime = StringUtil.isNotEmpty(endTimeStr) ? DateUtil.parseDate(endTimeStr) : null;

        UserInfo loginUser = TokenUtil.getUserInfo();
        MtStaff staff = staffService.queryStaffByMobile(loginUser.getMobile());

        if (staff == null || !merchantId.equals(staff.getMerchantId())) {
            return getFailureResult(201, "您不是员工，没有操作权限");
        }

        if (staff.getStoreId() != null && staff.getStoreId() > 0) {
            storeId = staff.getStoreId();
        }

        List<GoodsTopDto> goodsList = goodsService.getGoodsSaleTopList(merchantId, storeId, startTime, endTime);
        List<MemberTopDto> memberList = memberService.getMemberConsumeTopList(merchantId, storeId, startTime, endTime);

        Map<String, Object> result = new HashMap<>();
        result.put("goodsList", goodsList);
        result.put("memberList", memberList);

        return getSuccessResult(result);
    }
}
