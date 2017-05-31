package com.ctrip.framework.apollo.configservice.internal;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.ctrip.framework.apollo.core.dto.ApolloConfigNotification;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.Map;

/**
 * @author lepdou 2017-05-27
 */
public class DeferredResultWrapper {

  private static final long TIMEOUT = 30 * 1000;//30 seconds
  private static final ResponseEntity<List<ApolloConfigNotification>>
      NOT_MODIFIED_RESPONSE_LIST = new ResponseEntity<>(HttpStatus.NOT_MODIFIED);

  private Map<String, String> correctedNamespaceNameMapOriginal;
  private DeferredResult<ResponseEntity<List<ApolloConfigNotification>>> result;


  public DeferredResultWrapper() {
    correctedNamespaceNameMapOriginal = Maps.newHashMap();
    result = new DeferredResult<>(TIMEOUT, NOT_MODIFIED_RESPONSE_LIST);
  }

  public void recordNamespaceNameCorrectResult(String originalNamespaceName, String correctedNamespaceName) {
    correctedNamespaceNameMapOriginal.put(correctedNamespaceName, originalNamespaceName);
  }

  public String getOriginalNamespaceName(String correctedNamespaceName) {
    String originalNamespaceName = correctedNamespaceNameMapOriginal.get(correctedNamespaceName);

    return originalNamespaceName != null ? originalNamespaceName : correctedNamespaceName;
  }

  public void onTimeout(Runnable timeoutCallback) {
    result.onTimeout(timeoutCallback);
  }

  public void onCompletion(Runnable completionCallback) {
    result.onCompletion(completionCallback);
  }

  public void setResult(String changedNamespace, long newMegId) {
    String originalNamespaceName = correctedNamespaceNameMapOriginal.get(changedNamespace);
    originalNamespaceName = originalNamespaceName == null ? changedNamespace : originalNamespaceName;

    setResult(Lists.newArrayList(new ApolloConfigNotification(originalNamespaceName, newMegId)));
  }

  public void setResult(List<ApolloConfigNotification> notifications) {
    result.setResult(new ResponseEntity<>(notifications, HttpStatus.OK));
  }

  public boolean isSetOrExpired() {
    return result.isSetOrExpired();
  }

  public DeferredResult<ResponseEntity<List<ApolloConfigNotification>>> getResult() {
    return result;
  }
}
