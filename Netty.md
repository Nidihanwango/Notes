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
