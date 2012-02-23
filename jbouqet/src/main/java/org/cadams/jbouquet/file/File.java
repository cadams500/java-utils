package org.cadams.jbouquet.file;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

/**
 * An immutable file object.
 * 
 * <p>
 * This object is an extension to the {@code java.io.File} class and provides
 * common utility methods used on files.
 * </p>
 * <p>
 * Since this call is immutable the caller must be aware of which calls
 * are destructive. In short, any call that moves the file or renames it
 * will be destructive and return a new instance of {@code File} which 
 * must be used for all new calls from that point onward.
 * </p>
 * @author Chris Adams
 *
 */
public class File extends java.io.File {
	private static final long serialVersionUID = 1L;

	public File(java.io.File file) {
		super(file.getParent(), file.getName());
	}
	
	public File(java.io.File parent, String child) {
		super(parent, child);
	}

	public File(String parent, String child) {
		super(parent, child);
	}

	public File(String pathname) {
		super(pathname);
	}

	public File(URI uri) {
		super(uri);
	}
	
	/**
	 * Hides the current file by placing the . in front of the file itself,
	 * returns a reference to the renamed file.
	 * 
	 * <p>
	 * NOTE: This is a destructive call and the current file reference will
	 * now point to a non-existent file. You must use the return File reference.
	 * </p>
	 */
	public File hide() {
		final File rename = new File(this.getParent(), "." + FilenameUtils.getName(this.getName()));
		final File file = new File(this.getParent(), FilenameUtils.getName(this.getName()));
		file.renameTo(rename);
		
		return rename;
	}
	
	/**
	  * Copies from the specified InputStream to the specified OutputStream.
	  * 
	  * @param is
	  * @param os
	  */
	 public static void copy(InputStream is, OutputStream os, boolean closeInput) {
		try {
			IOUtils.copy(is, os);
		} catch (Exception e) {
			throw new FileException(e);
		} finally {
			if (closeInput) {
				IOUtils.closeQuietly(is);
			}
		}
	 }	
	 
	 /**
	  * Copies the contents of this file to the specified directory.
	  * <p>
	  * The file will have the same filename as the original file.
	  * 
	  * @param destDir
	  */
	 public void copyToDirectory(File destDir) {
		 try {
			 FileUtils.copyFileToDirectory(this, destDir);
		 } catch(Exception e) {
			 throw new FileException(e);
		 }
	 }
	
	/**
	 * Unhides the current file by removing the . in front of the file.
	 * Returns a reference to the new unidden file.
	 * 
	 * <p>
	 * NOTE: This is a destructive call and the current file reference will
	 * now point to a non-existent file. You must use the return File reference.
	 * </p>
	 * @return
	 */
	public File unhide() {
		String existingName = this.getName();
		String newName = existingName;
		if (existingName.startsWith(".")) {
			newName = existingName.substring(1);
		}
		
		return this.renameTo(newName);
	}
		
	
	/**
	 * Moves the file to specified path and returns a reference to the new file.
	 * 
	 * <p>
	 * NOTE: This is a destructive call and the current file reference will
	 * now point to a non-existent file. You must use the return File reference.
	 * </p>
	 * @return
	 */
	public File move(final java.io.File newDirectory, final boolean createFolders, boolean overwrite) throws IOException {
		File file = new File(newDirectory, this.getName());
		
		if (overwrite) {
			if (file.exists()) 
				file.delete();
		}
		
		org.apache.commons.io.FileUtils.moveFileToDirectory(this, newDirectory, createFolders);
		
		return file;
	}	
	
	/**
	 * Moves the file to specified path and returns a reference to the new file.
	 * 
	 * <p>
	 * NOTE: This is a destructive call and the current file reference will
	 * now point to a non-existent file. You MUST use the return File reference.
	 * </p>
	 * @return
	 */
	public File move(String newDirectory, boolean createFolders) throws IOException {
		return move(new File(newDirectory), createFolders, true);
	}
	
	/**
	 * Renames the file while keeping the current path the same
	 * 
	 * <p>
	 * NOTE: This is a destructive call and the current file reference will
	 * now point to a non-existent file. You MUST use the return File reference.
	 * </p>
	 * @param newName
	 */
	public File renameTo(String newName) {
		File newname = new File(this.getParentFile(), newName);
		this.renameTo(newname);
		
		return newname;
	}
	
	/**
	 * Implements the same behavior as the "touch" utility on Unix
	 */
	public void touch() {
		try {
			FileUtils.touch(this);
		} catch(Exception e) {
			throw new FileException(e);
		}
	}
	
	/**
	 * Writes the specified text to the file.
	 * 
	 * @param text
	 */
	public void writeString(String text) {
		try {
			FileUtils.writeStringToFile(this, text);
		} catch(Exception e) {
			throw new FileException(e);
		}
	}
	
	/**
	 * Writes the specified input stream to the file.
	 * <p>
	 * This call will automatically close the InputStream.
	 * @param is
	 */
	public void write(InputStream is) {
		try {
			FileOutputStream os = new FileOutputStream(this);
			File.copy(is, os, true);
		} catch(Exception e) {
			throw new FileException(e);
		}
	}
	
	/**
	 * Writes the specified byte array to the file
	 * @param data
	 */
	public void write(byte [] data) {
		try {
			FileUtils.writeByteArrayToFile(this, data);
		} catch(Exception e) {
			throw new FileException(e);
		}
	}
	
	/**
	 * Reads the file into the specified String
	 * @return
	 */
	public String readAsString() {
		try {
			return FileUtils.readFileToString(this);
		} catch(Exception e) {
			throw new FileException(e);
		}
	}
	
	/**
	 * Reads the file into a byte array.
	 * @return
	 */
	public byte [] read() {
		try {
			return FileUtils.readFileToByteArray(this);
		} catch(Exception e) {
			throw new FileException(e);
		}
	}
	
	/**
	 * Reads the contents of the file to the specified outputstream.
	 * @param stream
	 */
	public void readTo(OutputStream os) {
		InputStream is = null;
		try {
			is = FileUtils.openInputStream(this);
			IOUtils.copy(is, os);
		} catch(Exception e) {
			throw new FileException(e);
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	/**
	 * Creates the directory structure represented by this file or directory
	 */
	public void createDirectory() {
		try {
			FileUtils.forceMkdir(this);
		} catch(Exception e) {
			throw new FileException(e);
		}
	}
	
	public void deleteDirectory() {
		try {
			FileUtils.deleteDirectory(this);
		} catch(Exception e) {
			throw new FileException(e);
		}
	}

	public boolean deleteQuietly() {
		return FileUtils.deleteQuietly(this);
	}
}
