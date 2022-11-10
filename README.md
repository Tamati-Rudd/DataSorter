# DataSorter
Project that uses a thread pool on a TCP server to sort randomly generated numeric data for TCP clients.

A TCP client makes a request to the TCP server, which will have a thread in the pool perform a task for that client, notifying a TaskObserver on progress. When the task is complete, the result is sent to the client.
- The project is setup in such a way where additional classes could be added to facilitate new tasks.
- The thread pool can be resized and closed (will not disrupt tasks in progress).
- Uses the observer design pattern to notify listeners on task progress.
- Uses the singleton design pattern to ensure each task instance has a unique ID.

Created in Netbeans for a university assignment.
