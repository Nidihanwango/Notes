# Netty 网络编程

## Netty的基本介绍

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Netty%E7%9A%84%E4%BB%8B%E7%BB%8D.png)

异步是计算机多线程的异步处理。与同步处理相对，异步处理不用阻塞当前线程来等待处理完成，而是允许后续操作，直至其它线程将处理完成，并回调通知此线程。

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/%E5%9B%BE%E7%A4%BA%E5%BC%82%E6%AD%A5.png)

Netty是基于NIO的框架

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Netty.png)

## Netty的应用场景

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Netty%E7%9A%84%E5%BA%94%E7%94%A8%E5%9C%BA%E6%99%AF1.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Netty%E7%9A%84%E5%BA%94%E7%94%A8%E5%9C%BA%E6%99%AF2.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Netty%E7%9A%84%E5%BA%94%E7%94%A8%E5%9C%BA%E6%99%AF3.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Netty%E7%9A%84%E5%BA%94%E7%94%A8%E5%9C%BA%E6%99%AF4.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Netty%E5%AD%A6%E4%B9%A0%E8%B5%84%E6%96%99.png)

## I/O模型基本说明

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/IO%E6%A8%A1%E5%9E%8B.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/BIO.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/NIO.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/io%E6%A8%A1%E5%9E%8B%E5%9C%BA%E6%99%AF%E5%88%86%E6%9E%90.png)

## Java BIO编程

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/BIO%E5%9F%BA%E6%9C%AC%E4%BB%8B%E7%BB%8D.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/BIO%E5%B7%A5%E4%BD%9C%E6%9C%BA%E5%88%B6.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/BIO%E5%BA%94%E7%94%A8%E5%AE%9E%E4%BE%8B.png)

```java
// 实例代码
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BIOTest {
    public static void main(String[] args) {

        // 创建线程池
        ExecutorService threadPool = Executors.newCachedThreadPool();
        // 创建ServerSocket
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(6666);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 监听6666端口, 如果有客户端访问6666端口, 则创建一个线程, 建立连接
        while (true) {
            try {
                final Socket accept = serverSocket.accept();
                System.out.println("当前线程id为: " + Thread.currentThread().getId());
                System.out.println("当前线程名为: " + Thread.currentThread().getName());
                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("与服务器建立连接");
                        System.out.println("当前线程id为: " + Thread.currentThread().getId());
                        System.out.println("当前线程名为: " + Thread.currentThread().getName());
                        communicate(accept);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 客户端和服务器通信的逻辑
     * @param socket
     */
    private static void communicate(Socket socket){
        InputStream inputStream = null;
        try {
            inputStream = socket.getInputStream();
            byte[] bytes = new byte[1024];
            while (true) {
                int read = inputStream.read(bytes);
                if (read != -1) {
                    System.out.println("服务器收到的内容是: " + new String(bytes));
                } else {
                    System.out.println("断开连接");
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
```

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/BIO%E9%97%AE%E9%A2%98%E5%88%86%E6%9E%90.png)

## Java NIO编程

### 基本介绍

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/NIO%E5%9F%BA%E6%9C%AC%E4%BB%8B%E7%BB%8D.png)

### 图解NIO三大核心部分

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/NIO%E6%A8%A1%E5%9E%8B.png)

selector可以选择一个通道channel, channel和缓冲区buffer可以相互读写

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/NIO%E5%9F%BA%E6%9C%AC%E4%BB%8B%E7%BB%8D2.png)

### NIO案例

```java
// NIO案例
// buffer的基本使用
import java.nio.IntBuffer;

public class BasicBuffer {
    public static void main(String[] args) {
        IntBuffer intBuffer = IntBuffer.allocate(5);

        intBuffer.put(0);
        intBuffer.put(1);
        intBuffer.put(2);
        intBuffer.put(3);
        intBuffer.put(4);
		// 切换buffer读写操作
        intBuffer.flip();
        
        System.out.println(intBuffer.get());
        System.out.println(intBuffer.get());
        System.out.println(intBuffer.get());
        System.out.println(intBuffer.get());
        System.out.println(intBuffer.get());
    }
}
```

### NIO和BIO的比较

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/NIO%E5%92%8CBIO%E7%9A%84%E6%AF%94%E8%BE%83.png)

### NIO三大核心组件关系

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/NIO%E4%B8%89%E5%A4%A7%E6%A0%B8%E5%BF%83%E7%BB%84%E4%BB%B6%E5%85%B3%E7%B3%BB.png)

### 缓冲区Buffer

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/%E7%BC%93%E5%86%B2%E5%8C%BABuffer.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Buffer%E7%B1%BB%E5%8F%8A%E5%85%B6%E5%AD%90%E7%B1%BB.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Buffer%E7%B1%BB%E5%8F%8A%E5%85%B6%E5%AD%90%E7%B1%BB2.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Buffer%E7%B1%BB%E5%8F%8A%E5%85%B6%E5%AD%90%E7%B1%BB3.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/ByteBuffer.png)

### 通道 Channel

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/channel.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/channel-2.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/ServerSocketChannel.png)

当有NIO程序访问服务器时,服务器端生成一个ServerSocketChannelImpl类,该类生成一个SocketChannel用于接受或者发送NIO程序的数据

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/FileChannel.png)

#### 案例1 将数据写入本地文件

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/channel-%E6%A1%88%E4%BE%8B1.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/channel-%E6%A1%88%E4%BE%8B1-%E5%9B%BE%E7%A4%BA2.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/FileoutputStream.png)

代码

```java
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ChannelTest01 {
    public static void main(String[] args) throws Exception{
        String str = "Hello,Channel!";
        // 创建文件输出流
        FileOutputStream fileOutputStream = new FileOutputStream("d:/a.txt");
        // 生成FileChannel
        FileChannel channel = fileOutputStream.getChannel();
        // 创建Buffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        // 将数据写入Buffer中
        byteBuffer.put(str.getBytes());
        // 将数据写入FileChannel中
        byteBuffer.flip();
        channel.write(byteBuffer);
        // 关闭输出流
        fileOutputStream.close();
    }
}
```

#### 案例2 从本地文件读取数据

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/channel-%E6%A1%88%E4%BE%8B2.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/channel-%E6%A1%88%E4%BE%8B2-%E5%9B%BE%E7%A4%BA.png)

代码

```java
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileChannelTest02 {
    public static void main(String[] args) throws Exception{

        File file = new File("d:/a.txt");
        // 创建文件输入流
        FileInputStream fileInputStream = new FileInputStream(file);
        // 获取FileChannel
        FileChannel fileChannel = fileInputStream.getChannel();
        // 创建ByteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate((int) file.length());
        // 将数据从FileChannel中读到byteBuffer中
        fileChannel.read(byteBuffer);
        // 将字节数组转化为字符串并输出到控制台
        System.out.println(new String(byteBuffer.array()));
        // 关闭输入流
        fileInputStream.close();
    }
}
```

#### 案例3 文件拷贝

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/channel-%E6%A1%88%E4%BE%8B3.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/channel-%E6%A1%88%E4%BE%8B3-%E5%9B%BE%E7%A4%BA.png)

代码

```java
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileChannelTest03 {
    public static void main(String[] args) throws Exception{
        // 创建文件输入流并获取信道
        FileInputStream fileInputStream = new FileInputStream("d:/a.txt");
        FileChannel fileChannel01 = fileInputStream.getChannel();
        // 创建文件输出流并获取信道
        FileOutputStream fileOutputStream = new FileOutputStream("d:/a.txt.backup");
        FileChannel fileChannel02 = fileOutputStream.getChannel();
        // 创建ByteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        // 循环拷贝文件
        while (true) {
            // 重置byteBuffer,不然第二次进入循环时,read=0,进入死循环
            byteBuffer.clear();
            // 将数据从fileChannel01中读取到byteBuffer中
            int read = fileChannel01.read(byteBuffer);
            System.out.println("read = " + read);
            if (read == -1) {
                break;
            }
            // 翻转byteBuffer
            byteBuffer.flip();
            // 将数据从byteBuffer中写入到fileChannel02中
            fileChannel02.write(byteBuffer);
        }
    }
}
```

#### 案例4 transferFrom()方法

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/channel-%E6%A1%88%E4%BE%8B4.png)

```java
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class FileChannelTest04 {
    public static void main(String[] args) throws Exception {
        // 创建文件输入,输出流
        FileInputStream fileInputStream = new FileInputStream("d:/测试图片.jpg");
        FileOutputStream fileOutputStream = new FileOutputStream("d:/拷贝图片.jpg");
        // 获取FileChannel
        FileChannel sourceCh = fileInputStream.getChannel();
        FileChannel destCh = fileOutputStream.getChannel();
        // 拷贝文件
        sourceCh.transferTo(0, sourceCh.size(), destCh);
        // 关闭流
        fileInputStream.close();
        fileOutputStream.close();
    }
}
```

### 关于Buffer和Channel的注意事项和细节

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/buffer%E5%92%8Cchannel%E7%9A%84%E7%BB%86%E8%8A%82.png)

```java
// 举例说明1
import java.nio.ByteBuffer;

public class TypingBuffer {
    public static void main(String[] args) {
        // 先创建一个buffer
        ByteBuffer buffer = ByteBuffer.allocate(64);
        // 向buffer中存入数据,先存入一个int类型,再存入一个Long类型
        buffer.putInt(100);
        buffer.putShort((short) 4);
        // 翻转buffer
        buffer.flip();
        // 取出buffer中的数据,取出时要按照存入的顺序取,否则可能会报异常,不报异常数据也会和原来不一样
        System.out.println(buffer.getInt());
        System.out.println(buffer.getLong());
    }
}
// 举例说明2
import java.nio.IntBuffer;

public class ReadOnlyBuffer {
    public static void main(String[] args) {
        // 创建Buffer
        IntBuffer buffer = IntBuffer.allocate(5);
        // 放一些数据进Buffer
        for (int i = 0; i < 5; i++) {
            buffer.put(i);
        }
        // 翻转buffer
        buffer.flip();
        // 得到一个只读的buffer
        IntBuffer intBuffer = buffer.asReadOnlyBuffer();
        System.out.println(intBuffer.getClass());
        // 读取数据
        while (intBuffer.hasRemaining()) {
            System.out.println(intBuffer.get());
        }
        // 如果再向只读的buffer中存放数据,会报异常
        intBuffer.put(5);
    }
}
// 运行结果
class java.nio.HeapIntBufferR
0
1
2
3
4
Exception in thread "main" java.nio.ReadOnlyBufferException
	at java.nio.HeapIntBufferR.put(HeapIntBufferR.java:175)
	at com.syh.nio.buffer.ReadOnlyBuffer.main(ReadOnlyBuffer.java:23);

// 举例说明3    
// MappedByteBuffer 可以让文件直接在内存(堆外内存)修改,操作系统不需要拷贝一次,属于操作系统级别的修改,性能比较高
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MapperByteBufferTest {
    public static void main(String[] args) throws Exception{
        // 创建随机访问文件的流
        RandomAccessFile randomAccessFile = new RandomAccessFile("d:/a.txt", "rw");
        // 获取对应的通道
        FileChannel channel = randomAccessFile.getChannel();
        /**
         * 调用Channel的map方法获取MapperByteBuffer
         * 参数1: FileChannel.MapMode.READ_WRITE 表示读写模式
         * 参数2: 0 表示可以直接修改的起始位置
         * 参数3: 5 是映射到内存的大小,及将文件的多少个字节映射到内存
         * 参数2和参数3决定了可以在程序中修改的范围是 [0,5)
         * 超过范围会报IndexOutOfBoundsException
         */
        MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 16);

        mappedByteBuffer.put(0, (byte) 'h');
        mappedByteBuffer.put(6, (byte) 'c');
        // 关闭流
        randomAccessFile.close();
    }
}

// 举例说明4
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Scattering: 将数据写入到buffer时,可以采用buffer数据,依次写入 [分散]
 * Gathering: 从buffer读取数据时,可以采用buffer数组,依次从读取 [聚集]
 */
public class ScatteringAndGatheringTest {
    public static void main(String[] args) throws Exception {
        // 创建ServerSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 绑定端口号7000
        serverSocketChannel.socket().bind(new InetSocketAddress(7000));
        // 创建buffer数组
        ByteBuffer[] buffers = new ByteBuffer[2];
        buffers[0] = ByteBuffer.allocate(6);
        buffers[1] = ByteBuffer.allocate(8);
        // 等待telnet连接服务器,创建SocketChannel
        SocketChannel socketChannel = serverSocketChannel.accept();
        // 将数据写入到buffer数组中 分散
        int messageLength = 14;
        // 从信道中读取到的字节数
        int readBytes = 0;
        for (ByteBuffer buffer : buffers) {
            buffer.clear();
        }
        // 循环读取,直到读取的字节数达到messageLength
        while (readBytes < messageLength) {
            long read = socketChannel.read(buffers);
            readBytes += read;
            // 打印每个buffer的状态
            for (ByteBuffer buffer : buffers) {
                System.out.println("buffer: [position: " + buffer.position() + ", limit: " + buffer.limit() + "]");
                System.out.println("buffer: " + new String(buffer.array()));
            }
        }
        // 翻转buffers
        for (ByteBuffer buffer : buffers) {
            buffer.flip();
        }
        // 从buffer数组中读取数据 聚集
        long write = socketChannel.write(buffers);
        System.out.println("从buffers中读取到的字节数为: " + write);
        // 关闭ServerSocketChannel
        serverSocketChannel.close();
    }
}
```

### Selector 选择器

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Selector%E5%9F%BA%E6%9C%AC%E4%BB%8B%E7%BB%8D.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Selector.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Selector%E7%89%B9%E7%82%B9%E8%AF%B4%E6%98%8E.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Selector%E7%B1%BB%E7%9B%B8%E5%85%B3%E6%96%B9%E6%B3%95.png)

SelectionKey 和 Channel 相关联, 可以通过 selectionKey 获取 Channel

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Selector%E7%9B%B8%E5%85%B3%E6%96%B9%E6%B3%95%E8%AF%B4%E6%98%8E.png)

### NIO 编程原理

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/NIO%E7%BC%96%E7%A8%8B%E5%9B%BE%E8%A7%A3.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/NIO%E5%8E%9F%E7%90%86%E5%88%86%E6%9E%90%E5%9B%BE.png)

### NIO 入门案例

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/NIO%E5%85%A5%E9%97%A8%E6%A1%88%E4%BE%8B.png)

```java
// 服务器端代码
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class Server {
    public static void main(String[] args) throws Exception {
        // 创建ServerSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // ServerSocketChannel绑定端口6666
        serverSocketChannel.socket().bind(new InetSocketAddress(6666));
        // ServerSocketChannel设置为非阻塞模式
        serverSocketChannel.configureBlocking(false);
        // 创建Selector
        Selector selector = Selector.open();
        // 将ServerSocketChannel注册到Selector中
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        // 开启循环,Selector监听事件发生,等待客户端连接
        while (true) {
            int select = selector.select(1000);
            if (select == 0) { // 监听时没有事件发生,直接返回
//                System.out.println("服务器等待了一秒, 没有事件发生");
                continue;
            }
            // 如果select != 0 说明有事件发生,获取selectionKeys
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            // 获取selectionKeys迭代器
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            // 遍历selectionKeys
            while (iterator.hasNext()) {
                SelectionKey next = iterator.next();
                // 根据selectionKey,对应的通道发生的事件做出相应的处理
                // 如果是连接事件,则创建SocketChannel,并将其注册到Selector中
                if (next.isAcceptable()) {
                    SocketChannel accept = serverSocketChannel.accept();
                    // 将socketChannel设置为非阻塞
                    accept.configureBlocking(false);
                    accept.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                    System.out.println("服务器接受到客户端的连接...");
                }
                // 如果是读事件,则反向获取Channel和Buffer,查看数据
                if (next.isReadable()) {
                    SocketChannel channel = (SocketChannel) next.channel();
                    ByteBuffer buffer = (ByteBuffer) next.attachment();
                    channel.read(buffer);
                    System.out.println("服务器接收到 " + channel.hashCode() + " 的数据");
                    System.out.println("服务器接收到的数据: " + new String(buffer.array()));
                }
                // 手动从集合中删除当前selectionKey,防止重复操作
                iterator.remove();
            }
        }
    }
}

// 客户端代码
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client {
    public static void main(String[] args) throws Exception {
        // 得到一个网络通道SocketChannel
        SocketChannel socketChannel = SocketChannel.open();
        // 设置为非阻塞
        socketChannel.configureBlocking(false);
        // 设置服务器地址和端口
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 6666);
        // 连接服务器
        // 连接失败
        if (!socketChannel.connect(inetSocketAddress)) {
            while (!socketChannel.finishConnect()) {
                System.out.println("客户端没有阻塞,可以做其他工作...");
            }
        }
        // 连接成功,向服务器发送数据
        String message = "Hello,NIO.";
        ByteBuffer wrapBuffer = ByteBuffer.wrap(message.getBytes());
        socketChannel.write(wrapBuffer);
        System.out.println(socketChannel.hashCode() + "向服务器发送了一条数据.");
        System.in.read();
    }
}

```

### SelectionKey API

Selector类

- 上述案例中的Selector的实例对象为WindowsSelectorImpl

- keys()方法返回所有注册到selector中的通道的selectorKey
- selectedKeys()方法返回注册到selector中并且有事件发生的通道的selectorKey

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/SelectionKey-2.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/SelectionKeys.png)

### ServerSocketChannel API

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/ServerSocketChannel-API.png)

### SocketChannel API

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/SocketChannel-API.png)

### NIO 网络编程应用实例 - 群聊系统

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/NIO%E5%BA%94%E7%94%A8%E5%AE%9E%E4%BE%8B-%E7%BE%A4%E8%81%8A%E7%B3%BB%E7%BB%9F2.png)

```shell
# 思路
1.先编写服务器端
1.1 服务器启动并监听端口6667
1.2 服务器接受客户端信息,并实现转发,处理客户端上线离线
2.编写客户端
2.1 连接服务器
2.2 发送消息
2.3 接收服务器消息
```

```java
// 服务器代码
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class Server {

    public static void main(String[] args) {
        Server server = new Server();
        server.listen();
    }

    // 定义服务器属性
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private static final Integer PORT = 6667;

    // 构造器
    public Server() {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 服务器监听端口,等待客户端连接
    public void listen() {
        while (true) {
            try {
                // 阻塞2秒,等待客户端连接
                int select = selector.select(2000);
                if (select <= 0) {
//                    System.out.println("暂无客户端连接...");
                } else {
                    // 获取连接通道的selectionKeys
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    // 遍历selectionKeys
                    while (iterator.hasNext()) {
                        SelectionKey next = iterator.next();
                        // 判断事件类型
                        if (next.isAcceptable()) { // 如果是OP_ACCEPT
                            // 创建SocketChannel
                            SocketChannel socketChannel = serverSocketChannel.accept();
                            // 将该socketChannel设置为非阻塞
                            socketChannel.configureBlocking(false);
                            // 将该socketChannel注册到Selector
                            socketChannel.register(selector, SelectionKey.OP_READ);
                            // 服务端提示有客户端上线
                            System.out.println(socketChannel.getRemoteAddress() + "上线...");
                        }
                        if (next.isReadable()) { // 如果是OP_READ
                            readData(next);
                        }
                        // 处理完将key从iterator中删除,防止重复处理
                        iterator.remove();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void readData(SelectionKey key) {
        SocketChannel channel = null;
        try {
            // 通过SelectionKey反向获取Channel
            channel = (SocketChannel) key.channel();
            // 创建buffer
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            // 从信道中读取数据
            int read = channel.read(buffer);
            if (read > 0) {
                // 将buffer中读取到的数据转换成String类型
                String message = new String(buffer.array());
                // 在服务器端输出该消息
                System.out.println(message.trim());
                // 将消息转发给其他在线的客户端
                messageForward(message, channel);
            }
        } catch (IOException e) {
            try {
                System.out.println(channel.getRemoteAddress() + "离线了...");
                // 取消注册
                key.cancel();
                // 关闭通道
                channel.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    private void messageForward(String message, SocketChannel self) throws IOException {
        System.out.println("服务器转发消息 message: " + message.trim());
        // 遍历Selector中所有的Key
        for (SelectionKey next : selector.keys()) {
            Channel channel = next.channel();
            if (channel instanceof SocketChannel && channel != self) {
                // 转型
                SocketChannel socketChannel = (SocketChannel) channel;
                // 将message存入buffer
                ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
                // 将message从buffer中写入SocketChannel中
                socketChannel.write(buffer);
            }
        }
    }
}

```

```java
// 客户端代码
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        // 启动客户端
        Client client = new Client();
        // 启动一个线程
        new Thread(){
            @Override
            public void run() {
                System.out.println(client.username + " is ok...");
                while (true) {
                    client.acceptMessage();
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();
        // 向服务器发送数据
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String input = scanner.nextLine();
            client.sendMessage(input);
        }
    }
    // 客户端属性
    private String username;
    private Selector selector;
    private SocketChannel socketChannel;
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 6667;
    // 初始化
    public Client(){
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open(new InetSocketAddress(HOST, PORT));
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
            username = socketChannel.getLocalAddress().toString().substring(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 向服务器发送信息
    public void sendMessage(String message) {
        try {
            message = username + "说: " + message;
            socketChannel.write(ByteBuffer.wrap(message.getBytes()));
            System.out.println(username + "向服务器发送信息: " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 从服务器接受信息
    public void acceptMessage() {
        try{
            int select = selector.select();
            if (select > 0) {
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey next = iterator.next();
                    SocketChannel channel = (SocketChannel) next.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    int read = channel.read(buffer);
                    String message = new String(buffer.array());
                    if (read > 0) {
                        System.out.println(message.trim());
                    }
                    // 处理完将key从iterator中删除,防止重复处理
                    iterator.remove();
                }
            } else {
                System.out.println("暂时没有消息...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

### NIO和零拷贝

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/%E9%9B%B6%E6%8B%B7%E8%B4%9D.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/%E4%BC%A0%E7%BB%9F%E6%95%B0%E6%8D%AE%E8%AF%BB%E5%86%99.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/%E4%BC%A0%E7%BB%9Fio%E6%8B%B7%E8%B4%9D%E6%96%87%E4%BB%B62.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/%E4%BC%A0%E7%BB%9Fio%E6%8B%B7%E8%B4%9D%E6%96%87%E4%BB%B6.png)

```shell
#数据拷贝
	从上图中可以看出，共产生了四次数据拷贝，即使使用了DMA来处理了与硬件的通讯，CPU仍然需要处理两次数据拷贝，与此同时，在用户态与内核态也发生了多次上下文切换，无疑也加重了CPU负担。
	在此过程中，我们没有对文件内容做任何修改，那么在内核空间和用户空间来回拷贝数据无疑就是一种浪费，而零拷贝主要就是为了解决这种低效性。
```

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/mmap%E4%BC%98%E5%8C%96.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/sendFile%E4%BC%98%E5%8C%96.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/sendFile%E4%BC%98%E5%8C%962.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/%E9%9B%B6%E6%8B%B7%E8%B4%9D%E7%9A%84%E7%90%86%E8%A7%A3.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/mmap%E5%92%8CsendFile%E7%9A%84%E5%8C%BA%E5%88%AB.png)

### NIO零拷贝案例

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/NIO%E9%9B%B6%E6%8B%B7%E8%B4%9D%E6%A1%88%E4%BE%8B.png)

```java
// 1.传统IO方法
// 服务器
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        // 创建ServerSocket
        ServerSocket serverSocket = new ServerSocket(7001);
        // 创建字节数据作为缓冲区
        byte[] buffer = new byte[4096];
        // 等待客户端连接
        System.out.println("服务器启动成功, 等待连接...");
        Socket socket = serverSocket.accept();
        // 创建文件输入流
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        // 读取数据
        int total = 0;
        while (true) {
            int read = dataInputStream.read(buffer, 0, buffer.length);
            if (read == -1) {
                break;
            }
            total += read;
        }
        System.out.println("服务器接收到的字节数为: " + total);
        // 关闭连接
        serverSocket.close();
        dataInputStream.close();
    }
}

// 客户端
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        // 通过socket连接服务器
        Socket socket = new Socket("127.0.0.1", 7001);
        // 创建字节数据作为缓冲区
        byte[] buffer = new byte[4096];
        // 文件名
//        String fileName = "E:\\Linux\\软件\\mysql-5.7.26-1.el7.x86_64.rpm-bundle.tar"; 500+MB
        String fileName = "E:\\Windows\\软件\\windows_10_cmake_Release_graphviz-install-5.0.1-win64.exe"; //4+MB
        // 文件输入流
        FileInputStream fileInputStream = new FileInputStream(fileName);
        // 输出流
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        // 开始的时间
        long start = System.currentTimeMillis();
        // 将文件传输给服务器
        int total = 0;
        while (true) {
            int read = fileInputStream.read(buffer, 0, buffer.length);
            if (read == -1) {
                break;
            }
            total += read;
            dataOutputStream.write(buffer);
        }
        // 结束的时间
        long end = System.currentTimeMillis();
        // 输出结果
        // 客户端共发送530882560字节, 用时: 5561ms
        // 客户端共发送4957959字节, 用时: 83ms.
        System.out.println("客户端共发送" + total + "字节, 用时: " + (end - start) + "ms.");
        // 关闭资源
        dataOutputStream.close();
        socket.close();
        fileInputStream.close();
    }
}
```

```java
// NIO零拷贝方式 transferTo()
// 服务器端代码
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Server {
    public static void main(String[] args) throws IOException {
        // 创建ServerSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 配置端口号
        serverSocketChannel.socket().bind(new InetSocketAddress(7002));
        // 创建Buffer
        ByteBuffer buffer = ByteBuffer.allocate(4096);
        // 等待客户端连接
        SocketChannel accept = serverSocketChannel.accept();
        // 接受数据
        int total = 0;
        while (true) {
            buffer.clear();
            int read = accept.read(buffer);
            if (read == -1) {
                break;
            }
            total += read;
            System.out.println("total = " + total);
        }
        // 输出结果
        System.out.println("服务器端接收" + total + "字节.");
        // 关闭资源
        serverSocketChannel.close();
    }
}

// 客户端代码
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class Client {
    public static void main(String[] args) throws IOException {
        // 创建SocketChannel
        SocketChannel socketChannel = SocketChannel.open();
        // 配置服务器地址
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 7002);
        // 连接服务器
        boolean connect = socketChannel.connect(inetSocketAddress);
        if (!connect) {
            System.out.println("连接服务器失败");
            return;
        }
        System.out.println("连接服务器成功");
        // 创建文件输入流
//        String fileName = "E:\\Linux\\软件\\mysql-5.7.26-1.el7.x86_64.rpm-bundle.tar"; 500+MB
        String fileName = "E:\\Windows\\软件\\windows_10_cmake_Release_graphviz-install-5.0.1-win64.exe"; //4+MB
        FileInputStream fileInputStream = new FileInputStream(fileName);
        FileChannel fileChannel = fileInputStream.getChannel();
        // 开始时间
        long start = System.currentTimeMillis();
        // 向服务器发送文件
        // 在linux下一个transferTo()方法就可以完成传输
        // 在Windows下调用一次transferTo()方法最多只能发送8m,大文件需要分段传输
        // transferTo 底层使用到零拷贝
        long transferTo = fileChannel.transferTo(0, fileChannel.size(), socketChannel);
        // 结束时间
        long end = System.currentTimeMillis();
        // 用时
        // 向服务器传输4957959字节, 用时: 37ms
        long costTime = end - start;
        System.out.println("向服务器传输" + transferTo + "字节, 用时: " + costTime + "ms");
        // 关闭资源
        fileInputStream.close();
        fileChannel.close();
        socketChannel.close();
    }
}
```

## Java AIO基本介绍

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/JavaAIO.png)

## BIO,NIO,AIO 对比

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/BIO,NIO,AIO%E5%AF%B9%E6%AF%94.png)

## Netty 概述

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Netty%E6%A6%82%E8%BF%B0.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Netty%E5%AE%98%E7%BD%91.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Netty%E5%AE%98%E7%BD%91%E8%AF%B4%E6%98%8E.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Netty%E7%9A%84%E4%BC%98%E7%82%B9.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Netty%E7%9A%84%E7%89%88%E6%9C%AC%E8%AF%B4%E6%98%8E.png)

## Netty 高性能架构设计

### 线程模型介绍

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Netty%E7%BA%BF%E7%A8%8B%E6%A8%A1%E5%9E%8B.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/%E4%BC%A0%E7%BB%9F%E9%98%BB%E5%A1%9EIO%E6%A8%A1%E5%9E%8B.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Reactor%E6%A8%A1%E5%9E%8B2.png)

Reactor 对应的叫法:

1. 反应器模式
2. 分发者模式 Dispatcher
3. 通知者模式 Notifier

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Reactor%E6%A8%A1%E5%BC%8F%E7%9A%84%E8%AE%BE%E8%AE%A1%E6%80%9D%E6%83%B3.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Reactor%E6%A0%B8%E5%BF%83.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Reactor%E6%A8%A1%E5%BC%8F%E5%88%86%E7%B1%BB.png)

### 单Reactor 单线程

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/%E5%8D%95Reactor%E5%8D%95%E7%BA%BF%E7%A8%8B.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/%E5%8D%95Reactor%E5%8D%95%E7%BA%BF%E7%A8%8B%E8%AF%B4%E6%98%8E.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/%E5%8D%95Reactor%E5%8D%95%E7%BA%BF%E7%A8%8B%E4%BC%98%E7%BC%BA%E7%82%B9.png)

### 单Reactor 多线程

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/%E5%8D%95Reactor%E5%A4%9A%E7%BA%BF%E7%A8%8B.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/%E5%8D%95Reactor%E5%A4%9A%E7%BA%BF%E7%A8%8B%E4%BC%98%E7%BC%BA%E7%82%B9.png)

### 主从Reactor 多线程

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/%E4%B8%BB%E4%BB%8EReactor%E5%A4%9A%E7%BA%BF%E7%A8%8B.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/ScalableIOinJava.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/DougLea.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/UsingMultipleReactors.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/%E4%B8%BB%E4%BB%8EReactor%E5%A4%9A%E7%BA%BF%E7%A8%8B%E4%BC%98%E7%BC%BA%E7%82%B9.png)

### Reactor模式总结

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Reactor%E6%A8%A1%E5%BC%8F%E5%B0%8F%E7%BB%93.png)

### Netty模型

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Netty%E5%B7%A5%E4%BD%9C%E5%8E%9F%E7%90%86%E7%A4%BA%E6%84%8F%E5%9B%BE%E7%AE%80%E5%8D%95%E7%89%88.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Netty%E5%B7%A5%E4%BD%9C%E5%8E%9F%E7%90%86%E7%A4%BA%E6%84%8F%E5%9B%BE%E8%BF%9B%E9%98%B6%E7%89%88.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Netty%E5%B7%A5%E4%BD%9C%E5%8E%9F%E7%90%86%E7%A4%BA%E6%84%8F%E5%9B%BE%E8%AF%A6%E7%BB%86%E7%89%88.png)

### Netty入门案例

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Netty%E5%85%A5%E9%97%A8%E6%A1%88%E4%BE%8B.png)

```xml
<!--maven引入netty依赖-->
<dependency>
    <groupId>io.netty</groupId>
    <artifactId>netty-all</artifactId>
    <version>4.1.20.Final</version>
</dependency>
```

```java
// ServerHandler
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import java.nio.charset.StandardCharsets;

/**
 * 编写服务器Handler,需要继承 ChannelInboundHandlerAdapter
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {
    /**
     * 读取客户端发送给服务器的信息
     * @param ctx 上下文对象,含有popeLine管道,channel信道,客户端地址等信息...
     * @param msg 客户端发送的数据,默认Object类型
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 将msg转换成一个ByteBuf对象,该类型由Netty提供,不同于NIO的ByteBuffer
        ByteBuf message = (ByteBuf) msg;
        System.out.println("服务器接受到来自: " + ctx.channel().remoteAddress() +
                "的消息: [" + message.toString(StandardCharsets.UTF_8) + "]");
    }

    /**
     * 数据读取完毕后执行的方法
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 向客户端回送数据
        // 将数据写入缓存,并刷新
        String message = "Hello," + ctx.channel().remoteAddress();
        ctx.writeAndFlush(Unpooled.copiedBuffer(message, CharsetUtil.UTF_8));
    }

    /**
     * 处理异常,一般是关闭通道
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
```

```java
// Server
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {
    public static void main(String[] args) throws Exception{
        EventLoopGroup bossGroup = null;
        EventLoopGroup workerGroup = null;
        try {
            // 创建BossGroup和WorkerGroup
            // BossGroup处理连接请求,WorkerGroup处理真正的业务,二者都是无限循环
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup(4);
            // 创建服务器启动对象
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 配置服务器参数
            serverBootstrap.group(bossGroup, workerGroup) // 设置两个线程组
                    .channel(NioServerSocketChannel.class) // 使用NioServerSocketChannel作为服务器的通道实现
                    .option(ChannelOption.SO_BACKLOG, 128) // 设置线程队列得到连接个数
                    .childOption(ChannelOption.SO_KEEPALIVE, true) // 设置保持活动连接状态
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 创建一个通道初始化对象(匿名对象)
                        // 给workerGroup的EventLoop对应的管道PipeLine设置处理器
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new ServerHandler());
                        }
                    });
            System.out.println("Server is ready...");
            // 启动服务器(并绑定端口)
            // 绑定一个端口并且同步,生成一个ChannelFuture对象
            ChannelFuture channelFuture = serverBootstrap.bind(6668).sync();
            // 对关闭通道事件进行监听
            channelFuture.channel().closeFuture().sync();
        } finally {
            if (bossGroup != null) {
                bossGroup.shutdownGracefully();
            }
            if (workerGroup != null) {
                workerGroup.shutdownGracefully();
            }
        }
    }
}
```

```java
// ClientHandler
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    /**
     * 当通道就绪时会触发该方法
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String message = "Hello,server: " + ctx.channel().remoteAddress();
        ctx.writeAndFlush(Unpooled.copiedBuffer(message, CharsetUtil.UTF_8));
    }
    /**
     * 当通道有读取事件时,会触发
     * 接受来自服务器的消息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        String message = buf.toString(CharsetUtil.UTF_8);
        System.out.println("来自服务器" + ctx.channel().remoteAddress() + "的消息: [" + message + "]");
    }
    /**
     * 异常捕获
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
```

```java
// Client
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup clientGroup = null;
        try {
            // 创建事件循环组
            clientGroup = new NioEventLoopGroup();
            // 创建客户端启动对象
            Bootstrap bootstrap = new Bootstrap();
            // 设置相关参数
            bootstrap.group(clientGroup) // 添加线程组
                    .channel(NioSocketChannel.class) // 设置客户端的信道类型
                    .handler(new ChannelInitializer<SocketChannel>() { // 初始化客户端信道
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // 向管道PipeLine中添加处理器
                            socketChannel.pipeline().addLast(new ClientHandler());
                        }
                    });
            System.out.println("Client is ok...");
            // 启动客户端去连接服务器
            // ChannelFuture之后分析,涉及到netty的异步模型
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 6668).sync();
            // 给关闭通道进行监听
//            channelFuture.channel().closeFuture().sync();
        } finally {
            if (clientGroup != null) {
                clientGroup.shutdownGracefully();
            }
        }
    }
}
```

```java
// 入门案例踩坑
// Client端最后不加上下面这一行代码
channelFuture.channel().closeFuture().sync();
// 服务端报错
java.io.IOException: 你的主机中的软件中止了一个已建立的连接。
	at sun.nio.ch.SocketDispatcher.read0(Native Method)
	at sun.nio.ch.SocketDispatcher.read(SocketDispatcher.java:43)
	at sun.nio.ch.IOUtil.readIntoNativeBuffer(IOUtil.java:223)
	at sun.nio.ch.IOUtil.read(IOUtil.java:192)
	at sun.nio.ch.SocketChannelImpl.read(SocketChannelImpl.java:378)
	at io.netty.buffer.PooledUnsafeDirectByteBuf.setBytes(PooledUnsafeDirectByteBuf.java:288)
	at io.netty.buffer.AbstractByteBuf.writeBytes(AbstractByteBuf.java:1108)
```

### 任务队列中的Task使用场景

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/%E4%BB%BB%E5%8A%A1%E9%98%9F%E5%88%97%E4%B8%AD%E7%9A%84Task.png)

```java
// 1.用户程序自定义的普通任务 eventLoop.execute()
// 任务二在任务一结束后开始执行,也就是任务一 10s 后完成,任务二 10s + 20s 后完成
// 2.用户自定义定时任务 eventLoop.schedule()
// 定时任务和任务一同时开始计时,但需要等待普通任务执行完后执行
package com.syh.netty.helloworld;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import io.netty.util.CharsetUtil;

public class TaskHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 用户自定义任务放入TaskQueue中
        EventLoop eventLoop = ctx.channel().eventLoop();
        // 任务一
        eventLoop.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    long start = System.currentTimeMillis();
                    Thread.sleep(10000);
                    long end = System.currentTimeMillis();
                    long costTime = end - start;
                    String message = "自定义任务一结束, 花费时间" + costTime + "ms";
                    ctx.writeAndFlush(Unpooled.copiedBuffer(message, CharsetUtil.UTF_8));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        // 任务二
        eventLoop.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    long start = System.currentTimeMillis();
                    Thread.sleep(20000);
                    long end = System.currentTimeMillis();
                    long costTime = end - start;
                    String message = "自定义任务二结束, 花费时间" + costTime + "ms";
                    ctx.writeAndFlush(Unpooled.copiedBuffer(message, CharsetUtil.UTF_8));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        // 任务三
        eventLoop.schedule(new Runnable() {
            @Override
            public void run() {
                String message = "自定义任务三结束";
                ctx.writeAndFlush(Unpooled.copiedBuffer(message, CharsetUtil.UTF_8));
            }
        }, 40, TimeUnit.SECONDS);
        System.out.println("等待客户端请求...");
    }
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        String message = "TaskHandler.channelReadComplete is executing...";
        ctx.writeAndFlush(Unpooled.copiedBuffer(message, CharsetUtil.UTF_8));
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
```

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Debug%E6%9F%A5%E7%9C%8B%E8%87%AA%E5%AE%9A%E4%B9%89%E4%BB%BB%E5%8A%A1.png)

3.非当前Reactor线程调用Channel的各种方法

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/%E9%9D%9EReactor%E7%BA%BF%E7%A8%8B%E6%B7%BB%E5%8A%A0%E8%87%AA%E5%AE%9A%E4%B9%89%E4%BB%BB%E5%8A%A1.png)

### Netty模型方案再说明

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/%E6%96%B9%E6%A1%88%E5%86%8D%E8%AF%B4%E6%98%8E.png)

## Netty 异步

### 异步模型介绍

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/%E5%BC%82%E6%AD%A5%E6%A8%A1%E5%9E%8B.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Future.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/%E5%BC%82%E6%AD%A5%E6%A8%A1%E5%9E%8B%E5%B7%A5%E4%BD%9C%E5%8E%9F%E7%90%86.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/%E5%BC%82%E6%AD%A5%E6%A8%A1%E5%9E%8B%E5%B7%A5%E4%BD%9C%E5%8E%9F%E7%90%86%E7%A4%BA%E6%84%8F%E5%9B%BE.png)

### Future-Listener机制

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Future-Listener.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Future-Listener-%E6%A1%88%E4%BE%8B.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Future-Listener-%E6%A1%88%E4%BE%8B2.png)

```java
// 服务器代码
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {
    public static void main(String[] args) throws Exception{
        EventLoopGroup bossGroup = null;
        EventLoopGroup workerGroup = null;
        try {
            // 创建BossGroup和WorkerGroup
            // BossGroup处理连接请求,WorkerGroup处理真正的业务,二者都是无限循环
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup(4);
            // 创建服务器启动对象
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 配置服务器参数
            serverBootstrap.group(bossGroup, workerGroup) // 设置两个线程组
                    .channel(NioServerSocketChannel.class) // 使用NioServerSocketChannel作为服务器的通道实现
                    .option(ChannelOption.SO_BACKLOG, 128) // 设置线程队列得到连接个数
                    .childOption(ChannelOption.SO_KEEPALIVE, true) // 设置保持活动连接状态
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 创建一个通道初始化对象(匿名对象)
                        // 给workerGroup的EventLoop对应的管道PipeLine设置处理器
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
//                            socketChannel.pipeline().addLast(new ServerHandler());
                            socketChannel.pipeline().addLast(new TaskHandler());
                        }
                    });
            System.out.println("Server is ready...");
            // 启动服务器(并绑定端口)
            // 绑定一个端口并且同步,生成一个ChannelFuture对象
            ChannelFuture channelFuture = serverBootstrap.bind(6668).sync();
            // 监听服务器绑定端口事件
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        System.out.println("监听端口成功");
                    } else {
                        System.out.println("监听端口失败");
                    }
                }
            });
            // 对关闭通道事件进行监听
            channelFuture.channel().closeFuture().sync();
        } finally {
            if (bossGroup != null) {
                bossGroup.shutdownGracefully();
            }
            if (workerGroup != null) {
                workerGroup.shutdownGracefully();
            }
        }
    }
}
```

### 快速入门案例-HTTP服务

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/%E5%BC%82%E6%AD%A5HelloWorld.png)

```java
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import java.net.URI;

/**
 * 自定义Http请求处理器
 * SimpleChannelInboundHandler继承ChannelInboundHandlerAdapter
 * HttpObject: 客户端和服务器端相互通信的数据被封装成HttpObject
 * 一个handler对应一个pipeLine对应一个客户端请求
 * http不是长连接,用完就会断掉,所以每次刷新浏览器访问localhost:8081,所用的handler不是同一个对象
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<HttpObject> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        // 判断msg是否为HttpRequest类型
        if (msg instanceof HttpRequest) {
            System.out.println("msg类型: " + msg.getClass());
            System.out.println("客户端地址: " + ctx.channel().remoteAddress());
            // 将msg转换成HttpRequest类型
            HttpRequest message = (HttpRequest) msg;
            // 获取uri,过滤指定资源
            URI uri = new URI(message.uri());
            if (uri.getPath().equals("/favicon.ico")) {
                System.out.println("客户端请求了favicon.ico,不做响应");
                return;
            }
            // 回复信息给浏览器[Http协议]
            ByteBuf content = Unpooled.copiedBuffer("Hello,I'm Server.", CharsetUtil.UTF_8);
            // 构建一个http的响应,HttpResponse
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
            // 设置响应头
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
            // 返回response
            ctx.writeAndFlush(response);
        }
    }
}

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * 初始化服务器
 */
public class ServerInitialize extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // 获取对应的管道
        ChannelPipeline pipeline = ch.pipeline();
        // 向管道中添加一个Netty提供的HttpServerCodec[codec: coder -- decoder 编码,解码器]
        pipeline.addLast(new HttpServerCodec());
        // 添加一个自定义的handler
        pipeline.addLast(new HttpRequestHandler());
    }
}

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
/**
 * 服务器 端口8081
 */
public class Server {
    public static void main(String[] args) {
        EventLoopGroup bossGroup = null;
        EventLoopGroup workerGroup = null;
        try {
            // 创建两个事件循环组
            bossGroup= new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup(4);
            // 创建服务器启动器
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 设置服务器参数
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerInitialize());
            System.out.println("Server is OK");
            // 服务器绑定端口
            ChannelFuture channelFuture = serverBootstrap.bind(8081).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bossGroup != null) {
                bossGroup.shutdownGracefully();
            }
            if (workerGroup != null) {
                workerGroup.shutdownGracefully();
            }
        }

    }
}
```

## Netty 核心模块组件

### Bootstrap,ServerBootstrap

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Bootstrap.png)

```java
 // 补充
 public B handler(ChannelHandler handler) // 用来给当前的Bootstrap设置处理器
```

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Future,ChannelFuture.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Channel%E4%BB%8B%E7%BB%8D.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Channel%E4%BB%8B%E7%BB%8D2.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Netty-Select.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Netty-ChannelHandler.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Netty-ChannelHandler%E5%8F%8A%E5%85%B6%E5%AE%9E%E7%8E%B0%E7%B1%BB.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Netty-ChannelHandler3.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Netty/Netty-PipeLine.png)

![]()
