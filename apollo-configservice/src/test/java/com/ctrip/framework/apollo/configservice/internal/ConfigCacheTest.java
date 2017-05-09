package com.ctrip.framework.apollo.configservice.internal;

import com.ctrip.framework.apollo.biz.config.BizConfig;
import com.ctrip.framework.apollo.biz.entity.Release;
import com.ctrip.framework.apollo.biz.service.ReleaseService;
import com.ctrip.framework.apollo.biz.utils.MockBeanFactory;
import com.ctrip.framework.apollo.configservice.internal.impl.DefaultConfigCache;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConfigCacheTest {

  @Mock
  private ReleaseService releaseService;
  @Mock
  private BizConfig bizConfig;
  @InjectMocks
  private DefaultConfigCache configCache;

  private String appId = "appId";
  private String cluster = "cluster";
  private String namespace = "namespace";
  private long releaseId = 1;
  private String releaseKey = "releaseKey";


  @Test
  public void testFirstGetMissAndSecondGetHit() {

    when(bizConfig.configCacheSize()).thenReturn(1000);
    when(bizConfig.configCacheExpireDuration()).thenReturn(5 * 60);

    configCache.init();

    Release result = MockBeanFactory.mockRelease(releaseId, releaseKey, appId, cluster, namespace, null);

    when(releaseService.findLatestActiveRelease(appId, cluster, namespace)).thenReturn(result);

    Release firstGetResult = configCache.get(appId, cluster, namespace);
    Assert.assertNotNull(firstGetResult);
    Assert.assertEquals(releaseKey, firstGetResult.getReleaseKey());
    verify(releaseService).findLatestActiveRelease(appId, cluster, namespace);

    Release secondGetResult = configCache.get(appId, cluster, namespace);
    Assert.assertNotNull(secondGetResult);
    Assert.assertEquals(releaseKey, secondGetResult.getReleaseKey());
    verify(releaseService, times(1)).findLatestActiveRelease(appId, cluster, namespace);
  }

  @Test
  public void testCacheExpire() throws InterruptedException {
    when(bizConfig.configCacheSize()).thenReturn(1000);
    when(bizConfig.configCacheExpireDuration()).thenReturn(5);

    configCache.init();

    Release result = MockBeanFactory.mockRelease(releaseId, releaseKey, appId, cluster, namespace, null);
    when(releaseService.findLatestActiveRelease(appId, cluster, namespace)).thenReturn(result);

    Release firstGetResult = configCache.get(appId, cluster, namespace);
    Assert.assertNotNull(firstGetResult);
    Assert.assertEquals(releaseKey, firstGetResult.getReleaseKey());
    verify(releaseService).findLatestActiveRelease(appId, cluster, namespace);

    TimeUnit.SECONDS.sleep(6);

    Release secondGetResult = configCache.get(appId, cluster, namespace);
    Assert.assertNotNull(secondGetResult);
    Assert.assertEquals(releaseKey, secondGetResult.getReleaseKey());
    verify(releaseService, times(2)).findLatestActiveRelease(appId, cluster, namespace);
  }

  @Test
  public void testCacheInvalidate() {
    when(bizConfig.configCacheSize()).thenReturn(1000);
    when(bizConfig.configCacheExpireDuration()).thenReturn(1);

    configCache.init();

    Release result = MockBeanFactory.mockRelease(releaseId, releaseKey, appId, cluster, namespace, null);
    when(releaseService.findLatestActiveRelease(appId, cluster, namespace)).thenReturn(result);

    Release firstGetResult = configCache.get(appId, cluster, namespace);
    Assert.assertNotNull(firstGetResult);
    Assert.assertEquals(releaseKey, firstGetResult.getReleaseKey());
    verify(releaseService).findLatestActiveRelease(appId, cluster, namespace);

    configCache.invalidate(appId, cluster, namespace);
    Release secondGetResult = configCache.get(appId, cluster, namespace);
    Assert.assertNotNull(secondGetResult);
    Assert.assertEquals(releaseKey, secondGetResult.getReleaseKey());
    verify(releaseService, times(2)).findLatestActiveRelease(appId, cluster, namespace);
  }

  @Test
  public void testMultiThreadGet() throws InterruptedException {
    when(bizConfig.configCacheSize()).thenReturn(1000);
    when(bizConfig.configCacheExpireDuration()).thenReturn(3000);

    configCache.init();

    Release result = MockBeanFactory.mockRelease(releaseId, releaseKey, appId, cluster, namespace, null);
    when(releaseService.findLatestActiveRelease(appId, cluster, namespace)).thenReturn(result);

    int threadCount = 100;
    CountDownLatch latch = new CountDownLatch(threadCount);
    for (int i = 0; i < threadCount; i++) {
      new Thread(() -> {
        Release release = configCache.get(appId, cluster, namespace);
        Assert.assertNotNull(release);
        Assert.assertEquals(releaseKey, release.getReleaseKey());
        latch.countDown();
      }).start();
    }

    latch.await();

    verify(releaseService).findLatestActiveRelease(appId, cluster, namespace);
  }

  @Test
  public void testMultiThreadGetWithExpireOneTime() throws InterruptedException {
    when(bizConfig.configCacheSize()).thenReturn(1000);
    when(bizConfig.configCacheExpireDuration()).thenReturn(1);

    configCache.init();

    Release result = MockBeanFactory.mockRelease(releaseId, releaseKey, appId, cluster, namespace, null);
    when(releaseService.findLatestActiveRelease(appId, cluster, namespace)).thenReturn(result);
    int threadCount = 10;
    CountDownLatch latch = new CountDownLatch(threadCount);
    for (int i = 0; i < threadCount; i++) {
      new Thread(() -> {
        Release release = configCache.get(appId, cluster, namespace);
        Assert.assertNotNull(release);
        Assert.assertEquals(releaseKey, release.getReleaseKey());
        latch.countDown();
      }).start();
    }

    latch.await();
    //expire
    sleep(1000);

    Release secondGetResult = configCache.get(appId, cluster, namespace);
    Assert.assertNotNull(secondGetResult);
    Assert.assertEquals(releaseKey, secondGetResult.getReleaseKey());

    verify(releaseService, times(2)).findLatestActiveRelease(appId, cluster, namespace);
  }

  private void sleep(int milliseconds) {
    try {
      TimeUnit.MILLISECONDS.sleep(milliseconds);
    } catch (InterruptedException e) {
    }
  }

}
