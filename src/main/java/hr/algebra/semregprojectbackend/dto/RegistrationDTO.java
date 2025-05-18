package hr.algebra.semregprojectbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDTO {
    private Long id;
    private StudentDTO student; // Promijenjeno na StudentDTO
    private SeminarDTO seminar; // Promijenjeno na SeminarDTO
    private LocalDateTime registeredAt;
}