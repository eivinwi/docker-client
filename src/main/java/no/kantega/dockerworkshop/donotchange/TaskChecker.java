package no.kantega.dockerworkshop.donotchange;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

@Component
public class TaskChecker {

    public boolean inspectDockerImage(String imageName, List<String> tokenChain, List<String> expectedContent) {
        String cmd = "docker inspect " + imageName;
        Runtime run = Runtime.getRuntime();
        JsonParser parser;

        try {
            Process pr = run.exec(cmd);
            pr.waitFor();
            BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            JsonFactory factory = new JsonFactory();
            parser = factory.createParser(buf);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(parser);
            parser.close();

            int index = 0;
            while(node != null && index < tokenChain.size()) {
                node = node.findValue(tokenChain.get(index));
                index++;
            }

            if(node == null || node.size() != expectedContent.size()) {
                return false;
            }

            Iterator<JsonNode> it = node.elements();
            while(it.hasNext()) {
                JsonNode n = it.next();
                if(!expectedContent.contains(n.asText())) {
                    return false;
                }
            }
            return true;

        } catch(Exception e) {
            System.err.println("Error while running bash script: " + e);
        }
        return false;
    }

    



}
