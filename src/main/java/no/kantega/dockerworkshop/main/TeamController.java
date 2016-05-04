package no.kantega.dockerworkshop.main;

import java.io.DataOutputStream;
import java.net.URL;
import java.util.Arrays;

import no.kantega.dockerworkshop.donotchange.TaskChecker;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;

/**
 * Hei!
 * Meningen er at du skal løse oppgavene uten å se i denne filen.
 * Ikke noe juks!
 */
@Controller
public class TeamController implements InitializingBean {

    @Value("${team.name}")
    private String teamName;
    @Value("${server.url}")
    private String url;

    private String name;

    private TaskChecker taskChecker;

    @Autowired
    public TeamController(TaskChecker taskChecker) {
        this.taskChecker = taskChecker;
    }

    public void afterPropertiesSet() {
        //trengs ikke?
    }

    @RequestMapping(value = "/test")
    @ResponseBody
    public String test(HttpServletRequest request) {
/*        boolean test = taskChecker.inspectDockerImage(
            "testscripts",
            Arrays.asList("Config", "Entrypoint"),
            Arrays.asList("uname", "-a")
        );*/
        return "Application is working, your team-name is currently: " + teamName;
    }

    @RequestMapping(value = "/apply", method = RequestMethod.POST)
    public void apply(HttpServletRequest request) {
        name = (request.getParameter("name") != null)? request.getParameter("name") : "";


    }


    @RequestMapping(value = "/teamName", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getTeamName() {
        return teamName;
    }

    // HTTP POST request
    @SuppressWarnings("unused")
    private void sendPost(String url) throws Exception {
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add request header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "DockerClient");
        con.setRequestProperty("Accept-Language", "no-NO,no;");
        con.setRequestProperty("name", name);

        //String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        //wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        System.out.println("\nSending 'POST' request to URL : " + url);
        int responseCode = con.getResponseCode();
        //System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);

        /*
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        //print result
        System.out.println(response.toString());*/
    }

    @ResponseBody
    @RequestMapping(value = "/check", produces = MediaType.TEXT_PLAIN_VALUE)
    public String checkTask(@RequestParam String task) {
        if("4".equals(task)) {
            return "changeit".equals(teamName)? "Teamnavn skal forandres" : "korrekt";
        }
        return taskChecker.checkTask(task);
    }
}

