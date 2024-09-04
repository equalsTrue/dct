package com.dct.model.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * AdminUserV)
 *
 * @author Vic on 2019/1/16
 */
@Data
public class AdminUserVO {

    private String id;

    private Date createTime;

    private String username;

    private String password;

    private String confirmPassword;

    private String oldPassword;

    private String email;

    private String workWeChatUserId;

    private List<String> roles;

    public Date getCreateTime() {
        if(null == this.createTime){
            return null;
        }
        return (Date) createTime.clone();
    }

    public void setCreateTime(Date createTime) {
        if(null == createTime) {
            this.createTime = null;
        }else{
            this.createTime = (Date) createTime.clone();
        }
    }
}
