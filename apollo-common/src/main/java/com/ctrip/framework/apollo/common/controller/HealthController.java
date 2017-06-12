package com.ctrip.framework.apollo.common.controller;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

/**
 * @author lepdou 2017-06-06
 */
@RestController
public class HealthController {

  private Health health;

  @PostConstruct
  public void init() {
    Health.Builder builder = new Health.Builder();
    health = builder.status(Status.UP).build();
  }

  @RequestMapping(value = "/health", method = RequestMethod.GET)
  public ResponseEntity<Health> health() {
    return ResponseEntity.ok(health);
  }

}
