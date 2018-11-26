package com.yeta.chat;

import org.directwebremoting.spring.DwrSpringServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
//导入dwr的配置文件
@ImportResource(locations = "classpath:config/dwrConfig.xml")
public class ChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatApplication.class, args);
    }

    /**
     * 配置dwr Servlet
     * 重点注意取名
     * @return
     */
    @Bean
    public ServletRegistrationBean dwrServlet() {
        DwrSpringServlet servlet = new DwrSpringServlet();
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(servlet, "/dwr/*");
        registrationBean.addInitParameter("debug", "true");
        //使用服务器反转Ajax
        registrationBean.addInitParameter("activeReverseAjaxEnabled", "true");
        //能够从其他域请求true：开启； false：关闭
        registrationBean.addInitParameter("crossDomainSessionSecurity", "false");
        //允许远程调用JS
        registrationBean.addInitParameter("allowScriptTagRemoting", "true");
        return registrationBean;
    }
}
