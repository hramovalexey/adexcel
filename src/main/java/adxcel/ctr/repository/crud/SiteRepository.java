package adxcel.ctr.repository.crud;

import adxcel.ctr.entity.Site;
import adxcel.ctr.entity.Time;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteRepository extends CrudRepository<Site, Long> {
    Site save(Site entity);
}
