# Project tech-template
The goal of the 'tech-template' project is to provide examples of using various technologies and tools on the Java platform.

It consists of small code examples, configurations and instructions divided into modules:
* minio;

## Table of Contents
* [Code Download](#code-download)
* [Preparation of Environment](#preparation-of-environment)
* [Building the Project](#building-the-project)
* [Module Minio](minio/README.md#module-minio)


## Code Download
To download the 'tech-template' project code to your local machine, use the following command:
```
git clone git@github.com:FedorIzvekov/tech-template.git
```


## Preparation of Environment
To work with the 'tech-template' project, you need to install the following components:
* Java Development Kit version 17;
* Maven for building the project;
* Docker and Docker Compose for container deployment and management.


## Building the Project
To build the project, follow these steps:

1. Navigate to the project directory:
```
cd <projects_directory>/tech-template
```
2. Create an empty Git repository for the 'git-commit-id-maven-plugin':
```
git init
```
3. Build the project using Maven:
```
mvn clean install
```
