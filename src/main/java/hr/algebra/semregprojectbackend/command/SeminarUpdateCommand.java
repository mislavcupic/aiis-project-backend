package hr.algebra.semregprojectbackend.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;
import org.springframework.context.annotation.Primary;

@Data
public class SeminarUpdateCommand {


    private Long id;

    @NotBlank(message = "Topic is required")
    private String topic;

    @NotBlank(message = "Lecturer is required")
    private String lecturer;

    @Primary

    public Long getId() {
        return id;
    }
}
