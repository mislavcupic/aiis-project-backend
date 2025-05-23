package hr.algebra.semregprojectbackend.controller;

import org.camunda.bpm.engine.RuntimeService;
import hr.algebra.semregprojectbackend.command.SeminarUpdateCommand;
import hr.algebra.semregprojectbackend.dto.SeminarDTO;
import hr.algebra.semregprojectbackend.service.SeminarService;
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
    private final RuntimeService runtimeService;

    public SeminarController(SeminarService seminarService, JmsTemplate jmsTemplate, RuntimeService runtimeService) {
        this.seminarService = seminarService;
        this.jmsTemplate = jmsTemplate;
        this.runtimeService = runtimeService;
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

        // --- IZMJENJENI DIO ---
        try {
            Map<String, Object> processVariables = new HashMap<>();
            // Proslijedimo cijeli command objekt ili pojedinačna polja koja trebaju procesu
            processVariables.put("seminarTopic", seminarUpdateCommand.getTopic());
            processVariables.put("seminarLecturer", seminarUpdateCommand.getLecturer());

            // "createNewSeminar" je ID tvog BPMN procesa (iz Camunda Modeler Properties -> General -> ID)
            // Važno je da je u BPMN-u postavljeno isExecutable="true"
            // Startamo proces i dobijamo instancu procesa
            runtimeService.startProcessInstanceByKey("createNewSeminar", processVariables);

            // NE SPREMAMO SEMINAR OVDJE VIŠE! Spremanje će se dogoditi unutar Camunda procesa.
            // S obzirom da se seminar sprema asinkrono unutar Camunde,
            // ne možemo odmah vratiti SeminarDTO s ID-om.
            // Ovisno o zahtjevima, možemo:
            // 1. Vratiti 202 Accepted (što znači da je zahtjev prihvaćen i proces pokrenut)
            // 2. Vratiti samo SeminarUpdateCommand koji je poslan
            // 3. Implementirati asinkroni povratni poziv ili WebSocket ako je potrebno odmah vratiti potpuni SeminarDTO s ID-om.
            // Za sada, predlažem povrat 202 Accepted ili originalnog DTO-a bez ID-a.
            // Ako je cilj da se ID seminara vrati u odgovoru, morat ćeš pričekati završetak procesa
            // što ide protiv asinkronog prirode Camunde ili implementirati kompleksniji mehanizam.
            // Za ovaj primjer, pretpostavljam da je dovoljno da se proces pokrene.

            // Vraćamo DTO koji se bazira na podacima iz zahtjeva, bez ID-a baze, jer je ID tek stvoren u Camundi
            SeminarDTO responseDto = new SeminarDTO(null, seminarUpdateCommand.getTopic(), seminarUpdateCommand.getLecturer());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseDto); // 202 Accepted

        } catch (Exception e) {
            // Logiraj grešku umjesto System.out.println
            // Dodaj LOGGER na vrh klase: private static final Logger logger = LoggerFactory.getLogger(SeminarController.class);
            // logger.error("Greška prilikom pokretanja Camunda procesa za seminar: {}", seminarUpdateCommand.getTopic(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        // --- KRAJ IZMJENJENOG DIJELA ---
    }

    /* @PutMapping // Mapira PUT zahtjeve na /api/seminars
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
 */
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

    // --- Camunda za PUT (Ažuriranje) ---
    @PutMapping // Mapira PUT zahtjeve na /api/seminars
    public ResponseEntity<SeminarDTO> updateSeminar(@RequestParam Long id, @RequestBody SeminarDTO seminarDTO) {

        jmsTemplate.convertAndSend("seminar-requests", "Primljen PUT zahtjev za ažuriranje seminara s ID: " + id + ", nova tema: " + seminarDTO.getTopic());

        try {
            Map<String, Object> processVariables = new HashMap<>();
            processVariables.put("seminarId", id);
            processVariables.put("seminarTopic", seminarDTO.getTopic());
            processVariables.put("seminarLecturer", seminarDTO.getLecturer());

            // PROMJENI OVDJE! Koristi STVARNI ID TVOJ CAMUNDA PROCESA ZA UPDATE
            // Na primjer, ako je ID u BPMN-u "updateSeminarProcessV2" ili "seminar-update-flow"
            runtimeService.startProcessInstanceByKey("updateSeminar", processVariables); // <--- PROVJERI OVAJ ID U CAMUNDA MODELERU!

            return ResponseEntity.status(HttpStatus.ACCEPTED).build(); // 202 Accepted
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
/*
    // --- Camunda za DELETE (Brisanje) ---
    @ResponseStatus(HttpStatus.NO_CONTENT) // Vraća 204 No Content
    @DeleteMapping("/{id}")
    public void deleteSeminar(@PathVariable Long id) {
        jmsTemplate.convertAndSend("seminar-requests", "Primljen DELETE zahtjev za brisanje seminara s ID: " + id);

        try {
            Map<String, Object> processVariables = new HashMap<>();
            processVariables.put("seminarId", id);

            runtimeService.startProcessInstanceByKey("deleteSeminar", processVariables); // Koristi ID iz BPMN-a

            logger.info("Camunda proces za brisanje seminara pokrenut za ID: {}", id);
            // HTTP 204 No Content se vraća automatski jer je metoda void i ima @ResponseStatus
        } catch (Exception e) {
            logger.error("Greška prilikom pokretanja Camunda procesa za brisanje seminara s ID {}:", id, e);
            // U slučaju greške, ako želiš vratiti status različit od 204, morao bi promijeniti povratni tip metode u ResponseEntity<?>
            // Npr. return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

 */
/*package hr.algebra.semregprojectbackend.controller;


import org.camunda.bpm.engine.RuntimeService; // <-- Ovdje ga uvoziš!

import hr.algebra.semregprojectbackend.command.SeminarUpdateCommand;
import hr.algebra.semregprojectbackend.dto.SeminarDTO;
import hr.algebra.semregprojectbackend.service.SeminarService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.annotation.Lazy; // DODAJ OVAJ IMPORT
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/seminars")
public class SeminarController {

    private final SeminarService seminarService;
    private final JmsTemplate jmsTemplate;
    private final RuntimeService runtimeService; // <-- Ovdje ga deklariraš kao polje


    // Ažuriran konstruktor za injektiranje RuntimeService
    public SeminarController(SeminarService seminarService, JmsTemplate jmsTemplate, RuntimeService runtimeService) { // <-- Ovdje ga Spring injektira u konstruktor
        this.seminarService = seminarService;
        this.jmsTemplate = jmsTemplate;
        this.runtimeService = runtimeService; // <-- Ovdje ga spremaš u polje
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

            // --- Pokretanje Camunda procesa ---
            try {
                Map<String, Object> processVariables = new HashMap<>();
                // Šaljemo varijable koje tvoj Camunda proces i delegate očekuju
                processVariables.put("seminarTopic", savedSeminar.get().getTopic());
                processVariables.put("seminarLecturer", savedSeminar.get().getLecturer());
                // Dodaj ID kreiranog seminara ako je relevantno za proces
                processVariables.put("createdSeminarId", savedSeminar.get().getId());


                // "createNewSeminar" je ID tvog BPMN procesa (iz Camunda Modeler Properties -> General -> ID)
                // Važno je da je u BPMN-u postavljeno isExecutable="true"
                runtimeService.startProcessInstanceByKey("createNewSeminar", processVariables);


            } catch (Exception e) {
                // Koristimo klasni logger umjesto lokalnog null logera
                System.out.println("Error: " + e.getMessage());

                // Ovdje možeš dodati logiku za hendlanje greške, npr. vratiti drugačiji HTTP status
                // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
            // --- Kraj Camunda dijela ---

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

 */