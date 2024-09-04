package com.dct.security;

import com.dct.model.dct.AdminUserModel;
import com.dct.repo.security.AdminUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * 登陆身份认证
 * @author: JoeTao
 * createAt: 2018/9/14
 */
@Component(value="CustomUserDetailsService")
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AdminUserRepo adminUserRepo;

    @Override
    public UserDetail loadUserByUsername(String name) throws UsernameNotFoundException {
        UserDetail userDetail = new UserDetail();

        AdminUserModel adminUser = adminUserRepo.findFirstByUsername(name);
        if (adminUser == null) {
            throw new UsernameNotFoundException(String.format("No userDetail found with username '%s'.", name));
        }
        userDetail.setId(String.valueOf(adminUser.getId()));
        userDetail.setUsername(adminUser.getUsername());
        userDetail.setPassword(adminUser.getPassword());

        return userDetail;
    }
}
