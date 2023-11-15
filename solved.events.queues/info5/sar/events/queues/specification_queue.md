**Overview: Message Queue / Event-oriented Implementation**

A message queue is an event-oriented implementation that facilitates communication between different components through a broker. This design supports asynchronous communication, allowing tasks to interact without blocking each other. The message queue operates on a publish-subscribe model, where a sender publishes messages to a queue, and a receiver subscribes to receive and process those messages.

**Connecting**
  - The QueueBroker class provides methods for accepting connections.
  - The event-oriented alternative is the bind method, binding a port to the broker and notifying a listener upon connection acceptance.
  - The unbind method unbinds a port from the broker.

**Sending and Receiving**

**Disconnecting**

- **Closing Connections:**
  - The close method in the MessageQueue class closes the queue, disconnecting it from the broker.

- **Handling Disconnections:**
  - The closed method in the MessageQueue class checks if the queue is closed.
  - Disconnections may occur asynchronously. Therefore, listeners are notified of disconnections, and exceptions (e.g., ClosedException) are thrown when attempting to send or receive on a closed queue.

**Note:**
- The event-oriented design allows for efficient handling of communication tasks without blocking, enabling parallelism and responsiveness.
- Exception handling is crucial to manage disconnections and ensure robustness in the communication process.