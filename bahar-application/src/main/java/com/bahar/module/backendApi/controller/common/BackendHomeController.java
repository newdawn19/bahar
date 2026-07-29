package com.bahar.module.backendApi.controller.common;

import com.bahar.common.dto.order.UserOrderDto;
import com.bahar.common.dto.system.AccountInfo;
import com.bahar.common.service.OrderService;
import com.bahar.common.service.ReportService;
import com.bahar.common.util.DateUtil;
import com.bahar.common.util.TokenUtil;
import com.bahar.framework.web.BaseController;
import com.bahar.framework.web.ResponseObject;
import com.bahar.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * 首页控制器
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
@Api(tags="管理端-首页相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/backendApi/home")
public class BackendHomeController extends BaseController {

    /**
     * 订单服务接口
     * */
    private OrderService orderService;

    /**
     * 报表服务接口
     * */
    private ReportService reportService;

    /**
     * 首页统计数据
     */
    @ApiOperation(value = "首页统计数据")
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject index() throws ParseException {
        AccountInfo accountInfo = TokenUtil.getAccountInfo();

        String startTime = DateUtil.formatDate(DateUtil.getDayBegin(), "yyyy-MM-dd HH:mm:ss");
        String endTime = DateUtil.formatDate(DateUtil.getDayEnd(), "yyyy-MM-dd HH:mm:ss");

        Map<String, Object> data = reportService.getReportOverview(accountInfo.getMerchantId(), accountInfo.getStoreId(), startTime, endTime);

        Map<String, Object> result = new HashMap<>();
        result.put("todayUser", data.get("userCount"));
        result.put("totalUser", data.get("totalUserCount"));
        result.put("todayOrder", data.get("orderCount"));
        result.put("totalOrder", data.get("totalOrderCount"));
        result.put("todayPay", data.get("payAmount"));
        result.put("totalPay", data.get("totalPayAmount"));
        result.put("todayActiveUser", data.get("activeUserCount"));
        result.put("totalPayUser", data.get("totalPayUserCount"));

        return getSuccessResult(result);
    }

    /**
     * 首页图表统计数据
     */
    @ApiOperation(value = "首页图表统计数据")
    @RequestMapping(value = "/statistic", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject statistic(HttpServletRequest request) {
        String tag = request.getParameter("tag") == null ? "order,user_active" : request.getParameter("tag");
        Integer storeId = StringUtil.isEmpty(request.getParameter("storeId")) ? 0 : Integer.parseInt(request.getParameter("storeId"));
        AccountInfo accountInfo = TokenUtil.getAccountInfo();
        Integer merchantId = accountInfo.getMerchantId() == null ? 0 : accountInfo.getMerchantId();
        if (accountInfo.getStoreId() != null && accountInfo.getStoreId() > 0) {
            storeId = accountInfo.getStoreId();
        }
        Map<String, Object> result = reportService.getChartData(tag, merchantId, storeId);
        return getSuccessResult(result);
    }

    /**
     * 获取收款结果
     */
    @ApiOperation(value = "获取收款结果")
    @RequestMapping(value = "/cashierResult", method = RequestMethod.GET)
    @CrossOrigin
    public ResponseObject cashierResult(HttpServletRequest request) {
        Integer orderId = request.getParameter("orderId") == null ? 0 : Integer.parseInt(request.getParameter("orderId"));

        UserOrderDto orderInfo = orderService.getOrderById(orderId);
        Map<String, Object> result = new HashMap<>();
        result.put("orderInfo", orderInfo);

        return getSuccessResult(result);
    }
}
