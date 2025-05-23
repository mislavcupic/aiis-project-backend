package hr.algebra.semregprojectbackend.delegate;


import hr.algebra.semregprojectbackend.service.SeminarService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

// Dodaj logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component // Obavezno da bi Spring prepoznao kao komponentu i injektirao SeminarService
public class SeminarDeleter implements JavaDelegate {

    private static final Logger logger = LoggerFactory.getLogger(SeminarDeleter.class);

    private final SeminarService seminarService;

    // Spring će automatski injektirati SeminarService zbog @Component
    public SeminarDeleter(SeminarService seminarService) {
        this.seminarService = seminarService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        // Dohvati varijablu iz Camunda procesa
        Long seminarId = (Long) execution.getVariable("seminarId");

        logger.info("Executing SeminarDeleter for seminar ID: {}", seminarId);

        // Pozovi metodu servisa za brisanje
        seminarService.deleteById(seminarId);

        logger.info("Seminar ID {} successfully deleted from database.", seminarId);
    }
}