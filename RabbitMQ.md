# RabbitMQ

## 介绍

##### 概念

RabbitMQ 是一个消息中间件，作用是接受，存储，转发消息

##### 四大核心概念

生产者 

​	产生数据消息的程序是生产者

交换机 

​	接受生产者的消息，然后将消息推送到队列中

队列 

​	MQ使用的数据结构，存放消息

消费者

​	等待接收消息的程序

##### RabbitMQ核心部分

共六种模式

- HelloWorld
- Work queues
- Publish/Subscribe
- Routing
- Topics
- Publisher Confirms

##### 工作原理

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/RabbitMQ工作原理.png)

## 安装

##### 安装RabbitMQ

安装RabbitMQ需要先安装erlang语言环境，然后再安装RabbitMQ

##### 安装web插件

安装web界面插件时需要先关闭rabbitmq-server服务

安装web界面插件命令：rabbitmq-plugins enable rabbitmq_management

web访问地址：ip:15672

## 初始化

```shell
# 创建新用户
  rabbitmqctl add_user admin 123 
# 设置用户角色
  rabbitmqctl set_user_tags admin administrator
# 设置用户权限
  rabbitmqctl set_permissions [-p <vhostpath>] <user> <conf> <write> <read>
  rabbitmqctl set_permissions -p "/" admin ".*" ".*" ".*"
#查看所有的用户
  rabbitmqctl list_users
```

## RabbitMQ核心部分

### HelloWorld

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/RabbitMQ-HelloWorld.png)

在新创建的maven空项目中导入依赖

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.10.1</version>
            <configuration>
                <source>8</source>
                <target>8</target>
            </configuration>
        </plugin>
    </plugins>
</build>

<dependencies>
    <!--rabbitmq依赖客户端-->
    <dependency>
        <groupId>com.rabbitmq</groupId>
        <artifactId>amqp-client</artifactId>
        <version>5.8.0</version>
    </dependency>
    <dependency>
        <groupId>commons-io</groupId>
        <artifactId>commons-io</artifactId>
        <version>2.6</version>
    </dependency>
</dependencies>
```

```java
// 生产者的代码
// 创建连接需要打开linux服务器防火墙5672端口
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer {
    // 队列名
    public static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception{
        // 创建链接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        // 配置链接工厂
        connectionFactory.setHost("192.168.200.130");
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("123");
        // 创建生产者和RabbitMQ的链接
        Connection connection = null;
        try {
            connection = connectionFactory.newConnection();
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
        // 获取信道channel
        Channel channel = connection.createChannel();
        /*
        创建队列
        各参数的含义：
            1.队列名称
            2.队列中的消息是否进行持久化
            3.是否进行消息共享
            4.最后一个消费者断开链接后，是否自动删除该队列
            5.其他参数
         */
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        // 生产者发送消息通过链接和信道到RabbitMQ
        String message = "Hello World";
        /**
         * 发送一条消息
         *  参数含义：
         *      1.发送到哪一个交换机
         *      2.路由的key是哪个 本次是队列名称
         *      3.其他参数
         *      4.需要发送的消息的二进制
         */
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        System.out.println("消息发送完毕");
    }
}
```

```java
// 消费者代码
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Consume {
    public static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception {
        // 创建连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.200.130");
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("123");
        // 创建连接
        Connection connection = connectionFactory.newConnection();
        // 生成信道
        Channel channel = connection.createChannel();
        // 通过信道消费消息
        channel.basicConsume(QUEUE_NAME,
                (consumerTag, message) -> {
                    System.out.println(new String(message.getBody()));
                    System.out.println(consumerTag);
                },
                (consumerTag) -> {
                    System.out.println("消费被中断");
                }
        );
    }
}
```

### Work Queues

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/RabbitMQ-WorkQueues.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/工作队列模式原理图.png)

#### 轮次分发消息

work queues会轮训分发消息, 多个消费者是竞争关系, 轮次消费消息

```java
// 工具类代码
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMQUtils {

    public static Channel getChannel() throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.200.130");
        factory.setUsername("admin");
        factory.setPassword("123");
        Connection connection = factory.newConnection();
        return connection.createChannel();
    }
}
```



```java
// 生产者代码
public class Producer {

    public static final String QUEUE_NAME = "Work Queues";

    public static void main(String[] args) throws Exception{
        // 创建信道
        Channel channel = RabbitMQUtils.getChannel();

        // 创建队列
        channel.queueDeclare(QUEUE_NAME, false, false, false ,null);

        // 要发送的消息
        String[] strings = new String[7];
        for (int i = 0; i < strings.length; i++) {
            strings[i] = "测试消息: " + i;
        }

        // 发送消息
        for (String string : strings) {
            channel.basicPublish("", QUEUE_NAME, null, string.getBytes());
        }
    }
}
```

```java
// 消费者代码
// 编辑idea设置可以打开多个消费者进程,无需复制代码
import com.rabbitmq.client.Channel;
import com.syh.utils.RabbitMQUtils;

public class WorkerA {

    public static final String QUEUE_NAME = "Work Queues";

    public static void main(String[] args) throws Exception {
        // 通过工具类创建信道
        Channel channel = RabbitMQUtils.getChannel();

        System.out.println("WorkerA wait consume message ...");
		// System.out.println("WorkerB wait consume message ...");
        // 消费消息
        channel.basicConsume(QUEUE_NAME, true,
                (consumerTag, message) -> {
                    System.out.println("消费的消息为: " + new String(message.getBody()));
                },
                (consumerTag) -> {
                    System.out.println(consumerTag + "消费者取消消费接口回调逻辑");
                }
        );

    }
}

WorkerA wait consume message ...
消费的消息为: 测试消息: 1
消费的消息为: 测试消息: 3
消费的消息为: 测试消息: 5
    
WorkerB wait consume message ...
消费的消息为: 测试消息: 0
消费的消息为: 测试消息: 2
消费的消息为: 测试消息: 4
消费的消息为: 测试消息: 6
```

#### 消息应答

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/RabbitMQ-消息应答概念.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/自动应答.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/手动应答的方式.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/Multiple的解释.png)



![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/图解Multiple.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/消息重新入队.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/图解消息重新入队.png)

通过演示图解内容测试RabbitMQ的消息自动重新入队功能

```java
// 生产者代码
import com.rabbitmq.client.Channel;
import com.syh.utils.RabbitMQUtils;
import java.util.Scanner;

public class Producer {
    private static final String QUEUE_NAME = "customize_ack";

    public static void main(String[] args) throws Exception{
        // 创建信道
        Channel channel = RabbitMQUtils.getChannel();
        // 声明队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        // 根据控制台生成消息, 并发送到MQ
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.nextLine();
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println("消息发送完成: " + message);
        }

    }
}
```

```java
// 消费者代码
import com.rabbitmq.client.Channel;
import com.syh.utils.RabbitMQUtils;
import java.nio.charset.StandardCharsets;

public class Consumer {
    private static final String QUEUE_NAME = "customize_ack";

    public static void main(String[] args) throws Exception {
        // 创建信道
        Channel channel = RabbitMQUtils.getChannel();
        System.out.println("Consumer is waiting message ...");
        // 消费队列中的消息
        channel.basicConsume(QUEUE_NAME, false,
                (consumerTag, delivery) -> {
                    String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Consumer消费的消息: " + message);
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                },
                (consumerTag) -> {
                    System.out.println(consumerTag + "消费者取消消费接口回调逻辑");
                });
    }
}
// 消费者2代码
import com.rabbitmq.client.Channel;
import com.syh.utils.RabbitMQUtils;
import java.nio.charset.StandardCharsets;

public class Consumer2 {
    private static final String QUEUE_NAME = "customize_ack";

    public static void main(String[] args) throws Exception {
        // 创建信道
        Channel channel = RabbitMQUtils.getChannel();
        System.out.println("Consumer2 is waiting message ...");
        // 消费队列中的消息
        channel.basicConsume(QUEUE_NAME, false,
                (consumerTag, delivery) -> {
                    String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                    System.out.println("Consumer2消费的消息: " + message);
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                },
                (consumerTag) -> {
                    System.out.println(consumerTag + "消费者取消消费接口回调逻辑");
                });
    }
}
```

#### RabbitMQ持久化

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/持久化.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/如何实现持久化.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/如何持久化2.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/消息实现持久化.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/不公平分发.png)

在消费者代码中修改信道的channel的basicQos属性值为1

这个属性的含义就是: 我还没干完上一个, 你这个消息别给我

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/不公平分发2.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/不公平分发3.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/预取值.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/图解预取值.png)

该值定义通道上允许的未确认消息的最大数量

```java
// 生产者代码
import com.rabbitmq.client.Channel;
import com.syh.utils.RabbitMQUtils;
import java.util.Scanner;

public class Producer {
    private static final String QUEUE_NAME = "customize_ack";

    public static void main(String[] args) throws Exception{
        // 创建信道
        Channel channel = RabbitMQUtils.getChannel();
        // 声明队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        // 根据控制台生成消息, 并发送到MQ
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.nextLine();
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println("消息发送完成: " + message);
        }

    }
}
```

```java
// 消费者1代码
import com.rabbitmq.client.Channel;
import com.syh.utils.RabbitMQUtils;
import java.nio.charset.StandardCharsets;

public class Consumer {
    private static final String QUEUE_NAME = "customize_ack";

    public static void main(String[] args) throws Exception {
        // 创建信道
        Channel channel = RabbitMQUtils.getChannel();
        channel.basicQos(2); // 设置信道预取值
        System.out.println("Consumer is waiting message ...");
        // 消费队列中的消息
        channel.basicConsume(QUEUE_NAME, false,
                (consumerTag, delivery) -> {
                    String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Consumer消费的消息: " + message);
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                },
                (consumerTag) -> {
                    System.out.println(consumerTag + "消费者取消消费接口回调逻辑");
                });
    }
}

// 消费者2代码
import com.rabbitmq.client.Channel;
import com.syh.utils.RabbitMQUtils;
import java.nio.charset.StandardCharsets;

public class Consumer2 {
    private static final String QUEUE_NAME = "customize_ack";

    public static void main(String[] args) throws Exception {
        // 创建信道
        Channel channel = RabbitMQUtils.getChannel();
        channel.basicQos(5); // 设置信道预取值
        System.out.println("Consumer2 is waiting message ...");
        // 消费队列中的消息
        channel.basicConsume(QUEUE_NAME, false,
                (consumerTag, delivery) -> {
                    String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                    System.out.println("Consumer2消费的消息: " + message);
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                },
                (consumerTag) -> {
                    System.out.println(consumerTag + "消费者取消消费接口回调逻辑");
                });
    }
}
```



### 发布确认

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/发布确认.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/图解发布确认.png)

#### 发布确认的策略

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/发布确认的策略1.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/单个确认发布.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/批量发布确认.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/异步发布确认.png)

```java
// 发布确认代码
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import com.syh.utils.RabbitMQUtils;

public class Producer {

    private static final int MESSAGE_COUNT = 1000;

    public static void main(String[] args) throws Exception {
        long begin = System.currentTimeMillis();
        // 单个确认 发送1000条消息, 耗时: 1447ms
//        publishSingleConfirm();
        // 批量确认 发送1000条消息, 耗时: 204ms
//        publishBatchConfirm();
        // 异步确认 发送1000条消息, 耗时: 241ms
        publishAsyncConfirm();
        long end = System.currentTimeMillis();
        System.out.println("发送1000条消息, 耗时: " + (end - begin) + "ms");
    }
	// 单个确认
    private static void publishSingleConfirm() throws Exception {
        String queue_name = "publish_confirm_single";
        // 生成信道
        Channel channel = RabbitMQUtils.getChannel();
        // 开启发布确认
        channel.confirmSelect();
        // 声明队列, 并将队列设置为持久化
        channel.queueDeclare(queue_name, true, false, false, null);
        // 发送1000条数据
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = "message: " + i;
            channel.basicPublish("", queue_name, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
            boolean flag = channel.waitForConfirms();
            if (flag) {
                System.out.println(message + " 发送成功");
            }
        }
        System.out.println("消息发送完毕");
    }
	// 批量确认
    private static void publishBatchConfirm() throws Exception {
        String queue_name = "publish_confirm_batch";
        // 获取信道
        Channel channel = RabbitMQUtils.getChannel();
        // 开启发布确认
        channel.confirmSelect();
        // 声明队列, 并将队列设置为持久化
        channel.queueDeclare(queue_name, true, false, false, null);
        // 发送1000条消息, 每100条消息确认一次
        for (int i = 1; i <= MESSAGE_COUNT; i++) {
            String message = "message: " + i;
            channel.basicPublish("", queue_name, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
            if (i % 100 == 0) {
                System.out.println("100条消息发送成功, 当前发送的消息为: " + message);
            }
        }
        System.out.println("消息发送完毕");
    }
	// 异步确认
    private static void publishAsyncConfirm() throws Exception {
        // 队列名
        String queue_name = "publish_confirm_async";
        // 生成信道
        Channel channel = RabbitMQUtils.getChannel();
        // 开启发布确认模式
        channel.confirmSelect();
        // 声明队列, 并启用持久化
        channel.queueDeclare(queue_name, true, false, false, null);
        // 编写监听器, 监听MQ对消息的处理结果
        channel.addConfirmListener(
                ((deliveryTag, multiple) -> {
                    System.out.println("确认的消息: " + deliveryTag);
                }),
                ((deliveryTag, multiple) -> {
                    System.out.println("未确认的消息: " + deliveryTag);
                })
        );
        // 发送1000条消息
        for (int i = 1; i <= MESSAGE_COUNT; i++) {
            String message = "message: " + i;
            channel.basicPublish("", queue_name, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
        }
        System.out.println("消息发送完毕");
    }
}
```

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/处理异步未确认消息.png)

```java
// 代码
// 问题: 异步确认的时间并没有减少, 成功确认回调函数也出现问题
```

### 交换机 / 发布订阅模式

#### 概念

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/交换机.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/图解发布订阅模式.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/交换机概念.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/交换机类型.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/创建临时队列.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/临时队列2.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/临时队列.png)

#### 绑定

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/绑定.png)



![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/绑定2.png)

#### Fanout

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/fanout.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/fanout实战.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/实战binding.png)

```java
// 生产者代码
import com.rabbitmq.client.Channel;
import com.syh.utils.RabbitMQUtils;
import java.util.Scanner;

public class Producer {

    private static final String EXCHANGE_NAME = "Logs";
    private static final String QUEUE_NAME = "tom";
    private static final String QUEUE_NAME2 = "jerry";

    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMQUtils.getChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel.queueDeclare(QUEUE_NAME2, false, false, false, null);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");
        channel.queueBind(QUEUE_NAME2, EXCHANGE_NAME, "");
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.nextLine();
            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
            System.out.println("发送消息: " + message);
        }
    }
}
```

```java
// 消费者1代码
import com.rabbitmq.client.Channel;
import com.syh.utils.RabbitMQUtils;

public class ReceiveLogs01 {

    private static final String EXCHANGE_NAME = "Logs";
    private static final String QUEUE_NAME = "tom";
    private static final String QUEUE_NAME2 = "jerry";

    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMQUtils.getChannel();
        channel.basicConsume(QUEUE_NAME, true,
                (consumerTag, message) -> {
                    System.out.println(new String(message.getBody()));
                    System.out.println(consumerTag);
                },
                (consumerTag) -> {
                    System.out.println("消费被中断");
                }
        );
    }
}

// 消费者2代码
import com.rabbitmq.client.Channel;
import com.syh.utils.RabbitMQUtils;

public class ReceiveLogs02 {

    private static final String EXCHANGE_NAME = "Logs";
    private static final String QUEUE_NAME = "tom";
    private static final String QUEUE_NAME2 = "jerry";

    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMQUtils.getChannel();
        channel.basicConsume(QUEUE_NAME2, true,
                (consumerTag, message) -> {
                    System.out.println(new String(message.getBody()));
                    System.out.println(consumerTag);
                },
                (consumerTag) -> {
                    System.out.println("消费被中断");
                }
        );
    }
}
```

#### Direct Exchange

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/DirectExchange.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/直接交换机介绍.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/直接交换机介绍2.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/多重绑定.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/直接交换机实战.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/直接交换机实战2.png)

#### Topics

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/Topic.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/Topics的要求.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/Topics的要求2.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/注意.png)

```java
// Topics交换机实例生产者代码
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.syh.utils.RabbitMQUtils;
import java.util.Scanner;

public class Producer {
    private static final String EXCHANGE_NAME = "x";
    private static final String QUEUE_NAME = "q1";
    private static final String QUEUE2_NAME = "q2";

    public static void main(String[] args) throws Exception {
        // 获取信道
        Channel channel = RabbitMQUtils.getChannel();
        // 声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        // 声明队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel.queueDeclare(QUEUE2_NAME, false, false, false, null);
        // 绑定队列
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "*.orange.*");
        channel.queueBind(QUEUE2_NAME, EXCHANGE_NAME, "*.*.rabbit");
        channel.queueBind(QUEUE2_NAME, EXCHANGE_NAME, "lazy.#");
        // 控制台输入routingKey,发送消息
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String routingKey = scanner.nextLine();
            String message = "Hello World";
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes());
        }
    }
}

// 消费者代码
import com.rabbitmq.client.Channel;
import com.syh.utils.RabbitMQUtils;

public class Q1 {
    private static final String QUEUE_NAME = "q1";

    public static void main(String[] args) throws Exception {
        // 生成信道
        Channel channel = RabbitMQUtils.getChannel();
        // 接受消息
        channel.basicConsume(QUEUE_NAME, true,
                (consumerTag, message) -> {
                    System.out.println("Q1 消费的消息: " + new String(message.getBody()));
                },
                (consumerTag) -> {

                }
        );

    }
}

```

### 死信队列

#### 死信的概念

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/死信的概念.png)

#### 死信的来源

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/死信的来源.png)

#### 死信实战

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/死信实战.png) 	

```java
// 死信队列演示 设置消息过期时间为10s,产生死信
// 生产者代码
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.syh.utils.RabbitMQUtils;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Producer {
    private static final String NORMAL_EXCHANGE = "normal_exchange";
    private static final String DEAD_EXCHANGE = "dead_exchange";
    private static final String NORMAL_QUEUE = "normal_queue";
    private static final String DEAD_QUEUE = "dead_queue";

    public static void main(String[] args) throws Exception {
        // 获取信道
        Channel channel = RabbitMQUtils.getChannel();
        // 声明交换机
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);
        // 声明队列
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", DEAD_EXCHANGE);
        arguments.put("x-dead-letter-routing-key", "dead");
        channel.queueDeclare(NORMAL_QUEUE, false, false, false, arguments);
        channel.queueDeclare(DEAD_QUEUE, false, false, false, null);
        // 绑定队列
        channel.queueBind(NORMAL_QUEUE, NORMAL_EXCHANGE, "normal");
        channel.queueBind(DEAD_QUEUE, DEAD_EXCHANGE, "dead");
        // 发送消息
        AMQP.BasicProperties props = new AMQP.BasicProperties().builder().expiration("10000").build();
        for (int i = 1; i < 11; i++) {
            String message = "message: " + i;
            channel.basicPublish(NORMAL_EXCHANGE, "normal", props, message.getBytes(StandardCharsets.UTF_8));
        }
    }
}
```

```java
// 消费者代码
import com.rabbitmq.client.Channel;
import com.syh.utils.RabbitMQUtils;

public class DConsumer {
    private static final String DEAD_QUEUE = "dead_queue";

    public static void main(String[] args) throws Exception {
        // 获取信道
        Channel channel = RabbitMQUtils.getChannel();
        // 接受消息
        channel.basicConsume(DEAD_QUEUE, true,
                ((consumerTag, message) -> {
                    System.out.println("死信队列消费的消息: " + new String(message.getBody()));
                }),
                (consumerTag -> {

                })
        );
    }
}
```

```java
// 死信队列演示 设置队列最大长度,超过最大长度,产生死信
// 生产者代码
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.syh.utils.RabbitMQUtils;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Producer {
    private static final String NORMAL_EXCHANGE = "normal_exchange";
    private static final String DEAD_EXCHANGE = "dead_exchange";
    private static final String NORMAL_QUEUE = "normal_queue";
    private static final String DEAD_QUEUE = "dead_queue";

    public static void main(String[] args) throws Exception {
        // 获取信道
        Channel channel = RabbitMQUtils.getChannel();
        // 声明交换机
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);
        // 声明队列
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", DEAD_EXCHANGE);
        arguments.put("x-dead-letter-routing-key", "dead");
        arguments.put("x-max-length", 6); // 设置最大长度
        channel.queueDeclare(NORMAL_QUEUE, false, false, false, arguments);
        channel.queueDeclare(DEAD_QUEUE, false, false, false, null);
        // 绑定队列
        channel.queueBind(NORMAL_QUEUE, NORMAL_EXCHANGE, "normal");
        channel.queueBind(DEAD_QUEUE, DEAD_EXCHANGE, "dead");
        // 设置过期时间
        AMQP.BasicProperties props = new AMQP.BasicProperties().builder().expiration("10000").build();
        // 发送消息
        for (int i = 1; i < 11; i++) {
            String message = "message: " + i;
            channel.basicPublish(NORMAL_EXCHANGE, "normal", null, message.getBytes(StandardCharsets.UTF_8));
        }
    }
}
```

```java
// 死信队列演示 消费者拒绝消费消息,并设置requeue为false,产生死信
// 生产者代码
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.syh.utils.RabbitMQUtils;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Producer {
    private static final String NORMAL_EXCHANGE = "normal_exchange";
    private static final String DEAD_EXCHANGE = "dead_exchange";
    private static final String NORMAL_QUEUE = "normal_queue";
    private static final String DEAD_QUEUE = "dead_queue";

    public static void main(String[] args) throws Exception {
        // 获取信道
        Channel channel = RabbitMQUtils.getChannel();
        // 声明交换机
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);
        // 声明队列
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", DEAD_EXCHANGE);
        arguments.put("x-dead-letter-routing-key", "dead");
//        arguments.put("x-max-length", 6);
        channel.queueDeclare(NORMAL_QUEUE, false, false, false, arguments);
        channel.queueDeclare(DEAD_QUEUE, false, false, false, null);
        // 绑定队列
        channel.queueBind(NORMAL_QUEUE, NORMAL_EXCHANGE, "normal");
        channel.queueBind(DEAD_QUEUE, DEAD_EXCHANGE, "dead");
        // 设置过期时间
        AMQP.BasicProperties props = new AMQP.BasicProperties().builder().expiration("10000").build();
        // 发送消息
        for (int i = 1; i < 11; i++) {
            String message = "message: " + i;
            channel.basicPublish(NORMAL_EXCHANGE, "normal", null, message.getBytes(StandardCharsets.UTF_8));
        }
    }
}
// 消费者代码
import com.rabbitmq.client.Channel;
import com.syh.utils.RabbitMQUtils;

public class NConsumer {
    private static final String NORMAL_QUEUE = "normal_queue";

    public static void main(String[] args) throws Exception {
        // 获取信道
        Channel channel = RabbitMQUtils.getChannel();
        // 接受消息
        channel.basicConsume(NORMAL_QUEUE, false,
                ((consumerTag, message) -> {
                    String msg = new String(message.getBody());
                    if (msg.equals("message: 5")){
                        System.out.println("正常队列拒绝的消息: " + msg);
                        channel.basicReject(message.getEnvelope().getDeliveryTag(), false);
                    } else {
                        System.out.println("正常队列消费的消息: " + msg);
                    }
                }),
                (consumerTag -> {

                })
        );
    }
}
```

### 延迟队列

#### 概念

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/延迟队列概念.png)

#### 使用场景

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/延迟队列使用场景.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/场景特点.png)

#### 项目流程图

下图显示不全的地方

1. 购票订单数据库表
2. 更新订单状态为已失效,表示已经超过30分钟了仍未付款	

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/项目流程.png)

#### RabbitMQ中的TTL

```JAVA
// 队列设置过期时间TTL
@Bean("queueA")
public Queue queueA(){
    Map<String, Object> arguments = new HashMap<>();
    arguments.put("x-dead-letter-exchange", yExchange);
    arguments.put("x-dead-letter-routing-key", "YD");
    arguments.put("x-message-ttl",10000); // 设置过期时间为 10000ms
    return QueueBuilder.durable(queueA).withArguments(arguments).build();
}
// 消息设置过期时间TTL
@RequestMapping("/sendMessageWithTtl/{message}/{ttl}")
public String sendMessage(@PathVariable String message, @PathVariable String ttl){
    log.info("当前时间: {}, 发送一条延时{}ms的消息: {} 给ttl队列", new Date(), ttl, message);
    rabbitTemplate.convertAndSend("X", "XC", message, msg -> {
        msg.getMessageProperties().setExpiration(ttl);
        return msg;
    });
    return "发送成功";
}
```



#### 整合SpringBoot

```xml
<!--pom.xml文件-->
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
         <version>2.3.11.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.syh</groupId>
    <artifactId>RabbitMQ-Test</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>RabbitMQ-Test</name>
    <description>RabbitMQ-Test</description>
    <properties>
        <java.version>1.8</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.amqp</groupId>
            <artifactId>spring-rabbit-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.47</version>
        </dependency>
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger2</artifactId>
            <version>2.9.2</version>
        </dependency>
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger-ui</artifactId>
            <version>2.9.2</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>2.7.4</version>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>
```

```java
// 配置Swagger
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket webApiConfig() {
        return new Docket(DocumentationType.SWAGGER_2) // 1.SWAGGER_2
                .groupName("webApi")
                .apiInfo(webApiInfo())
                .select()
                .build();
    }
    private ApiInfo webApiInfo() {
        return new ApiInfoBuilder()
                .title("rabbitMQ接口文档")
                .description("本文档描述了RabbitMQ微服务接口定义")
                .version("1.0")
                .contact(new Contact("Tom", "https://nidihanwang.com", "10086123@qq.com"))
                .build();
    }
}
```

```properties
#修改配置文件
spring.rabbitmq.host=192.168.200.130
spring.rabbitmq.username=admin
spring.rabbitmq.password=123
```



#### 队列TTL

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/队列TTL.png)

```JAVA
// IDEA报错之Failed to start bean ‘documentationPluginsBootstrapper‘问题及解决方案

原因：这是因为Springfox使用的路径匹配是基于AntPathMatcher的，而Spring Boot 2.6.X使用的是PathPatternMatcher。
解决：在application.properties里配置：spring.mvc.pathmatch.matching-strategy=ANT_PATH_MATCHER
```

```JAVA
// RabbitMQ 配置类
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {
    private static final String xExchange = "X";
    private static final String yExchange = "Y";
    private static final String queueA = "A";
    private static final String queueB = "B";
    private static final String queueD = "D";

    @Bean("xExchange")
    public DirectExchange xExchange(){
        return new DirectExchange(xExchange);
    }
    @Bean("yExchange")
    public DirectExchange yExchange(){
        return new DirectExchange(yExchange);
    }
    @Bean("queueA")
    public Queue queueA(){
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", yExchange);
        arguments.put("x-dead-letter-routing-key", "YD");
        arguments.put("x-message-ttl",10000);
        return QueueBuilder.durable(queueA).withArguments(arguments).build();
//        return QueueBuilder.nonDurable(queueA).withArguments(arguments).build();
    }
    @Bean("queueB")
    public Queue queueB(){
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", yExchange);
        arguments.put("x-dead-letter-routing-key", "YD");
        arguments.put("x-message-ttl",40000);
        return QueueBuilder.durable(queueB).withArguments(arguments).build();
    }
    @Bean("queueD")
    public Queue queueD(){
        return QueueBuilder.durable(queueD).build();
    }
    @Bean
    public Binding xBindQueueA(@Qualifier("queueA") Queue queue, @Qualifier("xExchange") DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("XA");
    }
    @Bean
    public Binding xBindQueueB(@Qualifier("queueB") Queue queue, @Qualifier("xExchange") DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("XB");
    }
    @Bean
    public Binding yBindQueueD(@Qualifier("queueD") Queue queue, @Qualifier("yExchange") DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("YD");
    }
}
// 生产者
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/ttl")
public class Producer {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RequestMapping("/sendMessage/{message}")
    public String sendMessage(@PathVariable String message){
        rabbitTemplate.convertAndSend("X", "XA", message.getBytes());
        rabbitTemplate.convertAndSend("X", "XB", message.getBytes());
        log.info("当前时间: {}, 发送一条消息: {} 给两个ttl队列", new Date(), message);
        return "发送成功";
    }
}
// 消费者
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
@Slf4j
public class Consumer {
    @RabbitListener(queues = "D")
    public void Consume(Message message, Channel channel) throws Exception{
        String msg = new String(message.getBody());
        log.info("当前时间: {}, 收到死信队列的消息: {}", new Date(), msg);
    }
}
```

#### 延时队列优化

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/队列ttl缺点.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/延时队列优化代码架构图.png)

```java
// 配置类
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class QueueConfig {
    private static final String xExchange = "X";
    private static final String yExchange = "Y";
    private static final String queueC = "C";

    @Bean("queueC")
    public Queue queueC(){
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", yExchange);
        arguments.put("x-dead-letter-routing-key", "YD");
        return QueueBuilder.nonDurable(queueC).withArguments(arguments).build();
    }

    @Bean
    public Binding XBindQueueC(@Qualifier("queueC") Queue queue, @Qualifier("xExchange") DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("XC");
    }
}
// 生产者代码
@RequestMapping("/sendMessageWithTtl/{message}/{ttl}")
public String sendMessage(@PathVariable String message, @PathVariable String ttl){
    log.info("当前时间: {}, 发送一条延时{}ms的消息: {} 给ttl队列", new Date(), ttl, message);
    rabbitTemplate.convertAndSend("X", "XC", message, msg -> {
        msg.getMessageProperties().setExpiration(ttl);
        return msg;
    });
    return "发送成功";
}
```

#### 基于死信队列实现的TTL队列的缺陷

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/基于死信队列实现TTL队列的缺陷.png)

#### RabbitMQ插件实现延迟队列

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/RabbitMQ.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/安装插件.png)

插件下载地址:

- https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/releases/tag/v3.8.0

插件安装完成后如图示

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/x-delayed-message.png)
插件实现延迟队列原理图

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/插件实现延迟队列原理图.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/插件实现延迟队列代码架构图.png)

```java
// 配置类代码
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DelayQueueConfig {
    public static final String EXCHANGE_NAME = "delayed.exchange";
    public static final String QUEUE_NAME = "delayed.queue";
    public static final String ROUTING_KEY = "delayed.routingkey";
    @Bean
    public CustomExchange delayedExchange() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-delayed-type", "direct");
        return new CustomExchange(EXCHANGE_NAME, "x-delayed-message", false, true, arguments);
    }
    @Bean
    public Queue delayedQueue() {
        return QueueBuilder.nonDurable(QUEUE_NAME).build();
    }
    @Bean
    public Binding delayedBinding(
            @Qualifier("delayedExchange") CustomExchange exchange,
            @Qualifier("delayedQueue") Queue queue) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY).noargs();
    }
}
// 生产者代码
@RequestMapping("/sendDelayedMessage/{message}/{delayedTime}")
public String sendDelayedMessage(@PathVariable String message, @PathVariable Integer delayedTime){
    log.info("当前时间: {}, 给延迟队列发送一条延迟{}ms的消息: {}", new Date(), delayedTime, message);
    rabbitTemplate.convertAndSend(DelayQueueConfig.EXCHANGE_NAME, DelayQueueConfig.ROUTING_KEY, message, msg -> {
        msg.getMessageProperties().setDelay(delayedTime);
        return msg;
    });
    return "发送成功";
}
// 消费者代码
import com.rabbitmq.client.Channel;
import com.syh.rabbitmqtest.config.DelayQueueConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
@Slf4j
public class Consumer {
    @RabbitListener(queues = DelayQueueConfig.QUEUE_NAME)
    public void Consume(Message message, Channel channel) throws Exception{
        String msg = new String(message.getBody());
        log.info("当前时间: {}, 收到死信队列的消息: {}", new Date(), msg);
    }
}
```

#### 延迟队列总结

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/延迟队列总结.png)

### 发布确认高级

#### 问题

- 交换机收不到消息,如何让生产者知道
- 交换机通过routingkey将消息路由给队列时,队列没收到消息,如何让生产者知道

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/发布确认高级-问题.png)

#### 演示-springboot版本演示

解决 "交换机收不到消息,如何让生产者知道" 的问题

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/发布确认机制.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/发布确认高级-代码架构图.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/发布确认高级-修改配置文件.png)

```java
// 演示交换机收不到消息的情况
// 接口
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

@Component
@Slf4j
public class PublishConfirmCallBack implements RabbitTemplate.ConfirmCallback{
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @PostConstruct
    public void init() {
        // 将接口实现类注入到rabbitTemplate中
        rabbitTemplate.setConfirmCallback(this::confirm);
        rabbitTemplate.setReturnCallback(this::returnedMessage);
    }
    /**
     * 交换机确认回调方法 无论交换机成功收到消息还是没收到消息,都会调用该方法
     * @param correlationData 保存回调消息的ID及相关信息
     * @param ack 判断交换机是否收到消息,收到消息为true,没收到为false
     * @param reason 交换机没收到消息的原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String reason) {
        String id = correlationData == null ? "" : correlationData.getId();
        if (ack){
            log.info("交换机收到id为{}的信息", id);
        } else {
            log.info("交换机没有收到id为{}的信息,原因是:{}", id, reason);
        }
    }
}
// 配置类
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfirmConfig {
    public static final String EXCHANGE_NAME = "confirm.exchange";
    public static final String QUEUE_NAME = "confirm.queue";
    public static final String ROUTING_KEY = "key1";
    @Bean
    public DirectExchange confirmExchange(){
        return new DirectExchange(EXCHANGE_NAME, false, true);
    }
    @Bean
    public Queue confirmQueue(){
        return QueueBuilder.nonDurable(QUEUE_NAME).build();
    }
    @Bean
    public Binding confirmExchangeBindQueue(@Qualifier("confirmExchange") DirectExchange exchange, @Qualifier("confirmQueue") Queue queue){
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }
}
// 生产者代码
@RequestMapping("/sendConfirmMessage/{message}")
public String sendConfirmMessage(@PathVariable String message){
    CorrelationData correlationData = new CorrelationData("1");
    log.info("当前时间: {}, 给confirm.queue队列发送一条消息: {}", new Date(), message);
    // 通过修改交换机名来演示交换机无法收到消息的情况
    rabbitTemplate.convertAndSend("confirm.exchange1", ConfirmConfig.ROUTING_KEY, message, correlationData);
    return "发送成功";
}
// 消费者代码
import com.rabbitmq.client.Channel;
import com.syh.rabbitmqtest.config.ConfirmConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
@Slf4j
public class Consumer {
    @RabbitListener(queues = ConfirmConfig.QUEUE_NAME)
    public void Consume(Message message, Channel channel) throws Exception{
        String msg = new String(message.getBody());
        log.info("当前时间: {}, 收到confirm.queue队列的消息: {}", new Date(), msg);
    }
}
```

#### 回退消息

解决 "交换机通过routingkey将消息路由给队列时,队列没收到消息,如何让生产者知道" 的问题

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/回退消息.png)

```properties
spring.rabbitmq.host=192.168.200.130
spring.rabbitmq.port=5672
spring.rabbitmq.username=admin
spring.rabbitmq.password=123
spring.rabbitmq.publisher-confirm-type=correlated
spring.rabbitmq.publisher-returns=true
```

```java
// 配置类
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfirmConfig {
    public static final String EXCHANGE_NAME = "confirm.exchange";
    public static final String QUEUE_NAME = "confirm.queue";
    public static final String ROUTING_KEY = "key1";
    @Bean
    public DirectExchange confirmExchange(){
        return new DirectExchange(EXCHANGE_NAME, false, true);
    }
    @Bean
    public Queue confirmQueue(){
        return QueueBuilder.nonDurable(QUEUE_NAME).build();
    }
    @Bean
    public Binding confirmExchangeBindQueue(@Qualifier("confirmExchange") DirectExchange exchange, @Qualifier("confirmQueue") Queue queue){
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }
}
// 回调接口
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

@Component
@Slf4j
public class PublishConfirmCallBack implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback{
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @PostConstruct
    public void init() {
        // 将接口实现类注入到rabbitTemplate中
        rabbitTemplate.setConfirmCallback(this::confirm);
        rabbitTemplate.setReturnCallback(this::returnedMessage);
    }
    /**
     * 交换机确认回调方法 无论交换机成功收到消息还是没收到消息,都会调用该方法
     * @param correlationData 保存回调消息的ID及相关信息
     * @param ack 判断交换机是否收到消息,收到消息为true,没收到为false
     * @param reason 交换机没收到消息的原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String reason) {
        String id = correlationData == null ? "" : correlationData.getId();
        if (ack){
            log.info("交换机收到id为{}的信息", id);
        } else {
            log.info("交换机没有收到id为{}的信息,原因是:{}", id, reason);
        }
    }
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.error("由交换机{}通过routingKey{}路由的消息{}由于{}没有发送成功", exchange, routingKey, new String(message.getBody()), replyText);
    }
}
// 生产者
@RequestMapping("/sendConfirmMessage/{message}")
public String sendConfirmMessage(@PathVariable String message){
    CorrelationData correlationData = new CorrelationData("1");
    log.info("给confirm.queue队列发送一条消息: {}", message);
    rabbitTemplate.convertAndSend("confirm.exchange", "key12", message, correlationData);
    return "发送成功";
}
// 消费者
import com.rabbitmq.client.Channel;
import com.syh.rabbitmqtest.config.ConfirmConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
@Component
@Slf4j
public class Consumer {
    @RabbitListener(queues = ConfirmConfig.QUEUE_NAME)
    public void Consume(Message message, Channel channel) throws Exception{
        String msg = new String(message.getBody());
        log.info("消费confirm.queue队列的消息: {}", msg);
    }
}
```

#### 备份交换机

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/备份交换机.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/备份交换机-代码架构图.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/备份交换机-结果分析.png)

```java
// 配置类
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfirmConfig {
    public static final String EXCHANGE_NAME = "confirm.exchange";
    public static final String QUEUE_NAME = "confirm.queue";
    public static final String ROUTING_KEY = "key1";
    // 备份交换机
    public static final String BACKUP_EXCHANGE_NAME = "backup.exchange";
    // 备份队列
    public static final String BACKUP_QUEUE_NAME = "backup.queue";
    // 警告队列
    public static final String WARNING_QUEUE_NAME = "warning_queue";
    @Bean
    public DirectExchange confirmExchange(){
        return ExchangeBuilder.directExchange(EXCHANGE_NAME).withArgument("alternate-exchange", BACKUP_EXCHANGE_NAME).build();
    }
    @Bean
    public FanoutExchange backupExchange(){
        return new FanoutExchange(BACKUP_EXCHANGE_NAME);
    }
    @Bean
    public Queue confirmQueue(){
        return QueueBuilder.nonDurable(QUEUE_NAME).build();
    }
    @Bean
    public Queue backupQueue() {
        return QueueBuilder.durable(BACKUP_QUEUE_NAME).build();
    }
    @Bean
    public Queue warningQueue() {
        return QueueBuilder.durable(WARNING_QUEUE_NAME).build();
    }
    @Bean
    public Binding confirmExchangeBindQueue(@Qualifier("confirmExchange") DirectExchange exchange, @Qualifier("confirmQueue") Queue queue){
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }
    @Bean
    public Binding backupExchangeBindingBackupQueue(@Qualifier("backupExchange") FanoutExchange exchange, @Qualifier("backupQueue") Queue queue){
        return BindingBuilder.bind(queue).to(exchange);
    }
    @Bean
    public Binding backupExchangeBindingWarningQueue(@Qualifier("backupExchange") FanoutExchange exchange, @Qualifier("warningQueue") Queue queue){
        return BindingBuilder.bind(queue).to(exchange);
    }
}
// 生产者
import com.syh.rabbitmqtest.config.DelayQueueConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/ttl")
public class Producer {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RequestMapping("/sendMessage/{message}")
    public String sendMessage(@PathVariable String message){
        rabbitTemplate.convertAndSend("X", "XA", message.getBytes());
        rabbitTemplate.convertAndSend("X", "XB", message.getBytes());
        log.info("当前时间: {}, 发送一条消息: {} 给两个ttl队列", new Date(), message);
        return "发送成功";
    }

    @RequestMapping("/sendMessageWithTtl/{message}/{ttl}")
    public String sendMessage(@PathVariable String message, @PathVariable String ttl){
        log.info("当前时间: {}, 发送一条延时{}ms的消息: {} 给ttl队列", new Date(), ttl, message);
        rabbitTemplate.convertAndSend("X", "XC", message, msg -> {
            msg.getMessageProperties().setExpiration(ttl);
            return msg;
        });
        return "发送成功";
    }

    @RequestMapping("/sendDelayedMessage/{message}/{delayedTime}")
    public String sendDelayedMessage(@PathVariable String message, @PathVariable Integer delayedTime){
        log.info("当前时间: {}, 给延迟队列发送一条延迟{}ms的消息: {}", new Date(), delayedTime, message);
        rabbitTemplate.convertAndSend(DelayQueueConfig.EXCHANGE_NAME, DelayQueueConfig.ROUTING_KEY, message, msg -> {
            msg.getMessageProperties().setDelay(delayedTime);
            return msg;
        });
        return "发送成功";
    }

    @RequestMapping("/sendConfirmMessage/{message}")
    public String sendConfirmMessage(@PathVariable String message){
        CorrelationData correlationData = new CorrelationData("1");
        log.info("给confirm.queue队列发送一条消息: {}", message);
        rabbitTemplate.convertAndSend("confirm.exchange", "key12", message, correlationData);
        return "发送成功";
    }
}
// 消费者
import com.rabbitmq.client.Channel;
import com.syh.rabbitmqtest.config.ConfirmConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
@Component
@Slf4j
public class Consumer {
    @RabbitListener(queues = ConfirmConfig.QUEUE_NAME)
    public void Consume(Message message, Channel channel) throws Exception{
        String msg = new String(message.getBody());
        log.info("消费confirm.queue队列的消息: {}", msg);
    }
    @RabbitListener(queues = ConfirmConfig.WARNING_QUEUE_NAME)
    public void receive(Message message) {
        log.error("发现无法路由的消息: {}", new String(message.getBody()));
    }
}
```

## RabbitMQ 其他知识点

### 幂等性

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/幂等性.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/解决幂等性问题.png)

### 优先级队列

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/优先级队列.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/优先级队列原理图.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/控制台添加优先级队列.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/代码添加优先级队列.png)

```java
// 代码添加优先队列
// 生产者
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.syh.utils.RabbitMQUtils;
import java.util.HashMap;
import java.util.Map;

public class Producer {
    private static final String QUEUE_NAME = "priority.queue";

    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMQUtils.getChannel();
        Map<String, Object> arguments = new HashMap<>();
        // 优先队列的值最大设为10, 将值设置成比较小的数可以节省对cpu和内存的使用
        arguments.put("x-max-priority", 10);
        channel.queueDeclare(QUEUE_NAME, true, false, false, arguments);
        // 发送10条消息
        for (int i = 1; i < 11; i++) {
            String message = "info" + i;
            if (message.equals("info5")) {
                AMQP.BasicProperties props = new AMQP.BasicProperties().builder().priority(5).build();
                channel.basicPublish("", QUEUE_NAME, props, message.getBytes());
            } else {
                channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            }
        }
    }
}
// 消费者
import com.rabbitmq.client.Channel;
import com.syh.utils.RabbitMQUtils;

public class Consumer {
    private static final String QUEUE_NAME = "priority.queue";

    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMQUtils.getChannel();
        channel.basicConsume(QUEUE_NAME, true,
                ((consumerTag, message) -> {
                    System.out.println(new String(message.getBody()));
                }),
                (consumerTag -> {

                })
        );
    }
}
```

### 惰性队列

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/惰性队列.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/惰性队列图解.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/惰性队列两种模式.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/内存开销对比.png)

## RabbitMQ 集群

### clustering

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/使用集群的原因.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/集群-图示.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/搭建步骤-1,2.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/搭建步骤-3,4.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/搭建步骤-5.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/搭建步骤-6.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/搭建步骤-7.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/搭建步骤-8.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/搭建步骤-9.png)

### 镜像队列

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/使用镜像队列的原因.png)

**搭建步骤**

1. 启动三台集群节点
2. 随便找一个节点添加policy
3. 如下图

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/创建镜像队列的步骤.png)

4. 就算整个集群只剩下一台机器了,依然能消费队列里的消息, 说明队列里的消息被镜像队列传递到相应机器里了
5. 演示,下图

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/镜像队列.png)

然后关闭node_2

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/镜像队列-演示2.png)

镜像队列将备份到node_3上

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/镜像队列-演示3.png)

### Haproxy+Keepalive实现高可用负载均衡

**架构图**

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/高可用负载均衡架构图.png)

### Federation Exchange

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/联邦交换机.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/联邦交换机图示.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/联邦交换机-搭建步骤.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/原理图.png)

搭建步骤:

1. 先搭建上图中fed_exhange和node2_queue
2. 然后完成下图 Add a new upstream 和 Add / update a policy

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/搭建联邦交换机2.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/搭建联邦交换机3.png)

### Federation Queue

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/联邦队列.png)

**搭建步骤**

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/联邦队列搭建原理图.png)

2. 添加upstream同联邦交换机添加upstream
3. 添加policy

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/联邦队列搭建3.png)

### Shovel

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/Shovel.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/Shovel搭建步骤.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/Shovel原理图.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/RabbitMQ/配置Shovel.png)

```java
// 生产者代码
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class ProducerWithQ1 {
    private static final String EXCHANGE_NAME = "shovel1_exchange";
    private static final String QUEUE_NAME = "Q1";
    private static final String ROUTING_KEY = "routingKey.q1";

    public static void main(String[] args) throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.200.130");
        factory.setUsername("admin");
        factory.setPassword("111");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);
        String message = "Q1.HelloWorld";
        channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, message.getBytes());
        channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, message.getBytes());
        channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, message.getBytes());
    }
}
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class ProducerWithQ2 {
    private static final String EXCHANGE_NAME = "shovel2_exchange";
    private static final String QUEUE_NAME = "Q2";
    private static final String ROUTING_KEY = "routingKey.q2";

    public static void main(String[] args) throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.200.131");
        factory.setUsername("admin");
        factory.setPassword("123");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);
        String message = "Q2.WDNMD";
        channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, message.getBytes());
    }
}
```

