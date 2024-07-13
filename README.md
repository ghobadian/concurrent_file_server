# MultiThreadedFileServer

A Java-based multithreaded file server designed to handle concurrent file read and write requests with basic synchronization mechanisms. This project demonstrates the implementation of thread pools, mutex for critical section management, and robust file operations to ensure data integrity and efficient resource usage.

## Project Overview
In this project, you will design and implement a multithreaded file server that manages multiple client requests for reading and writing files simultaneously. You will learn and apply basic synchronization techniques to manage access to shared resources and avoid race conditions. This project helps in understanding key operating system concepts like process management, file I/O, and synchronization mechanisms.

## Project Goals
1. Implement multithreading in a programming language (Java).
2. Apply basic synchronization techniques using mutexes.
3. Develop an operational file server that can handle concurrent client requests.
4. Document your design and implementation process.

## Project Components
1. **Multithreaded File Server**
    - Implementation of a basic TCP server that accepts client connections.
    - Use of multithreading to manage multiple client connections simultaneously.

2. **Thread Pool**
    - Implementation of a thread pool to manage a fixed number of threads for processing client requests.
    - Ensure optimal resource utilization and reduce overhead from creating and destroying threads.

3. **Synchronization Mechanisms**
    - Use of mutexes to protect critical sections of code that access shared resources (e.g., files).
    - Ensure proper synchronization to avoid race conditions and guarantee data consistency.

4. **File Operations**
    - Implementation of read and write file operations as requested by clients.
    - Manage simultaneous file access by multiple clients.

5. **Testing and Documentation**
    - Develop test cases to validate the file server's functionality.
    - Document the design and implementation of the file server.


## Project Steps

### Step 1: Design and Implement the Basic Server
- Implement a basic TCP server that listens for client connections.
- Write a simple client to connect to the server and send a test message.
- Ensure the server accepts the connection and handles the message correctly.

### Step 2: Add Multithreading and Thread Pool
- Modify the server to create a new thread for each client connection.
- Ensure the server can handle multiple client connections concurrently.
- Implement a thread pool to manage a fixed number of threads for processing client requests efficiently.

### Step 3: Implement Synchronization and File Operations
- Identify critical sections in your code where shared resources are accessed (e.g., file operations).
- Implement mutexes to protect these critical sections.
- Implement file read and write operations requested by clients.
- Ensure clients can read and write files simultaneously without data corruption.

## Additional Features (Optional)

To enhance the functionality of your file server, you can implement a TCP server for smartwatches that handles specific commands. These additional features simulate real-world scenarios where smart devices communicate with a server to perform various tasks. Here are the detailed features:

1. **Power Off Command**
    - The server sends a command to the smartwatch to power off.
    - **Message Format**: `[3G*IMEI*POWEROFF]`
    - **Functionality**: Upon receiving this command, the smartwatch will power down.

2. **Find Command**
    - The server sends a command to the smartwatch to help locate it when lost.
    - **Message Format**: `[3G*IMEI*FIND]`
    - **Functionality**: The smartwatch will start ringing for 30 seconds to help the user locate it.

3. **Health Data Reporting**
    - The smartwatch periodically sends health data to the server.
    - **Message Format**: `[3G*IMEI*HEALTH*heartrate, blood pressure low, blood pressure high]`
    - **Functionality**: Every 30 seconds, the smartwatch reports its health metrics to the server.

4. **Location Data Reporting**
    - The smartwatch periodically sends its geographic location to the server.
    - **Message Format**: `[3G*IMEI*UD, lat, lon]`
    - **Functionality**: Every 45 seconds, the smartwatch sends its latitude and longitude to the server.

---

**Note**: Ensure that the server design includes proper logging and handling of client activities, especially when simulating smartwatch operations.

## License
This project is licensed under the MIT License - see the LICENSE file for details.

