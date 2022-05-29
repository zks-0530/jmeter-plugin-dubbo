package com.shouke.jmeter.plugin.dubbo.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.apache.log4j.Logger;
import java.lang.reflect.Type;

/**
 * JsonUtil
 */
public class JsonUtil {
    private static org.apache.log4j.Logger logger = Logger.getLogger(JsonUtil.class);
	private static final Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

	public static String toJson(Object obj) {
		return gson.toJson(obj);
	}

	public static String toJson(Object obj, Type type) {
		return gson.toJson(obj, type);
	}

	public static <T> T formJson(String json, Class<T> classOfT) {
		try {
			return gson.fromJson(json, classOfT);
		} catch (JsonSyntaxException e) {
			logger.error("json to class[" + classOfT.getName() + "] is error!", e);
		}
		return null;
	}

	public static <T> T formJson(String json, Type type) {
		try {
			return gson.fromJson(json, type);
		} catch (JsonSyntaxException e) {
			logger.error("json to class[" + type.getClass().getName()
					+ "] is error!", e);
		}
		return null;
	}
}
