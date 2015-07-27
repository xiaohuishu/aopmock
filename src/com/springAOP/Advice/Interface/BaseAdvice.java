package com.springAOP.Advice.Interface;

import com.springAOP.JoinPoint.ProceedingJoinPoint;

public interface BaseAdvice {

	void Before();
	
	
	Object Around(ProceedingJoinPoint joinPoint);
	
	
	void After();
	
}
