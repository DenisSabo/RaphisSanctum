# Assignment_03 of "Verteilte Verarbeitung"
## Messaging 
This application is using Apache ActiveMQ (Message Oriented Middleware) + JMS (Java Message Service) for 
communication between JMS-Clients.  

### Getting Started
1. Get Apache ActiveMQ and start a server on your local machine. http://activemq.apache.org/

2. Try to get the code running in your IDE 
    - Create new **Gradle**-Application
    - Copy **dependencies** from the **build.gradle** file
    - Copy files from **src**\main\java\vv\fh\rosenheim into your **new created project**

### Run Code parts in your IDE

1. Start **Telematics Unit's** main first.
    - Here you will be asked how many units you want to start. Type in any number BUT some information will be printed to console -> For clearity reasons do not start to much units
    - Now you will be asked for a time interval at which messages will be sended frequently
        - Smaller time interval -> More messages
    - Now messages will be created and send to your Apache ActiveMQ-Server 
        - **http://localhost:8161/admin/index.jsp** -> Manage Queues/Topics, Producers/Consumers
        - Messages will be send into queue "trip data"

2. Next thing to start is the **filter**'s main
    - This class will consume messages from queue "trip data"
    - if an "alarm-message" was found, it will be send to the queue "alarms"
    - if it is a normal message -> Will be send to topic "distributor"

3. Now you can start the **Driver's logbook** or the **DataWarehouse's** main
    - Both classes are (durable) subscribers to the topic "distributor" 
    - **Driver's logbook** -> Saves messages as list to each unit on the hard drive (C://telematicsLists/)
    - It prints the complete covered distance of each unit in regular intervals
    - **DataWarehouse** -> does not store the messages, but instead stores the distance travelled by each unit at each hour at each date
    - As the logbook, it prints the collected information in regular intervals

4. Some information to how the message is handled here
    - The class TelematicsMessage contains all data needed for the other classes, which consume this messages
    - An instance of this message is created and filled properly by the telematics units
    - Then it will be converted to JSON and send as text message to Queues/Topic ...


