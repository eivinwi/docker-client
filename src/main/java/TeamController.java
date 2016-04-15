import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.Instant;
import java.util.*;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;

@Controller
@PropertySource("classpath:team.properties")
public class TeamController implements InitializingBean {

    //@Value("${team.name}")
    //private String teamName;
    @Value("${server.url}")
    private String url;

    private String name;

    public void afterPropertiesSet() {
        //trengs ikke?
    }


    @RequestMapping(value = "/apply", method = RequestMethod.POST)
    public void apply(HttpServletRequest request) {
        name = (request.getParameter("name") != null)? request.getParameter("name") : "";


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


}

