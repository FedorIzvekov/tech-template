# Module minio

## Table of Contents
* [Main Project](../README.md#project-tech-template)
* [Building Only the Module](#building-only-the-module)
* [Running the Module](#running-the-module)
* [Checking Actuator](#checking-actuator)


## Building Only the Module
To build only the 'minio' module, follow these steps:

1. Navigate to the project directory:
```
cd <projects_directory>/tech-template
```
2. Build the module using Maven:
```
mvn clean install -pl minio
```


## Running the Module
Run the module using the following command:
```
java -jar ./minio/target/minio-1.0.0-SNAPSHOT.jar
```


## Checking Actuator
To check the actuator endpoints of the 'minio' module, follow these steps:

1. Check probes:
``` 
curl -v http://localhost:8081/monitoring/health/readiness
curl -v http://localhost:8081/monitoring/health/liveness 
curl -v http://localhost:8081/monitoring/info 
```
2. Get Prometheus metrics:
``` 
curl -v http://localhost:8081/monitoring/prometheus 
```
3. Check logging level:
``` 
curl -v http://localhost:8081/monitoring/loggers 
```
4. Switch logging level without restarting the app:
``` 
curl -v -i -X POST -H 'Content-Type: application/json' -d '{"configuredLevel": "DEBUG"}' http://localhost:8081/monitoring/loggers/org.springframework 
```
5. Shutdown the app:
``` 
curl -v -X POST http://localhost:8081/monitoring/shutdown 
``` 