package com.app.controller;

import com.app.customexception.FuelTypeNotFound;
import com.app.customexception.InValidPrice;
import com.app.customexception.RecordNotFound;
import com.app.model.FuelConsumed;
import com.app.model.FuelConsumption;
import com.app.model.FuelType;
import com.app.service.FuelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/fuel")
public class FuelController {

    @Autowired
    private FuelService fuelService;

    @RequestMapping(value = "/consumption/all",method = RequestMethod.GET)
    public List<FuelConsumption> getAll(){
        List<FuelConsumption> all = fuelService.getAll();
        return all;
    }
    @RequestMapping(value = "/consumption/add",method = RequestMethod.POST)
    public List<FuelConsumption> add(@RequestBody  @Validated List<FuelConsumption> fuelConsumption) throws FuelTypeNotFound, InValidPrice {
        List<FuelConsumption> consumptions = fuelService.addAll(fuelConsumption);
        return consumptions;
    }
    //total money spent grouped by month
    @RequestMapping(value = "/get/amount/{id}",method = RequestMethod.GET)
    public HashMap<String, FuelConsumed> getDetailsForDriver(@PathVariable Integer id) throws RecordNotFound {
        return fuelService.getDetailsForDriver(id);
    }
    @RequestMapping(value = "/get/amount",method = RequestMethod.GET)
    public HashMap<Integer, HashMap<String, FuelConsumed>> getDetails(){
        return fuelService.getDetails();
    }
    @RequestMapping(value = "/update/details",method = RequestMethod.PUT)
    public List<FuelConsumption> updateDetails(@RequestBody List<FuelConsumption> fuelConsumption) throws FuelTypeNotFound, InValidPrice {
        return fuelService.updateDetails(fuelConsumption);
    }
    @RequestMapping(value = "/delete/{id}",method = RequestMethod.DELETE)
    public String updateDetails(@PathVariable Integer id) throws RecordNotFound {
        if(fuelService.deleteById(id)){
            return "Successfully deleted";
        }
       return "Deletion Failed";

    }

    @RequestMapping(value = "/type",method = RequestMethod.GET)
    public Map<String,FuelType> getFuelTypeDetails(){
        return fuelService.getFuelTypeDetails();
    }

    @RequestMapping(value = "/type/add",method = RequestMethod.POST)
    public List<FuelType> addFuelType(@RequestBody List<FuelType> fuelTypeList){
        return fuelService.addFuelType(fuelTypeList);
    }

}
