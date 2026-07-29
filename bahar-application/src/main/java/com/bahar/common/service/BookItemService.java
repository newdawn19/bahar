package com.bahar.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bahar.common.dto.book.BookItemDto;
import com.bahar.common.dto.system.AccountInfo;
import com.bahar.common.param.BookItemPage;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.repository.model.MtBookItem;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 预约订单业务接口
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
public interface BookItemService extends IService<MtBookItem> {

    /**
     * 分页查询列表
     *
     * @param bookItemPage
     * @return
     */
    PaginationResponse<BookItemDto> queryBookItemListByPagination(BookItemPage bookItemPage);

    /**
     * 添加预约订单
     *
     * @param  mtBookItem
     * @throws BusinessCheckException
     * @return
     */
    MtBookItem addBookItem(MtBookItem mtBookItem) throws BusinessCheckException, ParseException;

    /**
     * 根据ID获取预约订单信息
     *
     * @param  id 预约订单ID
     * @return
     */
    MtBookItem getBookItemById(Integer id);

    /**
     * 获取用户预约订单信息
     *
     * @param  bookId 预约项目ID
     * @param userId 用户ID
     * @param orderGoodsId 订单商品ID
     * @return
     */
    MtBookItem getUserBookItem(Integer bookId, Integer userId, Integer orderGoodsId);

    /**
     * 根据ID获取预约订单详情
     *
     * @param  id 预约订单ID
     * @return
     */
    BookItemDto getBookDetail(Integer id);

    /**
     * 更新预约订单
     *
     * @param  mtBookItem
     * @param accountInfo
     * @throws BusinessCheckException
     * @return
     * */
    MtBookItem updateBookItem(MtBookItem mtBookItem, AccountInfo accountInfo) throws BusinessCheckException;

    /**
     * 根据条件搜索预约订单
     *
     * @param  params 查询参数
     * @return
     * */
    List<MtBookItem> queryBookItemListByParams(Map<String, Object> params);

    /**
     * 取消预约
     *
     * @param id 预约订单ID
     * @param remark 备注信息
     * @throws BusinessCheckException
     * @return
     * */
    Boolean cancelBook(Integer id, String remark) throws BusinessCheckException;
}
