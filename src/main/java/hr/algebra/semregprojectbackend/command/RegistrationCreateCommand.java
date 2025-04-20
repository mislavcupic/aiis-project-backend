package hr.algebra.semregprojectbackend.command;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RegistrationCreateCommand {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Seminar ID is required")
    private Long seminarId;

    @NotNull(message = "Registration time is required")
    @JsonFormat(pattern = "d/M/yyyy HH:mm:ss")
    private LocalDateTime registeredAt;
}

