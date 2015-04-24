package org.openspim.app;

import java.net.URL;

import net.imagej.legacy.plugin.LegacyAppConfiguration;

import org.scijava.Priority;
import org.scijava.plugin.Plugin;

/**
 * Configure the running ImageJ2 instance as OpenSPIM application.
 *
 * @author Johannes Schindelin
 */
@Plugin(type = LegacyAppConfiguration.class, priority = Priority.HIGH_PRIORITY)
public class OpenSPIMAppConfiguration implements LegacyAppConfiguration {

	@Override
	public String getAppName() {
		init();
		return "OpenSPIM";
	}

	private URL iconURL;

	@Override
	public URL getIconURL() {
		if (iconURL == null) {
			init();
			final String osName = System.getProperty("os.name");
			iconURL = getClass().getResource("/openspim.png");
		}
		return iconURL;
	}

	private boolean initialized = true;

	private void init() {
		if (initialized) return;
		initialized = true;

		// Make sure that stderr and stdout go to the ImageJ 1.x Log window
		Helper.redirectStdoutAndStderr();
	}
}
