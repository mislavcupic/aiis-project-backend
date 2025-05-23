package hr.algebra.semregprojectbackend.dto;

import hr.algebra.semregprojectbackend.domain.Seminar;
import jakarta.validation.constraints.NotBlank;

public class SeminarDTO {
    private Long id;
    private String topic;
    private String lecturer;

    public SeminarDTO() {
        // Default constructor needed for Jackson
    }
public SeminarDTO(Seminar seminar) {
    this.id = seminar.getId();
    this.topic = seminar.getTopic();
    this.lecturer = seminar.getLecturer();
}

    public SeminarDTO(Object o, @NotBlank(message = "Topic is required") String topic, @NotBlank(message = "Lecturer is required") String lecturer) {
        this.topic = topic;
        this.lecturer = lecturer;
        this.id = (Long) o;
    }


    public Long getId() {
        return id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }
}
