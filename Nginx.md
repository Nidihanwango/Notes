# Nginx

## 安装部署



![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Nginx/Nginx%E5%9B%9B%E5%A4%A7%E9%98%B5%E8%90%A5.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Nginx/%E5%AE%89%E8%A3%85nginx%E5%BC%80%E6%BA%90%E7%89%88.png)

```shell
#解压nginx安装包
tar -zxvf nginx-1.22.1.tar.gz
#进入目录
./configure --prefix=/usr/local/nginx
#如果出现报错
[root@Nginx01 nginx-1.22.1]# ./configure
checking for OS
 + Linux 3.10.0-1160.71.1.el7.x86_64 x86_64
checking for C compiler ... not found

./configure: error: C compiler cc is not found
#需要安装gcc
yum install -y gcc
#配置nginx prefix指定安装位置
./configure --prefix=/usr/local/nginx
#可能出现报错 缺少依赖
./configure: error: the HTTP rewrite module requires the PCRE library.
You can either disable the module by using --without-http_rewrite_module
option, or install the PCRE library into the system, or build the PCRE library
statically from the source with nginx by using --with-pcre=<path> option.
#安装prel库
yum install -y pcre pcre-devel
#重新检查nginx是否缺依赖
./configure --prefix=/usr/local/nginx
#如果报错
./configure: error: the HTTP gzip module requires the zlib library.
You can either disable the module by using --without-http_gzip_module
option, or install the zlib library into the system, or build the zlib library
statically from the source with nginx by using --with-zlib=<path> option.
#安装zlib库
yum install -y zlib zlib-devel
#安装
make
make install
#进入安装目录
cd /usr/local/nginx/sbin
#启动nginx
./nginx
./nginx -s stop 快速停止
./nginx -s quit 优雅关闭, 在退出前完成已经接受的连接请求
./nginx -s reload 重新加载配置
#打开端口80/tcp
firewall-cmd --zone=public --permanent --add-port=80/tcp
firewall-cmd --reload
#访问服务器ip地址
```

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Nginx/%E8%AE%BF%E9%97%AEnginx.png)

```shell
#安装成系统服务
使用源码包安装的nginx，安装路径为: /usr/local/nginx

安装完成后，为了能方便的使用systemctl启动，停止，重加载，退出nginx，在路径：/usr/lib/systemd/system下新建一个nginx.service文件，文件内容如下：

[Unit]
Description=nging -  web server
After=syslog.target network.target remote-fs.target nss-lookup.target
 
[Service]
Type=forking
PIDFile=/usr/local/nginx/logs/nginx.pid
ExecStartPre=/usr/local/nginx/sbin/nginx -t
ExecStart=/usr/local/nginx/sbin/nginx
ExecReload=/usr/local/nginx/sbin/nginx -s reload
ExecStop=/usr/local/nginx/sbin/nginx -s stop
ExecQuit=/usr/local/nginx/sbin/nginx -s quit
PrivateTmp=true
 
 
[Install]
WantedBy=multi-user.target

也可以在路径：/etc/systemd/system下创建该文件
创建完成之后不能直接使用systemctl stop nginx.service，会报错
Failed to stop nginx.service: Unit nginx.service not loaded.

需要首先重新加载系统服务
systemctl daemon-reload

然后就可以使用systemctl命令启动和关闭nginx了

#开机自启
systemctl enable nginx
```

## 基本使用

### 目录结构

/usr/local/nginx

- conf 核心配置文件目录
  - nginx.conf 主配置文件
- html 页面文件目录
- logs 日志文件目录
  - access.log 用户访问日志
  - error.log 错误日志
  - nginx.pid 进程号
- sbin
  - ngins 主进程文件

### 基本运行原理

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Nginx/%E5%9F%BA%E6%9C%AC%E8%BF%90%E8%A1%8C%E5%8E%9F%E7%90%86.png)

### 基础配置

```shell
#启动worker进程的个数
worker_processes  1;

#事件驱动模块 每一个worker进程可以创建多少个连接
events {
    worker_connections  1024;
}

http {
	#include 引入其他配置文件; mime.types:请求头 告诉浏览器返回文件的类型
    include       mime.types;
	
	#如果mime.types文件中没有需要的类型,那么就以默认类型传输
    default_type  application/octet-stream;
	
	#下面图示 开启减少一次将文件读取到内存的过程
    sendfile        on;

	#连接超时时间
    keepalive_timeout  65;

	#虚拟主机 virtualhost vhost 
	#一个server模块代表一个主机,可以配置多个主机,通过端口号区分
    server {
		#监听端口号 服务端口号
        listen       80;
		#主机名或域名 可以在/etc/hosts文件中配置主机名
        server_name  localhost;
		#完整的url: http://atguigu.com/xxoo/index.html
		#location匹配域名后跟的uri:xxoo/index.html
        location / {
			#从该目录中查找用户请求的文件, 相对路径, 相对/usr/local/nginx主目录
            root   html;
			#默认页
            index  index.html index.htm;
        }
		#错误页  错误码  跳转页 
        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
    }
}
```

sendfile 配置图示

- 两者之间少一次将文件加载到内存的过程

- sendfile        off;

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Nginx/sendfileoff.png)



sendfile        on;

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Nginx/sendfileon.png)

### 虚拟主机与域名解析

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Nginx/%E8%99%9A%E6%8B%9F%E4%B8%BB%E6%9C%BA%E4%B8%8E%E5%9F%9F%E5%90%8D%E8%A7%A3%E6%9E%90.png)

#### 浏览器,Nginx与http,https协议

- 浏览器和Nginx通过http协议进行数据传输
- https协议是在http协议上加了一些保证数据安全措施

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Nginx/%E5%9B%BE%E7%A4%BAnginx%E6%9C%8D%E5%8A%A1%E5%99%A8.png)

#### 虚拟主机原理

由于有些主机的访问量不高, 造成cpu 内存的浪费,为了防止资源的浪费,采用了虚拟主机

原理: 虚拟主机是将一台服务器分割成多个虚拟专享服务器的优质服务,将多个域名绑定到一个主机的IP地址上

#### 域名解析和泛域名解析

通过修改"C:\Windows\System32\drivers\etc\hosts" 文件来配置主机名或者域名

- 域名指向ip地址
- 域名指向另一个域名

```shell
# Copyright (c) 1993-2009 Microsoft Corp.
#
# This is a sample HOSTS file used by Microsoft TCP/IP for Windows.
#
# This file contains the mappings of IP addresses to host names. Each
# entry should be kept on an individual line. The IP address should
# be placed in the first column followed by the corresponding host name.
# The IP address and the host name should be separated by at least one
# space.
#
# Additionally, comments (such as these) may be inserted on individual
# lines or following the machine name denoted by a '#' symbol.
#
# For example:
#
#      102.54.94.97     rhino.acme.com          # source server
#       38.25.63.10     x.acme.com              # x client host

# localhost name resolution is handled within DNS itself.
#	127.0.0.1       localhost
#	::1             localhost
#Vhost Nginx01
192.168.200.130 nginx01.com
```

*号为通配符

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Nginx/%E6%B3%9B%E5%9F%9F%E5%90%8D%E8%A7%A3%E6%9E%90.png)

#### Nginx虚拟主机域名配置

通过修改配置文件/usr/local/nginx/conf/nginx.conf



两种方式

- 一种是通过修改端口号来配置虚拟主机
- 另一种是通过修改server_name来配置虚拟主机

ps: 配置的 端口号+server_name 要具有唯一性

```shell
#方式一
#/usr/local/nginx/conf/nginx.conf
worker_processes  1;
events {
    worker_connections  1024;
}
http {
    include       mime.types;
    default_type  application/octet-stream;
    sendfile        on;
    keepalive_timeout  65;
    #vhost 01
    server {
        listen       80;
        server_name  localhost;

        location / {
            root   /pages/vod;
            index  index.html index.htm;
        }

        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
    }
    #vhost 02
    server {
        listen       81;
        server_name  localhost;
        location / {
            root   /pages/mp3;
            index  index.html index.htm;
        }
        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
    }
}
```

```shell
#方式二
#C:\Windows\System32\drivers\etc\hosts
#Vhost Nginx01
192.168.200.130 nginx01.com nginx01.vod.com nginx01.mp3.com
#/usr/local/nginx/conf/nginx.conf
worker_processes  1;
events {
    worker_connections  1024;
}
http {
    include       mime.types;
    default_type  application/octet-stream;
    sendfile        on;
    keepalive_timeout  65;
    #vhost 01
    server {
        listen       80;
        server_name  nginx01.vod.com;

        location / {
            root   /pages/vod;
            index  index.html index.htm;
        }

        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
    }
    #vhost 02
    server {
        listen       80;
        server_name  nginx01.mp3.com;
        location / {
            root   /pages/mp3;
            index  index.html index.htm;
        }
        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
    }
}

```

##### ServerName匹配规则

###### 可以在同一servername中匹配多个域名

```shell
#vhost 02
server {
	listen       80;
	server_name  nginx01.mp3.com nginx01.mp2.com;
	location / {
		root   /pages/mp3;
		index  index.html index.htm;
	}
	error_page   500 502 503 504  /50x.html;
	location = /50x.html {
		root   html;
	}
}
```

###### 完整匹配

```shell
#vhost 01
	server {
	listen       80;
	server_name  nginx01.vod.com;
	location / {
		root   /pages/vod;
		index  index.html index.htm;
	}

	error_page   500 502 503 504  /50x.html;
	location = /50x.html {
		root   html;
	}
}
```

###### 通配符匹配

```shell
#vhost 01
	server {
	listen       80;
	server_name  *.vod.com;
	location / {
		root   /pages/vod;
		index  index.html index.htm;
	}

	error_page   500 502 503 504  /50x.html;
	location = /50x.html {
		root   html;
	}
}
```

###### 通配符结束匹配

```shell
#vhost 01
	server {
	listen       80;
	server_name  nginx01.vod.*;
	location / {
		root   /pages/vod;
		index  index.html index.htm;
	}

	error_page   500 502 503 504  /50x.html;
	location = /50x.html {
		root   html;
	}
}
```

###### 正则匹配

```shell
#vhost 01
	server {
	listen       80;
	server_name  ~^[0-9]+\.nginx01\.com$;
	location / {
		root   /pages/vod;
		index  index.html index.htm;
	}

	error_page   500 502 503 504  /50x.html;
	location = /50x.html {
		root   html;
	}
}
```

#### 域名解析相关企业项目实战技术架构

##### 多用户二级域名

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Nginx/%E5%A4%9A%E5%9F%9F%E5%90%8D%E9%9C%80%E6%B1%82.png)

##### 短网址

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Nginx/%E7%9F%AD%E7%BD%91%E5%9D%80.png)

##### httpdns

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Nginx/httpdns.png)



### 反向代理

#### 网关,代理与反向代理

Nginx隧道式模型

由应用方提供的代理服务器叫做反向代理

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Nginx/Nginx%E9%9A%A7%E9%81%93%E5%BC%8F%E6%A8%A1%E5%9E%8B.png)

正向代理

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Nginx/%E6%AD%A3%E5%90%91%E4%BB%A3%E7%90%86%E6%9C%8D%E5%8A%A1%E5%99%A8.png)

网关

访问网络的入口

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Nginx/%E7%BD%91%E5%85%B3.png)

DR模型

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Nginx/DR%E6%A8%A1%E5%9E%8B%E5%9B%BE%E7%A4%BA.png)

#### 反向代理在系统架构中的应用场景

Nginx做业务中转,请求中转

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Nginx/%E5%8F%8D%E5%90%91%E4%BB%A3%E7%90%86%E5%BA%94%E7%94%A8%E5%9C%BA%E6%99%AF.png)

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Nginx/%E5%8F%8D%E5%90%91%E4%BB%A3%E7%90%86%E4%BC%81%E4%B8%9A%E5%BA%94%E7%94%A8%E5%9C%BA%E6%99%AF2.png)

#### Nginx的反向代理配置

```shell
#Nginx01的配置文件
worker_processes  1;
events {
    worker_connections  1024;
}
http {
    include       mime.types;
    default_type  application/octet-stream;
    sendfile        on;
    keepalive_timeout  65;
    #vhost 01
    server {
        listen       80;
        server_name  nginx01.com;

        location / {
            #root   /pages/vod;
            #index  index.html index.htm;
            #跳转Nginx02, "http://" 不能省略, 最后加分号
            proxy_pass http://192.168.200.131;
            #跳转http://www.atguigu.com
            proxy_pass http://www.atguigu.com;
        }

        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
    }
}
```

#### 基于反向代理的负载均衡器

 ![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Nginx/%E8%B4%9F%E8%BD%BD%E5%9D%87%E8%A1%A1.png)

```shell
#Nginx01的配置文件
#Nginx01作为负载均衡器, Nginx02和Nginx03作为服务器集群
#proxy_pass后的内容要和upstream对应上
worker_processes  1;
events {
    worker_connections  1024;
}
http {
    include       mime.types;
    default_type  application/octet-stream;
    sendfile        on;
    keepalive_timeout  65;
    upstream nginxs {
    	#加上端口号
        server 192.168.200.131:80;
        server 192.168.200.132:80;
    }
    #vhost 01
    server {
        listen       80;
        server_name  nginx01.com;

        location / {
            #root   /pages/vod;
            #index  index.html index.htm;
            proxy_pass http://nginxs;
        }

        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
    }
}
```



#### 负载均衡策略

负载均衡之权重

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Nginx/%E8%B4%9F%E8%BD%BD%E5%9D%87%E8%A1%A1%E4%B9%8B%E6%9D%83%E9%87%8D.png)

```shell
#Nginx01的配置文件分配权重 8 2 1
worker_processes  1;
events {
    worker_connections  1024;
}
http {
    include       mime.types;
    default_type  application/octet-stream;
    sendfile        on;
    keepalive_timeout  65;
    upstream nginxs {
        server 192.168.200.131:80 weight=8;
        server 192.168.200.132:80 weight=2;
        server 192.168.200.133:80 weight=1;
    }
    #vhost 01
    server {
        listen       80;
        server_name  nginx01.com;

        location / {
            #root   /pages/vod;
            #index  index.html index.htm;
            proxy_pass http://nginxs;
        }

        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
    }
}
```

down

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Nginx/%E8%B4%9F%E8%BD%BD%E5%9D%87%E8%A1%A1-down.png)

```shell
#Nginx01的配置文件 Nginx02down
worker_processes  1;
events {
    worker_connections  1024;
}
http {
    include       mime.types;
    default_type  application/octet-stream;
    sendfile        on;
    keepalive_timeout  65;
    upstream nginxs {
        server 192.168.200.131:80 weight=8 down;
        server 192.168.200.132:80 weight=2;
        server 192.168.200.133:80 weight=1;
    }
    #vhost 01
    server {
        listen       80;
        server_name  nginx01.com;

        location / {
            #root   /pages/vod;
            #index  index.html index.htm;
            proxy_pass http://nginxs;
        }

        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
    }
}
```

backup

其他主机不能正常工作时,使用备用机,使用方式同上

使用token实现无状态会话

### 动静分离

使用动静分离的原理和场景

Tomcat负责处理一些动态的请求

将静态资源放在Nginx上实现动静分离

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Nginx/%E5%8A%A8%E9%9D%99%E5%88%86%E7%A6%BB%E7%9A%84%E5%8E%9F%E7%90%86.png)

Nginx动静分离配置

```shell
#Nginx01的配置文件
worker_processes  1;
events {
    worker_connections  1024;
}
http {
    include       mime.types;
    default_type  application/octet-stream;
    sendfile        on;
    keepalive_timeout  65;
    upstream nginxs {
        server 192.168.200.131:80 weight=8 down;
        server 192.168.200.132:80 weight=2;
        server 192.168.200.133:80 weight=1;
    }
    #vhost 01
    server {
        listen       80;
        server_name  nginx01.com;
		#反向代理Tomcat服务器
        location / {
            proxy_pass http://192.168.200.102:8080;
        }
        #在Nginx01上配置静态资源文件,需要在页面上使用相对路径
        location /static {
            root html;
            index index.html index.htm;
        }
        #使用正则匹配动静分离
		location ~*/(js|img|css) {
            root html;
            index index.html index.htm;
        }
      
        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
    }
}
```

### 负载均衡+URLRewrite

URLRewrite 概念

- URL Rewrite即URL重写，就是把传入Web的请求重定向到其他URL的过程。URL Rewrite最常见的应用是URL伪静态化，是将动态页面显示为静态页面方式的一种技术。比如http://www.123.com/news/index.asp?id=123 使用UrlRewrite转换后可以显示为http://www.123.com/news/123.html

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Nginx/URLrewrite.png)

```shell
#应用服务器开启防火墙
systemctl start firewalld
#配置指定端口和ip访问
firewall-cmd --permanent --add-rich-rule="rule family="ipv4" source address="192.168.200.130" port protocol="tcp" port="8080" accept"
#移除规则
firewall-cmd --permanent --remove-rich-rule="rule family="ipv4" source address="192.168.200.130" port protocol="tcp" port="8080" accept"
#查看防火墙的所有配置
firewall-cmd --list-all
#在网关上配置URLRewrite
worker_processes  1;
events {
    worker_connections  1024;
}
http {
    include       mime.types;
    default_type  application/octet-stream;
    sendfile        on;
    keepalive_timeout  65;
    upstream nginxs {
        server 192.168.200.102:8080 weight=8;       
        server 192.168.200.101:8080 weight=1;
    }
    #vhost 01
    server {
        listen       80;
        server_name  nginx01.com;

        location / {
            rewrite ^/([0-9]+).html$ /index.jsp?pageNum=$1 redirect;
            proxy_pass http://nginxs;
        }
        location /static {
            root html;
            index index.html index.htm;
        }

        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
    }
}

```

### 防盗链

#### http协议中的referer

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Nginx/referer.png)

- 第一次请求index.html页面, 页面中需要再请求一些其他资源, 比如css,js等, 第二次及之后的请求头中就会带上referer, 用来表示从哪儿链接到当前的网页

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Nginx/http-referer.png)

#### 盗链的概念

- 盗链是指服务提供商自己不提供服务的内容，通过技术手段绕过其它有利益的最终用户界面（如广告），直接在自己的网站上向最终用户提供其它服务提供商的服务内容，骗取最终用户的浏览和点击率。受益者不提供资源或提供很少的资源，而真正的服务提供商却得不到任何的收益。

#### 配置防盗链

```shell
valid_referers none | blocked | server_name | strings ...;
```

- none, 检测 Referer头域不存在的情况
- blocked, 检测Referer头域的值被防火墙或者代理服务器删除或伪装的情况,这种情况该头域的值以"http://" 或 "https://" 开头
- server_names, 设置一个或多个URL, 检测Referer头域的值是否是这些URL中的某一个

在需要防盗链的location中配置

```shell
valid_referers 192.168.200.130;
if ($invalid_referer) {
	return 403;
}
```

#### 使用浏览器或curl检测

安装curl

```shell
yum install -y curl
```

使用curl测试

```shell
curl -I http://192.168.200.130/static/img/hope.jpg
-I: 表示不显示完整页面,只显示响应头信息
```

带引用

```shell
curl -e "http://baidu.com" -I http://192.168.200.130/static/img/hope.jpg
```

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Nginx/curl%E6%B5%8B%E8%AF%95.png)

#### 返回错误码

```shell
worker_processes  1;
events {
    worker_connections  1024;
}
http {
    include       mime.types;
    default_type  application/octet-stream;
    sendfile        on;
    keepalive_timeout  65;
    upstream nginxs {
        server 192.168.200.102:8080 weight=2;
        server 192.168.200.103:8080 weight=1;
    }
    #vhost 01
    server {
        listen       80;
        server_name  nginx01.com;
        location / {
            rewrite ^/([0-9]+).html$ /index.jsp?pageNum=$1 redirect;
            proxy_pass http://nginxs;
        }
        location /static {
            valid_referers none 192.168.200.130;
            if ($invalid_referer) {
                return 401;
            }
            root html;
        }
        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
    }
}
```



#### 返回错误页面

Nginx01配置错误页

```shell
worker_processes  1;
events {
    worker_connections  1024;
}
http {
    include       mime.types;
    default_type  application/octet-stream;
    sendfile        on;
    keepalive_timeout  65;
    upstream nginxs {
        server 192.168.200.102:8080 weight=2;
        server 192.168.200.103:8080 weight=1;
    }
    #vhost 01
    server {
        listen       80;
        server_name  nginx01.com;
        location / {
            rewrite ^/([0-9]+).html$ /index.jsp?pageNum=$1 redirect;
            proxy_pass http://nginxs;
        }
        location /static {
            valid_referers none 192.168.200.130;
            if ($invalid_referer) {
                return 401;
            }
            root html;
        }
        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
		#配置错误页
        error_page 401 /401.html;
        location = /401.html {
            root   html;
        }
    }
}
```

返回错误页实例

/usr/local/nginx/html/401.html

```html
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8"> 
<title>Error</title>
<style>
html { color-scheme: light dark; }
body { width: 35em; margin: 0 auto;
font-family: Tahoma, Verdana, Arial, sans-serif; }
</style>
</head>
<body>
<h1>An error occurred.</h1>
<p>Sorry, the page you are looking for is currently unavailable.<br/>
Please try again later.</p>
<p>401,非法请求</p>
<p><em>Faithfully yours, nginx.</em></p>
</body>
</html>

```

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Nginx/401.png)

#### 整合rewrite返回报错图片

```shell
worker_processes  1;
events {
    worker_connections  1024;
}
http {
    include       mime.types;
    default_type  application/octet-stream;
    sendfile        on;
    keepalive_timeout  65;
    upstream nginxs {
        server 192.168.200.102:8080 weight=2;
        server 192.168.200.103:8080 weight=1;
    }
    #vhost 01
    server {
        listen       80;
        server_name  nginx01.com;
        location / {
            rewrite ^/([0-9]+).html$ /index.jsp?pageNum=$1 redirect;
            proxy_pass http://nginxs;
        }
        location /static {
            valid_referers none 192.168.200.130;
            if ($invalid_referer) {
                rewrite ^/     /x.png break;
            }
            root html;
        }
        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
        error_page 401 /401.html;
        location = /401.html {
            root   html;
        }
    }
}
```

### 高可用配置

#### 高可用场景及解决方案

使用keepalived将虚拟ip在主机和备机上漂移

keepalived通过检测keepalived进程是否正常运行来判断是否漂移虚拟ip

所以当主机正常运行,但是nginx服务器出问题时,keepalived并不知道

解决方式: 通过脚本来检测nginx是否正常运行,如果nginx不能正常运行,则kill掉keepalived进程,那么虚拟ip vip 就漂移到备用机上了

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Nginx/%E9%AB%98%E5%8F%AF%E7%94%A8.png)

#### 安装keepalived

准备好主机和备用机

第一步,两台机器安装keepalived

```shell
yum install -y keepalived
```

第二步,分别编写主机和备用机的配置文件

```shell
vim /etc/keepalived/keepalived.conf
#主机的配置文件
! Configuration File for keepalived
global_defs {
   router_id Nginx01
}
vrrp_instance VI_1 {
    state MASTER
    interface ens33
    virtual_router_id 51
    priority 100
    advert_int 1
    authentication {
        auth_type PASS
        auth_pass 1111
    }
    virtual_ipaddress {
        192.168.200.200
    }
}
#备用机的配置文件
! Configuration File for keepalived
global_defs {
   router_id Nginx01_backup
}
vrrp_instance VI_1 {
    state BACKUP
    interface ens33
    virtual_router_id 51
    priority 50
    advert_int 1
    authentication {
        auth_type PASS
        auth_pass 1111
    }
    virtual_ipaddress {
        192.168.200.200
    }
}
```

第三步,查看keepalived的运行状态

```shell
systemctl status keepalived
#查看机器的ip地址和虚拟ip
ip addr
```

第四步,测试

cmd窗口下

```shell
ping -t 192.168.200.200
```

然后down掉主机,观察ping的情况

#### 选举方式

根据keepalived配置文件中的优先级参数来判断, 两台机器选举,优先级高的当选

### Https证书配置

#### 不安全的Http协议

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Nginx/http%E5%8D%8F%E8%AE%AE.png)

#### 非对称加密算法原理

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Nginx/%E9%9D%9E%E5%AF%B9%E7%A7%B0%E5%8A%A0%E5%AF%86%E7%AE%97%E6%B3%95.png)

#### 同样不安全的非对称加密算法

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Nginx/%E5%90%8C%E6%A0%B7%E4%B8%8D%E5%AE%89%E5%85%A8%E7%9A%84%E9%9D%9E%E5%AF%B9%E7%A7%B0%E5%8A%A0%E5%AF%86%E7%AE%97%E6%B3%95.png)

#### Https原理

- CA机构
- 证书
- 客户端(浏览器)
- 服务器端

![](https://gcore.jsdelivr.net/gh/Nidihanwango/PicGo/img/Nginx/ca%E6%9C%BA%E6%9E%84%E5%8F%82%E4%B8%8E%E4%BF%9D%E8%AF%81%E4%BA%92%E8%81%94%E7%BD%91%E5%AE%89%E5%85%A8.png)

#### 证书自签名

自签名

OpenSSL

图形化工具XCA

下载地址

​	https://www.hohnstaedt.de/xca/index.php/download

#### 在线证书申请

一键安装集成环境网站

oneinstack.com

参考时评

#### 证书安装

```
server {
	listen 443 ssl;
	server_name aa.abc.com;
	ssl_certificate /data/cert/server.crt;
	ssl_certificate_key /data/cert/server.key;
}
```

