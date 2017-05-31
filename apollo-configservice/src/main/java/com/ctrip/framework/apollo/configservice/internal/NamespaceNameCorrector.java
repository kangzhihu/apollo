package com.ctrip.framework.apollo.configservice.internal;

import com.ctrip.framework.apollo.common.entity.AppNamespace;
import com.ctrip.framework.apollo.configservice.service.AppNamespaceServiceWithCache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author lepdou 2017-05-27
 */
@Component
public class NamespaceNameCorrector {

  @Autowired
  private AppNamespaceServiceWithCache appNamespaceServiceWithCache;


  public String correct(String appId, String namespaceName) {
    AppNamespace appNamespace = appNamespaceServiceWithCache.findByAppIdAndNamespaceIgnoreCase(appId, namespaceName);
    if (appNamespace != null) {
      return appNamespace.getName();
    }

    appNamespace = appNamespaceServiceWithCache.findPublicNamespaceByNameIgnoreCase(namespaceName);
    if (appNamespace != null) {
      return appNamespace.getName();
    }

    return namespaceName;
  }
}
