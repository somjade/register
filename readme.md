# Assignment
> We need new registration service for mobile application. 
>
> Please provide Restful API for user to register and get user information after registration. 
>
> Try your best to deliver 2 API services with security (access token, encryption, etc.) and testability (test cases).
>
> Submit source code on GIT control with document (design, description, installation guide, usage, etc.)
>
> Please design data for registration process (username, password, address, phone, etc.) and keep it in SQL database for retrieval. 
>
> Please also implement business logic below.
>
> Register process has to generate reference code from register date and last 4 digits of phone number like this “YYYYMMDDXXXX” (ex. 201708154652) and keep it in database.
>
> Member type classify from salary
>
> * Platinum (salary > 50,000 baht)
>
> * Gold (salary between 30,000 to 50,000)
>
> * Silver (salary < 30,000)
>
> * Reject if salary < 15,000 with error code (please define error code)

# Overview
From as assignment expose 'RegisterApplication' service securing RESTful APIs with JWTs authentication listening port `8008`.

During the authentication process, when a user successfully logs in using their credentials, a JSON Web Token is returned and must be saved locally (typically in local storage). Whenever the user wants to access a protected route or resource (an endpoint), the user agent must send the JWT, usually in the `Authorization` header using the `Bearer` schema, along with the request.

When a backend server receives a request with a JWT, the first thing to do is to validate the token. This consists of a series of steps, and if any of these fails then, the request must be rejected. The following list shows the validation steps needed:

- Check that the JWT is well formed
- Check the signature
- Validate the standard claims
- Check the Client permissions (scopes)

#### RESTful APIs

Accessible at the http://localhost on port 8008.

1. Register: http://localhost:8008/v1/users/register (Not validate the access token)
2. Login: http://localhost:8008/v1/users/login (Not validate the access token)
3. Get my profile: http://localhost:8008/v1/users/me (Validate the access token)

# Installation Guide & Test

### Prerequisite

1.Clone source code from github 
```
$ git clone https://github.com/somjade/register.git
```

2.Setup database and database user

Please create database and db user, In order to access the database, you can connect to it using the following details:
```
Hostname: localhost
Port: 3306 (Local MySQL), 3307 (MySQL run by docker-compose) 
Database name: register
Database user: register_app
Database password: user1234!
```
- Create by local MySQL installed use 2.1
- Create by docker-compose use 2.2

2.1 Setup by local MySQL installed

- Connect to MySQL local installed and create database and db user.

``` 
$ mysql -uroot -p${YOUR_ROOT_PASSWORD}
mysql: [Warning] Using a password on the command line interface can be insecure.
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 5
Server version: 5.7.28 MySQL Community Server (GPL)

Copyright (c) 2000, 2019, Oracle and/or its affiliates. All rights reserved.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql>
mysql> -- If already exists drop first;
mysql> DROP DATABASE register;
mysql> DROP USER 'register_app'@'localhost';
mysql>
mysql> -- Create 
mysql> CREATE DATABASE register;
mysql> CREATE USER 'register_app'@'localhost' IDENTIFIED BY 'user1234!';
mysql> GRANT ALL PRIVILEGES ON register.* TO 'register_app'@'localhost';
mysql> FLUSH PRIVILEGES;
mysql>
mysql> -- Verify database and db user created
mysql> SHOW DATABASES;
+--------------------+
| Database           |
+--------------------+
| information_schema |
| register           |
+--------------------+
2 rows in set (0.01 sec)
mysql> 
mysql> SHOW GRANTS FOR 'register_app'@'localhost';
+--------------------------------------------------------------------+
| Grants for register_app@localhost                                  |
+--------------------------------------------------------------------+
| GRANT USAGE ON *.* TO 'register_app'@'localhost'                   |
| GRANT ALL PRIVILEGES ON `register`.* TO 'register_app'@'localhost' |
+--------------------------------------------------------------------+
2 rows in set (0.01 sec)
```

2.2 Setup by docker-compose ([Install Docker Compose](https://docs.docker.com/compose/install/) if does not exist.)

Please make sure you are at root of clone repository directory

* Run docker-compose to create database ans db user
```
$ cd register
$ docker-compose up -d
...
Creating network "register-service_default" with the default driver
Creating register-service_db_1 ... done
```

   * Verify mysql up and running, database and db user created
```
$ docker-compose exec db sh -c 'mysql -uroot -p${MYSQL_ROOT_PASSWORD}'
mysql: [Warning] Using a password on the command line interface can be insecure.
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 8
Server version: 8.0.22 MySQL Community Server - GPL

Copyright (c) 2000, 2020, Oracle and/or its affiliates. All rights reserved.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql>

mysql> SHOW DATABASES;
+--------------------+
| Database           |
+--------------------+
| information_schema |
| mysql              |
| performance_schema |
| register           |
| sys                |
+--------------------+
5 rows in set (0.02 sec)
```

### Run & Test

#### Please note 

The default connection string in config use docker-compose port `3307`
If your use local MySQL please change connection string to your MySQL listening port (default`3306`)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/register?characterEncoding=UTF-8
    username: register_app
    password: user1234!
    flyway:
      enabled: true
    jpa:
```

1.Then, we run the **RESTful API** by either issuing the by command command line or by building and running the project in your favorite IDE.

1.1.Open your command-line terminal and change directory to the root of cloned source code directory from github. 

* For **Mac:** ./gradlew :bootRun
* For **Windows:** gradlew.bat :bootRun

This instruction will use **Mac:** 
```
$ cd register
$ ./gradlew :bootRun
```

Log show up and running with **`Started RegisterApplication`** in console log
```java
...
2021-02-06 13:10:05.394  INFO 4875 --- [  restartedMain] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8008 (http) with context path ''
2021-02-06 13:10:05.414  INFO 4875 --- [  restartedMain] c.tmb.test.register.RegisterApplication  : Started RegisterApplication in 6.133 seconds (JVM running for 6.695)
<==========---> 80% EXECUTING [15s]
> :bootRun
```
Make sure table **'USERS'** created by flyway.

```
mysql> USE register;
Reading table information for completion of table and column names
You can turn off this feature to get a quicker startup with -A

Database changed
mysql> SHOW TABLES;
+-----------------------+
| Tables_in_register    |
+-----------------------+
| USERS                 |
| flyway_schema_history |
+-----------------------+
2 rows in set (0.01 sec)

mysql> DESC USERS;
+--------------+----------------------------------+------+-----+-------------------+-----------------------------+
| Field        | Type                             | Null | Key | Default           | Extra                       |
+--------------+----------------------------------+------+-----+-------------------+-----------------------------+
| USER_ID      | int(11)                          | NO   | PRI | NULL              | auto_increment              |
| REF_CODE     | varchar(12)                      | NO   | UNI | NULL              |                             |
| PASSWORD     | varchar(100)                     | NO   |     | NULL              |                             |
| EMAIL        | varchar(50)                      | NO   | UNI | NULL              |                             |
| TITLE        | varchar(20)                      | YES  |     | NULL              |                             |
| FIRSTNAME    | varchar(50)                      | YES  |     | NULL              |                             |
| LASTNAME     | varchar(50)                      | YES  |     | NULL              |                             |
| PHONE        | varchar(10)                      | YES  | UNI | NULL              |                             |
| SALARY       | decimal(8,2)                     | NO   |     | NULL              |                             |
| HOUSE_NO     | varchar(10)                      | YES  |     | NULL              |                             |
| MOO          | int(11)                          | YES  |     | NULL              |                             |
| STREET       | varchar(50)                      | YES  |     | NULL              |                             |
| ROAD         | varchar(50)                      | YES  |     | NULL              |                             |
| SUB_DISTRICT | varchar(50)                      | YES  |     | NULL              |                             |
| DISTRICT     | varchar(50)                      | YES  |     | NULL              |                             |
| PROVINCE     | varchar(50)                      | YES  |     | NULL              |                             |
| POSTCODE     | varchar(50)                      | YES  |     | NULL              |                             |
| CREATED_DATE | datetime                         | NO   |     | CURRENT_TIMESTAMP |                             |
| UPDATED_DATE | datetime                         | YES  |     | NULL              | on update CURRENT_TIMESTAMP |
| MEMBER_TYPE  | enum('PLATINUM','GOLD','SILVER') | YES  |     | NULL              |                             |
+--------------+----------------------------------+------+-----+-------------------+-----------------------------+
20 rows in set (0.00 sec)
mysql> 
```

2.If everything works as expected, our api will be up and running ready to test, we can use a tool like `Postman` or `curl` to issue request to the available endpoints:

Open new terminal use curl to issue request

#### Test Register

##### Scenario#1 Register user has been reject when `salary < 15,000`

- Register Request:
```
curl -H "Content-Type: application/json" -X POST -d '{
    "email": "rejetced@abc.com",
    "password": "12345!",
    "title": "MR.",
    "first_name": "John",
    "last_name": "Doe",
    "phone": "0891111111",
    "salary":"15000",
    "house_no": "100",
    "moo": "12",
    "street": "Bangna",
    "road": "Bangna",
    "sub_district": "Bang Phi",
    "district": "Bang Phi Yai",
    "post_code": "10540",
    "province": "Samutprakarn"
}' http://localhost:8008/v1/users/register
```

- Register Response: (Http Status: 400, Bad Request)
```json 
{"status_code":400,"message":"Reject salary below minimum allowing"}
```

##### Scenario#2 Register user when `salary < 30,000` MEMBER_TYPE: **SILVER**

* Register Request:
```
curl -H "Content-Type: application/json" -X POST -d '{
    "email": "silver@abc.com",
    "password": "Silver12345!",
    "title": "MR.",
    "first_name": "SILVER",
    "last_name": "MEMBER",
    "phone": "0891111111",
    "salary":"15001",
    "house_no": "100",
    "moo": "12",
    "street": "Bangna",
    "road": "Bangna",
    "sub_district": "Bang Phi",
    "district": "Bang Phi Yai",
    "post_code": "10540",
    "province": "Samutprakarn"
}' http://localhost:8008/v1/users/register
```

* Register Response: (Http Status: 200) return generate reference code from register date and last 4 digits of phone number like this “YYYYMMDDXXXX” (ex. 201708154652) and keep it in database.
```json 
{"ref_code":"202102061111"}
```

##### Scenario#3 Register user when `salary between 30,000 to 50,000` MEMBER_TYPE: **GOLD**

* Register Request:
```
curl -H "Content-Type: application/json" -X POST -d '{
    "email": "gold@abc.com",
    "password": "Gold12345!",
    "title": "MR.",
    "first_name": "GOLD",
    "last_name": "MEMBER",
    "phone": "0892222222",
    "salary":"30001",
    "house_no": "101",
    "moo": "12",
    "street": "Bangna",
    "road": "Bangna",
    "sub_district": "Bang Phi",
    "district": "Bang Phi Yai",
    "post_code": "10540",
    "province": "Samutprakarn"
}' http://localhost:8008/v1/users/register
```

* Register Response: (Http Status: 200) return generate reference code from register date and last 4 digits of phone number like this “YYYYMMDDXXXX” (ex. 201708154652) and keep it in database.
```json 
{"ref_code":"202102062222"}
```

##### Scenario#4 Register user when `salary > 50,000` MEMBER_TYPE: **PLATINUM**

* Register Request:
```
curl -H "Content-Type: application/json" -X POST -d '{
    "email": "platinum@abc.com",
    "password": "Platinum12345!",
    "title": "MR.",
    "first_name": "PLATINUM",
    "last_name": "MEMBER",
    "phone": "0893333333",
    "salary":"50001",
    "house_no": "103",
    "moo": "12",
    "street": "Bangna",
    "road": "Bangna",
    "sub_district": "Bang Phi",
    "district": "Bang Phi Yai",
    "post_code": "10540",
    "province": "Samutprakarn"
}' http://localhost:8008/v1/users/register
```

* Register Response: (Http Status: 200) return generate reference code from register date and last 4 digits of phone number like this “YYYYMMDDXXXX” (ex. 201708154652) and keep it in database.
```json 
{"ref_code":"202102063333"}
```

##### Scenario#5 When user name (email) already registered. 
 
* Register Request:
```
curl -H "Content-Type: application/json" -X POST -d '{
    "email": "silver@abc.com",
    "password": "Silver12345!",
    "title": "MR.",
    "first_name": "SILVER",
    "last_name": "MEMBER",
    "phone": "0891111111",
    "salary":"15001",
    "house_no": "100",
    "moo": "12",
    "street": "Bangna",
    "road": "Bangna",
    "sub_district": "Bang Phi",
    "district": "Bang Phi Yai",
    "post_code": "10540",
    "province": "Samutprakarn"
}' http://localhost:8008/v1/users/register
```

* Register Response: (Http Status: 409, Conflict), return simple error message during development.
```json 
{
    "status_code":409,
    "message":"Duplicate entry 'silver@abc.com' for key 'users.EMAIL_UNIQUE'"
}
```

##### Verify above success register 3 users created in database.

```
mysql> USE register;
mysql> SELECT REF_CODE, EMAIL, PASSWORD, SALARY, MEMBER_TYPE FROM USERS;
+--------------+------------------+----------------------------------------------+----------+-------------+
| REF_CODE     | EMAIL            | PASSWORD                                     | SALARY   | MEMBER_TYPE |
+--------------+------------------+----------------------------------------------+----------+-------------+
| 202102061111 | silver@abc.com   | eSlq/2RgeuqeKb1uXoPaGv7t5nnJ3Nke75BuE+1OfoY= | 15001.00 | SILVER      |
| 202102062222 | gold@abc.com     | fOEE874u3yzT7V8a8N38xk7daUP8oQKJQkVilR4zObQ= | 30001.00 | GOLD        |
| 202102063333 | platinum@abc.com | FCPvBDOTf+tkPXdTd66sY12N3R/gK3+PH9xw/RS/EcQ= | 50001.00 | PLATINUM    |
+--------------+------------------+----------------------------------------------+----------+-------------+
3 rows in set (0.01 sec)
```

#### Test Login & Get User Profile

Test login into the application (JWT is generated) and get user profile steps
* Login by user name (email) and password to get `{jwt access token}`.
* Replace `{jwt access token}` form login response replace to **header** `Application: Barer {jwt access token}` get user profile.

##### Scenario#1 Login and get profile by user name **"silver@abc.com"**

* Login Request: 
```
curl -s -H "Content-Type: application/json" -X POST -d '{ 
  "user_name": "silver@abc.com", 
  "password": "Silver12345!" 
}' http://localhost:8008/v1/users/login
```
* Login Response:
```json
{
  "access_token":"{jwt access token}"
}
```

* Replace `{jwt access token}` from login response and call get user profile
```
curl -s -H "Content-Type: application/json" -H 'Authorization: Bearer {jwt access token}' http://localhost:8008/v1/users/me
```

* User Profile Response: 
```json
{
  "full_name": "MR. SILVER MEMBER",
  "email": "silver@abc.com",
  "phone": "0891111111",
  "address": "100 ,Moo 12 ,Bangna ,Bangna ,Bang Phi ,Bang Phi Yai ,Samutprakarn ,10540",
  "member_type": "SILVER"
}
```

##### Repeat Scenario#1 steps for other user to see the result.
* User name: gold@abc.com, password: Gold12345!
* User name: platinum@abc.com, password: Platinum12345!