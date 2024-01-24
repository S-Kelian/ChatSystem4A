# Java report

## Tech stack

- UDP : Mainly used to update the contact lists. The easy use of this protocol makes it the best one for this task, indeed we don't anything more complicated or that would flood the network. It is also used as the initiator of the chats, giving the possibility to refuse to chat with somebody so that you cannot receice tons of incoming chats and have lots of TCP sockets opened against your will.
- TCP : Used for chats between users. This protocol fits the use and gives enough features to make sure that messages are well reveived. We had to open two TCP connections so that messages could flow from user A to B and from B to A.
- Swing : This provides an easy access to a user interface. The software provides a decent graphic interface while not being either ressources hungry nor hard to use. The code of the views did not have to be the main part of our job and thanks to Swing, it has not been. Nonetheless, the user experience is not as good as we could have expected.
- SQLite : This tool was used to store the messages. It provides an easy access to a local database service. Since we did not need advanced functionnalities of databases it was a good solution. We thought about using a system of files which could have made it easy to transfer and read old messages from the file explorer but this would have meant to create a whole lot of drivers and we would have lost the optimisations of SQL requests.
- Maven : It is a good tool for this kind of projects once it we know how to use it.

## Testing policy

- e

## Highlight

- TCP messages contain java objects. This makes the messages heavier but it allows a lot of functionnalities :
  - Easy use of timestamps
  - Easy to send different types of documents with the "type" attribute

- The contact list updates by itself to avoid conflicts. If by any chance some of the connected users would have his application crashing and as a consequence not sending the "DISCONNECT" message to update others contact lists, the contact list of other users update periodically by sending an update request using UDP in broadcast mode and being answered with UDP unicast messages. We had to make sure that those request are not too frequent in order not to overuse the network.
