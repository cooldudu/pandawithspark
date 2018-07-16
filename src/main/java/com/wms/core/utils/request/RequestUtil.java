package com.wms.core.utils.request;


import java.util.Map;

import com.wms.core.utils.common.ObjectUtils;
import com.wms.core.utils.common.StaticData;
import is.tagomor.woothee.Classifier;
import org.springframework.web.server.ServerWebExchange;

/**
 * request工具类
 *
 * @author xb
 *
 */
@SuppressWarnings("rawtypes")
public class RequestUtil {
	private static final ThreadLocal<String> threadLocal = new ThreadLocal<String>();
	public static String getBasePath() {
		String basePath = threadLocal.get();
		return basePath;
	}

	public static String makeParamUrl(Map<String, String> map) {
		StringBuffer result = new StringBuffer();
		if (!ObjectUtils.isEmpty(map)) {
			for (Map.Entry<String,String> entity : map.entrySet()) {
				String key = entity.getKey();
				String value = entity.getValue();
				if(ObjectUtils.isNotEmpty(result)){
					result.append("&"+key+"="+value);
				}else{
					result.append("?"+key+"="+value);
				}
			}
		}
		return result.toString();
	}

	public static String getBrowser(ServerWebExchange exchange){
		String Agent = exchange.getRequest().getHeaders().get("User-Agent").get(StaticData.FIRST);
		Map info = Classifier.parse(Agent);
		return (String) info.get("name");
	}

	public static String getOs(ServerWebExchange exchange){
		String Agent = exchange.getRequest().getHeaders().get("User-Agent").get(StaticData.FIRST);
		Map info = Classifier.parse(Agent);
		return (String) info.get("os");
	}

}
