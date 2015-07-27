package com.springAOP.InitBeanconfigByXml;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.springAOP.Bean.AspectInfo;
import com.springAOP.JoinPoint.ProceedingJoinPoint;
import com.springAOP.Proxy.ProxyHandler.ProxyHandler;

//import com.JavaPersistence.util.annotationUtil;
/**
 * 解析beans XML的工具类，得到bean对象，和AOP切面对象
 * 
 * @author antsmarth
 * 
 */
public class ParseXmlConfigByIoC {
	// 不能实例化
	private ParseXmlConfigByIoC() {

	}

	private static Element rootElement = null;
	private static SAXReader saxReader = null;
	private static String pathName = "beans.xml";
	// private static annotationUtil autil = null;
	// 存储所有的bean实体类的Map集合
	private static Map<String, Object> beans = new HashMap<String, Object>();
	/**
	 * 静态块，进行初始化得到beans.xmlroot节点，得到beans集合
	 */
	static {
		// autil = new annotationUtil();
		saxReader = new SAXReader();
		try {

			rootElement = getRootElementByXml(pathName);
			initBeans();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	/**
	 * 通过xml的名称得到xml文件的rootElement节点
	 * 
	 * @param pathName2
	 *            : xml的名称
	 * @return
	 * @throws Exception
	 */
	private static Element getRootElementByXml(String pathName2)
			throws Exception {
		Element rootElement = null;
		InputStream in = ParseXmlConfigByIoC.class.getClassLoader()
				.getResourceAsStream(pathName2);
		// System.out.println(in);
		Document doc = null;
		try {
			doc = saxReader.read(in);
		} catch (DocumentException e) {

			e.printStackTrace();
		}

		rootElement = doc.getRootElement();

		if (rootElement == null)
			throw new Exception("rootElement为空！！");

		return rootElement;

	}

	/**
	 * 初始化beans Map集合和切面信息对象，
	 */
	private static void initBeans() {
		@SuppressWarnings("unchecked")
		Iterator<Element> iterElement = getRootElement().elementIterator();
		while (iterElement.hasNext()) {
			Element element = iterElement.next();
			if (element.getName().equals("bean")) {
				beans.put(element.attributeValue("id"), initBean(element));
			} else if (element.getName().equals("aop-config")) {
				// System.out.println(element.asXML());
				initAOP(element);

			}

		}

	}

	/**
	 * 根据传入的aop配置的节点信息，来得到切面信息
	 * 
	 * @param element
	 *            ： AOP的配置节点信息
	 */
	private static void initAOP(Element element) {
		@SuppressWarnings("unchecked")
		List<Element> elements = element.elements();
		for (Element elementchild : elements) {
			if (elementchild.getName().equals("aspect")) {
				initAspect(elementchild);
			}
		}
	}

	/**
	 * aop配置节点的切面节点信息初始化
	 * 
	 * @param aspectelement
	 *            ： aspect详细节点信息
	 */
	private static void initAspect(Element aspectelement) {
		String aspectRef = aspectelement.attributeValue("ref");
		Object aspect = null;
		Method beforeMethod = null;
		Method aroundMethod = null;
		Method afterMethod = null;
		if (beans.containsKey(aspectRef)) {
			aspect = beans.get(aspectRef);
		} else {
			Element element = getElementById(aspectRef);
			if (element.getName().equals("bean")) {
				aspect = initBean(element);
			}
		}
		Element pointcutElement = getElementByName(aspectelement, "pointcut");

		if(pointcutElement.attribute("method") == null)
			throw new RuntimeException("请配置好切入点方法...");
		
		@SuppressWarnings("unchecked")
		List<Element> aspectElements = aspectelement.elements();
		
		// 获取通知的方法：before around after 等方法
		for (Element aspectchild : aspectElements) {
			String methodName = aspectchild.attributeValue("method");
			try {
				if (aspectchild.getName().equals("before")) {
					beforeMethod = aspect.getClass().getMethod(methodName);
				} else if (aspectchild.getName().equals("around")) {
					aroundMethod = aspect.getClass().getMethod(methodName,
							ProceedingJoinPoint.class);
				} else if (aspectchild.getName().equals("after")) {
					afterMethod = aspect.getClass().getMethod(methodName);
				}
			} catch (Exception e) {
				throw new RuntimeException("检查切面是否有此方法...");
			}
		}

		

		// 实例化切面信息，将其加入到代理类中
		
		if (pointcutElement.attribute("interceptObj") == null) {
			
			AspectInfo aspectinfo = new AspectInfo.Builder(aspect,
					".*",
					pointcutElement.attributeValue("method"))
					.before(beforeMethod).around(aroundMethod)
					.after(afterMethod).build();
			
			ProxyHandler.addAspectinfo(aspectinfo, true);
			
		}

		else {
			AspectInfo aspectinfo = new AspectInfo.Builder(aspect,
					pointcutElement.attributeValue("interceptObj"),
					pointcutElement.attributeValue("method"))
					.before(beforeMethod).around(aroundMethod)
					.after(afterMethod).build();
			
			ProxyHandler.addAspectinfo(aspectinfo, false);

		}

	}

	/**
	 * 根据名称在根据根节点找到子节点
	 * 
	 * @param aspect
	 *            : 根节点
	 * @param elementName
	 *            ： 子节点名称
	 * @return
	 */
	private static Element getElementByName(Element aspect, String elementName) {
		Element element = null;
		@SuppressWarnings("unchecked")
		List<Element> elements = aspect.elements();
		for (Element elementchild : elements) {
			if (elementchild.getName().equals(elementName)) {
				element = elementchild;
				break;
			}
		}

		return element;
	}

	/**
	 * 根据Bean节点来初始化得到实体类对象加入到beans集合
	 * 
	 * @param element
	 *            ： bean节点
	 * @return
	 */
	private static Object initBean(Element element) {
		Class<?> targetClass = null;
		Object target = null;
		try {
			targetClass = Class.forName(element.attributeValue("class"));
			target = targetClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 如果加载的bean为基本类型bean
		if (isPrimitive(element.attributeValue("class"))
				&& element.attribute("value") != null) {
			target = getInstanceOfName(element.attributeValue("class"),
					element.attributeValue("value"));

			return target;
		}
		@SuppressWarnings("unchecked")
		List<Element> elements = element.elements();
		for (Element elementchild : elements) {
			if (elementchild.getName().equals("property")) {
				String propertyName = elementchild.attributeValue("name");
				Method[] methods = targetClass.getMethods();
				Method writerMethod = null;
				for (int i = 0; i < methods.length; i++) {
					if (methods[i].getName().equalsIgnoreCase(
							"set" + propertyName)) {
						writerMethod = methods[i];
					}
				}
				Object value = null;// 属性值
				if (elementchild.attribute("value") != null) {
					String propertyValue = elementchild.attributeValue("value");
					Class<?>[] types = writerMethod.getParameterTypes();

					value = getInstanceOfName(types[0].getName(), propertyValue);
				} else if (elementchild.attribute("ref") != null) {
					String refId = elementchild.attributeValue("ref");
					if (beans.containsKey(refId)) {
						value = beans.get(refId);
					} else {
						Element elementref = getElementById(refId);
						if (elementref.getName().equals("bean")) {
							value = initBean(elementref);
						}
					}
				}
				try {
					writerMethod.invoke(target, value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (target.getClass().getInterfaces().length == 0) {
			return target;
		} else {
			// 返回经过代理的bean
			return new ProxyHandler(target).getInstanceByProxy();
		}

	}

	/**
	 * 根据id类获取beans的实体对象
	 * 
	 * @param id
	 *            ： 节点中id属性的名称
	 * @return bean
	 */
	public static Object getBean(String id) {
		Object bean = beans.get(id);
		if (bean == null) {
			Element element = getElementById(id);
			if (element.getName().equals("bean")) {
				bean = initBean(element);
			} else 
				throw new RuntimeException("beans.xml中没有配置此对象...");
		}
		return bean;
	}

	/**
	 * 获取beans.xml rootElement节点
	 * 
	 * @return rootElement
	 */
	public static Element getRootElement() {
		return rootElement;
	}

	/**
	 * 根据id名称来找到节点Element
	 * 
	 * @param id
	 * @return： 节点中id属性的名称
	 */
	public static Element getElementById(String id) {
		Element element = null;
		@SuppressWarnings("unchecked")
		List<Element> elementLists = rootElement.elements();
		for (Element elementchild : elementLists) {
			if (elementchild.attributeValue("id").equals(id)) {
				element = elementchild;
				break;
			}
		}
		return element;
	}

	/**
	 * 通过字符串反射类型，增加了对基本数据类型的封装
	 * 
	 * @param name
	 *            ：类型名
	 * @param value
	 *            ：值
	 * @return
	 */
	private static Object getInstanceOfName(String name, String value) {
		Class<?> clazz = NameToClass(name);

		Object object = null;
		try {
			object = clazz.getConstructor(String.class).newInstance(value);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return object;
	}

	/**
	 * 通过类型名来获取包装器的名称
	 * 
	 * @param name
	 *            : 类型名
	 * @return
	 */
	private static Class<?> NameToClass(String name) {
		Class<?> clazz = null;
		if (name.equals("int")) {
			clazz = Integer.class;
		} else if (name.equals("char")) {
			clazz = Character.class;
		} else if (name.equals("boolean")) {
			clazz = Boolean.class;
		} else if (name.equals("short")) {
			clazz = Short.class;
		} else if (name.equals("long")) {
			clazz = Long.class;
		} else if (name.equals("float")) {
			clazz = Float.class;
		} else if (name.equals("double")) {
			clazz = Double.class;
		} else if (name.equals("byte")) {
			clazz = Byte.class;
		} else {
			try {
				clazz = Class.forName(name);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		return clazz;

	}

	/**
	 * 判断是否是数据基本数据类型对象
	 * 
	 * @param className
	 *            ：class名称
	 * @return
	 */
	public static boolean isPrimitive(String className) {
		Class<?> clazz = null;
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		String name = clazz.getName();
		if (clazz.isPrimitive() || name.equals("java.lang.String")
				|| name.equals("java.lang.Integer")
				|| name.equals("java.lang.Float")
				|| name.equals("java.lang.Double")
				|| name.equals("java.lang.Character")
				|| name.equals("java.lang.Integer")
				|| name.equals("java.lang.Boolean")
				|| name.equals("java.lang.Short")) {
			return true;
		} else {
			return false;
		}

	}
}
