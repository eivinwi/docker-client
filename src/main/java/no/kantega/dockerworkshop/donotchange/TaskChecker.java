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

    /**
     *
     * @param imageName Name of docker image to inspect (see list by using 'docker images').
     * @param tokenChain Chain of JSON-tokens to look for (ex: {"Config", "Hostname"})
     * @param expectedContent List of expected tokens at the end of the token chain.
     * @return true if all expected tokens are found.
     */
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

    /**
     * Run a script present in the docker container
     * @param location absolute path to the bash file
     */
    public void runScript(String location) {
        String[] cmd = {"/bin/sh", location};
        try {
            Process pr = Runtime.getRuntime().exec(cmd);
            pr.waitFor();
            BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line = "";
            while((line = buf.readLine()) != null) {
                System.out.println(line);
                //TODO
            }

        } catch (Exception e) {
            System.err.println("Error while running script file: " + e);
        }
    }

}
