package hr.algebra.semregprojectbackend.service;

import hr.algebra.semregprojectbackend.command.RegistrationCreateCommand;
import hr.algebra.semregprojectbackend.command.RegistrationUpdateCommand;
import hr.algebra.semregprojectbackend.domain.Registration;
import hr.algebra.semregprojectbackend.domain.Seminar;
import hr.algebra.semregprojectbackend.domain.Student;
import hr.algebra.semregprojectbackend.dto.RegistrationDTO;
import hr.algebra.semregprojectbackend.repository.RegistrationRepository;
import hr.algebra.semregprojectbackend.repository.SeminarRepository;
import hr.algebra.semregprojectbackend.repository.StudentRepository;
import hr.algebra.semregprojectbackend.service.SeminarRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
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
        return registrationRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public Optional<List<RegistrationDTO>> getRegistrationsByTopic(String topic) {
        List<Registration> registrations = registrationRepository.findAllBySeminar_Topic(topic)
                .orElse(Collections.emptyList());

        if (registrations.isEmpty()) {
            return Optional.empty();
        }

        List<RegistrationDTO> dtoList = registrations.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return Optional.of(dtoList);
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

    private RegistrationDTO mapToDto(Registration reg) {
        RegistrationDTO dto = new RegistrationDTO();
        dto.setId(reg.getId());
        dto.setStudentId(reg.getStudent().getId());
        dto.setSeminarId(reg.getSeminar().getId());
        dto.setRegisteredAt(reg.getRegisteredAt());
        return dto;
    }
}
