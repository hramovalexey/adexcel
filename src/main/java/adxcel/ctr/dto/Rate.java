package adxcel.ctr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Rate {
    LocalDateTime time;
    float rate;
    int imprq;
}
