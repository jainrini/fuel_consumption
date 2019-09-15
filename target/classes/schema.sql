DROP TABLE IF EXISTS Fuel_Consumption;
DROP TABLE IF EXISTS Fuel_Type;


CREATE TABLE Fuel_Consumption(
  fid INT AUTO_INCREMENT  PRIMARY KEY,
  driverid INT,
  fueltype VARCHAR(250),
  price NUMBER(8,2),
  volume NUMBER(8,2),
  date DATE
);

CREATE TABLE Fuel_Type(
  typeid INT AUTO_INCREMENT,
  price NUMBER(10,2) NOT NULL,
  typename VARCHAR(250) NOT NULL,
  PRIMARY KEY (typeid,typename)
);