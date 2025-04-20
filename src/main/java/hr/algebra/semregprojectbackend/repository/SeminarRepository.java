package hr.algebra.semregprojectbackend.repository;

import hr.algebra.semregprojectbackend.domain.Seminar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Optional;


public interface SeminarRepository extends JpaRepository<Seminar, Long> {
    List<Seminar> findAll();
    Optional<Seminar> findByTopicIgnoreCase(String topic);



}
