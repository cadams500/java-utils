package org.cadams.jbouquet.email;

/**
 * Represents an email attachment used by the EmailClient.
 * 
 * @author cta
 *
 */
public class Attachment {
	private byte [] data;
	private String filename;
	private String mimeType;
	
	public Attachment() {
	}
	
	public Attachment(String filename, String mimeType, byte [] data) {
		this.data = data;
		this.filename = filename;
		this.mimeType = mimeType;
	}
	
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
}
