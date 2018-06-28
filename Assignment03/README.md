# Assignment_03 of "Verteilte Verarbeitung"
## Messaging 
This application is using Apache ActiveMQ (Message Oriented Middleware) + JMS (Java Message Service) for 
communication between JMS-Clients.  

### Getting Started
1. Get Apache ActiveMQ and start a server on your local machine. http://activemq.apache.org/

2. Try to get the code running in your IDE 
    - Create new **Gradle**-Application
    - Copy **dependencies** from the **build.gradle** file
    - Copy source files from **src**\main\java\vv\fh\rosenheim into your **new created project**

### Run Code parts in your IDE

1. Start **Telematics Unit's** main first.
    - Here you will be asked how many units you want to start. Type in any number BUT some information will be printed to console -> For clearity reasons do not start to much units
    - Now you will be asked for a time interval at which messages will be sended regularly. Type in '5' for instance and each unit will send a message of current status each 5 seconds
        - Smaller time interval -> More messages
    - Now messages will be created and send to your Apache ActiveMQ-Server 
        - **http://localhost:8161/admin/index.jsp** -> Manage Queues/Topics, Producers/Consumers
        - Messages will be send into queue "trip data"
    - Besides that each unit sends one **alarm**-message after a random time (Between 20 and 60 seconds after start)

2. Next thing to start is the **filter**'s main
    - This class will consume messages from queue "trip data"
    - if an "alarm-message" was found, it will be send to the queue "alarms"
    - if it is a normal message -> Will be send to topic "distributor"

3. Now you can start the **Driver's logbook** or the **DataWarehouse's** main
    - Both classes are (durable) subscribers to the topic "distributor" 
    - **Driver's logbook** -> Saves messages as list for each telematics unit. Will be saved on the hard drive  (C://telematicsLists/)
        - Example. Only one unit creating messages -> One file in C://telematisLists/<TelematicsID>.txt
    - It prints the complete covered distance of each unit in regular intervals by iterating through the lists and summing up the distances
    - **DataWarehouse** -> does not store the messages, but instead stores the distances travelled by each unit for each hour a day 
        - Prints information regularly
Unit : <UnitID>; Date: 28.06.2018

| Hour of Day | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 | 11 | 12 | 13 | 14 | 15 | 16 | 17 | 18 | 19 | 20 | 21 | 22 | 23 | 24 |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|                     
| Covered Distance | 0 | 0 | 0 | 0 | 0 | 250 | 50.000 | ... | ... | ... | ... | ... | ... | ... | ... | ... | ... | ... | ... | ... | ... | ... | ... | ... |


4. Some information about message-serialization
    - The class TelematicsMessage contains all data needed for the other classes, which consume this messages
        - Telematics ID which created this message, location, driven distance since last message and timestamp of time when message was created
    - An instance of this message is created and filled properly by the telematics units
    - Then it will be converted to JSON and send as text message to Queues/Topic ...


