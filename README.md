# fuel_consumption
Build maven project and run Demo.classOnce the server starts successfully , say on port 8080 you can use below api's and test using PostMan or any other client.If the server port 8080 is busy , we can configure that in application.properties

1. To add records for a driver or list of drivers     
POST request: http://localhost:8080/fuel/consumption/add 
Payload: [{"driverid": 1,
        "fueltype": "A",
        "price": 10,
        "volume": 20,
        "date": "2019-08-11"
        },
       {
        "driverid": 3,
        "fueltype": "B",
        "price": 20,
        "volume": 30,
        "date": "2019-09-11"
            }]
Note : Validations on add and update:1. DriverId,FuelType,Price,Volume and Date cannot be null2.Date format should be : yyyy-MM-dd3. The fuel type and its price are defined in separate table 'FuelType'.
Assumption: FuelTypes should be  fixed and user should only be allowed to choose available type only .The price of every fuel per lit should be fixed and allowed.
Any changes required can be done using below API's that allows to add and update FuelType.
a. Get all existing FuelTypes and corresponding prices:    
GET Request: http://localhost:8080/fuel/type
b. Update existing fuelType 
PUT Request : http://localhost:8080/fuel/type/update
c. Add fuelType   
POST Request: http://localhost:8080/fuel/type/add  
Payload: [{  "price": 10,
        "typename": "A"
    }]
Below are the validation errors for Invalid Price and Type:{
    "timestamp": "2019-09-15T13:34:34.141+0000",
    "status": 500,
    "error": "Internal Server Error",
    "message": "Fuel Types not found B00  Please add corresponding valid types [A, B, C, D]",
    "path": "/fuel/consumption/add"
}
{
    "timestamp": "2019-09-15T13:29:51.117+0000",
    "status": 500,
    "error": "Internal Server Error",
    "message": "The valid price of fuel typeBis :20.0",
    "path": "/fuel/consumption/add"
}


2. FuelConsumption data retrieval :
a. For a driver :
GET request: http://localhost:8080/fuel/get/amount/{id}
b. For all drivers :POST request: http://localhost:8080/fuel/get/amount

3. Update Records:
PUT request : http://localhost:8080/fuel/update/details
4.Delete records for a driver:
DELETE request : http://localhost:8080/fuel/delete/{id}
