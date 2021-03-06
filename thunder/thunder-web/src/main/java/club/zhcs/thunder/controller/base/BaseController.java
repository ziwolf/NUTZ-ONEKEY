package club.zhcs.thunder.controller.base;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.View;
import org.nutz.mvc.view.ForwardView;
import org.nutz.mvc.view.JspView;
import org.nutz.mvc.view.ServerRedirectView;
import org.nutz.mvc.view.UTF8JsonView;
import org.springframework.beans.factory.annotation.Autowired;

import eu.bitwalker.useragentutils.UserAgent;

/**
 * 
 * @author kerbores@gmail.com
 *
 */
public class BaseController {

	@Autowired
	protected HttpServletRequest request;

	@Autowired
	protected HttpServletResponse response;

	protected Log log = Logs.get();

	public Logger logger = Logger.getLogger(getClass());

	protected void addCookie(String name, String value, int age) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath("/");
		cookie.setMaxAge(age);
		response.addCookie(cookie);
	}

	public HttpServletRequest request() {
		return request;
	}

	public String base() {
		return request.getContextPath();
	}

	public UserAgent ua() {
		logger.debug(request.getHeader("user-agent"));
		return new UserAgent(request.getHeader("user-agent"));
	}

	public int fixPage(int page) {
		return ((page <= 0) ? 1 : page);
	}

	public String fixSearchKey(String key) {
		if ((Strings.equalsIgnoreCase("get", request.getMethod())) && (Lang.isWin())) {
			key = (Strings.isBlank(key)) ? "" : key;
			try {
				return new String(key.getBytes("iso-8859-1"), request.getCharacterEncoding());
			} catch (UnsupportedEncodingException e) {
				logger.debug(e);
				return "";
			}
		}
		return ((Strings.isBlank(key)) ? "" : key);
	}

	protected String getCookie(String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (Strings.equals(cookie.getName(), name)) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	public String getNameSpace() {
		return null;
	}

	public String ip() {
		return Lang.getIP(request);
	}

	protected void putSession(String key, Object value) {
		request.getSession().setAttribute(key, value);
	}

	public View renderForward(String path, Object[] objs) {
		request.setAttribute("objs", objs);
		return new ForwardView(path);
	}

	public View renderJson(Object[] objs) {
		UTF8JsonView view = (UTF8JsonView) UTF8JsonView.NICE;
		view.setData(objs);
		return view;
	}

	public View renderJsp(String path, Object[] objs) {
		request.setAttribute("objs", objs);
		return new JspView(path);
	}

	public View renderRedirct(String path) {
		return new ServerRedirectView(path);
	}
}
