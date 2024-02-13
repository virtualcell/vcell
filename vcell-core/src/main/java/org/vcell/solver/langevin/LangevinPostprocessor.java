package org.vcell.solver.langevin;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;


public class LangevinPostprocessor {

    public static final String FULL_BOND_DATA_CSV = "FullBondData.csv";
    public static final String FULL_COUNT_DATA_CSV = "FullCountData.csv";
    public static final String FULL_STATE_COUNT_DATA_CSV = "FullStateCountData.csv";
    public static final String SITE_PROPERTY_DATA_CSV__NOT_USED_ = "SitePropertyData.csv";

    /**
     * Converts Langevin's output to a singular .IDA file in dir
     * @param langevinOutputDir : The path to the folder which contains LangevinNoVis01's output files
     * @param idaFile: The path to the file to the IdaFile
     */
    public static void writeIdaFile(Path langevinOutputDir, Path idaFile) throws IOException {
//		int cluster_time = 0;
        // Initialize .ida file
        FileReader fullBondDataReader = null;
        FileReader fullCountDataReader = null;
        FileReader fullStateCountDataReader = null;

        try {
            fullBondDataReader = new FileReader(new File(langevinOutputDir.toFile(), FULL_BOND_DATA_CSV));
            fullCountDataReader = new FileReader(new File(langevinOutputDir.toFile(), FULL_COUNT_DATA_CSV));
            fullStateCountDataReader = new FileReader(new File(langevinOutputDir.toFile(), FULL_STATE_COUNT_DATA_CSV));
            //FileReader sitePropertyDataReader = new FileReader(langevinOutputDir + SITE_PROPERTY_DATA_CSV);
            //FileReader clustersTimeReader = new FileReader(langevinOutputDir + "Clusters_Time_.csv")

            CSVParser fullBondData = CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrailingDelimiter().withTrim().parse(fullBondDataReader);
            CSVParser fullCountData = CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrailingDelimiter().withTrim().parse(fullCountDataReader);
            CSVParser fullStateCountData = CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrailingDelimiter().withTrim().parse(fullStateCountDataReader);
            //CSVParser sitePropertyData = CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrailingDelimiter().withTrim().parse(sitePropertyDataReader);
            //CSVParser clustersTime = CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrailingDelimiter().withTrim().parse(clustersTimeReader);

            List<String> fullBondHeaders = fullBondData.getHeaderNames();
            List<String> fullCountHeaders = fullCountData.getHeaderNames();
            List<String> fullStateCountHeaders = fullStateCountData.getHeaderNames();
            //List<String> sitePropertyHeaders = sitePropertyData.getHeaderNames();
            //List<String> clustersTimeHeaders = clustersTime.getHeaderNames();

            var fullBondDataIter = fullBondData.iterator();
            var fullCountDataIter = fullCountData.iterator();
            var fullStateCountDataIter = fullStateCountData.iterator();
            //var sitePropertyDataIter = sitePropertyData.iterator();
            //var clustersTimeIter = clustersTime.iterator();

            // Create the header for the .ida file, which is a combination of all the headers from the Langevin output files
            // 'Time' headers are repeated, so we remove all 'Time' headers, and insert a single 't' header
            List<String> headers = new ArrayList<>();
            headers.add("t");
            headers.addAll(fullBondHeaders.stream().filter(s -> !s.equals("Time")).toList());
            headers.addAll(fullCountHeaders.stream().filter(s -> !s.equals("Time")).toList());
            headers.addAll(fullStateCountHeaders.stream().filter(s -> !s.equals("Time")).toList());
            //headers.addAll(sitePropertyHeaders.stream().filter(s -> !s.equals("Time")).toList());
            //headers.addAll(clustersTimeHeaders.stream().filter(s -> !s.equals("Time")).toList());
            List<String> combined_headers = headers.stream().map(s -> s.replace(" ", "_").replace(":","")).toList();


            try (FileWriter writer = new FileWriter(idaFile.toFile().getAbsoluteFile(), false)) {
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withFirstRecordAsHeader().withDelimiter(' '));

                // print header row
                String header_row = String.join(":", combined_headers);
                csvPrinter.print(header_row);

                // print each data row
                boolean bFirstRow = true;
                while (true) {

                    CSVRecord fullBondDataRecord = fullBondDataIter.hasNext() ? fullBondDataIter.next() : null;
                    CSVRecord fullCountDataRecord = fullCountDataIter.hasNext() ? fullCountDataIter.next() : null;
                    CSVRecord fullStateCountDataRecord = fullStateCountDataIter.hasNext() ? fullStateCountDataIter.next() : null;
                    //CSVRecord sitePropertyDataRecord = sitePropertyDataIter.hasNext() ? sitePropertyDataIter.next() : null;
                    //CSVRecord clustersTimeRecord = clustersTimeIter.hasNext() ? clustersTimeIter.next() : null;

                    if (fullBondDataRecord == null || fullCountDataRecord == null || fullStateCountDataRecord == null) {
                        break;
                    }

                    // skip the rest of the rows if padded with zero times (an artifact of the way the data is written)
                    double time = fullBondDataRecord.get(0) != null ? Double.parseDouble(fullBondDataRecord.get(0)) : 0.0;
                    if (!bFirstRow && time == 0.0){
                        break;
                    }
                    bFirstRow = false;

                    // use fullBondData's first column as the time column (starts at 0 index)
                    for (int i = 0; i < fullBondDataRecord.size(); i++) {
                        csvPrinter.print(fullBondDataRecord.get(i));
                    }
                    for (int i = 1; i < fullCountDataRecord.size(); i++) {
                        csvPrinter.print(fullCountDataRecord.get(i));
                    }
                    for (int i = 1; i < fullStateCountDataRecord.size(); i++) {
                        csvPrinter.print(fullStateCountDataRecord.get(i));
                    }
                    //for (int i = 1; i < sitePropertyDataRecord.size(); i++) {
                    //    csvPrinter.print(sitePropertyDataRecord.get(i));
                    //}
                    //for (int i = 1; i < clustersTimeRecord.size(); i++) {
                    //	csvPrinter.print(clustersTimeRecord.get(i));
                    //}
                    csvPrinter.println(); // end line
                }
                csvPrinter.flush();
            }
        } finally {
            if(fullBondDataReader != null) {
                fullBondDataReader.close();
            }
            if(fullCountDataReader != null) {
                fullCountDataReader.close();
            }
            if(fullStateCountDataReader != null) {
                fullStateCountDataReader.close();
            }
        }
    }

	public ArrayList<String> readClustersToArray_NOT_USED_(String path) throws FileNotFoundException {

		File full = new File(path);
		File dir = new File(full.getParent());
		ArrayList<String> array = new ArrayList<>();

		String[] file_paths = dir.list();
		for (String f: file_paths) {
			if (f.contains("Clusters_Time_")) {
				try(Scanner scanner = new Scanner(new File(dir, f))) {
					while (scanner.hasNextLine()) {
						String line = scanner.nextLine();
						line = line.replace(",",":");
						int index = line.indexOf(":");
						if (!(line.equals(""))) {
							if (array.size() < 1) {
								array.add(line.substring(0, index).replace(" ", "_")); //
								array.add(line.substring(index + 1).trim());
							} else {
								array.add(line.substring(index + 1));
								break;
							}
						}
					}
				}
			}
		}
		return array;
	}

}
