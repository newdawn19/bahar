package com.bahar.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bahar.common.dto.system.AccountInfo;
import com.bahar.common.param.UserGradePage;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.repository.model.MtUser;
import com.bahar.repository.model.MtUserGrade;

import java.util.List;

/**
 * 会员等级业务接口
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
public interface UserGradeService extends IService<MtUserGrade> {

    /**
     * 分页查询会员等级列表
     *
     * @param userGradePage
     * @return
     */
    PaginationResponse<MtUserGrade> queryUserGradeListByPagination(UserGradePage userGradePage);

    /**
     * 添加会员等级
     *
     * @param  reqDto
     * @throws BusinessCheckException
     * @return
     */
    MtUserGrade addUserGrade(MtUserGrade reqDto) throws BusinessCheckException;

    /**
     * 修改会员等级
     *
     * @param  mtUserGrade
     * @throws BusinessCheckException
     * @return
     */
    MtUserGrade updateUserGrade(MtUserGrade mtUserGrade) throws BusinessCheckException;

    /**
     * 根据ID获取会员等级信息
     *
     * @param merchantId
     * @param gradeId ID
     * @param userId
     * @return
     */
    MtUserGrade queryUserGradeById(Integer merchantId, Integer gradeId, Integer userId);

    /**
     * 根据ID删除会员等级
     *
     * @param  id      ID
     * @param  accountInfo 操作人
     * @return
     */
    Integer deleteUserGrade(Integer id, AccountInfo accountInfo) throws BusinessCheckException;

    /**
     * 获取默认的会员等级
     *
     * @param merchantId
     * @return
     */
    MtUserGrade getInitUserGrade(Integer merchantId);

    /**
     * 获取付费会员等级列表
     *
     * @param  merchantId
     * @param  userInfo
     * @return
     * */
    List<MtUserGrade> getPayUserGradeList(Integer merchantId, MtUser userInfo);

    /**
     * 获取商户会员等级列表
     *
     * @param  merchantId 商户ID
     * @param status 状态
     * @return
     * */
    List<MtUserGrade> getMerchantGradeList(Integer merchantId, String status);

}
