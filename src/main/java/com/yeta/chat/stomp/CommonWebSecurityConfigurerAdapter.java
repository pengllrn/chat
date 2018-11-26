package com.yeta.chat.stomp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * spring security配置类
 * @author YETA
 * @date 2018/11/23/0:19
 */
@Configuration
@EnableWebSecurity
public class CommonWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Autowired
    private CommonUserDetailsService commonUserDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //内存中分配用户，spring security自己验证
        /*auth.inMemoryAuthentication()
                .passwordEncoder(new BCryptPasswordEncoder())
                .withUser("yeta1").password(new BCryptPasswordEncoder().encode("yeta1")).roles("USER")
                .and()
                .withUser("yeta2").password(new BCryptPasswordEncoder().encode("yeta2")).roles("USER");*/
        //自定义登陆验证，可以通过数据库来验证
        auth.userDetailsService(commonUserDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //设置/resources/static目录下的静态资源不拦截
        web.ignoring().antMatchers("/resources/static/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest()
                .authenticated()       //所有请求需要认证
                .and()
                .formLogin()
                .defaultSuccessUrl("/slogin", true)      //登陆验证成功路径
                .failureUrl("/flogin")       //登陆验证失败路径
                .permitAll();
        http.csrf()
                .disable();
    }
}
