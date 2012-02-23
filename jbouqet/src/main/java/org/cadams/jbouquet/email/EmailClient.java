package org.cadams.jbouquet.email;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.cadams.jbouquet.configuration.ConfigurationFinder;
import org.cadams.jbouquet.text.Strings;

/**
 * Email client replacement which makes sending multipart emails easier..
 * 
 * @author cta
 */
public class EmailClient {
	/**
	 * Gets a pre-configured client using the email-client.yaml settings file.
	 * <p>
	 * The format of this file is a simple yaml map file: <br>
	 * smtp-host: host
	 * smtp-port: port
	 * smtp-user: user
	 * smtp-pass: password
	 * <br><br>
	 * You can place this file in any of the well-defined ConfigurationFinder locations.
	 * </p>
	 * @return
	 */
	public static EmailClient getConfiguredClient() {
		Map<String,String> config = ConfigurationFinder.newInstance().readMap("email-client.yaml");
		
		String host = config.get("smtp-host");
		String user = config.get("smtp-user");
		String password = config.get("smtp-pass");
		int port = 25;
		if (config.get("smtp-port") != null) {
			port = Integer.parseInt(config.get("smtp-port"));
		}
		
		return new EmailClient(host, port, user, password);
	}
	
	private final String host;
	private final int port;
	private final String username;
	private final String password;
	
	private boolean debug = false;
	private Properties properties;
	private Authenticator auth;
	
	public EmailClient(String host) {
		this(host,25,null, null);
	}
	
	public EmailClient(String host, int port) {
		this(host,port,null,null);
	}
	
	public EmailClient(String host, String username, String password) {
		this(host,25,username, password);
	}
	
	public EmailClient(String host, int port, String username, String password) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		
		initialize();
	}
	
	private void initialize() {
		properties = new Properties();
		properties.put("mail.smtp.host", host);

		auth = null;
		if (!Strings.isEmpty(username)) {
			properties.put("mail.smtp.user", username);
			properties.put("mail.smtp.auth", "true");
			auth = new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					PasswordAuthentication result = new PasswordAuthentication(
							username, password);
					return result;
				}
			};
		}


	}
	
	/**
	 * Sends the specified email.
	 * 
	 * @param email
	 */
	public void sendEmail(Email email) {
		//Get our default session
		Session session = Session.getDefaultInstance(properties, auth);		
		if (debug)  {
			session.setDebug(true);
		}
		
		Transport transport = null;		
		try {
			Message msg = EmailClient.buildEmailMessage(session, email);
			transport = session.getTransport("smtp");
			transport.connect(host, port, username, password);
			transport.sendMessage(msg, msg.getAllRecipients());
		} catch(MessagingException e) {
			throw new RuntimeException(e);
		} finally {
			if (transport != null) {
				try {
					transport.close();
				} catch(Exception ignore) {}
			}
		}
	}
	
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	private static Message buildEmailMessage(Session session, Email email) throws MessagingException {
		Message msg = new MimeMessage(session);
		
		msg.setSubject(email.getSubject());
		EmailClient.addReceivers(msg, email);
		
		Multipart multipart = new MimeMultipart();
		EmailClient.addMessageBodyPart(multipart, email.getHtml(), "text/html");
		EmailClient.addAttachments(multipart, email);
		
		msg.setContent(multipart);
		return msg;
	}
	
	private static void addReceivers(Message msg, Email email) throws MessagingException {
		InternetAddress from = new InternetAddress(email.getFrom());
		msg.setFrom(from);
		
		InternetAddress[] to = EmailClient.getInternetAddresses(email.getTo());
		msg.setRecipients(RecipientType.TO, to);
		
		InternetAddress[] bcc = EmailClient.getInternetAddresses(email.getBcc());
		msg.setRecipients(RecipientType.BCC, bcc);
	}
	
	private static void addMessageBodyPart(Multipart multipart, String content, String mimeType) throws MessagingException {
		BodyPart part = new MimeBodyPart();
		part.setContent(content,mimeType);		
		multipart.addBodyPart(part);
	}
	
	private static void addAttachments(Multipart multipart, Email email) throws MessagingException {
		for (Attachment attachment : email.getAttachments()) {
			BodyPart body = new MimeBodyPart();
			DataSource source = new ByteArrayDataSource(attachment.getData(), attachment.getMimeType());
			body.setDataHandler(new DataHandler(source));
			body.setFileName(attachment.getFilename());
			multipart.addBodyPart(body);
		}
	}
	
	private static InternetAddress[] getInternetAddresses(List<String> addresses) throws MessagingException {
		List<InternetAddress> results = new ArrayList<InternetAddress>();
		for (String address : addresses) {
			results.add(new InternetAddress(address));
		}
		
		return results.toArray(new InternetAddress [] {});
	}
}
