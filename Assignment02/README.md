# Assignment_02 of "Verteilte Verarbeitung"
## RESTful client server
This is a RESTful server application, that provides basic CRUD operations on Customer- and their Contract-Entities.
- This application runs uses SpringBoot(H2-Database, SpringBootSecurity for Basic-Auth and other)

### Getting Started

1. Try to get the code running in your IDE 
    - Create new **SpringBoot**-Application using **Gradle** and copy/paste source code
    - You can copy dependencies from the build.gradle file
    
3. **CURL**-commands and other on: *https://documenter.getpostman.com/collection/view/3815462-ae0c490f-c3a2-442a-9c0f-5610cebc55f1*

### Before interacting with Server/API

1. This server uses a **self-signed SSL-Certificate**
    - If you make requests with *Browser*: Accept Website as trustworthy to be able to interact with API
    - If you are using *Postman*: Settings -> General -> SSL-Certification **off**
    - Or general solution: Add certificate to your truststore (Java-Truststore, ...)
    - Or you can just turn of SSL, if you just want to test the application

2. and basic-auth

### Authentication

1. This application uses basic-auth

2. **Authenticate** yourself by posting a new user to "https://localhost/8443/user" 

3. Then you can use the new Account, for further interaction with server (Especially with protected endpoints)

### Error Codes

1. Successfull get and delete requests return status code **200** (ok)

2. Successfull post and put requests lead to **201** (created)

3. Sometimes you get a **400** (bad request)
    - For example: Posting an invalid contract-, customer- or user-entity (look up **json entity** for **contract**/**customer**/**user** beneath)
    - For more information about, what a valid entity looks like, look up the **entity-section** beneath

4. A get, put or delete request to a non-existing source will lead to **404** (not found) 
    - Or you do a request to an url that is not specified ( for example: uri/some/endpoint/that/does/not/exist )

5. There are several reasons for a **409** (Conflict)
    - Your update would lead to the **lost** of a preceeding **update**, so it wont be executed (Strategy: First update wins)
    - You try to **delete** a **resource**, that is **referenced** by an other (F.e. you want to delete a contract, that is still used by a customer)
    - You try to create user with a **username** that **already exists**
    - You try to create Customer, that already exists (First name, last name, date of birth and adress is the same)
    - You try to create Contract, that already exists (kind of contract, and yearly fee is the same)

6. Else 500 if an unexpected error occurs. Please report


**Endpoint-URL is beneath entities**

1. JSON for posting to **User**:
```json
{
    "username": "Some Username", // min: 3
    "password": "Some Password", // min: 6
    "role": null // There are no roles yet
}
```

2. JSON for posting to **Customer**: 
```json
{
	"firstname": "Günther", // min: 3, max: 20
	"lastname": "Schmidt", // min: 3, max: 30
	"dateOfBirth": "1984-04-20", // Not null
	"address": {
		"street": "Hochschulstraße 1", // min: 5
		"postalcode": "83022", // Not empty
		"place": "Rosenheim" // min: 3
	},
	"contracts": [
        {
            "kindOfContract": "Krankenversicherung", // not null + allowed values 
            "yearlyFee": "222.22"   // Postive value
         },
        {
            "kindOfContract": "Haftpflicht",
            "yearlyFee": "222.22"
         }
	]
}
```

3. JSON for posting to **Contract**: 
```json
{
	"kindOfContract": "Krankenversicherung", // Allowed values: "KRANKENVERSICHERUNG", "HAFTPFLICHT", "RECHTSSCHUTZ", "KFZ"
	"yearlyFee": "42.01", 
}
```


### Endpoints

#### User

1. Get Customer by Username
    - **GET**
    ```
    https://localhost:8443/user/{username}
    ```

2. Get all Users
    - **GET**
    ```
    https://localhost:8443/users
    ```

3. Create new User
    - **POST**
    ```
    https://localhost:8443/user
    ```

4. Delete User by Username
    - **DELETE**
    ```
    https://localhost:8443/user/{username}
    ```

#### Customer

1. Get Customer by ID 
    - **GET**
    ```
    https://localhost:8443/customer/{id}
    ```

2. Get all Customers
    - **GET**
    ```
    https://localhost:8443/customer/{id}
    ```

3. Create new Customer
    - **POST**
    ```
    https://localhost:8443/customer
    ```

4. Update Customer by ID
    - **PUT** 
    ```
    https://localhost:8443/customer/{id}
    ```

5. Delete Customer by ID
    - **DELETE**
    ```
    https://localhost:8443/customer/{id}
    ```

#### Contracts

1. Get Contract by ID 
    - **GET**
    ```
    https://localhost:8443/contract/{id}
    ```

2. Get all Contracts
    - **GET**
    ```
    https://localhost:8443/contracts
    ```

3. Create new Contract
    - **POST**
    ```
    https://localhost:8443/contract
    ```

4. Update Contract by ID
    - **PUT** 
    ```
    https://localhost:8443/contract/{id}
    ```

5. Delete Contract by ID
    - **DELETE**
    ```
    https://localhost:8443/contract/{id}
    ```