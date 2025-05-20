package hr.algebra.semregprojectbackend.controller;

import hr.algebra.semregprojectbackend.command.SeminarCreateCommand;
import hr.algebra.semregprojectbackend.command.SeminarUpdateCommand;
import hr.algebra.semregprojectbackend.dto.SeminarDTO;
import hr.algebra.semregprojectbackend.service.SeminarService;
import hr.algebra.semregprojectbackend.service.jbpmservice.JbpmService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/seminars")
public class SeminarController {

    private final SeminarService seminarService;
    private final JmsTemplate jmsTemplate;
    private final JbpmService jbpmService;


    public SeminarController(SeminarService seminarService, JmsTemplate jmsTemplate, JbpmService jbpmService) {
        this.seminarService = seminarService;
        this.jmsTemplate = jmsTemplate;
        this.jbpmService = jbpmService;

    }

    @GetMapping
    public ResponseEntity<List<SeminarDTO>> getAllSeminars() {
        jmsTemplate.convertAndSend("seminar-requests", "Primljen GET zahtjev za sve seminare.");
        List<SeminarDTO> seminars = seminarService.getAllSeminars();
        jmsTemplate.convertAndSend("seminar-responses", "Uspješno dohvaćeno " + seminars.size() + " seminara.");
        return ResponseEntity.ok(seminars);
    }

    @GetMapping("/search")
    public ResponseEntity<SeminarDTO> findByTopicName(@RequestParam String topic) {
        jmsTemplate.convertAndSend("seminar-requests", "Primljen GET zahtjev za seminar s temom (preko /search): " + topic);
        Optional<SeminarDTO> found = seminarService.findSeminarByTopic(topic);
        if (found.isPresent()) {
            jmsTemplate.convertAndSend("seminar-responses", "Uspješno pronađen seminar s temom (preko /search): " + topic);
            return ResponseEntity.ok(found.get());
        } else {
            jmsTemplate.convertAndSend("seminar-responses", "Seminar s temom (preko /search): " + topic + " nije pronađen.");
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<SeminarDTO> createSeminar(@RequestBody @Valid SeminarUpdateCommand seminarUpdateCommand) {
        jmsTemplate.convertAndSend("seminar-requests", "Primljen POST zahtjev za kreiranje seminara s temom: " + seminarUpdateCommand.getTopic());
        Optional<SeminarDTO> savedSeminar = seminarService.save(seminarUpdateCommand);
        if (savedSeminar.isPresent()) {
            jmsTemplate.convertAndSend("seminar-responses", "Uspješno kreiran seminar s ID: "
                    + savedSeminar.get().getId() + ", tema: " + savedSeminar.get().getTopic());
            // Pokreni jBPM proces s podacima
            jbpmService.startJbpmProcess(seminarUpdateCommand);

            return ResponseEntity.ok(savedSeminar.get());
        } else {
            jmsTemplate.convertAndSend("seminar-responses", "Neuspješno kreiranje seminara s temom: " + seminarUpdateCommand.getTopic());
            return ResponseEntity.badRequest().build();
        }
    }

//@PostMapping("/create")
//public ResponseEntity<SeminarDTO> createSeminar(@RequestBody @Valid SeminarUpdateCommand seminarUpdateCommand) {
//    jmsTemplate.convertAndSend("seminar-requests", "Primljen POST zahtjev za kreiranje seminara s temom: " + seminarUpdateCommand.getTopic());
//    Optional<SeminarDTO> savedSeminar = seminarService.save(seminarUpdateCommand);
//    if (savedSeminar.isPresent()) {
//        jmsTemplate.convertAndSend("seminar-responses", "Uspješno kreiran seminar s ID: " + savedSeminar.get().getId() + ", tema: " + savedSeminar.get().getTopic());
//
//        // Pokrenite workflow ovdje - ISPRAVNO PROSLJEĐIVANJE ARGUMENATA
//        Map<String, Object> processParams = new HashMap<>();
//        processParams.put("topic", savedSeminar.get().getTopic());
//        processParams.put("lecturer", savedSeminar.get().getLecturer());
//      //  workflowService.startCreateSeminarProcess(processParams);
//
//        return ResponseEntity.ok(savedSeminar.get());
//    } else {
//        jmsTemplate.convertAndSend("seminar-responses", "Neuspješno kreiranje seminara s temom: " + seminarUpdateCommand.getTopic());
//        return ResponseEntity.badRequest().build();
//    }
//}

    @PutMapping // Mapira PUT zahtjeve na /api/seminars
    public ResponseEntity<SeminarDTO> updateSeminar(@RequestParam Long id, @RequestBody SeminarDTO seminarDTO) {
        jmsTemplate.convertAndSend("seminar-requests", "Primljen PUT zahtjev za ažuriranje seminara s ID: " + id + ", nova tema: " + seminarDTO.getTopic());
        SeminarDTO updated = seminarService.updateSeminar(id, seminarDTO);
        jmsTemplate.convertAndSend("seminar-responses", "Uspješno ažuriran seminar s ID: " + updated.getId() + ", nova tema: " + updated.getTopic());
        return ResponseEntity.ok(updated);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteSeminar(@PathVariable Long id) {
        jmsTemplate.convertAndSend("seminar-requests", "Primljen DELETE zahtjev za brisanje seminara s ID: " + id);
        seminarService.deleteById(id);
        jmsTemplate.convertAndSend("seminar-responses", "Uspješno obrisan seminar s ID: " + id);
    }

    @GetMapping("/{topic}")
    public ResponseEntity<SeminarDTO> findByTopic(@PathVariable String topic) {
        jmsTemplate.convertAndSend("seminar-requests", "Primljen GET zahtjev za seminar s temom (preko /{topic}): " + topic);
        Optional<SeminarDTO> found = seminarService.findSeminarByTopic(topic);
        if (found.isPresent()) {
            jmsTemplate.convertAndSend("seminar-responses", "Uspješno pronađen seminar s temom (preko /{topic}): " + topic);
            return ResponseEntity.ok(found.get());
        } else {
            jmsTemplate.convertAndSend("seminar-responses", "Seminar s temom (preko /{topic}): " + topic + " nije pronađen.");
            return ResponseEntity.notFound().build();
        }
    }
}