package com.bahar.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bahar.common.dto.coupon.ReqCouponGroupDto;
import com.bahar.common.dto.system.AccountInfo;
import com.bahar.common.param.CouponGroupPage;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.repository.model.MtCouponGroup;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;

/**
 * 卡券分组业务接口
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
public interface CouponGroupService extends IService<MtCouponGroup> {

    /**
     * 分页查询分组列表
     *
     * @param couponGroupPage
     * @return
     */
    PaginationResponse<MtCouponGroup> queryCouponGroupListByPagination(CouponGroupPage couponGroupPage);

    /**
     * 添加卡券分组
     *
     * @param reqCouponGroupDto
     * @return
     */
    MtCouponGroup addCouponGroup(ReqCouponGroupDto reqCouponGroupDto);

    /**
     * 修改卡券分组
     *
     * @param  reqCouponGroupDto
     * @param  accountInfo 操作人
     * @throws BusinessCheckException
     * @return
     */
    MtCouponGroup updateCouponGroup(ReqCouponGroupDto reqCouponGroupDto, AccountInfo accountInfo) throws BusinessCheckException;

    /**
     * 根据组ID获取分组信息
     *
     * @param id 分组ID
     * @return
     */
    MtCouponGroup queryCouponGroupById(Integer id);

    /**
     * 根据分组ID 删除分组信息
     *
     * @param id       分组ID
     * @param accountInfo 操作人
     * @throws BusinessCheckException
     * @return
     */
    void deleteCouponGroup(Integer id, AccountInfo accountInfo) throws BusinessCheckException;

    /**
     * 根据分组ID 获取券种类数量
     *
     * @param id       分组ID
     * @return
     */
    Integer getCouponNum(Integer id);

    /**
     * 根据分组ID 获取券总价值
     *
     * @param id 分组ID
     * @return
     */
    BigDecimal getCouponMoney(Integer id);

    /**
     * 获取已发放套数
     *
     * @param  id  分组ID
     * @return
     * */
    Integer getSendNum(Integer id);

    /**
     * 导入发券列表
     *
     * @param file excel文件
     * @param accountInfo 操作者
     * @return
     * */
    String importSendCoupon(MultipartFile file, AccountInfo accountInfo, String filePath) throws BusinessCheckException;

}
