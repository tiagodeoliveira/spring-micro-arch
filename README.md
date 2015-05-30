# spring-micro-arch
A small groovy multi-module application using gradle, springboot and springcloud.

The idea behind this project is to receive a REST json message on the receiver module, the expected message is:

```json
{ "callback" : "http://micro-arch-register-c9c4e156.tiagodeoliveira.svc.tutum.io:40082/register", 
"message" : "++++++++++[>+>+++>++++>+++++++>++++++++>+++++++++>++++++++++>+++++++++++>++++++++++++<<<<<<<<<-]>>>>+.>>>>+..<.<++++++++.>>>+.<<+.<<<<++++.<++.>>>+++++++.>>>.+++.<+++++++.--------.<<<<<+.<+++.---."}
```
The message holds a callback URL that will be called when the process is done, and a message on the [Brainfuck](http://en.wikipedia.org/wiki/Brainfuck) format. 
The receiver module will add a *start_time* tag on the message and post it on a RabbitMQ instance.

The processor module will be notified when a new message arrives on the specific queue and will translate the Brainfuck message. After that it will put on another queue with messages *to be delivered*.

The sender module will get the message and sent to the callback url from the original message.

The module register will receive the converted message (because its endpoint was specified on the first message call) and records it on a mongodb instance.

Provider module is a configuration server that provides the access to the configurations files of each module registered under [a git repository](https://github.com/tiagodeoliveira/spring-micro-arch-configurations).
