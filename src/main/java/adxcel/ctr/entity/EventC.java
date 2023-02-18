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
public class EventC {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "eventc_generator")
    private Long id;

    @Column(nullable = false, unique = true)
    @NonNull
    private String event;

    @OneToMany(mappedBy = "eventC", fetch = FetchType.LAZY)
    private List<HappenC> happenC;
}
