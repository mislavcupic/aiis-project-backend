package hr.algebra.semregprojectbackend.service;

import hr.algebra.semregprojectbackend.command.SeminarUpdateCommand;
import hr.algebra.semregprojectbackend.domain.Seminar;
import hr.algebra.semregprojectbackend.dto.SeminarDTO;
import hr.algebra.semregprojectbackend.repository.SeminarRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;



import java.util.List;
import java.util.Optional;

@Service
public class SeminarServiceImpl implements SeminarService {
    private final SeminarRepository seminarRepo;
    public SeminarServiceImpl(SeminarRepository seminarRepo) {
        this.seminarRepo = seminarRepo;
    }



    @Override
    public List<SeminarDTO> getAllSeminars() {
        return seminarRepo.findAll().stream().map(SeminarDTO::new).toList();
    }
    @Transactional
    @Override
    public Optional<SeminarDTO> save(SeminarUpdateCommand seminarUpdateCommand) {
        // Ako je id postavljen, baca se greška jer se ne smije ručno postavljati kod kreiranja
        if (seminarUpdateCommand.getId() != null) {
            throw new IllegalArgumentException("ID ne smije biti postavljen kod kreiranja novog seminara.");
        }

        Seminar seminar = new Seminar();
        seminar.setTopic(seminarUpdateCommand.getTopic());
        seminar.setLecturer(seminarUpdateCommand.getLecturer());

        Seminar savedSeminar = seminarRepo.save(seminar);
        return Optional.of(new SeminarDTO(savedSeminar));
    }

    @Transactional
    @Override
    public SeminarDTO updateSeminar( Long id, SeminarDTO seminarDTO) {
        Optional<Seminar> seminarOpt = seminarRepo.findById(id);
        if (seminarOpt.isPresent()) {
            Seminar seminar = seminarOpt.get();
            seminar.setTopic(seminarDTO.getTopic());
            seminar.setLecturer(seminarDTO.getLecturer());
            Seminar updated = seminarRepo.save(seminar);
            return new SeminarDTO(updated);
        } else {
            throw new RuntimeException("Seminar not found with id: " + seminarDTO.getId());
        }
    }

    @Override
    public void deleteById(Long id) {

        seminarRepo.deleteById(id);
    }



    @Override
    public Optional<SeminarDTO> findSeminarByTopic(String topic) {
        return seminarRepo.findByTopicIgnoreCase(topic.trim()).map(SeminarDTO::new);
    }

}
