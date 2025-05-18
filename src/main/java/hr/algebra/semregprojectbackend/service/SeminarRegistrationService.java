package hr.algebra.semregprojectbackend.service;

import hr.algebra.semregprojectbackend.command.RegistrationCreateCommand;
import hr.algebra.semregprojectbackend.command.RegistrationUpdateCommand;
import hr.algebra.semregprojectbackend.dto.RegistrationDTO;

import java.util.List;
import java.util.Optional;

public interface SeminarRegistrationService {
    List<RegistrationDTO> getAllRegistrations();
    Optional<List<RegistrationDTO>> getRegistrationsByStudentEmail(String email);
    Optional<List<RegistrationDTO>> getRegistrationsByTopic(String topic);
    RegistrationDTO createRegistration(RegistrationUpdateCommand registrationUpdateCommand);
    RegistrationDTO updateRegistration(Long id, RegistrationUpdateCommand registrationUpdateCommand);
    void deleteRegistration(Long id);
    Optional<List<RegistrationDTO>> getRegistrationsBySeminarId(Long seminarId);
}