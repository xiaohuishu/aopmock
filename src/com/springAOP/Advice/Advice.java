


    package com.springAOP.Advice;

    import com.springAOP.Advice.Interface.BaseAdvice;
    import com.springAOP.JoinPoint.ProceedingJoinPoint;



    /**
     * 切面类(增强类)
     * 用于测试AOP功能
     * @author antsmarth
     *
     */
    public class Advice implements BaseAdvice{
	    /**
	     * 前置通知
	     */
    	public void Before(){
    		System.out.println("Before it to do ....");
    	}
    	/**
    	 * 环绕通知,通过切入点执行调用目标对象指定方法
	     */
    	public Object Around(ProceedingJoinPoint joinPoint){
    		System.out.println("Before-around ...");
    		Object object = joinPoint.execute();
    		System.out.println("After-around ...");
    		return object;
    	}
    	/**
    	 * 后置通知
    	 */
    	public void After(){
    		System.out.println("After it to do over ...");
    	}
    }
