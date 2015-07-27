

    package com.springAOP.InitBeanconfigByXml;

    import org.junit.Test;

    import com.springAOP.Bean.Inteface.testAOPInterface;


    public class aopTest {

        @Test
    	public void aopTest1(){

            //通过IoC容器拿到配置好的实体Bean代理对象
            testAOPInterface aop = (testAOPInterface)ParseXmlConfigByIoC.getBean("shoutBean");

            //调用目标方法
            aop.shout();
	    }
    }
