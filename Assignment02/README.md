# Assignment_02 of "Verteilte Verarbeitung"
## RESTful client server
This is a RESTful server application, that provides basic CRUD interaction with Customers and their contracts.
    - Application runs with SpringBoot(H2, SpringBootSecurity)

### Getting Started

1. Try to get the code running in your IDE 
    - Create new **SpringBoot**-Application using **Gradle** and copy/paste source code
    
3. **CURL**-commands and other on: *https://documenter.getpostman.com/collection/view/3815462-ae0c490f-c3a2-442a-9c0f-5610cebc55f1*

### Before interacting with Server/API

1. This server uses a **self-signed SSL-Certificate**
    - If you make requests with *Browser*: Accept Website as trustworthy in general to be able to interact with API
    - If you are using *Postman*: Settings -> General -> SSL-Certification **off**

2. **Authenticate** yourself by posting to "https://localhost/8443/user" or you will get status code **401** (Unauthorized), when interacting with other endpoints.

### Authentication

1. This application uses basic authentication. Only then you will be able to get, create, update and delete entities.

### Error Codes

1. Successfull get and delete requests return status code **200** (ok)

2. a get request to a non existing resource leads to **204** (no content)

3. Successfull post and put requests lead to **201** (created)

4. Sometimes you get a **400** (bad request)
    - For example: Posting an invalid kind of contract (look up **json entity** for **contract** beneath)

5. A put or delete request to a non-existing source will lead to **404** (not found) 
    - Or you do a request to an url that is not specified

6. There are several reasons for a **409** (Conflict)
    - Your update leads to a **lost** of an preceeding **update**
    - You try to **delete** a **resource**, that is **referenced** by an other
    - You try to create user with a **username** that **already exists**

7. Else 500 if an unexpected error occurs. Please report

### Posting/Creating Entities
(Goes into request body)
**Endpoint-URL is beneath entities**

1. JSON for posting to **User**:
```json
{
    "username": "Some Username",
    "password": "Some Password",
    "role": null // There are no roles yet
}
```

2. JSON for posting to **Customer**: 
```json
{
	"firstname": "Günther",
	"lastname": "Schmidt", 
	"dateOfBirth": "1984-04-20",
	"adress": {
		"street": "Hochschulstraße 1",
		"postalcode": "83022",
		"place": "Rosenheim"
	},
	"contracts": [
		{"kindOfContract": "Krankenversicherung", "yearlyFee": "222.22"},
		{"kindOfContract": "Haftpflicht", "yearlyFee": "222.22"}
	]
}
```

3. JSON for posting to **Contract**: 
```json
{
	"kindOfContract": "Krankenversicherung", // Allowed values: "KRANKENVERSICHERUNG", "HAFTPFLICHT", "RECHTSSCHUTZ", "KFZ"
	"yearlyFee": "49", 
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

1. Get Cpntract by ID 
    - **GET**
    ```
    https://localhost:8443/contract/{id}
    ```

2. Get all Contracts
    - **GET**
    ```
    https://localhost:8443/contract/{id}
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