package adxcel.ctr.entity;

import lombok.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class Time {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "time_generator")
    private Long id;

    @Column(nullable = false, unique = true)
    @NonNull
    private LocalDateTime timeStamp;

    @OneToMany(mappedBy = "time", fetch = FetchType.LAZY)
    private List<ImpressionFact> impressionFacts;

    @Column(nullable = false)
    @NonNull
    private int minute;

    // 1 or 2
    @Column(nullable = false)
    @NonNull
    private int minute30;

    @Column(nullable = false)
    @NonNull
    private int hour;

    @Column(nullable = false)
    @NonNull
    private int day;

    @Column(nullable = false)
    @NonNull
    private int dayOfWeek; // 0 - 6 (Mon - Sun)

    @Column(nullable = false)
    @NonNull
    private int month; // 1 - 12

    @Column(nullable = false)
    @NonNull
    private int year;

}
