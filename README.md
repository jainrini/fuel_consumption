# Fuel Consumption Management Application
The application allows to register for fuel and retrieve information about all the drivers.


## Built With

* [JAVA 8] - Language
* [SpringBoot],[JUNIT],[Mockito]- Frameworks
* [Maven] - Build Tool

**Import and build the maven project**
Run Demo.class and wait for the server to be started
Once the server is up , you can test the API's using client like POSTMAN.
By default server starts at port 8080 but if it is busy then server configuration can be added in application.properties.

server.port=8081

### Prerequisites

## Using the API's

**To add records for a driver or list of drivers**
1.POST request: http://localhost:8080/fuel/consumption/add
```Payload: [{"driverid": 1,
"fueltype": "A",
"price": 10,
"volume": 20,
"date": "2019-08-11"
}]
```
**Note** : Validations on add and update:
1. DriverId,FuelType,Price,Volume and Date cannot be null
2. Date format should be : yyyy-MM-dd.
The fuel type and its price are defined in separate table 'FuelType'.
**Assumption:** FuelTypes should be fixed and user should only be allowed to choose available
type .The price of every fuel per lit should be fixed and should be auto populated on user
interface.
Any changes required can be done using below API's which allows to add and update FuelType
and its prices.
1. Get all existing FuelTypes and corresponding prices:
GET Request: http://localhost:8080/fuel/type
2. Update existing fuelType :
PUT Request : http://localhost:8080/fuel/type/update
3. Add fuelType :
POST Request: http://localhost:8080/fuel/type/add

Below are the error messages if there invalid price and type of fuel.
```"message": "Fuel Types not found B00 Please add corresponding valid types [A, B, C,
D]"
"message": "The valid price of fuel typeBis :20.0",

```
**2. FuelConsumption data retrieval :**
1.total spent amount of money grouped by month
2.list fuel consumption records for specified month (each row should contain: fuel type, volume, date, price, total price, driver ID) 
3.statistics for each month, list fuel consumption records grouped by fuel type (each row should contain: fuel type, volume, average price, total price)
4.The API will throw record not found exception if id doesn’t exist.
 a.For a driver : GET request: http://localhost:8080/fuel/get/amount/{id} 
 b.For all drivers :GET request: http://localhost:8080/fuel/get/amount
```
Sample Response:

{
    "SEPTEMBER": {
        "driverId": 1,
        "totalAmountSpent": 300,
        "fuelConsumptionList": [
            {
                "fid": 1,
                "driverid": 1,
                "fueltype": "A",
                "price": 10,
                "volume": 10,
                "date": "2019-09-11",
                "moneySpent": 100
            },
            {
                "fid": 2,
                "driverid": 1,
                "fueltype": "B",
                "price": 20,
                "volume": 10,
                "date": "2019-09-11",
                "moneySpent": 200
            }
        ],
        "statics": [
            {
                "fuelType": "A",
                "totalVolume": 10,
                "totalPrice": 10,
                "avgPrice": 10
            },
            {
                "fuelType": "B",
                "totalVolume": 10,
                "totalPrice": 20,
                "avgPrice": 20
            }
        ]
    }
}
```
**3. Update Records for drivers:**
        PUT request : http://localhost:8080/fuel/update/details

**4.Delete records for a driver:**
    DELETE request : http://localhost:8080/fuel/delete/{id}
