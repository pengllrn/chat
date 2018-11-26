package com.yeta.chat.stomp;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 自定义登陆验证，实现UserDetailsService
 * @author YETA
 * @date 2018/11/26/21:08
 */
@Service
public class CommonUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;
        if (username.equals("yeta1")) {
            user = new User("A01", "yeta1", new BCryptPasswordEncoder().encode("yeta1"), "USER");
        } else if (username.equals("yeta2")) {
            user = new User("A02", "yeta2", new BCryptPasswordEncoder().encode("yeta2"), "USER");
        } else {
            user = new User();
        }
        return user;
    }
}