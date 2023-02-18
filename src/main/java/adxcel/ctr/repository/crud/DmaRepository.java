package adxcel.ctr.repository.crud;

import adxcel.ctr.entity.Dma;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DmaRepository extends CrudRepository<Dma, Long> {
    Dma save(Dma entity);
}
