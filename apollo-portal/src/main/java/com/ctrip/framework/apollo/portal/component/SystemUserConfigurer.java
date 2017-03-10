package com.ctrip.framework.apollo.portal.component;

import com.ctrip.framework.apollo.common.event.ServerConfigRefreshedEvent;
import com.ctrip.framework.apollo.portal.component.config.PortalConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(99)
public class SystemUserConfigurer extends WebSecurityConfigurerAdapter {

  private static final String USER_ROLE = "user";

  @Autowired
  private PortalConfig portalConfig;

  private AuthenticationManagerBuilder auth;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.httpBasic();
    http.csrf().disable();
    http.headers().frameOptions().sameOrigin();
    http.authorizeRequests().anyRequest().hasAnyRole(USER_ROLE);
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    this.auth = auth;
    initSystemUsers();
  }

  @EventListener
  public void onServerConfigRefreshed(ServerConfigRefreshedEvent event) throws Exception {
    initSystemUsers();
  }

  private void initSystemUsers() throws Exception {
    if (auth == null) {
      return;
    }

    Map<String, String> users = portalConfig.systemUsers();
    if (users.isEmpty()) {
      return;
    }

    for (Map.Entry<String, String> user : users.entrySet()) {
      auth.inMemoryAuthentication().withUser(user.getKey())
          .password(user.getValue())
          .roles(USER_ROLE);
    }
  }

}
