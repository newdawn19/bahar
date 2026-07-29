package com.bahar.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bahar.common.param.BookCatePage;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.repository.model.MtBookCate;

import java.util.List;

/**
 * 预约类别业务接口
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
public interface BookCateService extends IService<MtBookCate> {

    /**
     * 分页查询列表
     *
     * @param bookCatePage
     * @return
     */
    PaginationResponse<MtBookCate> queryBookCateListByPagination(BookCatePage bookCatePage);

    /**
     * 添加预约类别
     *
     * @param  mtBookCate
     * @throws BusinessCheckException
     * @return
     */
    MtBookCate addBookCate(MtBookCate mtBookCate) throws BusinessCheckException;

    /**
     * 根据ID获取预约类别
     *
     * @param  id 预约分类ID
     * @return
     */
    MtBookCate getBookCateById(Integer id);

    /**
     * 更新预约类别
     *
     * @param  mtBookCate
     * @throws BusinessCheckException
     * @return
     * */
    MtBookCate updateBookCate(MtBookCate mtBookCate) throws BusinessCheckException;

    /**
     * 获取可用的预约类别
     *
     * @param  merchantId 商户ID
     * @param  storeId 店铺ID
     * @return
     * */
    List<MtBookCate> getAvailableBookCate(Integer merchantId, Integer storeId);

}
