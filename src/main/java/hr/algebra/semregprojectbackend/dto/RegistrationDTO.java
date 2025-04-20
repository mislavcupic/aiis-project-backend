package hr.algebra.semregprojectbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDTO {
    private Long id;
    private Long studentId;
    private Long seminarId;
    private Date registeredAt;


    public void setRegisteredAt(LocalDateTime registeredAt) {
    }


}
