
**IpcLayer**

IpcLayer is the subsystem used for handling commands to perform wallet operations. The name is a bit of a misnomer - because the Pylons wallet is designed largely around IPC-based use cases, resolving IPC messages was the original use case this system was designed for, but IpcLayer is a general-purpose message-handling system that provides callbacks throughout each part of a message's lifecycle. 

All wallet applications must include one class that extends IpcLayer, annotated w/ the IpcLayer.Implementation annotation. (Note that devdevwallet _does_ do this - it's just doing it by including the httpipc subproject.) If you're designing a wallet application that permits "unbound" operations - that does not require an IPC client - you should provide a value of 'true' for the permitUnboundOperations property in your IPCLayer's constructor. 

TODO: It may be preferable to have a system in place for having separate IpcLayer implementations for internal/external message queues; right now, we only have one instance that has to resolve all messages. Additionally, we should probably change this class's name, since it is not actually just an IPC layer.

**UILayer/UIHooks**

UILayer is the subsystem that allows walletcore to pause processing data in order to get input from a user before moving forward.

All wallet applications must include one class that extends UILayer, annotated w/ the UILayer.Implementation annotation. The UILayer class with this annotation will be automatically loaded and used by WalletCore at runtime.

UILayer works by adding and releasing UIHooks. (See tech.pylons.wallet.ipc.Message.UIHook.) Each message handled by the wallet creates a UIHook and runs your implementation's onAddUiHook method w/ its own UIHook as an argument. Most messages - those that do not require runtime user data input - will then immediately release their own UIHook, hitting your onReleaseUiHook method and your IpcLayer's onUiReleased method in that order. The message will then be resolved with the provided data, and any backend data transformation will take place.

In general, wallet applications should not accept user input while they have live (unreleased) UIHooks.
