package k.dawid.loginuserspringboot.service;

import k.dawid.loginuserspringboot.entity.Car;

import java.util.List;

public interface CarService {

    public List<Car> findAll();

    public Car findById(int id);

    public void save(Car car);

    public void deleteById (int id);

}
