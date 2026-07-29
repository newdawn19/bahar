package com.bahar.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bahar.common.dto.member.MemberGroupDto;
import com.bahar.common.dto.member.UserGroupDto;
import com.bahar.common.dto.system.AccountInfo;
import com.bahar.common.param.MemberGroupPage;
import com.bahar.framework.exception.BusinessCheckException;
import com.bahar.framework.pagination.PaginationResponse;
import com.bahar.repository.model.MtUserGroup;

/**
 * 会员分组业务接口
 *
 * Created by FSQ
 * CopyRight https://www.bahar.cn
 */
public interface MemberGroupService extends IService<MtUserGroup> {

    /**
     * 分页查询分组列表
     *
     * @param memberGroupPage
     * @return
     */
    PaginationResponse<UserGroupDto> queryMemberGroupListByPagination(MemberGroupPage memberGroupPage);

    /**
     * 新增会员分组
     *
     * @param  memberGroupDto
     * @return
     */
    MtUserGroup addMemberGroup(MemberGroupDto memberGroupDto);

    /**
     * 修改卡券分组
     *
     * @param  memberGroupDto
     * @param  accountInfo
     * @throws BusinessCheckException
     * @return
     */
    MtUserGroup updateMemberGroup(MemberGroupDto memberGroupDto, AccountInfo accountInfo) throws BusinessCheckException;

    /**
     * 根据组ID获取分组信息
     *
     * @param  id 分组ID
     * @return
     */
    MtUserGroup queryMemberGroupById(Integer id);

    /**
     * 根据分组ID删除分组信息
     *
     * @param  id 分组ID
     * @param  accountInfo 操作人
     * @return
     */
    void deleteMemberGroup(Integer id, AccountInfo accountInfo) throws BusinessCheckException;
}
