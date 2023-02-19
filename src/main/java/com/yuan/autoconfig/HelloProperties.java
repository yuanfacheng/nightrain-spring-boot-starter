package com.yuan.autoconfig;


import org.springframework.boot.context.properties.ConfigurationProperties;

//属性类，和配置文件进行映射
@ConfigurationProperties(prefix = "yuan.config")
public class HelloProperties {
	private String name = "默认值";

	private int age = 8;
	private int stage = 8;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
}
