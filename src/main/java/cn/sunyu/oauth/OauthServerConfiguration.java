package cn.sunyu.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.DefaultUserApprovalHandler;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * @Author: hrqx
 * @Date: 2019/4/3 14:53
 */
@Configuration
@EnableAuthorizationServer
public class OauthServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        ///TODO 大坑 通过表单提交和不通过表单提交过滤器不同，验证客户端方式也不同！！不通过表单需要在请求头添加参数Authorization:basic clientId:clientSecret的base64加密
        security.allowFormAuthenticationForClients();
        ///TODO获取token（oauth/token）接口权限 必须认定后才可以
        security.tokenKeyAccess("isAuthenticated()");
        ///TODO获取token（oauth/check_token）接口权限 必须认定后才可以
        security.checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients)
            throws Exception {
        //JdbcClientDetailsService可以动态管理数据库中客户端数据
        //http://localhost:8080/oauth/authorize?client_id=testclientid&redirect_uri=http://localhost:9001/authCodeCallback&response_type=code&scope=read_userinfo
        clients.jdbc(dataSource);
    }


    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {

        Collection<? extends GrantedAuthority> authorities = new HashSet<>();
        ArrayList<UserDetails> userDetails = new ArrayList<>();
        User user1 = new User("sunyu", "ceshi", authorities);
        User user2 = new User("sun2", "sun2", authorities);
        User user3 = new User("sun555", "sun555", authorities);
        userDetails.add(user1);
        userDetails.add(user2);
        userDetails.add(user3);
        //加载客户端信息方式
        endpoints.setClientDetailsService(new JdbcClientDetailsService(dataSource));
        endpoints.userApprovalHandler(new DefaultUserApprovalHandler());
        //加载用户信息方式，可以改为jdbc加载
        endpoints.userDetailsService(new InMemoryUserDetailsManager(userDetails));
        endpoints.tokenStore(new JdbcTokenStore(dataSource));
    }
}
