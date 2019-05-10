package k.dawid.loginuserspringboot.dao;

import k.dawid.loginuserspringboot.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, Integer> {

}
