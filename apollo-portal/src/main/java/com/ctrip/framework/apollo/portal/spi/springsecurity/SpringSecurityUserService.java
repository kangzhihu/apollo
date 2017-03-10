package com.ctrip.framework.apollo.portal.spi.springsecurity;

import com.google.common.collect.Lists;

import com.ctrip.framework.apollo.portal.component.config.PortalConfig;
import com.ctrip.framework.apollo.portal.entity.bo.UserInfo;
import com.ctrip.framework.apollo.portal.spi.UserService;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @author lepdou 2017-03-10
 */
public class SpringSecurityUserService implements UserService {

  @Autowired
  private PortalConfig portalConfig;

  @Override
  public List<UserInfo> searchUsers(String keyword, int offset, int limit) {
    Map<String, String> users = portalConfig.systemUsers();
    List<UserInfo> userInfos = Lists.newLinkedList();
    for (Map.Entry<String, String> user : users.entrySet()) {
      if (user.getKey().toLowerCase().contains(keyword.toLowerCase())) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getKey());
        userInfos.add(userInfo);
      }
    }

    return userInfos;
  }

  @Override
  public UserInfo findByUserId(String userId) {
    Map<String, String> users = portalConfig.systemUsers();
    if (users.containsKey(userId)) {
      UserInfo userInfo = new UserInfo();
      userInfo.setUserId(userId);
      return userInfo;
    }

    return null;
  }

  @Override
  public List<UserInfo> findByUserIds(List<String> userIds) {
    return null;
  }
}
