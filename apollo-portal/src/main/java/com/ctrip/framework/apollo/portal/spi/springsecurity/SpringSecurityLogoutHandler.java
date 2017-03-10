package com.ctrip.framework.apollo.portal.spi.springsecurity;

import com.ctrip.framework.apollo.portal.spi.LogoutHandler;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lepdou 2017-03-10
 */
public class SpringSecurityLogoutHandler implements LogoutHandler{

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response) {
    SecurityContextHolder.clearContext();;
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null){
      new SecurityContextLogoutHandler().logout(request, response, auth);
    }

    try {
      response.getWriter().write("logout seccuess!");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
