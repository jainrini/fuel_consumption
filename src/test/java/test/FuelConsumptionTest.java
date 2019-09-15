package test;

import com.app.customexception.FuelTypeNotFound;
import com.app.customexception.InValidPrice;
import com.app.customexception.RecordNotFound;
import com.app.model.FuelConsumed;
import com.app.model.FuelConsumption;
import com.app.model.FuelStatics;
import com.app.model.FuelType;
import com.app.repository.FuelConsumptionRepository;
import com.app.repository.FuelTypeRepository;
import com.app.service.FuelService;
import com.google.common.collect.ArrayListMultimap;
import org.assertj.core.util.Sets;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import sun.security.ec.point.ProjectivePoint;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyString;

@RunWith(MockitoJUnitRunner.class)
public class FuelConsumptionTest {

    @Mock
    private FuelConsumptionRepository fuelRepository;

    @Mock
    private FuelTypeRepository fuelTypeRepository;

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    @InjectMocks
    private FuelService service;

    @Before
    public void init(){

        FuelType t1= FuelType.builder().typeid(1).typename("A").price(10.02).build();
        FuelType t2= FuelType.builder().typeid(2).typename("B").price(20.02).build();
        FuelType t3= FuelType.builder().typeid(3).typename("C").price(10.01).build();
        List<FuelType> fuelTypes = Arrays.asList(t1, t2, t3);
        Mockito.when(fuelTypeRepository.findAll()).thenReturn(fuelTypes);
        FuelConsumption f1 = FuelConsumption.builder().driverid(123).date(LocalDate.of(2019, Month.SEPTEMBER, 10)).fueltype("A").price(10.02).volume(10.00).build();
        FuelConsumption f2 = FuelConsumption.builder().driverid(123).date(LocalDate.of(2019, Month.SEPTEMBER, 12)).fueltype("A").price(10.02).volume(20.00).build();
        FuelConsumption f3 = FuelConsumption.builder().driverid(123).date(LocalDate.of(2019, Month.AUGUST, 11)).fueltype("B").price(20.02).volume(30.00).build();
        FuelConsumption f4 = FuelConsumption.builder().driverid(123).date(LocalDate.of(2019, Month.AUGUST, 13)).fueltype("B").price(20.02).volume(10.99).build();
        FuelConsumption f5 = FuelConsumption.builder().driverid(124).date(LocalDate.of(2019, Month.AUGUST, 12)).fueltype("C").price(10.01).volume(10.99).build();
        FuelConsumption f6 = FuelConsumption.builder().driverid(124).date(LocalDate.of(2019, Month.AUGUST, 11)).fueltype("B").price(20.02).volume(10.99).build();
        FuelConsumption f7 = FuelConsumption.builder().driverid(125).date(LocalDate.of(2019, Month.OCTOBER, 10)).fueltype("A").price(10.02).volume(10.99).build();
        FuelConsumption f8 = FuelConsumption.builder().driverid(125).date(LocalDate.of(2019, Month.OCTOBER, 10)).fueltype("A").price(10.02).volume(10.99).build();
        FuelConsumption f9 = FuelConsumption.builder().driverid(126).date(LocalDate.of(2019, Month.OCTOBER, 10)).fueltype("C").price(10.01).volume(10.99).build();
        FuelConsumption f10 = FuelConsumption.builder().driverid(126).date(LocalDate.of(2019, Month.NOVEMBER, 10)).fueltype("B").price(20.02).volume(10.99).build();
        List<FuelConsumption> fuelConsumptionList = Arrays.asList(f1, f2, f3, f4, f5, f6, f7, f8, f9, f10);
        Mockito.when(fuelRepository.save(Mockito.any(FuelConsumption.class))).thenReturn(f1);
        List<FuelConsumption> fuelConsumptionList2 = Arrays.asList(f1, f2, f3, f4,f5,f6);
          Mockito.when(fuelRepository.findAllById(Arrays.asList(123))).thenReturn(fuelConsumptionList2);
          Mockito.when(fuelRepository.findAll()).thenReturn(fuelConsumptionList);
    }
    @Test
    public void addAll() throws FuelTypeNotFound, InValidPrice {
        FuelConsumption f1 = FuelConsumption.builder().driverid(123).date(LocalDate.of(2019, Month.SEPTEMBER, 10)).fueltype("A").price(10.02).volume(10.00).build();
        FuelConsumption f2 = FuelConsumption.builder().driverid(123).date(LocalDate.of(2019, Month.SEPTEMBER, 12)).fueltype("A").price(10.02).volume(20.00).build();
        FuelConsumption f3 = FuelConsumption.builder().driverid(123).date(LocalDate.of(2019, Month.AUGUST, 11)).fueltype("B").price(20.02).volume(30.00).build();
        FuelConsumption f4 = FuelConsumption.builder().driverid(123).date(LocalDate.of(2019, Month.AUGUST, 13)).fueltype("B").price(20.02).volume(10.99).build();
        FuelConsumption f5 = FuelConsumption.builder().driverid(124).date(LocalDate.of(2019, Month.AUGUST, 12)).fueltype("C").price(10.01).volume(10.99).build();
        FuelConsumption f6 = FuelConsumption.builder().driverid(124).date(LocalDate.of(2019, Month.AUGUST, 11)).fueltype("B").price(20.02).volume(10.99).build();
        FuelConsumption f7 = FuelConsumption.builder().driverid(125).date(LocalDate.of(2019, Month.OCTOBER, 10)).fueltype("A").price(10.02).volume(10.99).build();
        FuelConsumption f8 = FuelConsumption.builder().driverid(125).date(LocalDate.of(2019, Month.OCTOBER, 10)).fueltype("A").price(10.02).volume(10.99).build();
        FuelConsumption f9 = FuelConsumption.builder().driverid(126).date(LocalDate.of(2019, Month.OCTOBER, 10)).fueltype("C").price(10.01).volume(10.99).build();
        FuelConsumption f10 = FuelConsumption.builder().driverid(126).date(LocalDate.of(2019, Month.NOVEMBER, 10)).fueltype("B").price(20.02).volume(10.99).build();
        List<FuelConsumption> fuelConsumptionList = Arrays.asList(f1, f2, f3, f4, f5, f6, f7, f8, f9, f10);
        Mockito.when(fuelRepository.save(Mockito.any(FuelConsumption.class))).thenReturn(f1);
        List<FuelConsumption> updatedConsumption = service.addAll(fuelConsumptionList);
        Assert.assertNotNull(updatedConsumption);
        Assert.assertEquals(updatedConsumption.size(),10);
    }
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test(expected = FuelTypeNotFound.class)
    public void addInvalidType() throws FuelTypeNotFound, InValidPrice {
        FuelConsumption f1 = FuelConsumption.builder().driverid(123).date(LocalDate.of(2019, Month.SEPTEMBER, 10)).fueltype("D").price(10.02).volume(10.00).build();
        List<FuelConsumption> updatedConsumption = service.addAll(Arrays.asList(f1));
        exceptionRule.expect(FuelTypeNotFound.class);
        exceptionRule.expectMessage("Fuel Types not found "+ String.join(",",f1.getFueltype())
                + "Please add corresponding valid types " +"[A, B, C]");
        Assert.assertNull(updatedConsumption);
    }

    @Test
    public void getDetailsForDriver() throws RecordNotFound {
        HashMap<String, FuelConsumed> detailsForDriver = service.getDetailsForDriver(123);
        Set<String> strings = detailsForDriver.keySet();
        Assert.assertEquals(strings.size(),2);
        boolean b = strings.containsAll(Arrays.asList("AUGUST", "SEPTEMBER"));
        Assert.assertTrue(b);
        FuelConsumed fuelConsumedInSeptember = detailsForDriver.get("SEPTEMBER");
        Assert.assertEquals(fuelConsumedInSeptember.getTotalAmountSpent(),Double.valueOf(300.6));
        Assert.assertEquals(fuelConsumedInSeptember.getFuelConsumptionList().size(),2);
        Assert.assertEquals(fuelConsumedInSeptember.getStatics().size(),1);
        FuelStatics fuelStatics = fuelConsumedInSeptember.getStatics().get(0);
        Assert.assertEquals(fuelStatics.getAvgPrice(),Double.valueOf(10.02));
        Assert.assertEquals(fuelStatics.getTotalPrice(),Double.valueOf(20.04));
        FuelConsumed fuelConsumedInAugust = detailsForDriver.get("AUGUST");
        Assert.assertEquals(fuelConsumedInAugust.getTotalAmountSpent(),Double.valueOf(1150.65));
        Assert.assertEquals(fuelConsumedInAugust.getFuelConsumptionList().size(),4);
        Assert.assertEquals(fuelConsumedInAugust.getStatics().size(),2);
        FuelStatics fuelStatics2 = fuelConsumedInAugust.getStatics().get(0);
        Assert.assertEquals(fuelStatics2.getAvgPrice(),Double.valueOf(20.02));
        Assert.assertEquals(fuelStatics2.getTotalPrice(),Double.valueOf(60.06));
    }
    @Test
    public void getDetails() {
        HashMap<Integer, HashMap<String, FuelConsumed>> details = service.getDetails();
        HashMap<String, FuelConsumed> driverID1 = details.get(123);
        HashMap<String, FuelConsumed> driverID2 = details.get(124);
        HashMap<String, FuelConsumed> driverID3 = details.get(125);
        HashMap<String, FuelConsumed> driverID4 = details.get(126);
        Assert.assertTrue(driverID1.keySet().containsAll(Arrays.asList("AUGUST","SEPTEMBER")));
        Assert.assertTrue(driverID2.keySet().containsAll(Arrays.asList("AUGUST")));
        Assert.assertTrue(driverID3.keySet().containsAll(Arrays.asList("OCTOBER")));
        Assert.assertTrue(driverID4.keySet().containsAll(Arrays.asList("OCTOBER","NOVEMBER")));
        Assert.assertEquals(details.size(),4);

    }

    @Test
    public void updateDetails() throws FuelTypeNotFound, InValidPrice {
        FuelConsumption f1 = FuelConsumption.builder().driverid(123).date(LocalDate.of(2019, Month.SEPTEMBER, 10)).fueltype("A").price(10.02).volume(10.00).build();
        FuelConsumption f2 = FuelConsumption.builder().driverid(123).date(LocalDate.of(2019, Month.SEPTEMBER, 12)).fueltype("A").price(10.02).volume(20.00).build();
        FuelConsumption f3 = FuelConsumption.builder().driverid(123).date(LocalDate.of(2019, Month.AUGUST, 11)).fueltype("B").price(20.02).volume(30.00).build();
        List<FuelConsumption> consumptions = Arrays.asList(f1, f2, f3);
        List<FuelConsumption> updateDetails = service.updateDetails(consumptions);
        Assert.assertEquals(updateDetails.size(),3);

    }


}
