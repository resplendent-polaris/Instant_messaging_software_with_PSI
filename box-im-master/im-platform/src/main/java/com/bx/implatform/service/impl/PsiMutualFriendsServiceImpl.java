package com.bx.implatform.service.impl;

import com.bx.implatform.controller.PsiMutualFriends;
import com.bx.implatform.dto.PsiMutualFriendsDTO;
import com.bx.implatform.mapper.PsiMutualFriendsMapper;
import com.bx.implatform.service.PsiMutualFriendsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor


public class PsiMutualFriendsServiceImpl implements PsiMutualFriendsService {

    private final PsiMutualFriendsMapper psiMutualFriendsMapper;


    @Override
    public List<PsiMutualFriendsDTO> psi(Long userId1, Long userId2){
        List<PsiMutualFriendsDTO> list1 = psiMutualFriendsMapper.listById(userId1);
        List<PsiMutualFriendsDTO> list2 = psiMutualFriendsMapper.listById(userId2);

        Map<String, PsiMutualFriendsDTO> map = new HashMap<>();

        // 将第一个列表存入 HashMap，以用户名为键，PsiMutualFriendsDTO 对象为值
        for (PsiMutualFriendsDTO friend : list1) {
            map.put(friend.getUserName(), friend);
        }

        List<PsiMutualFriendsDTO> mutualFriends = new ArrayList<>();

        // 遍历第二个列表，检查是否存在于 HashMap 中
        for (PsiMutualFriendsDTO friend : list2) {
            if (map.containsKey(friend.getUserName())) {
                mutualFriends.add(friend); // 添加到共同朋友列表
            }
        }

        return mutualFriends ;
    }
}
