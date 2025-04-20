package hr.algebra.semregprojectbackend.repository;

import hr.algebra.semregprojectbackend.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StudentRepository  extends JpaRepository<Student, Long> {
    Optional<Student> findByNameIgnoreCase(String name);

    Optional<Student> findByName(String name);
}
