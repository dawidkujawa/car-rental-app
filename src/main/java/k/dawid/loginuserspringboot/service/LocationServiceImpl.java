package k.dawid.loginuserspringboot.service;

import k.dawid.loginuserspringboot.dao.LocationRepository;
import k.dawid.loginuserspringboot.entity.Car;
import k.dawid.loginuserspringboot.entity.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocationServiceImpl implements LocationService {

    LocationRepository locationRepository;

    @Autowired
    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public List<Location> findAll() {
        return locationRepository.findAll();
    }

    @Override
    public Location findById(int id) {
        Optional<Location> result = locationRepository.findById(id);

        Location location = null;
        if(result.isPresent()){
            location = result.get();
        } else {
            throw new RuntimeException("Did not find location id: " + id);
        }
        return location;
    }

    @Override
    public void save(String locationCity) {
        Location location = locationRepository.findByCity(locationCity);
        locationRepository.save(location);
    }

    @Override
    public void saveByLocation(Location location){
        locationRepository.save(location);
    }

    @Override
    public void deleteById(int id) {
        locationRepository.deleteById(id);
    }

    @Override
    public Location findByCity(String locationCity){
        Location location = locationRepository.findByCity(locationCity);
        return location;
    }
}
