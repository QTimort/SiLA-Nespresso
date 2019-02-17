# SiLA Espresso

## Description
A funny implementation of a [SiLA](https://sila-standard.com/) compliant server
that allows clients to see available Nespresso (CH) coffees and to make a shopping list.

## Motivation
I'm a coffee enthusiast working in a [Lab automation company](http://unitelabs.ch) and 
I thought that it could be fun to use our software with the [SiLA Standard](https://sila-standard.com/) 
in order to make it simpler for us to order coffee ☕ !  

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
    java -jar ./server/target/server-1.0.0-SNAPSHOT-jar-with-dependencies.jar -n YOUR-NET-INTERFACE
    ```

3. To stop the server type `stop`and press enter.

## Connect to the server
In order to interact with the server you'll need a client

### SiLA Browser
The easiest solution is to use the SiLA Browser which is available here: https://unitelabs.gitlab.io/unitelabs_base/

1. Download the jar
    ```
    https://unitelabs.gitlab.io/unitelabs_base/download/sila_browser.jar
    ```
2. Start the Browser
    ```
    java -jar sila_browser.jar
    ```
3. Go to http://127.0.0.1:8080/servers 

If everything is correct, you should be able to see and interact with the Nespresso SiLA Server

### SiLA Manager
Todo