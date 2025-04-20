package hr.algebra.semregprojectbackend.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SeminarCreateCommand {

    @NotBlank(message = "Topic is required")
    @Size(min = 3, message = "Topic must be at least 3 characters long")
    private String topic;

    @NotBlank(message = "Lecturer is required")
    private String lecturer;



}
