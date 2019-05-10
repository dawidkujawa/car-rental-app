package k.dawid.loginuserspringboot.dao;

import k.dawid.loginuserspringboot.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {

    Location findByCity(String city);

}
