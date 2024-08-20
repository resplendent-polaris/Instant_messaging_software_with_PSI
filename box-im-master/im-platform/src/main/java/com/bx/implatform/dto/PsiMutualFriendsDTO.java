package com.bx.implatform.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("隐私求交DTO")
public class PsiMutualFriendsDTO {

    @ApiModelProperty(value = "好友名称")
    private String friendNickName;

    @ApiModelProperty(value = "好友头像")
    private String friendHeadImage;

    public String getUserName() {
        return friendNickName;
    }
}
