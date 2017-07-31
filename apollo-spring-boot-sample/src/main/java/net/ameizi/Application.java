package net.ameizi;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChange;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Application {

  @Autowired
  private JavaConfigSample javaConfigSample;

  private Config config = ConfigService.getAppConfig();

  // 监听属性值变化
  {
    config.addChangeListener(changeEvent -> {
      System.out.println("Changes for namespace " + changeEvent.getNamespace());
      for (String key : changeEvent.changedKeys()) {
        ConfigChange change = changeEvent.getChange(key);
        System.out.println(String.format("Found change - key: %s, oldValue: %s, newValue: %s, changeType: %s",
                                         change.getPropertyName(), change.getOldValue(), change.getNewValue(),
                                         change.getChangeType()));
      }
    });
  }

  /**
   * 使用apollo client api获取配置文件，apollo管控台修改配置后，实时更新
   */
  @RequestMapping("/clientapi")
  public Sample apolloApiClient() {
    int timeout = config.getIntProperty("sample.timeout", 0);
    int size = config.getIntProperty("sample.size", 0);
    return Sample.builder().timeout(timeout).size(size).build();
  }

  /**
   * Java Config方式，JavaConfigSample内部使用了Config API，属性值的修改会立即生效
   */
  @RequestMapping("/javaconfig")
  public JavaConfigSample commonProperties() {
    return javaConfigSample;
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
