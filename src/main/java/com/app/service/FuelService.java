package com.app.service;

import com.app.customexception.FuelTypeNotFound;
import com.app.customexception.InValidPrice;
import com.app.customexception.RecordNotFound;
import com.app.model.FuelType;
import com.app.repository.FuelTypeRepository;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.app.model.FuelConsumed;
import com.app.model.FuelConsumption;
import com.app.model.FuelStatics;
import com.app.repository.FuelConsumptionRepository;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Array;
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

    DecimalFormat df = new DecimalFormat("0.00");
    public List<FuelConsumption> getAll() {
        List<FuelConsumption> all = fuelRepository.findAll();
        all.stream().forEach(a->a.setMoneySpent(Double.valueOf(df.format(a.getPrice()*a.getVolume()))));
        return all;

    }

    public List<FuelConsumption> addAll(List<FuelConsumption> fuelConsumption) throws FuelTypeNotFound, InValidPrice {

        isValidType(fuelConsumption);
        List<FuelConsumption> updatedConsumption = fuelRepository.saveAll(fuelConsumption);
        updatedConsumption.stream().forEach(a->a.setMoneySpent(Double.valueOf(df.format(a.getPrice()*a.getVolume()))));
        return updatedConsumption;
    }

    private void isValidType(List<FuelConsumption> fuelConsumption) throws FuelTypeNotFound, InValidPrice {
        List<String> fuelTypes=new ArrayList<>();
        List<FuelType> allTypes = fuelTypeRepository.findAll();

        Map<String, Double> fuelTypeMap = allTypes.stream().collect(Collectors.toMap(FuelType::getTypename, FuelType::getPrice));

        Set<String> fuelType = fuelConsumption.stream().map(f -> f.getFueltype()).collect(Collectors.toSet());
        for(String ft:fuelType){
            if(!fuelTypeMap.containsKey(ft)){
                fuelTypes.add(ft);
            }
        }
        if(!fuelTypes.isEmpty()){
            throw new FuelTypeNotFound("Fuel Types not found "+ String.join(",",fuelTypes)
                    + "  Please add corresponding valid types " +fuelTypeMap.keySet());
        }
        Map<String,Double> fuelConsumptionMap= new HashMap<>();
        for(FuelConsumption f:fuelConsumption){
            fuelConsumptionMap.put(f.getFueltype(),f.getPrice());
        }
        for(String s:fuelConsumptionMap.keySet()){
            if(!(fuelConsumptionMap.get(s).compareTo(fuelTypeMap.get(s))==0)){
                throw new InValidPrice("The valid price of fuel type " + s + "is :"+ fuelTypeMap.get(s));
            }
        }
    }

    public HashMap<String, FuelConsumed> getDetailsForDriver(Integer id) throws RecordNotFound {

        List<FuelConsumption> fuelConsumptions = fuelRepository.findAllById(Arrays.asList(id));
        List<FuelConsumption> all = fuelRepository.findAll();
        Multimap<Integer,FuelConsumption> map= ArrayListMultimap.create();
        all.stream().forEach(a->map.put(a.getDriverid(),a));
        Collection<FuelConsumption> fuelConsumptions1 = map.get(id);
        if(map.containsKey(id)){
            HashMap<String, FuelConsumed> monthFuelConsumedHashMap = getMonthFuelConsumedHashMap((List<FuelConsumption>) fuelConsumptions1);
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


    public List<FuelConsumption> updateDetails(List<FuelConsumption> fuelConsumption) throws FuelTypeNotFound, InValidPrice {
        isValidType(fuelConsumption);
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

    public boolean deleteById(Integer id) throws RecordNotFound {
        final List<FuelConsumption> all = fuelRepository.findAll();
        List<FuelConsumption> consumption= all.stream().filter(a -> a.getDriverid().compareTo(id) == 0).collect(Collectors.toList());
        if(!consumption.isEmpty()) {
            List<FuelConsumption> deleted = new ArrayList<>();
            for (FuelConsumption f : consumption) {
                fuelRepository.deleteById(f.getFid());
                deleted.add(f);
            }
            if (deleted.size() == consumption.size())
                return true;
            else {
                return false;
            }
        }
        throw  new RecordNotFound("Id does not exist");
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
