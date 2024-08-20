package com.bx.implatform.mapper;

import com.bx.implatform.controller.PsiMutualFriends;
import com.bx.implatform.dto.PsiMutualFriendsDTO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface PsiMutualFriendsMapper {

    @Select("select friend_nick_name, friend_head_image from im_friend where user_id = #{userId}")
    List<PsiMutualFriendsDTO> listById(Long userId);
}
