package adxcel.ctr.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.swing.*;
import java.util.List;

@Entity
@Table(indexes = @Index(columnList = "uid"))
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class ImpressionFact {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "impression_generator")
    private Long id;

    @Column(nullable = false)
    @NonNull
    private String uid;

    // TODO add all the rest fields

    @ManyToOne
    @NonNull
    @JoinColumn(nullable = false)
    private Time time;

    @ManyToOne
    @NonNull
    @JoinColumn(nullable = false)
    private Site site;

    @ManyToOne
    @NonNull
    @JoinColumn(nullable = false)
    private Dma dma;

    @OneToMany(mappedBy = "fact", fetch = FetchType.LAZY)
    private List<HappenC> happenC;

    @OneToMany(mappedBy = "fact", fetch = FetchType.LAZY)
    private List<HappenV> happenV;

}
