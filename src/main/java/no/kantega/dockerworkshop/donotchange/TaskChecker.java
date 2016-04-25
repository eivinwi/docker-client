package no.kantega.dockerworkshop.donotchange;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * Hei!
 * Meningen er at du skal løse oppgavene uten å se i denne filen.
 * Ikke noe juks!
 */
@Component
public class TaskChecker {

    private boolean fileContainsAll(String location, List<String> content) {
        StringBuilder sb = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(location))) {
            stream.forEach(sb::append);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String line = sb.toString();
        for(String s : content) {
            if(!line.contains(s)) {
                return false;
            }
        }
        return true;
    }

    private boolean fileContains(String location, String content) {
        String value = null;
        try (Stream<String> stream = Files.lines(Paths.get(location))) {
            value = stream.filter(line -> line.contains(content)).findAny().orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (value != null && value.equals(content));
    }

    private boolean fileStartsWith(String location, String content) {
        String value = null;
        try (Stream<String> stream = Files.lines(Paths.get(location))) {
            value = stream.filter(line -> line.startsWith(content)).findAny().orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (value != null && value.equals(content));
    }

    private boolean dockerfileStartsWith(String content) {
        return fileStartsWith("docker/Dockerfile", content);
    }

    private boolean dockerfileContains(String content) {
        return fileContains("docker/Dockerfile", content);
    }

    private boolean dockerfileContainsAll(List<String> content) {
        return fileContainsAll("docker/Dockerfile", content);
    }

    private boolean dockerImageExists(String name) {
        List<String> feedback = runCommand("docker images");
        for(String line : feedback) {
            if(line.contains(name)) {
                return true;
            }
        }
        return false;
    }

    private boolean dockerContainerRunning(String name) {
        List<String> feedback = runCommand("docker ps");
        for(String line : feedback) {
            if(line.contains(name)) {
                return true;
            }
        }
        return false;
    }

    private List<String> runCommand(String cmd) {
        List<String> lines = new ArrayList<>();
        Runtime run = Runtime.getRuntime();
        BufferedReader buf;
        try {
            Process pr = run.exec(cmd);
            pr.waitFor();
            buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line;
            while( (line = buf.readLine()) != null ) {
                lines.add(line);
            }
            buf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lines;
    }

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
            String line;
            while((line = buf.readLine()) != null) {
                System.out.println(line);
                //TODO
            }

        } catch (Exception e) {
            System.err.println("Error while running script file: " + e);
        }
    }

    public String checkTask(String task) {
        switch(task) {
            case "1": return "sjekk ikke implementert";
            case "2a": return checkTask2a();
            case "2b": return checkTask2b();
            case "2c": return checkTask2c();
            case "3a": return checkTask3a();
            case "3b": return checkTask3b();
            case "4": //sjekkes i TaskController, bør ikke komme hit
                return "feil";
            case "5": return "sjekk ikke implementert";
            case "6a": return checkTask6a();
            case "6b": return checkTask6b();
            case "6c": return checkTask6c();
            case "7": return "sjekk ikke implementert";
            case "8a": return checkTask8a();
            case "8b": return "sjekk ikke implementert";
            case "9a": return "sjekk ikke implementert";
            case "9b": return "sjekk ikke implementert";
            case "10": return "sjekk ikke implementert";
            default: break;
        }
        return "Ugyldig oppgave-nummer.";
    }

    private String checkTask2a() {
        boolean add = dockerfileStartsWith("ADD hei.sh");
        if(!add) {
            return "Dockerfilen legger ikke til scriptet hei.sh";
        }
        boolean cmd = dockerfileStartsWith("CMD");
        boolean entry = dockerfileStartsWith("ENTRYPOINT");
        if(!(cmd || entry)) {
            return "Dockerfilen kjører ikke scriptet hei.sh";
        }
        if(dockerfileContains("/bin/sh") || dockerfileContains("/bin/bash")) {
            return "Dockerfilen gjør ikke et kall til Bash under kjøring";
        }
        return "korrekt";
    }

    private String checkTask2b() {
        if(dockerfileStartsWith("FROM ubuntu:latest")) {
            return "korrekt";//true;
        }
        boolean ubuntu = dockerfileStartsWith("FROM ubuntu");
        if(ubuntu) {
            return "image er korrekt, men det bør settes til å alltid bruke siste versjon.";
        }
        return "feil";
    }

    //TODO: logikk i sjekker
    private String checkTask2c() {
        /*
        if(dockerfileStartsWith("RUN apt-get install") || dockerfileStartsWith("RUN apt install")) {
            return "nesten, men pakkelistene må oppdateres før ting kan installeres! (prøv med apt-get update)";
        }
        boolean updAndInst = dockerfileStartsWith("RUN apt-get update && apt-get install -y curl") ||
                dockerfileStartsWith("RUN apt update && apt install -y curl");

        return updAndInst? "korrekt" : "feil";*/
        return "korrekt";
    }

    private String checkTask3a() {
        boolean add = dockerfileStartsWith("ADD");
        if(!add) {
            return "Mangler å adde jar-filen";
        }
        boolean path = dockerfileContains("target/docker-client-1.0-SNAPSHOT.jar");
        if(!path) {
            return "path til jar-filen er feil";
        }
        boolean entry = dockerfileStartsWith("ENTRYPOINT [\"java\", \"-jar\"");
        if(!entry) {
            return "eksekverings-kommandoen er feil";
        }
        return "korrekt";
    }

    private String checkTask3b() {
        return dockerImageExists("dockerclient/docker-client-mvn")?
                "korrekt" : "ingen dockerimage med navn dockerclient/docker-client-mvn har blitt opprettet";
    }

    private String checkTask6a() {
        return task6Common("virtualbox");
    }

    private String checkTask6b() {
        return task6Common("digitalocean");
    }

    private String checkTask6c() {
        return "sjekk ikke implementert";
    }

    private String task6Common(String driver) {
        List<String> machines = runCommand("docker-machine ls");
        if(machines.size() < 2) {
            return "Ingen docker-machines er opprettet";
        }
        machines.remove(0); //fjerner kolonne med rad-beskrivelser

        for(String machine : machines) {
            String[] s = machine.split("\\s+");
            if(s.length < 3) {
                System.err.println("Misshaped line: " + machine);
            } else {
                if(driver.equalsIgnoreCase(s[2])) {
                    return "korrekt";
                }
            }
        }
        return "Ingen docker-machine som kjører på " + driver + " er opprettet";
    }

    private String checkTask8a() {
        List<String> machines = runCommand("docker-machine ls");
        if(machines.size() < 2) {
            return "Ingen docker-machines er opprettet.";
        }
        if(machines.size() < 5) {
            return "Det er ikke opprettet nok docker-machines enda!";
        }

        int masters = 0;
        String masterSwarm = "";
        Map<String, Integer> map = new HashMap<>();
        for(String machine : machines) {
            String[] s = machine.split("\\s+");
            if(s.length < 6) {
                System.err.println("Misshaped line: " + machine);
            } else {
                String swarm = s[5];

                if(swarm != null && !swarm.isEmpty()) {
                    if(!map.containsKey(swarm)) {
                        map.put(swarm, 1);
                    } else {
                        Integer count  = map.get(swarm);
                        map.put(swarm, count+1);
                    }

                    if(s[6] != null && s[6].contains("master")) {
                        masters++;
                        masterSwarm = s[5];
                    }
                }
            }
        }
        if(masters < 1 || masterSwarm == null || masterSwarm.isEmpty()) {
            return "Swarmen mangler en swarm master.";
        }

        for(Map.Entry<String, Integer> entry : map.entrySet()) {
            if(entry.getValue() > 3 && masterSwarm.equals(entry.getKey())) {
                return "korrekt";
            }
        }
        return "Ingen swarm med en swarm master og 3 eller flere noder finnes.";
    }
}
