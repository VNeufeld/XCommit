package com.bpcs.basic.servletFilter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.logging.LogManager;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.output.TeeOutputStream;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;


public class LoggingFilter implements Filter {

	private String filterExclusion;
	
	private static final Logger logger = Logger.getLogger(LoggingFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		String jbossDir = System.getProperty("jboss.home.dir");
		String jbossConfigUrl = System.getProperty("jboss.server.config.url");
		
		System.out.println("SO:  jbossDir = "+jbossDir);
		System.out.println("SO:  jbossConfigDir = "+jbossConfigUrl);
		
		String configPath =		jbossDir;
		
		if ( jbossConfigUrl != null && jbossConfigUrl.startsWith("file") ) {
			URI uri;
			try {
				uri = new URI(jbossConfigUrl);
				File file = new File(uri.toURL().getFile());

				configPath = file.toString(); // "D:/app/jboss-5.1.0.GA-jdk6/jboss-5.1.0.GA/server/default/conf/";
				if ( !configPath.endsWith("/"))
					configPath = configPath + "/";
				
				
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			
		}
		
		// init java.utils.logging for paypal sdk :
		// bis auf weiteres deaktiviren, weil SDK schreibt in Log4j
		//initJavaUtilsLogging();
		
		
		String path = configPath; // "D:/app/jboss-5.1.0.GA-jdk6/jboss-5.1.0.GA/server/default/conf/";
		
		String Log4JPath = path + "paypal_log4j.xml";
		
		System.out.println("SO:  Log4Config = "+Log4JPath);
		
		logger.info("(1)init log4J. Log4Config = "+Log4JPath);
		
		
		DOMConfigurator.configureAndWatch(Log4JPath);
		
		logger.info(" (2) init log4J. Log4Config = "+Log4JPath);
		
		String category = filterConfig.getInitParameter("log_category");
		logger.info("init LoggingFilter. param = "+category);
		loadFilterexclusions(category);
		

	}

	private void initJavaUtilsLogging() {
		
		File file = new File("C:/Temp/paypallog.properties");
		try {
			LogManager.getLogManager().readConfiguration(new FileInputStream(file));
			logger.info("initializing java.utils.logging ok");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void loadFilterexclusions(String category) {
		Properties props = new Properties();
		ClassLoader cl = this.getClass().getClassLoader();

		if (cl != null) {
			URL url = cl.getResource(category);
			if (null == url) {
				url = cl.getResource("/" + category);
			}
			if (null != url) {
				try {
					InputStream in = url.openStream();
					props.load(in);
					filterExclusion = props.getProperty("application.logging.filter");
					
				} catch (IOException e) {
					logger.error("",e);
				}

			}
		}
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {

		try {
			long startTime = System.currentTimeMillis();
			
			HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			HttpServletResponse httpServletResponse = (HttpServletResponse) response;
			Map<String, String> requestMap = this.getTypesafeRequestMap(httpServletRequest);
			BufferedRequestWrapper bufferedReqest = new BufferedRequestWrapper(httpServletRequest);
			BufferedResponseWrapper bufferedResponse = new BufferedResponseWrapper(httpServletResponse);
			
			String requestBody = getRequestBody(bufferedReqest.getRequestBody());

			final StringBuilder logMessage = new StringBuilder("REST Request - ").append("[HTTP METHOD:")
					.append(httpServletRequest.getMethod()).append("] [PATH INFO:")
					.append(httpServletRequest.getPathInfo()).append("] [REQUEST PARAMETERS:").append(requestMap)
					.append("ContentType = ").append(request.getContentType())

					.append("] [REQUEST BODY:").append(requestBody).append("] [REMOTE ADDRESS:")
					.append(httpServletRequest.getRemoteAddr()).append("]");

			logger.info(logMessage);

			chain.doFilter(bufferedReqest, bufferedResponse);

			if (bufferedResponse != null) {
				logger.info(" response content type = " + bufferedResponse.getContentType());
				logger.info(" response content = " + getRequestBody(bufferedResponse.getContent()));
			}

			logger.info(" needed time: " + (System.currentTimeMillis() - startTime) + " ms.");

		} catch (Throwable a) {
			logger.error("", a);
		}
	}

	private String getRequestBody(String requestBody) {
		String result = requestBody;
		// dummy value
		String[] pf = new String[] {"account"};
		if ( filterExclusion != null )
			// overwrrite from properties
			pf = filterExclusion.split(",");
		//result = FilterUtils.shadowKeyWords(requestBody, pf);
		return result;
	}

	private Map<String, String> getTypesafeRequestMap(HttpServletRequest request) {
		Map<String, String> typesafeRequestMap = new HashMap<String, String>();
		Enumeration<?> requestParamNames = request.getParameterNames();
		while (requestParamNames.hasMoreElements()) {
			String requestParamName = (String) requestParamNames.nextElement();
			String requestParamValue = request.getParameter(requestParamName);

			typesafeRequestMap.put(requestParamName, requestParamValue);
		}

		return typesafeRequestMap;
	}

	@Override
	public void destroy() {
	}

	private static final class BufferedRequestWrapper extends HttpServletRequestWrapper {
		private ByteArrayInputStream bais = null;
		private ByteArrayOutputStream baos = null;
		private BufferedServletInputStream bsis = null;
		private byte[] buffer = null;

		public BufferedRequestWrapper(HttpServletRequest req) throws IOException {

			super(req);

			// Read InputStream and store its content in a buffer.
			InputStream is = req.getInputStream();
			this.baos = new ByteArrayOutputStream();
			byte buf[] = new byte[1024];
			int letti;
			while ((letti = is.read(buf)) > 0) {
				this.baos.write(buf, 0, letti);
			}

			this.buffer = this.baos.toByteArray();
		}

		@Override
		public ServletInputStream getInputStream() {
			this.bais = new ByteArrayInputStream(this.buffer);
			this.bsis = new BufferedServletInputStream(this.bais);

			return this.bsis;

		}

		String getRequestBody() throws IOException {
			BufferedReader reader = new BufferedReader(new InputStreamReader(this.getInputStream()));
			String line = null;
			StringBuilder inputBuffer = new StringBuilder();

			do {
				line = reader.readLine();
				if (null != line) {
					inputBuffer.append(line.trim());
				}
			} while (line != null);
			reader.close();

			return inputBuffer.toString().trim();
		}
	}

	private static final class BufferedServletInputStream extends ServletInputStream {
		private ByteArrayInputStream bais;

		public BufferedServletInputStream(ByteArrayInputStream bais) {
			this.bais = bais;
		}

		@Override
		public int available() {
			return this.bais.available();
		}

		@Override
		public int read() {
			return this.bais.read();
		}

		@Override
		public int read(byte[] buf, int off, int len) {
			return this.bais.read(buf, off, len);
		}
	}

	public class TeeServletOutputStream extends ServletOutputStream {
		private final TeeOutputStream targetStream;

		public TeeServletOutputStream(OutputStream one, OutputStream two) {
			targetStream = new TeeOutputStream(one, two);
		}

		@Override
		public void write(int arg0) throws IOException {
			this.targetStream.write(arg0);
		}

		public void flush() throws IOException {
			super.flush();
			this.targetStream.flush();
		}

		public void close() throws IOException {
			super.close();
			this.targetStream.close();
		}
	}

	public class BufferedResponseWrapper implements HttpServletResponse {

		HttpServletResponse original;
		TeeServletOutputStream tee;
		ByteArrayOutputStream bos;

		public BufferedResponseWrapper(HttpServletResponse response) {
			original = response;
		}

		public String getContent() {
			if (bos == null)
				return "";
			return bos.toString();
		}

		public PrintWriter getWriter() throws IOException {
			return original.getWriter();
		}

		public ServletOutputStream getOutputStream() throws IOException {

			if (tee == null) {
				bos = new ByteArrayOutputStream();
				tee = new TeeServletOutputStream(original.getOutputStream(), bos);
			}

			return tee;
		}

		@Override
		public String getCharacterEncoding() {
			return original.getCharacterEncoding();
		}

		@Override
		public String getContentType() {
			return original.getContentType();
		}

		@Override
		public void setCharacterEncoding(String charset) {
			original.setCharacterEncoding(charset);
		}

		@Override
		public void setContentLength(int len) {
			original.setContentLength(len);
		}

		@Override
		public void setContentType(String type) {
			original.setContentType(type);
		}

		@Override
		public void setBufferSize(int size) {
			original.setBufferSize(size);
		}

		@Override
		public int getBufferSize() {
			return original.getBufferSize();
		}

		@Override
		public void flushBuffer() throws IOException {
			tee.flush();
		}

		@Override
		public void resetBuffer() {
			original.resetBuffer();
		}

		@Override
		public boolean isCommitted() {
			return original.isCommitted();
		}

		@Override
		public void reset() {
			original.reset();
		}

		@Override
		public void setLocale(Locale loc) {
			original.setLocale(loc);
		}

		@Override
		public Locale getLocale() {
			return original.getLocale();
		}

		@Override
		public void addCookie(Cookie cookie) {
			original.addCookie(cookie);
		}

		@Override
		public boolean containsHeader(String name) {
			return original.containsHeader(name);
		}

		@Override
		public String encodeURL(String url) {
			return original.encodeURL(url);
		}

		@Override
		public String encodeRedirectURL(String url) {
			return original.encodeRedirectURL(url);
		}

		@SuppressWarnings("deprecation")
		@Override
		public String encodeUrl(String url) {
			return original.encodeUrl(url);
		}

		@SuppressWarnings("deprecation")
		@Override
		public String encodeRedirectUrl(String url) {
			return original.encodeRedirectUrl(url);
		}

		@Override
		public void sendError(int sc, String msg) throws IOException {
			original.sendError(sc, msg);
		}

		@Override
		public void sendError(int sc) throws IOException {
			original.sendError(sc);
		}

		@Override
		public void sendRedirect(String location) throws IOException {
			original.sendRedirect(location);
		}

		@Override
		public void setDateHeader(String name, long date) {
			original.setDateHeader(name, date);
		}

		@Override
		public void addDateHeader(String name, long date) {
			original.addDateHeader(name, date);
		}

		@Override
		public void setHeader(String name, String value) {
			original.setHeader(name, value);
		}

		@Override
		public void addHeader(String name, String value) {
			original.addHeader(name, value);
		}

		@Override
		public void setIntHeader(String name, int value) {
			original.setIntHeader(name, value);
		}

		@Override
		public void addIntHeader(String name, int value) {
			original.addIntHeader(name, value);
		}

		@Override
		public void setStatus(int sc) {
			original.setStatus(sc);
		}

		@SuppressWarnings("deprecation")
		@Override
		public void setStatus(int sc, String sm) {
			original.setStatus(sc, sm);
		}
	}
}
