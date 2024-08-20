package com.bx.implatform.controller;

import com.bx.implatform.dto.LoginDTO;
import com.bx.implatform.dto.PsiMutualFriendsDTO;
import com.bx.implatform.result.Result;
import com.bx.implatform.result.ResultUtils;
import com.bx.implatform.service.PsiMutualFriendsService;
import com.bx.implatform.vo.LoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "隐私求交")
@RestController
@RequestMapping("/message/private")
@RequiredArgsConstructor
@Slf4j
public class PsiMutualFriends {

    private final PsiMutualFriendsService psiMutualFriendsService;

    @PostMapping("/getmutualfriends")
    @ApiOperation(value = "共同好友交集", notes = "共同好友交集")
    public Result<List<PsiMutualFriendsDTO>> psi(Long userId1, Long userId2) {
        log.info("共同好友求交:{},{}",userId1,userId2);

        return ResultUtils.success(psiMutualFriendsService.psi(userId1,userId2));
    }
}
