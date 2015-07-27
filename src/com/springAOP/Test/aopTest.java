package com.springAOP.InitBeanconfigByXml;

import org.junit.Test;

import com.springAOP.Bean.Inteface.testAOPInterface;


public class aopTest {
	@Test
	public void aopTest1(){
		testAOPInterface aop = (testAOPInterface)ParseXmlConfigByIoC.getBean("shoutBean");
		aop.shout();
	}
}
