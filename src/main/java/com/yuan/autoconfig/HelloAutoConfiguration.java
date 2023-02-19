package com.yuan.autoconfig;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//配置轻量模式，每次拿一个新的对象，Configuration注册为配置类
@Configuration(proxyBeanMethods = false)
//HelloService类存在才会配置
@ConditionalOnClass(HelloService.class)
//自动注入HelloProperties属性类
@EnableConfigurationProperties(HelloProperties.class)
//属性匹配类，当配置文件中的值符合条件后才配置
@ConditionalOnProperty(prefix = "yuan.config", name = "flag", havingValue = "true", matchIfMissing = true)
public class HelloAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(HelloService.class)
	public HelloService processAutoConfigratioNation(HelloProperties helloProperties) {
		HelloService helloService = new HelloService();
		helloService.setName(helloProperties.getName());
		helloService.setAge(helloProperties.getAge());

		System.out.println("name:" + helloService.getName() + "," + "age:" + helloService.getAge());
		return helloService;
	}
}
