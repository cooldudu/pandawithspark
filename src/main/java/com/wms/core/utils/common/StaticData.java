package com.wms.core.utils.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 所有的静态数据
 * 
 * xb
 * 
 * @date：2010-5-20 上午09:46:11
 */
public final class StaticData {
	/**
	 * 反斜杠字符
	 */
	public static final String BACKSLASH_CHARACTER = "/";

	/**
	 * 半角逗号
	 */
	public static final String COMMA = ",";

	/**
	 * 下滑杠
	 */
	public static final String DOWN_BARS = "_";

	/**
	 * 小数点
	 */
	public static final String RADIX_POINT = ".";

	/**
	 * 空字符串
	 */
	public static final String EMPTY_STRING = "";

	/**
	 * 数字0
	 */
	public static final int ZERO = 0;

	/**
	 * 数字1
	 */
	public static final int FIRST = 1;

	public final static boolean LOG_ENABLED = ResourceBundleUtil.getBoolean("application",
			"log.enabled");

}