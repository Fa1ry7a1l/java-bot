package edu.java.entity;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Chat {
    private Long id;
    private OffsetDateTime registeredAt;

    /*private void setRegisteredAt(OffsetDateTime offsetDateTime)
    {
        this.registeredAt = offsetDateTime.atZoneSimilarLocal(ZoneOffset.systemDefault()).toOffsetDateTime();
    }*/
}
