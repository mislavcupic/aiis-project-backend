package hr.algebra.semregprojectbackend.service;

import hr.algebra.semregprojectbackend.command.StudentUpdateCommand;
import hr.algebra.semregprojectbackend.domain.Student;
import hr.algebra.semregprojectbackend.dto.StudentDTO;
import hr.algebra.semregprojectbackend.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepo;

    public StudentServiceImpl(StudentRepository studentRepo) {
        this.studentRepo = studentRepo;
    }

    // Dohvati sve studente
    @Override
    public List<StudentDTO> getAllStudents() {
        return studentRepo.findAll().stream()
                .map(StudentDTO::new)
                .collect(Collectors.toList());
    }

    // Dohvati studenta prema imenu
    @Override
    public Optional<StudentDTO> getStudentByEmail(String email) {
        return studentRepo.findByEmailIgnoreCase(email.trim()).map(StudentDTO::new);
    }




    // Spremi novog studenta
    @Override
    public Optional<StudentDTO> save(StudentUpdateCommand studentUpdateCommand) {
        Student student = new Student();
        student.setName(studentUpdateCommand.getName());
        student.setEmail(studentUpdateCommand.getEmail());

        Student savedStudent = studentRepo.save(student);
        return Optional.of(new StudentDTO(savedStudent));
    }

    // Ažuriraj studenta prema imenu
    @Override
    public Optional<StudentDTO> updateStudent(Long id, StudentUpdateCommand studentUpdateCommand) {
        Optional<Student> studentOpt = studentRepo.findById(id);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            student.setName(studentUpdateCommand.getName());
            student.setEmail(studentUpdateCommand.getEmail());
            Student updatedStudent = studentRepo.save(student);
            return Optional.of(new StudentDTO(updatedStudent));
        } else {
            throw new RuntimeException("Student with id " + id + " not found.");
        }
    }


    // Obriši studenta prema ID-u
    @Override
    public void deleteById(Long id) {
        Optional<Student> studentOpt = studentRepo.findById(id);
        if (studentOpt.isPresent()) {
            studentRepo.deleteById(id);
        } else {
            throw new RuntimeException("Student not found with id: " + id);
        }
    }
}
