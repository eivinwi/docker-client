package no.kantega.dockerworkshop.main;

import no.kantega.dockerworkshop.donotchange.TaskChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Configuration
@EnableAutoConfiguration
@PropertySource("classpath:team.properties")
@ComponentScan(basePackages = {"no.kantega.dockerworkshop.main", "no.kantega.dockerworkshop.donotchange"})
public class Main {

    public static void main(String[] args) {
        //System.out.println("Hello World!");
        //Logger logger = LoggerFactory.getLogger(Main.class);
        //logger.info("DockerClient is starting!");
        SpringApplication.run(Main.class, args);
        //logger.info("DockerClient shutting down.");
        //loadProperties();
        /*String url = System.getProperty("team.url");
        try {
            //sendGet(url);
        } catch (Exception e) {
            System.err.println("Something went wrong: " + e);
        }*/
    }

    @Bean
    public TaskChecker taskChecker() {
        return new TaskChecker();
    }

    // HTTP GET request
    private static void sendGet(String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", "DockerClient");
        con.setRequestProperty("name", System.getProperty("team.name"));

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());
    }

    // HTTP POST request
    @SuppressWarnings("unused")
    private static void sendPost(String url) throws Exception {
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add request header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "DockerClient");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        //print result
        System.out.println(response.toString());
    }
}
