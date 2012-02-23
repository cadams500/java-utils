package org.cadams.jbouquet.configuration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

/**
 * A basic type-safe YAML parser.
 * 
 * <p>
 * Limitations:
 * - The class that you are instantiating with the parsed data can not be a
 *   nested class. It must be a top-level or static inner class
 * 
 * @author Chris Adams
 */
public final class YamlParser {
	
	public static Map<String,String> parseToMap(String yaml) {
		@SuppressWarnings("rawtypes")
		Map temp = parse(Map.class, yaml);
		
		Map<String,String> result = new HashMap<String,String>();
		for (Object key : temp.keySet()) {
			result.put((String)key, (String)temp.get(key));
		}
		
		return result;
	}
	
	/**
	 * Parses and returns a single result.
	 * 
	 * @param clz
	 * @param yaml
	 * @return
	 */
	public static <T> T parse(Class<T> clz, String yaml) {
		List<T> results = parseAll(clz, yaml);
		
		T result = null;
		if (results.size() > 0) 
			result = results.get(0);
		
		return result;
	}
	
	/**
	 * Parses and returns a single result.
	 * 
	 * @param clz
	 * @param yaml
	 * @return
	 */
	public static <T> T parse(Class<T> clz, File yaml) {
		List<T> results = parseAll(clz, yaml);
		
		T result = null;
		if (results.size() > 0) 
			result = results.get(0);
		
		return result;
	}	
	
	/**
	 * Parses the specifeid YAML document into the specified result type.
	 * 
	 * @param clz
	 * @param file
	 * @return
	 */
	public static <T> List<T> parseAll(Class<T> clz, File file) {
		try {
			String doc = FileUtils.readFileToString(file);
			return parseAll(clz, doc);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Parse the specified yaml document into the result type
	 * 
	 * @param clz
	 * @param document
	 * @return
	 */
	@SuppressWarnings("unchecked")	
	public static <T> List<T> parseAll(Class<T> clz, String document) {
		List<T> results = new ArrayList<T>();
		Yaml yaml = new Yaml(new Constructor(clz));

		try {
			Iterable<Object> items = yaml.loadAll(document);
			for (Object item : items) {
				results.add((T)item);
			}	
			return results;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}		
	}

	/**
	 * Parses the specified yaml file found on the classpath.
	 * 
	 * @param clz
	 * @param resourcePath
	 * @return
	 */
	public static <T> List<T> parseFromResourceAll(Class<T> clz, String resourcePath) {
		String resource = Resources.getResourceAsString(resourcePath);
		if (resource == null) 
			throw new IllegalArgumentException("The specified resource path " + resourcePath + " does not exist");
		
		return parseAll(clz, resource);
	}
}
