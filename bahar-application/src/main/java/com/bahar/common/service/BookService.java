package com.bahar.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bahar.common.dto.book.BookDto;
import com.bahar.common.dto.system.AccountInfo;
import com.bahar.common.param.BookPage;
import com.bahar.common.param.BookableParam;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.repository.model.MtBook;

import java.text.ParseException;
import java.util.List;

/**
 * 预约业务接口
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
public interface BookService extends IService<MtBook> {

    /**
     * 分页查询预约列表
     *
     * @param bookPage
     * @return
     */
    PaginationResponse<BookDto> queryBookListByPagination(BookPage bookPage);

    /**
     * 添加预约
     *
     * @param  bookDto
     * @throws BusinessCheckException
     * @return
     */
    MtBook addBook(BookDto bookDto) throws BusinessCheckException;

    /**
     * 根据ID获取预约项目信息
     *
     * @param  id 预约项目ID
     * @param fillDate 填充日期
     * @throws ParseException
     * @return
     */
    BookDto getBookById(Integer id, boolean fillDate) throws ParseException;

    /**
     * 更新预约项目
     *
     * @param  bookDto
     * @param  accountInfo 操作人信息
     * @throws BusinessCheckException
     * @return
     * */
    MtBook updateBook(BookDto bookDto, AccountInfo accountInfo) throws BusinessCheckException;

    /**
     * 是否可预约
     *
     * @param  param
     * @throws BusinessCheckException,ParseException
     * @return
     * */
    List<String> isBookable(BookableParam param) throws BusinessCheckException, ParseException;

    /**
     * 获取预约项目列表
     *
     * @param  merchantId 商户ID
     * @param  storeId 店铺ID
     * @return
     * */
    List<MtBook> getBookList(Integer merchantId, Integer storeId);

}
