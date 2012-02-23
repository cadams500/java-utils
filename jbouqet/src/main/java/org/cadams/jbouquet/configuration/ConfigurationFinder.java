package org.cadams.jbouquet.configuration;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Utility class which is used to locate configuration files.
 * <p>
 * This class has a hard-coded list of configuration paths and also supports
 * specifying other configuration paths using the -Deds.config=path1,path2,path3 JVM argument.
 * 
 * @author cta
 *
 */
public class ConfigurationFinder {
	
	private static List<String> globalPaths = new ArrayList<String>();
	static {
		globalPaths.add("/configs/%s");			
		globalPaths.add("/etc/jbouquet/configs/%s");
		globalPaths.add("/etc/jbouquet/configuration/%s");			
		globalPaths.add("../etc/%s");
		globalPaths.add("./%s");
	}
		
	/**
	 * Creates a new instance of the configuration finder appending the
	 * specified paths onto the search locations.
	 * 
	 * @param paths
	 * @return
	 */
	public static ConfigurationFinder newInstance(String ...paths) {
		List<String> items = new ArrayList<String>();		
		if (paths != null) {
			for (String path : paths) {
				if (path != null)
					items.add(path);
			}
		}
		
		return newInstance(items);
	}
	
	/**
	 * Creates a configuration finder with additional search paths added to the beginning of the pre-defined list.
	 * 
	 * @param additionalPaths
	 * @return
	 */
	public static ConfigurationFinder newInstance(List<String> additionalPaths) {
		return new ConfigurationFinder(additionalPaths);
	}
	
	/**
	 * Returns a default instance of the configuration finder.
	 * 
	 * @return
	 */
	public static ConfigurationFinder newInstance() {
		return new ConfigurationFinder(null);
	}
	
	private final List<String> paths;
	private ConfigurationFinder(List<String> additionalPaths) {
		this.paths = new ArrayList<String>();
		
		//Check if there are any configuration properties in the jvm arguments
		String cmdLineConfigs = System.getProperty("eds.config");
		if (cmdLineConfigs != null) 
			this.paths.addAll(Arrays.asList(cmdLineConfigs.split(",")));
		
		if (additionalPaths != null)
			this.paths.addAll(additionalPaths);
		
		this.paths.addAll(globalPaths);
	}
	
	/**
	 * Attempts to locate the configuration file, parse it, and return it to the caller.
	 * 
	 * @param classType
	 * @param filename
	 * @return
	 */
	public <T> T read(Class<T> classType, String filename) {		
		String yaml = getConfiguration(filename);		
		T result = YamlParser.parse(classType, yaml);
		
		return result;
	}
	
	/**
	 * Reads the specified yaml filename and assumes it's contents map to a Map.
	 *  
	 * @param filename
	 * @return
	 */
	public Map<String,String> readMap(String filename) {
		String yaml = getConfiguration(filename);
		return YamlParser.parseToMap(yaml);
	}
	
	/**
	 * Attempst to locate the specified properties file and returns it to the caller.
	 * 
	 * @param filename
	 * @return
	 */
	public Properties getProperties(String filename) {
		String file = getConfiguration(filename);
		if (file == null) 
			throw new IllegalArgumentException("The " + filename + " could not be found in any well-defined location.");
		
		InputStream is = new ByteArrayInputStream(file.getBytes());
		
		Properties properties = new Properties();			
		try {
			properties.load(is);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}	
		
		return properties;
	}
	
	/**
	 * Returns the raw configuration file contents as a string.
	 * 
	 * @param filename
	 * @return
	 */
	public String getConfiguration(String filename) {
		String yaml = Resources.findResourceAsString(filename, paths);
		if (yaml == null) 
			throw new IllegalArgumentException("Could not find the " + filename + " file in any location. Locations checked:" + paths.toString());
		
		return yaml;
	}
	
	/**
	 * Returns the path that the specified configuration file was located at.
	 * 
	 * @param filename
	 * @return
	 */
	public String findConfigurationPath(String filename) {
		String location = null;
		for (String path : this.paths) {
			String temp = String.format(path, filename);
			File file = new File(temp);
			if (file.exists()) {
				location = file.getAbsolutePath();
				break;
			}
		}
		
		return location;		
	}
}
