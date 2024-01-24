# Project : Chat system

Authors: Kelian Sebaici, Théophile Zenou-Truchot

This project aims to deliver a peer-to-peer app for communication on a local network. It provides an easy way to communicate within a company. It must be easy to use and easy to install.
The app runs on the version 17 of the Java Development Kit and uses Maven :

## Maven installation
If Maven is not installed on your machine, use the following commands to install it (Linux only) :

```bash
mkdir -p ~/bin
cd ~/bin
wget https://dlcdn.apache.org/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.tar.gz -O maven.tar.gz
tar xf maven.tar.gz
echo 'export PATH=~/bin/apache-maven-3.9.5/bin:$PATH' >> ~/.bashrc
source ~/.bashrc
```


## Linux instructions

Use the following command lines on your machine to compile and launch the app:

```bash
mvn clean package
```

```bash
java -jar target/ChatSytem-1.0-SNAPSHOT-jar-with-dependencies.jar
```


## Features

- Use a graphical interface.
- Create a profile by setting a pseudo.
- Change your pseudo and inform other people on the network of this change.
- Update the user list in the process when someone changes his nickname, when you press the "Refresh" button and once a minute automatically. (Only the refresh button causes the view to be actualised)
- See the list of the connected users
- Ask someone to start a chat
- Open the history of a chat without actually chatting
- Chat with anybody connected on the local network


The app is able to run on Linux and on Windows and no compatibility problem has been found yet, if so, please notify us by sending an email to <sebaici@insa-toulouse.fr> and <zenou-trucho@insa-toulouse.fr>. You can also contact us if you find a bug.

## Known bugs
- Number of userConnect not updated
- Message send twice with same session using SSH
- Stop Chat Session doesn't work sometimes
- Auto disconnect

## Documentation and reports

You can find all reports and documentation in the [doc folder](doc):
- [java-report.md] (doc/java-report.md)
- [Management](doc/Project management.pdf)
- [UML](doc/UML report.pdf)
- [JavaDoc](doc/javadoc/index.html)