# Maven 笔记

## Why

- 工程依赖的jar包很多，jar包之间相互依赖，不好管理，为简化开发，使用Maven来管理jar包

## What

- 自动化构建工具

## How

### First

- 首先，Maven项目具有一个固定的结构目录，这是约定俗成的，在编程中，约定大于配置，配置大于代码，能用约定实现的不用配置文件，能用配置文件实现的，不用代码
- 在Maven项目中有一个pom.xml文件，这是Maven的配置文件

### 常用的Maven命令

在pom.xml的目录下cmd使用

- mvn clean
- mvn compile
- mvn packaging
- mvn install
- 等等...

### Maven生命周期

- 抽象方法，需要依赖插件进行使用
- 执行顺序后面的方法执行时，会先调用顺序靠前的方法

### Maven的坐标

groupId，artifactId，version

这三个坐标类似数学中的空间坐标系的xyz坐标，通过三个坐标在仓库中定位一个jar包的位置，通过 dependencies，dependency标签和坐标来引入所需要的依赖

### Maven仓库

#### 本地仓库 

用于服务本地的所有Maven项目

#### 私服

架设在局域网上，服务于局域网中所有的Maven项目，如果私服中没有需要的jar包，私服会去中央仓库或者中央仓库镜像下载，然后保存在私服中

#### 中央仓库

在因特网上的仓库

#### 中央仓库镜像

为了分担中央仓库的流量，减轻中央仓库的压力，建设的仓库

### 寻找jar包依赖的网站

mavenrepository.com

### 可以在properties中定义依赖的版本以及其他配置

```java
<properties>
	<maven.compiler.source>8</maven.compiler.source>
	<maven.compiler.target>8</maven.compiler.target>
	<project.build.sourceEncoding>
		UTF-8
	</project.build.sourceEncoding>
	<log4j.version>1.2.14</log4j.version>
</properties>
<dependencies>
	<dependency>
		<groupId>log4j</groupId>
		<artifactId>log4j</artifactId>
		<version>${log4j.version}</version>
	</dependency>
</dependencies>
```

### 依赖传递

如果A依赖B，且A中的依赖scope为compile或者runtime，那么A中的依赖可以传递给B

### 继承

- 在pom.xml文件中添加<parent>标签，可以实现继承
- 在父工程中可以实现统一的依赖版本管理
- <packaging>pom</packaging> // 注意父工程打包方式

```java
<modules> // 聚合 子工程的artifactId添加到父工程的modules中
	<module>son1</module>
	<module>son2</module>
</modules>
<properties>
	<maven.compiler.source>8</maven.compiler.source>
	<maven.compiler.target>8</maven.compiler.target>
	<junit-version>4.12</junit-version>
</properties>
<dependencyManagement>
	<dependencies>
		<dependency>
			<groupId>junit</groupId>
			<artifactId>junit</artifactId>
			<version>${junit-version}</version>
		</dependency>
	</dependencies>
</dependencyManagement>


```





