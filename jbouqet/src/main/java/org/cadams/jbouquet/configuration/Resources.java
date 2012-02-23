package org.cadams.jbouquet.configuration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.ClassUtils;

/**
 * <p>
 * Generic resource loader which is able to load resources from the classpath,
 * file, or url.
 * </p>
 * 
 * <p>
 * The lookup order for this class is as follows:
 * <ul>
 * 	<li>URL resource (http://)</li>
 *  <li>Resource starting with &quot;classpath:&quot; identifier</li>
 *  <li>FileSystemResource</li>
 *  <li>Classpath resource without &quot;classpath:&quot; identifier</li>
 * </ul>
 * </p>
 * 
 * <p>
 * This class replaces the {@code com.emaildatasource.eds.java.helper.ResourceLoader} implementation.
 * </p>
 * 
 * <p>
 * <b>Examples:</b><br />
 * {@code Resources.getResource("classpath:com/edatasource/io/resource.properites");} (Reads from classpath)
 * {@code Resources.getResourceAsString("./resources.properties");} (Reads from file system)
 * </p>
 * @author Chris Adams
 *
 */
public class Resources {
	private static final Logger logger = Logger.getLogger(Resources.class.getName());
	
	/**
	 * Prefix used to signify that the resource must be loaded from the classpath instead of filesystem.
	 */
	public static final String CLASSLOADER_PREFIX = "classpath:";
	
	/**
	 * Returns a File containing the resource information.
	 * 
	 * @param location
	 * @return
	 */
	public static Resource getResource(String location) {
		ClassLoader loader = ClassUtils.getDefaultClassLoader();
		
		Resource resource = null;
		try {
			resource = new UrlResource(location);
		} catch(MalformedURLException e) {
			if (location.startsWith(DefaultResourceLoader.CLASSPATH_URL_PREFIX)) {
				resource = new ClassPathResource(location.substring(DefaultResourceLoader.CLASSPATH_URL_PREFIX.length()), loader);
			} else {
		        resource = new FileSystemResource(location);
	            if (!resource.exists()) {
	            	resource = new ClassPathResource(location, loader);
	            	if (!resource.exists()) {
	            		resource = null;
	            	}
	            }
			}
		}

	    return resource;
	}
	
	/**
	 * Reads the specified resource location and returns the data as a string.
	 * 
	 * @param location The resource location. 
	 * @return
	 */
	public static String getResourceAsString(String location) {
		Resource resource = getResource(location);
		String result = null;
		
		if (resource != null) {
			try {
				InputStream is = resource.getInputStream();
				try {
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					IOUtils.copy(is,os);
					
					byte [] data = os.toByteArray();
					
					result = new String(data);
				} finally {
					is.close();
				}
			} catch(IOException ignore) {}
		}
		
		return result;
	}
	
	public static String findResourceAsString(String formatReplacement, String ... pathsToCheck) {
		List<String> paths = new ArrayList<String>();
		for (String path : paths) {
			paths.add(path);
		}
		
		return findResourceAsString(formatReplacement, paths);
	}
	
	/**
	 * Attempts to locate the resource from the specified paths locations.
	 * <p>
	 * This call supports a single string format replacement, such that you can do file-%s and %s will be replaced with {@code formatReplacement}
	 * 
	 * @param formatReplacement the %s replacement or null
	 * @param pathsToCheck the paths to check for the specified resource
	 * 
	 * @return the file contents, if found; or null if not found.
	 */
	public static String findResourceAsString(String formatReplacement, List<String> pathsToCheck) {
		String content = null;
		for (String path : pathsToCheck) {
			if (formatReplacement != null)
				path = String.format(path,formatReplacement);
			
			content = Resources.getResourceAsString(path);
			if (logger.isInfoEnabled()) {
				logger.info("Checking " + path + " for configuration file: " + (content != null ? "PASS" : "FAILED"));
			}
			if (content != null)
				break;
		}	
		
		return content;
	}
}
