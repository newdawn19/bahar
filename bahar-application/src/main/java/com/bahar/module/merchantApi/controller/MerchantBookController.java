package com.bahar.module.merchantApi.controller;

import com.bahar.common.dto.member.UserInfo;
import com.bahar.common.dto.system.AccountInfo;
import com.bahar.common.enums.BookStatusEnum;
import com.bahar.common.param.BookItemPage;
import com.bahar.common.service.BookItemService;
import com.bahar.common.service.MemberService;
import com.bahar.common.service.StaffService;
import com.bahar.common.util.TokenUtil;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.framework.web.BaseController;
import com.bahar.framework.web.ResponseObject;
import com.bahar.module.merchantApi.request.BookConfirmParam;
import com.bahar.module.merchantApi.request.BookDetailParam;
import com.bahar.module.merchantApi.request.BookListRequest;
import com.bahar.repository.model.MtBookItem;
import com.bahar.repository.model.MtStaff;
import com.bahar.repository.model.MtUser;
import com.bahar.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 预约类controller
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
@Api(tags="商户端-预约管理相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/merchantApi/book")
public class MerchantBookController extends BaseController {

    /**
     * 预约单服务接口
     */
    private BookItemService bookItemService;

    /**
     * 会员服务接口
     */
    private MemberService memberService;

    /**
     * 店铺员工服务接口
     * */
    private StaffService staffService;

    /**
     * 获取预约单列表
     */
    @ApiOperation(value = "获取预约单列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject list(@RequestBody BookListRequest requestParams) throws BusinessCheckException {
        UserInfo userInfo = TokenUtil.getUserInfo();

        MtUser mtUser = memberService.queryMemberById(userInfo.getId());
        MtStaff staffInfo = staffService.queryStaffByMobile(mtUser.getMobile());
        BookItemPage bookItemPage = new BookItemPage();

        if (staffInfo == null) {
            return getFailureResult(1004);
        } else {
            bookItemPage.setMerchantId(staffInfo.getMerchantId());
            if (staffInfo.getStoreId() > 0) {
                bookItemPage.setStoreId(staffInfo.getStoreId());
            }
        }
        if (StringUtil.isNotEmpty(requestParams.getStatus())) {
            bookItemPage.setStatus(requestParams.getStatus());
        }

        PaginationResponse paginationResponse = bookItemService.queryBookItemListByPagination(bookItemPage);

        Map<String, Object> result = new HashMap<>();
        result.put("content", paginationResponse.getContent());
        result.put("pageSize", paginationResponse.getPageSize());
        result.put("pageNumber", paginationResponse.getCurrentPage());
        result.put("totalRow", paginationResponse.getTotalElements());
        result.put("totalPage", paginationResponse.getTotalPages());
        result.put("statusList", BookStatusEnum.getBookStatusList(BookStatusEnum.DELETE.getKey()));

        return getSuccessResult(result);
    }

    /**
     * 获取预约单详情
     */
    @ApiOperation(value = "获取预约单详情")
    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject detail(@RequestBody BookDetailParam param) throws BusinessCheckException {
        UserInfo userInfo = TokenUtil.getUserInfo();

        MtUser mtUser = memberService.queryMemberById(userInfo.getId());
        MtStaff mtStaff = staffService.queryStaffByMobile(mtUser.getMobile());
        if (mtStaff == null) {
            return getFailureResult(1004);
        }

        Integer bookId = param.getBookId();
        if (bookId == null) {
            return getFailureResult(2000, "预约ID不能为空");
        }

        MtBookItem bookInfo = bookItemService.getBookItemById(param.getBookId());
        return getSuccessResult(bookInfo);
    }

    /**
     * 取消预约
     */
    @ApiOperation(value = "取消预约")
    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject cancel(@RequestBody BookDetailParam param) throws BusinessCheckException {
        UserInfo mtUser = TokenUtil.getUserInfo();

        Integer bookId = param.getBookId();
        if (bookId == null) {
            return getFailureResult(201, "预约ID不能为空");
        }

        MtBookItem bookItem = bookItemService.getBookItemById(bookId);
        if (bookItem == null) {
            return getFailureResult(201, "预约单已不存在");
        }

        MtUser userInfo = memberService.queryMemberById(mtUser.getId());
        MtStaff staffInfo = staffService.queryStaffByMobile(userInfo.getMobile());

        if (staffInfo == null || (staffInfo.getStoreId() != null && staffInfo.getStoreId() > 0 && !staffInfo.getStoreId().equals(bookItem.getStoreId()))) {
            return getFailureResult(1004);
        }

        Boolean result = bookItemService.cancelBook(bookItem.getId(), "店员取消");
        return getSuccessResult(result);
    }

    /**
     * 确定预约
     */
    @ApiOperation(value = "确定预约")
    @RequestMapping(value = "/confirm", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject confirm(@RequestBody BookConfirmParam param) throws BusinessCheckException {
        UserInfo mtUser = TokenUtil.getUserInfo();

        Integer bookId = param.getBookId();
        MtBookItem bookItem = bookItemService.getBookItemById(bookId);
        if (bookItem == null) {
            return getFailureResult(201, "预约单不存在");
        }

        MtUser userInfo = memberService.queryMemberById(mtUser.getId());
        MtStaff staffInfo = staffService.queryStaffByMobile(userInfo.getMobile());
        if (staffInfo == null || (staffInfo.getStoreId() != null && staffInfo.getStoreId() > 0 && !staffInfo.getStoreId().equals(bookItem.getStoreId()))) {
            return getFailureResult(1004);
        }
        bookItem.setStatus(param.getStatus() == null ? BookStatusEnum.CONFIRM.getKey() : param.getStatus());
        bookItem.setOperator(staffInfo.getRealName());
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setMerchantId(staffInfo.getMerchantId());
        accountInfo.setStoreId(staffInfo.getStoreId());
        bookItemService.updateBookItem(bookItem, accountInfo);
        return getSuccessResult(true);
    }
}
