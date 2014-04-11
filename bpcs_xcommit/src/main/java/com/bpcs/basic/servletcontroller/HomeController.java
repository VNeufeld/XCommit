package com.bpcs.basic.servletcontroller;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bpcs.basic.hsgwService.BpcsHttpClient;



@Controller
public class HomeController {
	
	
	@Value("${application.version}")
	private String version;

	@Value("${application.name}")
	private String appname;

	@Value("${application.buildtime}")
	private String buildtime;
	
	@Autowired
	private BpcsHttpClient httpClient;

	
	protected final Logger logger = Logger.getLogger(this.getClass());

	@RequestMapping(value = { "/status", "/home" }, method = RequestMethod.GET)
	public String showHomePage(Locale locale, Model model,  HttpSession session) {
		logger.info("Welcome home! the client locale is " + locale.toString());

		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG,
				DateFormat.LONG, locale);

		String formattedDate = dateFormat.format(date);

		model.addAttribute("serverTime", formattedDate);

		String s = checkJndi();

		model.addAttribute("dataSource", s);

		model.addAttribute("Session", session.getId());
		
		String contextPath = "";
		String serverInfo = "";
		String realPath = "";

		try {
			if ( session.getServletContext() != null ) {
					serverInfo = session.getServletContext().getServerInfo();
					realPath = session.getServletContext().getRealPath("/home");
					//contextPath = session.getServletContext().getContextPath();
			}
		}
		catch(Exception err) {
			logger.error(err.getMessage(),err);	
		}
		
		String hsgwConnection = "";
		try {
			hsgwConnection = httpClient.getHsgwConnection();
		}
		catch(Exception err) {
			logger.error(err.getMessage(),err);	
		}
		
		model.addAttribute("contextPath", contextPath);
		model.addAttribute("serverInfo", serverInfo);
		model.addAttribute("realPath", realPath);

		model.addAttribute("hsgwUrl", hsgwConnection);

		model.addAttribute("version", version);
		model.addAttribute("appname", appname);
		model.addAttribute("buildtime", buildtime);

		return "Home";

	}

	private String checkJndi() {
		// TODO Auto-generated method stub
		Context initCtx;
		String jndi = "test_jndi: ";
		try {
			initCtx = new InitialContext();

			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			

			// A data source can be obtained by doing the following.
			// Look up our data source
			DataSource ds = (DataSource) initCtx.lookup("java:/ADACGW");
			

			// Allocate and use a connection from the pool
			Connection conn = ds.getConnection();

			jndi = jndi + conn.toString();

			// ... use this connection to access the database ...
			conn.close();
			return jndi;

		} catch (NamingException e) {
			e.printStackTrace();
			return jndi + "error " + e.getMessage();
		} catch (SQLException e) {
			e.printStackTrace();
			return jndi + "error " + e.getMessage();
		}
	}
}