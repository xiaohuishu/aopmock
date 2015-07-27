


    package com.springAOP.Advice.Interface;


    import com.springAOP.JoinPoint.ProceedingJoinPoint;


    /**
     *
     * 切面对象接口(声明Before,Around,After方法)
     */
    public interface BaseAdvice {

	    void Before();

    	Object Around(ProceedingJoinPoint joinPoint);

    	void After();

    }
