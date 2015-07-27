package com.springAOP.Bean;

import java.lang.reflect.Method;

/**
 * 封装好的切面信息Bean 包括： 切面(增强)Bean, 1个切点className,methodName 1个前置通知方法，1个环绕通知方法，1个后置通知方法
 * 
 * @author antsmarth
 * 
 */
public class AspectInfo {
	//定义私有变量
	private Object aspect = null;
	
	private String className = null;
	
	private String methodName = null;
	private Method beforeMethod = null;
	private Method aroundMethod = null;
	private Method afterMethod = null;
	
	public static class Builder {
		
		private final Object aspect;
		private final String className;
		private final String methodName;
		
		private Method beforeMethod = null;
		private Method aroundMethod = null;
		private Method afterMethod = null;
		
		
		public Builder(Object aspect, String className, String methodName) {
			this.aspect = aspect;
			this.className = className;
			this.methodName = methodName;
		}
		
		public Builder before(Method beforeMethod) {
			this.beforeMethod = beforeMethod;
			return this;
		}
		
		public Builder around(Method aroundMethod) {
			this.aroundMethod = aroundMethod;
			return this;
		}
		public Builder after(Method afterMethod) {
			this.afterMethod = afterMethod;
			return this;
		}
		
		public AspectInfo build() {
			return new AspectInfo(this);
		}
		
		
	}
	
	private AspectInfo(Builder builder) {
		this.aspect = builder.aspect;
		this.className = builder.className;
		this.methodName = builder.methodName;
		this.beforeMethod = builder.beforeMethod;
		this.aroundMethod = builder.aroundMethod;
		this.afterMethod = builder.afterMethod;
	}


	//为变量设置getter方法
	public String getClassName() {
		return className;
	}
	
	public String getMethodName() {
		return methodName;
	}

	public Object getAspect() {
		return aspect;
	}

	public Method getBeforeMethod() {
		return beforeMethod;
	}

	public Method getAroundMethod() {
		return aroundMethod;
	}

	public Method getAfterMethod() {
		return afterMethod;
	}

}
