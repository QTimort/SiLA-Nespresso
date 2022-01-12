# SiLA Nespresso

## Description
A fun implementation of a [SiLA](https://sila-standard.com/) compliant server
that allows clients to see available Nespresso (CH) coffees and to make a shopping list.

## Motivation
I'm a coffee enthusiast working in lab automation and thought that it would be fun to use the [SiLA Standard](https://sila-standard.com/) to make a coffee shopping list from Nespresso ☕ !  

## Setup

1. Clone repository
2. Clone submodules 
    ```
    git submodule update --init -f --recursive
    ```

3. Compile
    - With test
        ```
        mvn clean install
        ```
    - Or without
        ```
        mvn clean install -DskipTests
        ```
        
4. If everything compiled without error you're done!
 
## Server Usage

1. List available network interfaces
    ```
    java -jar ./server/target/server-1.0.0-SNAPSHOT-jar-with-dependencies.jar -l
    ```

2. Start server with discovery enabled. Replace `YOUR-NET-INTERFACE` with the name of one of the interface previously listed.
    ```
    java -jar ./server/target/server-1.0.0-SNAPSHOT.jar -n YOUR-NET-INTERFACE
    ```

3. To stop the server type `stop`and press enter.

## Connect to the server
In order to interact with the server you'll need a client

### Universal SiLA Client
The easiest solution to use the server is through the Universal SiLA Client which is available here: https://gitlab.com/SiLA2/universal-sila-client/sila_universal_client
If everything is correct, you should be able to see and interact with the Nespresso SiLA Server