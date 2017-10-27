package com.sid;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

/**
 * Created by sidharthgoyal on 5/9/17.
 */
public class Main {
    public static void main(String[] args) throws IOException{
		List<String> assetClasses = Arrays.asList("Commodities", "Credit", "Equity", "Foreign_Exchange", "Rates");
    		ObjectMapper mapper = new ObjectMapper();
        for (String assetClass : assetClasses) {
        		CSVWriter csvOutput = new CSVWriter(new FileWriter("attrs.csv", true));
        		Map<String, String> uniqueProperties = new HashMap<>();
        		try(Stream<Path> paths = Files.walk(Paths.get("/Users/sidharthgoyal/Documents/jsonreader/src/main/resources/"+assetClass))) {
                paths.forEach((Path filePath) -> {
                    if (Files.isRegularFile(filePath)) {
//                        System.out.println(filePath);
                        try {
                            byte[] encoded = Files.readAllBytes(filePath);
                            String schema = new String(encoded);
                            JsonNode newNode = mapper.readTree(schema);
                            Iterator<Entry<String,JsonNode>> iter = newNode.get("properties").fields();
                            while (iter.hasNext()) {
    							Map.Entry<java.lang.String, com.fasterxml.jackson.databind.JsonNode> entry = (Map.Entry<java.lang.String, com.fasterxml.jackson.databind.JsonNode>) iter
    									.next();
    							name(entry, uniqueProperties,entry.getKey());
    							
    						}
                          
                        }catch (Exception e){

                        }
                    }
                });
            }
        		List<String[]> allLines = new ArrayList<>();
        		Iterator<Map.Entry<String, String>> it = uniqueProperties.entrySet().iterator();
        		while (it.hasNext()) {
					Map.Entry<java.lang.String, java.lang.String> entry = (Map.Entry<java.lang.String, java.lang.String>) it
							.next();
					allLines.add(new String[] {assetClass,entry.getKey(), entry.getValue()});
				}
        		csvOutput.writeAll(allLines);
        		csvOutput.close();
		}
    }
    
    public static void name(Entry<String,JsonNode> entry, Map<String, String> uniqueProperties, String currentRoot) {
		if (!getType(entry.getValue().fields()).equals("object")) {
			uniqueProperties.put(currentRoot+"."+entry.getKey(), getType(entry.getValue().fields()));
		}else {
			Iterator<Entry<String,JsonNode>> iter = entry.getValue().get("properties").fields();
			while (iter.hasNext()) {
				Map.Entry<java.lang.String, com.fasterxml.jackson.databind.JsonNode> entry2 = (Map.Entry<java.lang.String, com.fasterxml.jackson.databind.JsonNode>) iter
						.next();
				name(entry2, uniqueProperties, currentRoot);
			}
		}
	}
    
    public static String getType(Iterator<Entry<String,JsonNode>> iter) {
    		while (iter.hasNext()) {
				Map.Entry<java.lang.String, com.fasterxml.jackson.databind.JsonNode> entry = (Map.Entry<java.lang.String, com.fasterxml.jackson.databind.JsonNode>) iter
						.next();
				if(entry.getKey().equals("type"))
					return entry.getValue().asText();
			}
    		return null;
    }

}
