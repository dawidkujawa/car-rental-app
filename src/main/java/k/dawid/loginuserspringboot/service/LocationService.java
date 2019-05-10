package k.dawid.loginuserspringboot.service;

import k.dawid.loginuserspringboot.entity.Car;
import k.dawid.loginuserspringboot.entity.Location;

import java.util.List;

public interface LocationService {

    public List<Location> findAll();

    public Location findById(int id);

    public void save(String locationCity);

    public void saveByLocation(Location location);

    public void deleteById(int id);

    public Location findByCity(String locationCity);

}
