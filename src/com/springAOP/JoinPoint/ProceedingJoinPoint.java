

    package com.springAOP.JoinPoint;


    import java.lang.reflect.InvocationTargetException;
    import java.lang.reflect.Method;

    /**
     * 用于处理AOP代理链时，封装相关参数统一传递
     *
     * @author antsmarth
     *
     */
    public class ProceedingJoinPoint {

    	private Object target;// 被代理的对象
    	private Method method;// 被代理的方法
    	private Object[] args;// 方法的参数

    	//构造方法初始化变量
    	public ProceedingJoinPoint(Object target, Method method, Object[] args) {
    			this.target = target;
    			this.method = method;
    			this.args = args;
    	}

    	//执行目标函数
    	public Object execute(){
    		Object result = null;

    		try {
    			result = method.invoke(target, args);
    		} catch (IllegalAccessException | IllegalArgumentException
    				| InvocationTargetException e) {

    		    throw new RuntimeException("目标对象方法调用失败...");
    		}

    		return result;
    	}
    }
