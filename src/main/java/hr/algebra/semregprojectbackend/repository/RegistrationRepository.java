package hr.algebra.semregprojectbackend.repository;

import hr.algebra.semregprojectbackend.domain.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    Optional<List<Registration>> findAllBySeminar_Topic(String topic);
}
