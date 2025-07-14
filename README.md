# Yoga - Numdev project

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 14.1.0.
![NumDev Logo](./ressources/images/banner-numdev.png)

Full-stack application developed with Angular and Java Spring Boot, aiming to manage a yoga studio platform.
This project focuses on testing (unit, integration, and end-to-end) with a target coverage of 80%, including at least 30% integration tests.

## Prerequisites

Ensure that the following softwares are installed on your system:

- **Java Development Kit (JDK)**
- **Apache Maven**
- **MySQ:**
- **Node.js**
- **7zip**

## Configuration

1. Install Java Development Kit (JDK) 8 with SDKMAN [SDKMAN](https://sdkman.io/)
It simplifies installation and switching between different SDKs. For this project, you'll need Zulu JDK 8 (JavaSE-1.8).
Prerequisite: [7zip](https://www.7-zip.org/)

Then, in Git Bash (run as administrator), create a symbolic link to make it accessible:

  ```shell
# Git Bash install 7zip
ln -s /c/Program\ Files/7-Zip/7z.exe /c/Program\ Files/Git/mingw64/bin/zip.exe

# Git Bash install SDK Man
export SDKMAN_DIR="/c/sdkman" && curl -s "https://get.sdkman.io" | bash
```

Now, install Java 8 using SDKMAN:

```shell
sdk install java 8.0.302-zulu
```

Set the JAVA_HOME Environment Variable: 
- Windows:

1. Open System Properties
2. Go to the `Advanced` tab
3. Click on `Environment Variables`
4. Under `System variables`, click  `New`.
   - Variable name: JAVA_HOME
   - Variable value: Path to your JDK installation
    (e.g. C:\sdkman\candidates\java\8.0.302-zulu)
5. Click `OK` to save

Restart your computer and verify your Java version installed:

```shell
java -version
```

You should see output similar to:

```shell
openjdk version "1.8.0_302"
OpenJDK Runtime Environment (Zulu 8.54.0.21)
```

2. Install MySQL DATABASE : 

SQL script for creating the schema is available `ressources/sql/script.sql`

The admin account is:
- login: yoga@studio.com
- password: test!1234

## Start the project

Git clone:

> git clone https://github.com/OpenClassrooms-Student-Center/P5-Full-Stack-testing


### Front-End

Go inside folder:

```shell
cd front
```

Install dependencies:

```shell
npm install
```

Launch Front-end:

```shell
npm run start
```

### Back-End

1. Configure the application in the `application.properties` file (access to your database)

2. Install dependencies:

```shell
mvn clean install
```

3. Run back :

```shell
mvn spring-boot:run
```

## Ressources

### Mockoon env 

### Postman collection

For Postman import the collection

> ressources/postman/yoga.postman_collection.json 

by following the documentation: 

https://learning.postman.com/docs/getting-started/importing-and-exporting-data/#importing-data-into-postman


### FRONT-END Test

#### E2E

Launching e2e test:

```shell
npm run e2e
```

Coverage report (launch e2e test before):

```shell
npm run e2e:coverage
```

Report is available here:

front/coverage/lcov-report/index.html

#### Unitary test

Launching test:

```shell
npm run test
```

watch changes:

```shell
npm run test:watch
```


### BACK-END Test

#### Unitary test

Jacoco test:
```shell
mvn clean site
```

Report is available here:

front/target/site/jacoco.index.html
