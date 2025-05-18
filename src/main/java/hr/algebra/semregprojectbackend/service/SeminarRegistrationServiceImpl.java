package hr.algebra.semregprojectbackend.service;

import hr.algebra.semregprojectbackend.command.RegistrationUpdateCommand;
import hr.algebra.semregprojectbackend.domain.Registration;
import hr.algebra.semregprojectbackend.domain.Seminar;
import hr.algebra.semregprojectbackend.domain.Student;
import hr.algebra.semregprojectbackend.dto.RegistrationDTO;
import hr.algebra.semregprojectbackend.dto.SeminarDTO;
import hr.algebra.semregprojectbackend.dto.StudentDTO;
import hr.algebra.semregprojectbackend.repository.RegistrationRepository;
import hr.algebra.semregprojectbackend.repository.SeminarRepository;
import hr.algebra.semregprojectbackend.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeminarRegistrationServiceImpl implements SeminarRegistrationService {

    private final RegistrationRepository registrationRepository;
    private final StudentRepository studentRepository;
    private final SeminarRepository seminarRepository;

    @Override
    public List<RegistrationDTO> getAllRegistrations() {
        return registrationRepository.findAllWithStudentsAndSeminars().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<List<RegistrationDTO>> getRegistrationsByTopic(String topic) {
        return registrationRepository.findAllBySeminar_Topic(topic)
                .map(registrations -> registrations.stream()
                        .map(this::mapToDto)
                        .collect(Collectors.toList()));
    }

    @Override
    public Optional<List<RegistrationDTO>> getRegistrationsByStudentEmail(String email) {
        return registrationRepository.findAllByStudent_EmailWithStudents(email) // Promijenjeno ime metode
                .map(registrations -> registrations.stream()
                        .map(this::mapToDto)
                        .collect(Collectors.toList()));
    }

    @Override
    public RegistrationDTO createRegistration(RegistrationUpdateCommand registrationUpdateCommand) {
        Student student = studentRepository.findById(registrationUpdateCommand.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Seminar seminar = seminarRepository.findById(registrationUpdateCommand.getSeminarId())
                .orElseThrow(() -> new RuntimeException("Seminar not found"));

        Registration registration = new Registration();
        registration.setStudent(student);
        registration.setSeminar(seminar);
        registration.setRegisteredAt(registrationUpdateCommand.getRegisteredAt());

        return mapToDto(registrationRepository.save(registration));
    }

    @Override
    public RegistrationDTO updateRegistration(Long id, RegistrationUpdateCommand registrationUpdateCommand) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        Student student = studentRepository.findById(registrationUpdateCommand.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Seminar seminar = seminarRepository.findById(registrationUpdateCommand.getSeminarId())
                .orElseThrow(() -> new RuntimeException("Seminar not found"));

        registration.setStudent(student);
        registration.setSeminar(seminar);
        registration.setRegisteredAt(registrationUpdateCommand.getRegisteredAt());

        return mapToDto(registrationRepository.save(registration));
    }

    @Override
    public void deleteRegistration(Long id) {
        registrationRepository.deleteById(id);
    }

    @Override
    public Optional<List<RegistrationDTO>> getRegistrationsBySeminarId(Long seminarId) {
        return registrationRepository.findAllBySeminar_IdWithStudents(seminarId)
                .map(registrations -> registrations.stream()
                        .map(this::mapToDto).collect(Collectors.toList()));
    }

    private RegistrationDTO mapToDto(Registration reg) {
        return new RegistrationDTO(
                reg.getId(),
                new StudentDTO(reg.getStudent()),
                new SeminarDTO(reg.getSeminar()),
                reg.getRegisteredAt()
        );
    }
}