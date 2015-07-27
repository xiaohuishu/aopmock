


    package com.springAOP.Proxy.ProxyHandler;



    import java.lang.reflect.InvocationHandler;
    import java.lang.reflect.Method;
    import java.lang.reflect.Proxy;
    import java.util.*;
    import java.util.Map.Entry;
    import java.util.regex.Matcher;
    import java.util.regex.Pattern;

    import com.springAOP.Bean.AspectInfo;
    import com.springAOP.JoinPoint.ProceedingJoinPoint;


    /**
     * 代理类，采用JDK1.5的动态代理实现，要求是前提被代理对象实现接口
     *
     * @author antsmarth
     *
     */
    public class ProxyHandler implements InvocationHandler {

	    // 存储所有的切面对象
	    private static Map<String, AspectInfo> aspectinfo = new HashMap<String, AspectInfo>();

    	private static boolean isAllFlag = false;

	    // 被代理的对象
	    private Object target = null;

	    public ProxyHandler() {

	    }

    	public ProxyHandler(Object target) {
    		this.target = target;
    	}

    	public static void addAspectinfo(AspectInfo aspect, boolean isAll) {

	    	aspectinfo.put(aspect.getClassName() + "." + aspect.getMethodName(),
	    			aspect);
            //标识是否是指定切入点类,还是只指定切入方法;
	    	isAllFlag = isAll;
    	}

    	// 获取代理的实例
    	public Object getInstanceByProxy() {
    		if (target == null) {
    			return null;
    		} else {

    			return Proxy.newProxyInstance(target.getClass().getClassLoader(),
    					target.getClass().getInterfaces(), this);

    		}
    	}

    	// 获取代理的实例通过传入目标对象
    	public Object getInstanceByProxy(Object target) {

            if (target == null) {

                return null;

            } else {

                return Proxy.newProxyInstance(target.getClass().getClassLoader(),
				    	target.getClass().getInterfaces(), this);
	    	}
    	}

    	/**
    	 * 代理执行方法，动态调用目标对象(被代理对象)的方法
    	 */
    	@Override
    	public Object invoke(Object proxy, Method method, Object[] args) {

    		Set<Entry<String, AspectInfo>> entrySet = aspectinfo.entrySet();

    		if (isAllFlag) {

    			try {
    				return invokeAllClass(entrySet, method, args);
    			} catch (Exception e) {
    				throw new RuntimeException("请检查before,around,after方法是否错误...");
    			}

    		} else {

    			try {
    				return invokeDesignClass(method, args);
    			} catch (Exception e) {
    				throw new RuntimeException("请检查before,around,after方法是否错误...");
    			}

    		}

    	}

        /**
         * 如果是指定了切入点类,直接获取切面对象;
         * 调用before,around,after方法;
         */
    	private Object invokeDesignClass(Method method, Object[] args) throws Exception {

    		Object result = null;

            //获取封装对象
    		AspectInfo aspect = aspectinfo.get(target.getClass().getName() + "."
    					+ method.getName());

            //获取切面对象
    		Object adviceBean = aspect.getAspect();

            //执行before方法
    		if(aspect.getBeforeMethod() != null)
    			aspect.getBeforeMethod().invoke(adviceBean, null);

            //执行around和目标方法
    		Object aroundObject = target;
    		Method aroundMethod = method;
    		Object[] aroundParames = args;

    		if (aspect.getAroundMethod() != null) {
    			aroundParames = new Object[] { new ProceedingJoinPoint(
    					aroundObject, aroundMethod, aroundParames) };
    			aroundObject = adviceBean;
    			aroundMethod = aspect.getAroundMethod();
    		}

    		result = aroundMethod.invoke(aroundObject, aroundParames);

            //执行after方法
    		if(aspect.getAfterMethod() != null)
    			aspect.getAfterMethod().invoke(adviceBean, null);

            //返回目标方法结果
    		return result;
    	}

        /**
         *
         * 如果没指定切入点类:
         *      首先通过匹配指定切入点方法,找到符合这个包含切入点方法所有包装
         *      实体类对象
         *
         *      对所有的对象进行before,around,after方法调用处理
         *
         */
	    private Object invokeAllClass(Set<Entry<String, AspectInfo>> entrySet,
		    	Method method, Object[] args) throws Exception {

	    	Object result = null;

		    List<AspectInfo> aspectlist = findMatchAspectInfo(entrySet, method);

		    // 执行前置增强，通知
    		for (AspectInfo aspect : aspectlist) {
    			Object adviceBean = aspect.getAspect();
    			if (aspect.getBeforeMethod() != null) {
    				aspect.getBeforeMethod().invoke(adviceBean, null);
    			}
    		}
    		// 执行环绕通知
    		Object aroundObject = target;
    		Method aroundMethod = method;
    		Object[] aroundParames = args;
    		for (AspectInfo aspect : aspectlist) {
    			Object adviceBean = aspect.getAspect();
    			if (aspect.getAroundMethod() != null) {
    				aroundParames = new Object[] { new ProceedingJoinPoint(
    						aroundObject, aroundMethod, aroundParames) };
    				aroundObject = adviceBean;
    				aroundMethod = aspect.getAroundMethod();
    			}
    		}
    		result = aroundMethod.invoke(aroundObject, aroundParames);

    		// 执行After通知增强
    		for (AspectInfo aspect : aspectlist) {
    			Object adviceBean = aspect.getAspect();
    			if (aspect.getAfterMethod() != null) {
    				aspect.getAfterMethod().invoke(adviceBean, null);
    			}
    		}
    		return result;

    	}

        /**
         *  通过指定切入点方法,匹配所有配置的包装实体Bean;
         *
         */
    	private List<AspectInfo> findMatchAspectInfo(Set<Entry<String, AspectInfo>> entrySet, Method method) {

            List<AspectInfo> aspectlist = new ArrayList<AspectInfo>();
    		// 遍历所有的切面列表，找到对应切入点的切面
    		for (Entry<String, AspectInfo> entry : entrySet) {

                String expression = entry.getKey();
	    		AspectInfo aspect = entry.getValue();
		    	Object adviceBean = aspect.getAspect();

			    Pattern pattern = Pattern.compile(expression);

		    	Matcher matcher = pattern.matcher(target.getClass().getName() + "."
	    				+ method.getName());

	    		if (matcher.find()) {
	    			AspectInfo aspectChild = new AspectInfo.Builder(adviceBean,
	    					aspect.getClassName(), aspect.getMethodName())
	    					.before(aspect.getBeforeMethod())
	    					.around(aspect.getAroundMethod())
	    					.after(aspect.getAfterMethod()).build();

    				aspectlist.add(aspectChild);
    			}

    		}

    		return aspectlist;
    	}

    }


