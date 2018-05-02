package com.blueline.lang;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  应用属性
 *
 *  属性读取优先级
 *  	命令行属性>系统属性>环境变量>应用当前目录属性文件>资源路径属性文件
 *  	高优先级属性覆盖低优先级属性
 *
 *  多环境配置 java -jar xxx.jar --profiles.active=<ActiveName>.
 * 		启用后对应属性文件:application[-<ActiveName>].properties
 * 		不指定环境配置时可以没有application.properties属性文件
 *
 *  命令行属性 java -jar xxx.jar --<PropertyName>=[PropertyValue]...
 *  	String属性"--" 开头： --<PropertyName>=[PropertyValue]
 *  	boolean属性：--<PropertyName> 读取时值为true
 *
 *  系统属性 System.getProperties()
 *  	java自动获取系统属性
 *  	命令行赋值String属性-D<PropertyName>=[PropertyValue]
 *
 *  环境变量 System.getenv()
 *  	系统及环境变量
 *
 *  应用当前目录属性文件
 *  	对应路径 System.getProperty("user.dir") 或 java -jar xxx.jar --profiles.path=<profiles.path>
 *  	如果指定了profiles.active 那么对应属性文件必须存在
 *  	属性文件名称：application[-<profiles.active>].properties
 *
 *  资源路径属性文件
 *  	对应路径 Properties.class.getResource("/")
 *  	如果指定了profiles.active 并且 application.properties 存在那么对应属性文件必须存在
 *  	属性文件名称：application[-<profiles.active>].properties
 *
 *  支持属性中引用直接引用属性 ${PropertyName},运行期间会替换为属性值
 *  	<PropertyNameX>=PropertyValueX
 *  	<PropertyNameY>=${PropertyNameX}-ok  解析后：<PropertyNameY>=PropertyValueX-ok
 *
 *  支持自动属性随机值
 *  	<PropertyName>=${random.value} 32位随机字符串:415a29f9e5dd38e23f2d4fd39e79a821
 *      <PropertyName>=${random.uuid} uuid随机字符串:505a3dd5-7742-4a51-bac9-e14291582e49
 *  	<PropertyName>=${random.long} 随机long值正数:0-MaxLongValue
 *  	<PropertyName>=${random.int} 随机int值正数:0-MaxIntValue
 *  	<PropertyName>=${random.int[MaxBound>]} 随机int值正数:0-MaxBound
 *  	<PropertyName>=${random.int[<MiniBound>,<MaxBound>]} 随机int值正数:MiniBound-MaxBound
 */
public class Property {

	private static final String PROPERTY_PROFILES_ACTIVE="profiles.active";
	private static final String PROPERTY_PROFILES_PATH="profiles.path";
	private static String PROFILES_ACTIVE=null;


	private static Properties CMD_PARAMS=paresCmdParams();
	private static Properties SYS_PROPERTIES=getProperties(System.getProperties());



	private static Properties SYS_ENV=getProperties(System.getenv());
	private static Properties APPLICATION_PROPERITES=getApplicationProperties(CMD_PARAMS.getProperty(PROPERTY_PROFILES_PATH,""));
	private static Properties CLASS_PROPERITES=getClassPathProperties();

	private static Properties FINAL_PROPERTIES=new Properties();
	private static Map<String,Map<String,ICalculate>> VALUE_FUN=new HashMap<String, Map<String, ICalculate>>();

	static{
		Map<String,ICalculate> computs=new HashMap<String, ICalculate>();
		computs.put("value",Randoms.VALUE);
		computs.put("int",Randoms.INT);
		computs.put("long",Randoms.LONG);
		computs.put("uuid",Randoms.UUID);
		VALUE_FUN.put("random",computs);

		FINAL_PROPERTIES=mergeProperties(CLASS_PROPERITES,APPLICATION_PROPERITES,SYS_ENV,SYS_PROPERTIES,CMD_PARAMS);
		FINAL_PROPERTIES=generatingDynamicValues(FINAL_PROPERTIES);
		FINAL_PROPERTIES=analyticVariable(FINAL_PROPERTIES);
	}
	private static Properties getClassPathProperties(){
		Properties properties=new Properties();
		InputStream inputStream=null;
		try {
			inputStream=Properties.class.getResourceAsStream("/application.properties");
			properties.load(inputStream);
		} catch (Exception e){
		}finally {
			if(inputStream!=null){
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if(PROFILES_ACTIVE==null) {
			PROFILES_ACTIVE = properties.getProperty(PROPERTY_PROFILES_ACTIVE, null);
		}
		if(PROFILES_ACTIVE==null){
			return properties;
		}else{
			try {
				inputStream=Properties.class.getResourceAsStream("/application-"+ PROFILES_ACTIVE +".properties");
				Properties activeProperties=new Properties();
				activeProperties.load(inputStream);
				properties=mergeProperties(properties,activeProperties);

			} catch (Exception e){
			}finally {
				if(inputStream!=null){
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return properties;
		}
	}

	private static Properties getApplicationProperties(String path){

		String propertiesPath=path;
		if(propertiesPath==null||propertiesPath.isEmpty()){
			propertiesPath=System.getProperty("user.dir");
		}

		Properties properties=new Properties();
		InputStream inputStream=null;
		try {
			inputStream=new FileInputStream(propertiesPath+ File.separatorChar+"application.properties");
			properties.load(inputStream);
		} catch (Exception e){
		}finally {
			if(inputStream!=null){
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if(PROFILES_ACTIVE==null) {
			PROFILES_ACTIVE = properties.getProperty(PROPERTY_PROFILES_ACTIVE, null);
		}
		if(PROFILES_ACTIVE==null){
			return properties;
		}else{
			try {
				inputStream=new FileInputStream(propertiesPath+"/application-"+ PROFILES_ACTIVE +".properties");
				Properties activeProperties=new Properties();
				activeProperties.load(inputStream);
				properties=mergeProperties(properties,activeProperties);
			} catch (Exception e){
				throw new RuntimeException(propertiesPath+"/application-"+ PROFILES_ACTIVE +".properties Exception.",e);
			}finally {
				if(inputStream!=null){
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return properties;
		}
	}

	private static Properties paresCmdParams(){
		String commandString=System.getProperty("sun.java.command");
		Properties properties=new Properties();
		if(commandString==null||commandString.isEmpty()){
			return  properties;
		}
		Pattern p = Pattern.compile("/\\s*(\".+?\"|[^:\\s])+((\\s*:\\s*(\".+?\"|[^\\s])+)|)|(\".+?\"|[^\"\\s])+");
		Matcher matcher = p.matcher(commandString);
		String paramStr;
		while (matcher.find()) {
			paramStr=matcher.group();
			if(paramStr.startsWith("--")){
				String key;
				String value;
				paramStr=paramStr.substring(2);
				int valueIndex=paramStr.indexOf('=');
				if(valueIndex<0) {
					key=paramStr;
					value="true";
				}else{

					key = paramStr.substring(0, valueIndex);
					value = paramStr.substring(valueIndex+1);

				}
				if(value.startsWith("\""))value=value.substring(1);
				if(value.endsWith("\""))value=value.substring(0,value.length()-1);
				properties.setProperty(key,value);
			}
		}
		if(PROFILES_ACTIVE==null) {
			PROFILES_ACTIVE = properties.getProperty(PROPERTY_PROFILES_ACTIVE, null);
		}
		return properties;
	}

	private static Properties analyticVariable(Properties prop){
		Set<Object> keys=prop.keySet();
		for (Object srcKey : keys) {
			for (Object destKey : keys) {
//				String var=srcKey
				prop.setProperty((String)destKey,prop.getProperty((String)destKey).replace("${"+srcKey+"}",prop.getProperty((String)srcKey,"")));
			}

		}
		return prop;
	}

	private static Properties generatingDynamicValues(Properties prop){
		Set<Object> keys=prop.keySet();

		for (Object srcKey : keys) {
//			prop.getProperty(keys).in

			String value=prop.getProperty((String)srcKey,"");
			Pattern p = Pattern.compile("\\$\\{[a-zA-Z0-9\\.\\,\\(\\)\\[\\]\\ ]*\\}");
			Matcher matcher = p.matcher(value);
			while(matcher.find()){
				String temp=matcher.group();
				String[] paramInfo=new String[0];
				String[] claInfo = (temp.substring(2,temp.length()-1)).split("\\.");
				if(claInfo.length<2)continue;
				String cla=claInfo[0];
				String[] funInfo=claInfo[1].split("[\\(\\[]");
				if(claInfo.length<1)continue;
				String fun=funInfo[0];
				if(funInfo.length>1&&!funInfo[1].isEmpty())
					paramInfo=funInfo[1].split("[\\,\\]\\)]");
				Map<String,ICalculate> claM=VALUE_FUN.get(cla);
				if(claM==null)continue;
				ICalculate funM=claM.get(fun);
				if(funM==null)continue;
				value=value.replace(temp,funM.compute(paramInfo));
			}
			prop.setProperty((String)srcKey,value);

		}
		return prop;
	}

	private static Properties mergeProperties(Properties...props){
		Properties finalProperties=new Properties();

		for(Properties prop:props){
			Set<Entry<Object,Object>> entrySet=prop.entrySet();
			for (Entry<Object, Object> entry : entrySet) {
				finalProperties.setProperty((String)entry.getKey(),(String)entry.getValue());
			}
		}
		return finalProperties;
	}

	private static Properties getProperties(Properties properties) {
		Set<Entry<Object,Object>> entrySet=properties.entrySet();
		Properties temp=new Properties();

		for(Entry<Object,Object> entry:entrySet){
			temp.setProperty((String)entry.getKey(),(String)entry.getValue());
		}
		if(PROFILES_ACTIVE==null) {
			PROFILES_ACTIVE = temp.getProperty(PROPERTY_PROFILES_ACTIVE, null);
		}
		return properties;
	}

	private static Properties getProperties(Map<String,String> map){
		Set<Entry<String,String>> entrySet=map.entrySet();
		Properties properties=new Properties();

		for(Entry<String,String> entry:entrySet){
			properties.setProperty(entry.getKey(),entry.getValue());
		}
		if(PROFILES_ACTIVE==null) {
			PROFILES_ACTIVE = properties.getProperty(PROPERTY_PROFILES_ACTIVE, null);
		}
		return properties;
	}


	/**
	 * 获取属性值
	 * @param propertyName 属性名
	 * @param defaultValue 当值不存在时返回此默认值，默认值不能为null
	 * @param <V> 默认值类型
	 * @return 根据默认值类型转换并返回属性名对应的值
	 */
	public static <V>V  get(String propertyName, V defaultValue){
		
		String linuxPropertyName=propertyName.replace('.', '_');
		String value=FINAL_PROPERTIES.getProperty(propertyName,null);
		value=(value==null||value.isEmpty())?FINAL_PROPERTIES.getProperty(linuxPropertyName,null):value;
		if(value!=null){
			return covertValue(defaultValue, value);
		}else{
			return defaultValue;
		}

	}

	/**
	 * 获取属性值
	 * @param propertyName 属性名
	 * @param defaultValue 当值不存在时返回此默认值，默认值不能为null
	 * @return 返回属性名对应的值
	 */
	public static char  get(String propertyName, char defaultValue){
		String linuxPropertyName=propertyName.replace('.', '_');
		String value=FINAL_PROPERTIES.getProperty(propertyName,null);
		value=(value==null||value.isEmpty())?FINAL_PROPERTIES.getProperty(linuxPropertyName,null):value;
		if(value!=null){
			return value.charAt(0);
		}else{
			return defaultValue;
		}
	}

	/**
	 * 设置应用内属性值，如果属性已存在将会被覆盖
	 * @param propertyName 属性名
	 * @param propertyValue 属性值
	 * @return 属性名源值
	 */
	public static synchronized String set(String propertyName, String propertyValue){
		System.out.println("Set property "+propertyName+"="+propertyValue);

		FINAL_PROPERTIES.setProperty(propertyName,propertyValue);
		return System.setProperty(propertyName,propertyValue);
	}

	private static <V> V covertValue(V defaultValue, String value) {
		
		if(defaultValue instanceof String){
			return (V)value.toString().trim();
		}		
		if(defaultValue instanceof Boolean){
			return (V)(Boolean.valueOf(value.trim()));
		}
		if(defaultValue instanceof Byte){
			return (V)(Byte.valueOf(value.trim()));
		}		
		if(defaultValue instanceof Short){
			return (V)(Short.valueOf(value.trim()));
		}
		if(defaultValue instanceof Integer){
			return (V)(Integer.valueOf(value.trim()));
		}
		if(defaultValue instanceof Long){
			return (V)(Long.valueOf(value.trim()));
		}
		if(defaultValue instanceof Float){
			return (V)(Float.valueOf(value.trim()));
		}
		if(defaultValue instanceof Double){
			return (V)(Double.valueOf(value.trim()));
		}
		
		return (V)value;
	}
	public static Properties getProperties(){
		return (Properties)FINAL_PROPERTIES.clone();
	}

	public static Properties getProperties(String fix){
		Properties ret =  new Properties();
		for (Object key : FINAL_PROPERTIES.keySet()) {
			if(((String)key).startsWith(fix)){
				ret.put(key,FINAL_PROPERTIES.getProperty((String)key));
			}
		}
		return ret;
	}
	private static void printProperties(Properties pro){
		Set<Entry<Object,Object>> entrySet=pro.entrySet();
		for(Entry<Object,Object> entry:entrySet){
			System.out.println(entry.getKey()+"\t=\t"+entry.getValue());
		}
	}

	public static void printProperties(){
		printProperties(FINAL_PROPERTIES);
	}


//	public static void main(String[] args) throws IOException {
//		printProperties();
//
//		System.out.println(Property.get("property.test2","?"));
//
//
//
//
//
//	}
	
}
