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
public class Dma {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dma_generator")
    private Long id;

    @Column(nullable = false, unique = true)
    @NonNull
    private int dma;

    @OneToMany(mappedBy = "dma", fetch = FetchType.LAZY)
    private List<ImpressionFact> impressionFacts;

}
