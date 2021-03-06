package net.bhl.matsim.uam.listeners;

import com.google.inject.Inject;
import net.bhl.matsim.uam.run.UAMConstants;
import org.apache.log4j.Logger;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.controler.events.ShutdownEvent;
import org.matsim.core.controler.listener.ShutdownListener;
import org.matsim.core.utils.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This listener copies the input UAM Vehicle file into the output folder after
 * the simulation ends
 *
 * @author Aitanm (Aitan Militao), RRothfeld (Raoul Rothfeld)
 */

public class UAMShutdownListener implements ShutdownListener {
	private static final Logger log = Logger.getLogger(UAMListener.class);
	@Inject
	private OutputDirectoryHierarchy controlerIO;

	@Override
	public void notifyShutdown(ShutdownEvent event) {
		try {
			writeUAMVehiclesFile(event);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	private void writeUAMVehiclesFile(ShutdownEvent event) throws MalformedURLException {
		String configPath = event.getServices().getConfig().getContext().getPath();
		int index = configPath.lastIndexOf('/');
		configPath = configPath.substring(0, index + 1).replace("%20", " ");

		String uamFileName = event.getServices().getConfig().getModules().get(UAMConstants.uam).getParams().get("inputUAMFile");

		InputStream fromStream = IOUtils.getInputStream(new URL(configPath + uamFileName));
		OutputStream toStream = IOUtils.getOutputStream(new URL(controlerIO.getOutputFilename("output_uam_vehicles.xml.gz")),true);

		try {
			IOUtils.copyStream(fromStream, toStream);

			fromStream.close();
			toStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
