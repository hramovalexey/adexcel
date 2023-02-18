package adxcel.ctr.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class EventV {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "eventv_generator")
    private Long id;

    @Column(nullable = false, unique = true)
    @NonNull
    private String event;

    @OneToMany(mappedBy = "eventV", fetch = FetchType.LAZY)
    private List<HappenV> happenV;

}
