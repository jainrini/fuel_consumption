package com.app.service;

import com.app.customexception.FuelTypeNotFound;
import com.app.customexception.RecordNotFound;
import com.app.model.FuelType;
import com.app.repository.FuelTypeRepository;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.app.model.FuelConsumed;
import com.app.model.FuelConsumption;
import com.app.model.FuelStatics;
import com.app.repository.FuelConsumptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FuelService {
    @Autowired
    private FuelConsumptionRepository fuelRepository;

    @Autowired
    private FuelTypeRepository fuelTypeRepository;
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public List<FuelConsumption> getAll() {
        List<FuelConsumption> all = fuelRepository.findAll();
        return all;

    }

    public List<FuelConsumption> addAll(List<FuelConsumption> fuelConsumption) throws FuelTypeNotFound {
        List<FuelType> allTypes = fuelTypeRepository.findAll();
        Map<String, FuelType> fuelTypeMap = allTypes.stream().collect(Collectors.toMap(FuelType::getTypename, Function.identity()));
        List<String> fuelTypes=new ArrayList<>();
        Set<String> fuelType = fuelConsumption.stream().map(f -> f.getFueltype()).collect(Collectors.toSet());
        for(String ft:fuelType){
            if(!fuelTypeMap.containsKey(ft)){
                fuelTypes.add(ft);
            }
        }
        if(!fuelTypes.isEmpty()){
            throw new FuelTypeNotFound("Fuel Types not found "+ String.join(",",fuelTypes)
                    + "Please add corresponding valid types " +fuelTypeMap.keySet());
        }
        /* List<FuelConsumption> updatedConsumption= new ArrayList<>();
         for(FuelConsumption f:fuelConsumption){
            FuelConsumption updatedData = fuelRepository.save(f);
            updatedConsumption.add(updatedData);
        }*/
        List<FuelConsumption> updatedConsumption = fuelRepository.saveAll(fuelConsumption);

        return updatedConsumption;
    }

    public HashMap<String, FuelConsumed> getDetailsForDriver(Integer id) throws RecordNotFound {
        List<FuelConsumption> fuelConsumptions = fuelRepository.findAllById(Arrays.asList(id));

        if(!fuelConsumptions.isEmpty()) {
            HashMap<String, FuelConsumed> monthFuelConsumedHashMap = getMonthFuelConsumedHashMap(fuelConsumptions);
            return monthFuelConsumedHashMap;
        }
        else{
            throw new RecordNotFound(" The record or driver Id "+ id+ "does not exist");
        }
    }

    private HashMap<String, FuelConsumed> getMonthFuelConsumedHashMap(List<FuelConsumption> fuelConsumptions) {
        HashMap<String,FuelConsumed> fuelConsumedHashMap = new HashMap<String,FuelConsumed>();
        DecimalFormat df = new DecimalFormat("0.00");
        for (FuelConsumption f : fuelConsumptions) {

            LocalDate localDate = f.getDate();
            Month month1 = localDate.getMonth();
            f.setDriverid(f.getDriverid());
            f.setMonth(month1.toString());
            f.setMoneySpent(Double.valueOf(df.format(f.getPrice()*f.getVolume())));
        }
        Map<String, Double> totalAmountPerMount = fuelConsumptions.stream().collect(
                Collectors.groupingBy(FuelConsumption::getMonth, Collectors.summingDouble(FuelConsumption::getMoneySpent)));
        Map<String, List<FuelConsumption>> fuelConsumptionPerMonth = fuelConsumptions.stream().collect(Collectors.groupingBy(FuelConsumption::getMonth));
        for(String id:totalAmountPerMount.keySet()){
            FuelConsumed fuelConsumed= new FuelConsumed();
            fuelConsumed.setDriverId(fuelConsumptions.stream().findAny().get().getDriverid());
            fuelConsumed.setTotalAmountSpent(totalAmountPerMount.get(id));
            fuelConsumed.setFuelConsumptionList(fuelConsumptionPerMonth.get(id));
            fuelConsumedHashMap.put(id,fuelConsumed);
        }
        Map<String,List<FuelStatics> > map= new HashMap<>();
        for(String k :fuelConsumptionPerMonth.keySet()){
            List<FuelConsumption> fuelConsumptionForMonth = fuelConsumptionPerMonth.get(k);
            Map<String, List<FuelConsumption>> fuelByType = fuelConsumptionForMonth.stream().collect(Collectors.groupingBy(FuelConsumption::getFueltype));
            List<FuelStatics> fuelStatics= new ArrayList<FuelStatics>();
            for(String type:fuelByType.keySet()){
                FuelStatics statics= new FuelStatics();
                statics.setFuelType(type);
                List<FuelConsumption> fuelByTypeList = fuelByType.get(type);
                Double totalPrice = fuelByTypeList.stream().collect(Collectors.summingDouble(FuelConsumption::getPrice));
                Double totalVolume = fuelByTypeList.stream().collect(Collectors.summingDouble(FuelConsumption::getVolume));
                Double avgPrice = fuelByTypeList.stream().collect(Collectors.averagingDouble(FuelConsumption::getPrice));
                statics.setTotalPrice(Double.valueOf(df.format(totalPrice)));
                statics.setAvgPrice(Double.valueOf(df.format(avgPrice)));
                statics.setTotalVolume(totalVolume);
                fuelStatics.add(statics);
            }
            map.put(k,fuelStatics);

        }
        for(String id:fuelConsumedHashMap.keySet()){
            FuelConsumed fuelConsumed = fuelConsumedHashMap.get(id);
            fuelConsumed.setStatics(map.get(id));
        }

        return fuelConsumedHashMap;
    }

    public HashMap<Integer, HashMap<String, FuelConsumed>> getDetails() {
        List<FuelConsumption> allDriverIds = getAll();
        Multimap<Integer, FuelConsumption> myMultimap = ArrayListMultimap.create();
        allDriverIds.stream().forEach(s->myMultimap.put(s.getDriverid(),s));
        HashMap<Integer,HashMap<String, FuelConsumed>> finalMap= new HashMap<>();
        for(Integer id:myMultimap.keySet()){
            Collection<FuelConsumption> fuelConsumptions = myMultimap.get(id);
            HashMap<String, FuelConsumed> monthFuelConsumedHashMap = getMonthFuelConsumedHashMap((List<FuelConsumption>) fuelConsumptions);
            finalMap.put(id,monthFuelConsumedHashMap);
        }
        return finalMap;
    }


    public List<FuelConsumption> updateDetails(List<FuelConsumption> fuelConsumption) {
        List<FuelConsumption> existingFuel = fuelRepository.findAll();
        List<FuelConsumption> deleteList= new ArrayList<>();
        for(FuelConsumption f:existingFuel){
            List<FuelConsumption> nonDeletable = fuelConsumption.stream().filter(f1->f1.getFid()!=null).filter(fuel -> fuel.getFid().equals(f.getFid())).collect(Collectors.toList());
            if(nonDeletable.isEmpty()){
                deleteList.add(f);
                fuelRepository.delete(f);
            }
        }
        fuelConsumption.remove(deleteList);
        List<FuelConsumption> newUpdateRecords= new ArrayList<>();
        for(FuelConsumption fnew:fuelConsumption){
            FuelConsumption updateNewRecords = fuelRepository.save(fnew);
            newUpdateRecords.add(fnew);
        }
        return newUpdateRecords;
    }

    public boolean deleteById(Integer id) {
        fuelRepository.deleteById(id);
        return true;
    }

    public Map<String, FuelType> getFuelTypeDetails() {
        final List<FuelType> all = fuelTypeRepository.findAll();
        Map<String, FuelType> fuelTypeMap = all.stream().collect(Collectors.toMap(FuelType::getTypename, Function.identity()));
        return fuelTypeMap;
    }

    public List<FuelType> addFuelType(List<FuelType> fuelTypeList) {
        List<FuelType> updatedFuelType = fuelTypeRepository.saveAll(fuelTypeList);
        return updatedFuelType;
    }
}
