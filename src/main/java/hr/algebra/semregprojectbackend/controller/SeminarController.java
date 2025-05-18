package hr.algebra.semregprojectbackend.controller;

import hr.algebra.semregprojectbackend.command.SeminarCreateCommand;
import hr.algebra.semregprojectbackend.command.SeminarUpdateCommand;
import hr.algebra.semregprojectbackend.dto.SeminarDTO;
import hr.algebra.semregprojectbackend.service.SeminarService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/seminars")
public class SeminarController {

    private final SeminarService seminarService;

    public SeminarController(SeminarService seminarService) {
        this.seminarService = seminarService;
    }

    @GetMapping
    public ResponseEntity<List<SeminarDTO>> getAllSeminars() {
        return ResponseEntity.ok(seminarService.getAllSeminars());
    }

    @GetMapping("/search")
    public ResponseEntity<SeminarDTO> findByTopicName(@RequestParam String topic) {
        Optional<SeminarDTO> found = seminarService.findSeminarByTopic(topic);
        return found.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping("/create")
    public ResponseEntity<SeminarDTO> createSeminar(@RequestBody @Valid SeminarUpdateCommand seminarUpdateCommand) {
        Optional<SeminarDTO> savedSeminar = seminarService.save(seminarUpdateCommand);
        return savedSeminar.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PutMapping // Mapira PUT zahtjeve na /api/seminars
    public ResponseEntity<SeminarDTO> updateSeminar(@RequestParam Long id, @RequestBody SeminarDTO seminarDTO) {
        SeminarDTO updated = seminarService.updateSeminar(id, seminarDTO);
        return ResponseEntity.ok(updated);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("{id}")
    public void deleteSeminar(@PathVariable Long id) {
        seminarService.deleteById(id);

    }

    @GetMapping("/{topic}")
    public ResponseEntity<SeminarDTO> findByTopic(@PathVariable String topic) {
        Optional<SeminarDTO> found = seminarService.findSeminarByTopic(topic);
        return found.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
