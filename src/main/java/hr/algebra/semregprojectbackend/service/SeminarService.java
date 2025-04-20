package hr.algebra.semregprojectbackend.service;

import hr.algebra.semregprojectbackend.command.SeminarCreateCommand;
import hr.algebra.semregprojectbackend.command.SeminarUpdateCommand;
import hr.algebra.semregprojectbackend.dto.SeminarDTO;
import jakarta.validation.Valid;


import java.util.List;
import java.util.Optional;

public interface SeminarService {

     List<SeminarDTO> getAllSeminars();
     Optional<SeminarDTO> save(@Valid SeminarUpdateCommand seminarUpdateCommand);
     SeminarDTO updateSeminar(Long id, SeminarDTO seminar);
     void deleteById(Long id);
     Optional<SeminarDTO> findSeminarByTopic(String topic);

}
