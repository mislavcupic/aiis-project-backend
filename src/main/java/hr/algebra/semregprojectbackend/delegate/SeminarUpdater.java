package hr.algebra.semregprojectbackend.delegate;

import hr.algebra.semregprojectbackend.dto.SeminarDTO;
import hr.algebra.semregprojectbackend.service.SeminarService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class SeminarUpdater implements JavaDelegate {

    private static final Logger logger = LoggerFactory.getLogger(SeminarUpdater.class);

    private final SeminarService seminarService;

    // Spring će automatski injektirati SeminarService zbog @Component
    public SeminarUpdater(SeminarService seminarService) {
        this.seminarService = seminarService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        // Dohvati varijable iz Camunda procesa
        Long seminarId = (Long) execution.getVariable("seminarId");
        String topic = (String) execution.getVariable("seminarTopic"); // Dohvati "seminarTopic" varijablu
        String lecturer = (String) execution.getVariable("seminarLecturer"); // Dohvati "seminarLecturer" varijablu

        // Kreiraj SeminarDTO objekt od dohvaćenih varijabli
        // Važno: Koristi ID dobiven iz procesnih varijabli
        SeminarDTO seminarToUpdate = new SeminarDTO(seminarId, topic, lecturer);

        logger.info("Executing SeminarUpdater for seminar ID: {} with new topic: {} and lecturer: {}", seminarId, topic, lecturer);

        // Pozovi metodu servisa za ažuriranje s ID-jem i DTO objektom
        SeminarDTO updatedSeminar = seminarService.updateSeminar(seminarId, seminarToUpdate);

        // Ako želiš proslijediti ažurirani seminar dalje u procesu (npr. za slanje notifikacije),
        // postavi ga kao varijablu. Inače, nije nužno.
      //  execution.setVariable("updatedSeminarObject", updatedSeminar); // Drugi naziv da se ne miješa s input DTO
        logger.info("Seminar ID {} successfully updated in database.", updatedSeminar.getId());
    }
}