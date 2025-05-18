package hr.algebra.semregprojectbackend.repository;

import hr.algebra.semregprojectbackend.domain.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    Optional<List<Registration>> findAllBySeminar_Topic(String topic);

    @Query("SELECT r FROM Registration r JOIN FETCH r.student JOIN FETCH r.seminar")
    List<Registration> findAllWithStudentsAndSeminars();

    @Query("SELECT r FROM Registration r JOIN FETCH r.student JOIN FETCH r.seminar WHERE r.student.email = :email")
    Optional<List<Registration>> findAllByStudent_EmailWithStudents(@Param("email") String email);

    @Query("SELECT r FROM Registration r JOIN FETCH r.student JOIN FETCH r.seminar WHERE r.seminar.id = :seminarId")
    Optional<List<Registration>> findAllBySeminar_IdWithStudents(@Param("seminarId") Long seminarId);




}