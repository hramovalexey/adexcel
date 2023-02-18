package adxcel.ctr.repository.crud;

import adxcel.ctr.entity.Time;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeRepository extends CrudRepository<Time, Long> {
    Time save(Time entity);
}
