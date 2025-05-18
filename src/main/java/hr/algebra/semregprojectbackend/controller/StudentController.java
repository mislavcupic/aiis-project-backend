package hr.algebra.semregprojectbackend.controller;

import hr.algebra.semregprojectbackend.command.StudentCreateCommand;
import hr.algebra.semregprojectbackend.command.StudentUpdateCommand;
import hr.algebra.semregprojectbackend.dto.StudentDTO;
import hr.algebra.semregprojectbackend.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping()
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }


    @GetMapping(params = "email")
    public ResponseEntity<StudentDTO> getStudentByEmail(@RequestParam String email) {
        Optional<StudentDTO> foundStudent = studentService.getStudentByEmail(email);
        return foundStudent.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<StudentDTO> createStudent(@RequestBody @Valid StudentUpdateCommand studentUpdateCommand) {
        Optional<StudentDTO> savedStudent = studentService.save(studentUpdateCommand);
        return savedStudent.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Optional<StudentDTO>> updateStudent(@PathVariable Long id, @RequestBody StudentUpdateCommand studentUpdateCommand) {
        Optional<StudentDTO> updatedStudent = studentService.updateStudent(id,studentUpdateCommand);
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStudent(@PathVariable Long id) {
        studentService.deleteById(id);
    }
}
