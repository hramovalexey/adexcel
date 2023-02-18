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
public class HappenC {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "happenc_generator")
    private Long id;

    @ManyToOne
    @NonNull
    @JoinColumn(nullable = false)
    private ImpressionFact fact;

    @ManyToOne
    @NonNull
    @JoinColumn(nullable = false)
    private EventC eventC;

    @Column(nullable = false)
    @NonNull
    private int qty;

    public int incrementQtyAndGet() {
        return ++qty;
    }
}
