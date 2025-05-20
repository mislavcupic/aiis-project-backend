package hr.algebra.semregprojectbackend.service.jbpmservice;


import hr.algebra.semregprojectbackend.command.SeminarUpdateCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class JbpmService {

    @Autowired
    private RestTemplate restTemplate;

    public Long startJbpmProcess(SeminarUpdateCommand cmd) {
        String url = "http://localhost:8080/kie-server/services/rest/server/containers/seminar-kjar_1.0.0/processes/seminar-process/instances";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth("kieuser", "kiepass");

        Map<String, Object> variables = new HashMap<>();
        variables.put("topic", cmd.getTopic());
       //variables.put("presenter", cmd.getPresenter());
        variables.put("lecturer", cmd.getLecturer().toString());

        Map<String, Object> request = Map.of("variables", variables);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return Long.parseLong(response.getBody());
        }
        return null;
    }
}
