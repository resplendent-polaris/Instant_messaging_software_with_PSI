package com.bx.implatform.service;

import com.bx.implatform.controller.PsiMutualFriends;
import com.bx.implatform.dto.PsiMutualFriendsDTO;

import java.util.List;

public interface PsiMutualFriendsService {

    List<PsiMutualFriendsDTO> psi(Long userId1, Long userId2);
}
