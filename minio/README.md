# Module minio

The 'minio' module is a component of the project that provides a distributed object storage system. It utilizes the MinIO server and client software to enable storage of unstructured data such as photos, videos, and documents. This module provides APIs for uploading, downloading, and managing files stored in the MinIO cluster.

## Table of Contents
* [Main Project](../README.md#project-tech-template)
* [Building and Running the Module](#building-and-running-the-module)
* [Checking Service](#checking-service)
* [Checking Actuator](#checking-actuator)
* [Stopping the Module](#stopping-the-module)


## Building and Running the Module
To build and run the 'minio' module, follow these steps:

1. Navigate to the project directory:
```
cd <projects_directory>/tech-template
```
2. Run the following command to build and run the module using the shell script:
```
sh ./run.sh "minio"
```
OR

Alternatively, you can build and run the module using the following commands:
```
mvn clean install -pl minio
cd ./minio
docker compose up -d
```


## Checking Service
To interact with the service Minio, use the following commands:

Create a new bucket in the S3 storage:
```
curl -X PUT http://127.0.0.1:8081/<bucket_name>
```

Upload a file to the S3 storage:
```
wget --method=PUT --body-file=<local_directory>/<file_name> --header="Content-Type:application/octet-stream" http://127.0.0.1:8081/<bucket_name>/<file_name>
```
```
curl -v -X PUT http://127.0.0.1:8081/<bucket_name>/<file_name> -H "Content-Type:application/octet-stream" --data-binary @<local_directory>/<file_name>
```

Download a file from the S3 storage:
```
curl -v http://127.0.0.1:8081/<bucket_name>/<file_name> -o <local_directory>/<file_name>
```
OR

Alternatively, download parts of a file from the S3 storage:
```
curl -v -H 'range: bytes=0-10000' http://127.0.0.1:8081/<bucket_name>/<file_name> -o <local_directory>/<file_name_part_1>
curl -v -H 'range: bytes=10000-20000' http://127.0.0.1:8081/<bucket_name>/<file_name> -o <local_directory>/<file_name_part_2>
```

Merge the parts of a file:
```
cat <local_directory>/<file_name_part_1> <local_directory>/<file_name_part_2> > <local_directory>/<file_name>
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


## Stopping the Module
To stop the 'minio' module, follow these steps:

Run the following command to stop the module using the shell script:
```
sh ./stop.sh "minio"
```
OR

Alternatively, you can stop the module using the following command:
```
docker compose down
```
