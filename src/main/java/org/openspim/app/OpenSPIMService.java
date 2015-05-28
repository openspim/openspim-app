package org.openspim.app;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;

import net.imagej.ui.swing.updater.ImageJUpdater;
import net.imagej.updater.FilesCollection;
import net.imagej.updater.UpdateSite;

import org.scijava.command.CommandService;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;
import org.scijava.util.AppUtils;

/**
 * Allows OpenSPIM to configure Fiji upon startup.
 *
 * @author Johannes Schindelin
 */
@Plugin(type = Service.class)
public class OpenSPIMService extends AbstractService {

	@Parameter
	private LogService log;

	@Parameter
	private CommandService commandService;

	private final static String urlOpenSPIM = "http://openspim.org/update/";
	private final static String urlMM = "http://sites.imagej.net/Micro-Manager-dev/";

	@Override
	public void initialize() {
		String imagejDirProperty = System.getProperty("imagej.dir");
		final File imagejRoot = imagejDirProperty != null ? new File(imagejDirProperty) :
			AppUtils.getBaseDirectory("ij.dir", FilesCollection.class, "updater");

		// Make sure that the plugins are found
		System.setProperty("org.micromanager.plugin.path", new File(imagejRoot, "mmplugins").getPath());
		System.setProperty("org.micromanager.autofocus.path", new File(imagejRoot, "mmautofocus").getPath());

		// Configure the update sites if they are not configured yet
		if (!imagejRoot.isDirectory() || !new File(imagejRoot, "db.xml.gz").exists()) return;
		final FilesCollection files = new FilesCollection(log, imagejRoot);
		try {
			files.read();

			UpdateSite siteOpenSPIM = files.getUpdateSite("OpenSPIM", true);
			final boolean hasOpenSPIM = siteOpenSPIM != null && siteOpenSPIM.isActive() && urlOpenSPIM.equals(siteOpenSPIM.getURL());
			UpdateSite siteMM = files.getUpdateSite("Micro-Manager-dev", true);
			final boolean hasMMNightly = siteMM != null && siteMM.isActive() && urlMM.equals(siteMM.getURL());
			if (!hasMMNightly) {
				Helper.log("Enabling the Micro-Manager-dev update site because OpenSPIM requires it!");
				if (siteMM != null) {
					siteMM.setActive(true);
					siteMM.setLastModified(-1);
				} else {
					siteMM = files.addUpdateSite("Micro-Manager-dev", urlMM, null, null, -1);
				}
				if (siteOpenSPIM == null) {
					siteOpenSPIM = files.addUpdateSite("OpenSPIM", urlOpenSPIM, null, null, -1);
				} else if (!hasOpenSPIM) {
					siteOpenSPIM.setActive(true);
				}
				if (siteMM.compareTo(siteOpenSPIM) > 0) {
					final Field updateSitesField = files.getClass().getDeclaredField("updateSites");
					updateSitesField.setAccessible(true);
					@SuppressWarnings("unchecked")
					final Map<String, UpdateSite> updateSites =
							(Map<String, UpdateSite>) updateSitesField.get(files);
					// OpenSPIM needs to be able to override Micro-Manager-dev
					updateSites.remove(siteOpenSPIM.getName());
					updateSites.put(siteOpenSPIM.getName(), siteOpenSPIM);
				}
				files.write();
				commandService.run(ImageJUpdater.class, true);
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

}
