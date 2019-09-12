package net.bhl.matsim.uam.analysis.uamstations.run;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.io.FileUtils;

public class BatchConvertUAMStationsFromUAMVehicles {
	// PROVIDE PARENT FOLDER OF OUTPUT FOLDERS
	private static String vehiclefile = "output_uam_vehicles.xml";
	private static String networkfile = "output_network.xml";

	public static void main(final String[] args) throws IOException {
		File folder = Paths.get(args[0]).toFile();

		String[] ext = {"gz", "xml"};
		Collection<File> potentialFiles = FileUtils.listFiles(folder, ext, true);
		
		String[] ecl = {"csv"};
		Collection<File> alreadyExistingFiles = FileUtils.listFiles(folder, ecl, true);
		Collection<String> alreadyExistingFileNames = new HashSet<String>();
		for (File f : alreadyExistingFiles) {
			alreadyExistingFileNames.add(f.getAbsolutePath());
		}
		
		for (File f : potentialFiles) {
			if (!f.getName().contains(vehiclefile) || alreadyExistingFileNames.contains(f.getAbsolutePath() + "_stations.csv"))
				continue;
			
			System.err.println("Working on: " + f.getAbsolutePath());
			String network = f.getAbsolutePath().replace(vehiclefile, networkfile);
			ConvertUAMStationsFromUAMVehicles.extract(network, f.getAbsolutePath(), f.getAbsolutePath() + "_stations.csv");
		}
		
		System.err.println("done.");
	}
}