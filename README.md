# Project : chat system

Autors: Kalian Sebaici, Théophile Zenou-Truchot

This project aims to deliver a peer-to-peer app for communication on a local network. It provides an easy way to communicate within a company. It must be easy to use and easy to install.
The app runs on the version 17 of the Java Development Kit and Maven.

With the current version you will be able to :

- create a profile by setting a pseudo
- change your pseudo and inform other people on the network of this change
- automatically/periodically update the list of the users currently connected on the network

First you will need to clone the repository on your machine using this command:

```bash
git clone https://github.com/insa-4ir-chatsystem/chatsystem-sebaici-zenou-truchot.git
```

Use the following command lines on your machine to launch the app:

```bash
mvn clean package
```

```bash
java -jar target/ChatSytem-1.0-SNAPSHOT.jar
```
