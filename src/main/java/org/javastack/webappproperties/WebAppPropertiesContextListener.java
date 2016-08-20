package org.javastack.webappproperties;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Load Properties from a file into SystemProperties or as an Attribute in ServletContext,
 * using WebappBaseName as reference in a servlet container like Tomcat
 * 
 * <p>
 * Load order:
 * <ul>
 * <li>Default SystemProperties (never overwrite)</li>
 * <li>Forced SystemProperties (always overwrite)</li>
 * <li>ContextAttribute</li>
 * </ul>
 * 
 * <p>
 * Resolution order of baseDir (system property):
 * <ul>
 * <li>org.javastack.webappproperties.conf</li>
 * <li>catalina.base</li>
 * <li>catalina.home</li>
 * <li>user.dir</li>
 * </ul>
 */
@WebListener
public class WebAppPropertiesContextListener implements ServletContextListener {
	private static final String CONST_CONF_PROP_NAME = "org.javastack.webappproperties.conf";
	private static final String CONST_CONTEXT_ATTRIBUTE_NAME = "org.javastack.webappproperties.ctx";

	@Override
	public void contextInitialized(final ServletContextEvent contextEvent) {
		final ServletContext ctx = contextEvent.getServletContext();
		loadProperties(ctx);
	}

	@Override
	public void contextDestroyed(final ServletContextEvent contextEvent) {
	}

	private static final void loadProperties(final ServletContext ctx) {
		final String baseName = getContextBaseName(ctx);
		final File confDir = new File(new File(getConfigBase(), "appconf"), baseName);
		final File defaultSysCfg = new File(confDir, "system-properties-default.properties");
		final File forcedSysCfg = new File(confDir, "system-properties-forced.properties");
		final File ctxCfg = new File(confDir, "context.properties");
		ctx.log("Loading properties for: " + baseName);
		if (defaultSysCfg.canRead()) {
			try {
				final Properties p = load(defaultSysCfg);
				setSystemProperties(p, false);
			} catch (Exception e) {
				ctx.log("Unable to load: " + defaultSysCfg, e);
			}
		}
		if (forcedSysCfg.canRead()) {
			try {
				final Properties p = load(forcedSysCfg);
				setSystemProperties(p, true);
			} catch (Exception e) {
				ctx.log("Unable to load: " + forcedSysCfg, e);
			}
		}
		if (ctxCfg.canRead()) {
			try {
				final Properties p = load(ctxCfg);
				ctx.setAttribute(CONST_CONTEXT_ATTRIBUTE_NAME, p);
			} catch (Exception e) {
				ctx.log("Unable to load: " + ctxCfg, e);
			}
		}
	}

	private static final Properties load(final File f) throws IOException {
		final Properties p = new Properties();
		FileInputStream is = null;
		try {
			is = new FileInputStream(f);
			p.load(is);
			return p;
		} finally {
			closeQuiet(is);
		}
	}

	private static final void setSystemProperties(final Properties p, final boolean forceLoad) {
		for (final String propName : p.stringPropertyNames()) {
			final String value = p.getProperty(propName);
			if (forceLoad || (System.getProperty(propName) == null)) {
				System.setProperty(propName, value);
			}
		}
	}

	private static final String getContextBaseName(final ServletContext ctx) {
		final String path = ctx.getContextPath();
		return (path.isEmpty() ? "ROOT" : path.substring(1).replace('/', '#'));
	}

	private static final void closeQuiet(final Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (Exception ign) {
			}
		}
	}

	private static String getCatalinaHome() {
		return System.getProperty("catalina.home", System.getProperty("user.dir"));
	}

	private static String getCatalinaBase() {
		return System.getProperty("catalina.base", getCatalinaHome());
	}

	private static String getConfigBase() {
		return System.getProperty(CONST_CONF_PROP_NAME, getCatalinaBase());
	}
}
