package com.springAOP.Bean;

import com.springAOP.Bean.Inteface.testAOPInterface;


/**
 * 测试AOP功能 采用JDK1.5自带的动态代理功能，故需继承接口方法，或者可以使用第三方的Jar包cglib.jar
 * 
 * @author antsmarth
 * 
 */
public class testAOPBean implements testAOPInterface {

	private String name = "aop-test";
	private int test = 1;

	public int getTest() {
		return test;
	}

	public void setTest(int test) {
		this.test = test;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int shout() {
		System.out.println("hello! my name is " + name +" test : "+test);
		return 1;
	}

}
