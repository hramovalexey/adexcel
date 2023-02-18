package adxcel.ctr.repository.crud;

import adxcel.ctr.entity.EventV;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventVRepository extends CrudRepository<EventV, Long> {
    EventV save(EventV entity);
}
