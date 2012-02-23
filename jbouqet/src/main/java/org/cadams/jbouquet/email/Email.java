package org.cadams.jbouquet.email;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an email object used by the EmailClient.
 * 
 * @author cta
 *
 */
public class Email {
	private String from;
	private List<String> to = new ArrayList<String>();
	private List<String> bcc = new ArrayList<String>();
	private String subject;
	private String html;
	private List<Attachment> attachments = new ArrayList<Attachment>();
	
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public List<Attachment> getAttachments() {
		return attachments;
	}
	public List<String> getTo() {
		return to;
	}
	public List<String> getBcc() {
		return bcc;
	}
	
	public String getHtml() {
		return html;
	}
	public void setHtml(String html) {
		this.html = html;
	}
}
