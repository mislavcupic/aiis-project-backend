package hr.algebra.semregprojectbackend.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hr.algebra.semregprojectbackend.command.SeminarUpdateCommand;
import hr.algebra.semregprojectbackend.dto.SeminarDTO;
import hr.algebra.semregprojectbackend.service.SeminarService;

import java.util.Optional;
import java.util.Date;

@Component("seminarSaver") // Ime Bean-a mora odgovarati Delegate Expression-u u BPMN-u
public class SeminarSaver implements JavaDelegate {

    private static final Logger logger = LoggerFactory.getLogger(SeminarSaver.class);

    private final SeminarService seminarService;
    private final JmsTemplate jmsTemplate;

    @Autowired
    public SeminarSaver(SeminarService seminarService, JmsTemplate jmsTemplate) {
        this.seminarService = seminarService;
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        logger.info("Executing 'Save Seminar Data' Service Task (via Camunda)...");

        String topic = (String) execution.getVariable("seminarTopic");
        String lecturer = (String) execution.getVariable("seminarLecturer");

        SeminarUpdateCommand seminarUpdateCommand = new SeminarUpdateCommand(topic, lecturer);

        jmsTemplate.convertAndSend("seminar-requests", "Primljen zahtjev za kreiranje seminara s temom: " + seminarUpdateCommand.getTopic() + " (iz BPM procesa - spremam u bazu).");
        logger.info("JMS: Attempting to save seminar via Camunda process for topic: {}", seminarUpdateCommand.getTopic());

        Optional<SeminarDTO> savedSeminar = seminarService.save(seminarUpdateCommand); // Ovo će sada biti JEDINO mjesto za spremanje!

        if (savedSeminar.isPresent()) {
            jmsTemplate.convertAndSend("seminar-responses", "Uspješno kreiran seminar s ID: "
                    + savedSeminar.get().getId() + ", tema: " + savedSeminar.get().getTopic() + " (iz BPM procesa).");
            logger.info("JMS: Successfully created seminar with ID: {}, topic: {}", savedSeminar.get().getId(), savedSeminar.get().getTopic());

            // Postavljanje varijabli natrag u proces
            execution.setVariable("seminarId", savedSeminar.get().getId());
            execution.setVariable("seminarTopic", savedSeminar.get().getTopic()); // Možda već postoji ali bolje da se osiguraš
            execution.setVariable("seminarLecturer", savedSeminar.get().getLecturer()); // Možda već postoji
            execution.setVariable("registrationSaveStatus", "SUCCESS");
            execution.setVariable("registrationDate", new Date());
            execution.setVariable("seminarApproved", true);
        } else {
            jmsTemplate.convertAndSend("seminar-responses", "Neuspješno kreiranje seminara s temom: " + seminarUpdateCommand.getTopic() + " (iz BPM procesa).");
            logger.warn("JMS: Failed to create seminar for topic: {}", seminarUpdateCommand.getTopic());

            execution.setVariable("registrationSaveStatus", "FAILED");
            execution.setVariable("seminarApproved", false);

            // Opcionalno: Bacanje iznimke može zaustaviti proces ili ga preusmjeriti na putanju za greške u BPMN-u
            // throw new RuntimeException("Failed to save seminar: " + topic);
        }
    }
}/*package hr.algebra.semregprojectbackend.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hr.algebra.semregprojectbackend.command.SeminarUpdateCommand; // Provjeri ovaj paket
import hr.algebra.semregprojectbackend.dto.SeminarDTO; // Provjeri ovaj paket
import hr.algebra.semregprojectbackend.service.SeminarService; // Provjeri ovaj paket

import java.util.Optional;
import java.util.Date;

@Component("registrationSaver") // Ime Bean-a mora odgovarati Delegate Expression-u u BPMN-u
public class RegistrationSaver implements JavaDelegate {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationSaver.class);

    private final SeminarService seminarService;
    private final JmsTemplate jmsTemplate;

    @Autowired
    public RegistrationSaver(SeminarService seminarService, JmsTemplate jmsTemplate) {
        this.seminarService = seminarService;
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        logger.info("Executing 'Save Registration Data' Service Task...");

        // Dohvaćanje samo potrebnih varijabli iz procesa
        String topic = (String) execution.getVariable("seminarTopic");
        String lecturer = (String) execution.getVariable("seminarLecturer");

        // Kreiranje SeminarUpdateCommand objekta samo s dostupnim varijablama
        // PRILAGODI OVO PREMA TVOJEM KONSTRUKTORU ILI SETTERIMA U SeminarUpdateCommand
        SeminarUpdateCommand seminarUpdateCommand = new SeminarUpdateCommand(topic,lecturer); // Primjer konstruktora

        // --- Tvoja postojeća logika za slanje JMS poruka i spremanje ---
        jmsTemplate.convertAndSend("seminar-requests", "Primljen zahtjev za kreiranje seminara s temom: " + seminarUpdateCommand.getTopic() + " (iz BPM procesa).");
        logger.info("JMS: Sending request for seminar creation for topic: {}", seminarUpdateCommand.getTopic());

        Optional<SeminarDTO> savedSeminar = seminarService.save(seminarUpdateCommand);

        if (savedSeminar.isPresent()) {
            jmsTemplate.convertAndSend("seminar-responses", "Uspješno kreiran seminar s ID: "
                    + savedSeminar.get().getId() + ", tema: " + savedSeminar.get().getTopic() + " (iz BPM procesa).");
            logger.info("JMS: Successfully created seminar with ID: {}, topic: {}", savedSeminar.get().getId(), savedSeminar.get().getTopic());

            // Postavljanje varijabli natrag u proces
            execution.setVariable("seminarId", savedSeminar.get().getId());
            execution.setVariable("seminarTopic", savedSeminar.get().getTopic());
            execution.setVariable("registrationSaveStatus", "SUCCESS");
            execution.setVariable("registrationDate", new Date());
            execution.setVariable("seminarApproved", true);
        } else {
            jmsTemplate.convertAndSend("seminar-responses", "Neuspješno kreiranje seminara s temom: " + seminarUpdateCommand.getTopic() + " (iz BPM procesa).");
            logger.warn("JMS: Failed to create seminar for topic: {}", seminarUpdateCommand.getTopic());

            execution.setVariable("registrationSaveStatus", "FAILED");
            execution.setVariable("seminarApproved", false);

            // Opcionalno: Bacanje iznimke može zaustaviti proces ili ga preusmjeriti na putanju za greške u BPMN-u
            // throw new RuntimeException("Failed to save seminar: " + topic);
        }
    }
}

 */