package com.ctrip.framework.apollo.portal.listener;

import org.springframework.context.ApplicationEvent;

/**
 * @author lepdou 2017-03-10
 */
public class ServerConfigChangeEvent extends ApplicationEvent {

  /**
   * Create a new ApplicationEvent.
   *
   * @param source the object on which the event initially occurred (never {@code null})
   */
  public ServerConfigChangeEvent(Object source) {
    super(source);
  }
}
