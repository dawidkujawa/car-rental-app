package k.dawid.loginuserspringboot.service;

import k.dawid.loginuserspringboot.dao.CarRepository;
import k.dawid.loginuserspringboot.entity.Car;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service("carService")
public class CarServiceImpl implements CarService {

    private CarRepository carRepository;

    @Autowired
    public CarServiceImpl(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    public List<Car> findAll() {
        return carRepository.findAll();
    }

    @Override
    public Car findById(int id) {
        Optional<Car> result = carRepository.findById(id);

        Car car = null;
        if(result.isPresent()){
            car = result.get();
        } else {
            throw new RuntimeException("Did not find car id: " + id);
        }
        return car;
    }

    @Override
    public void save(Car car) {
//        car.setStatus("available");
        carRepository.save(car);
    }

    @Override
    public void deleteById(int id) {
        carRepository.deleteById(id);
    }
}
