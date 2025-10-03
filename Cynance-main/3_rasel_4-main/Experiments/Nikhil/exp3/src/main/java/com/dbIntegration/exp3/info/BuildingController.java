package com.dbIntegration.exp3.info;


import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@RestController
public class BuildingController {
    private LinkedList<Building> buildings;

    //Building methods to follow CRUDL
    //add method
    @PostMapping("/addBuilding/{id}/{name}/{address}")
    public Building addBuilding(@PathVariable int id, @PathVariable String name, @PathVariable String address) {
        Building b = new Building(id, name, address);
        buildings.add(b);
        return b;
    }
    //update
    @PutMapping("/updateaBuilding/{id}/{name}/{address}")
    public Building updateBuilding(@PathVariable int id, @PathVariable String name, @PathVariable String address) {
        for (Building building : buildings) {
            if (building.getId() == id) {
                building.setName(name);
                building.setAddress(address);
                return building;
            }
        }
        return null;
        //id cannot be changed

    }
    //get all mappings
    @GetMapping("/building")
    public List<Building> getAllBuildings() {
        return buildings;
    }
    @GetMapping("/building/{id}")
    public Building getBuildingById(@PathVariable int id) {
        for (Building building : buildings) {
            if (building.getId() == id) {
                return building;
            }
        }
        return null;
    }

    @DeleteMapping("/deleteBuilding")
    public void deleteBuilding(@RequestParam int id) {
        for (Building building : buildings) {
            if (building.getId() == id) {
                buildings.remove(building);
                break;
            }
        }
    }

}
