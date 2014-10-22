% 让Lombok跟Maven和IntelliJ完美的工作在一起
% 王福强

# Lombok和Maven

通过[lombok-maven-plugin](http://awhitford.github.io/lombok.maven/lombok-maven-plugin/index.html)， 我们可以让这两者工作在一起：


~~~~~~~ {.xml}
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>test</groupId>
    <artifactId>test</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <compiler.version>1.6</compiler.version>
        <java_source_version>1.6</java_source_version>
        <java_target_version>1.6</java_target_version>
        <file_encoding>UTF-8</file_encoding>
    </properties>

    <build>
        <plugins>
            <!-- Java Compiler -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <compilerVersion>${compiler.version}</compilerVersion>
                    <source>${java_source_version}</source>
                    <target>${java_target_version}</target>
                    <encoding>${file_encoding}</encoding>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-source-plugin</artifactId>
                <executions>
                    <execution>
                        <id>attach-sources</id>
                        <goals>
                            <goal>jar</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-javadoc-plugin</artifactId>
                <configuration>
                    <charset>${file_encoding}</charset>
                    <encoding>${file_encoding}</encoding>
                </configuration>
                <executions>
                    <execution>
                        <id>attach-javadocs</id>
                        <goals>
                            <goal>jar</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok-maven-plugin</artifactId>
                <version>1.14.8.0</version>
                <configuration>
                    <encoding>${file_encoding}</encoding>
                </configuration>
                <executions>
                    <execution>
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>delombok</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
    <profiles>
        <profile>
            <id>lombok-needs-tools-jar</id>
            <activation>
                <file>
                    <exists>${java.home}/../lib/tools.jar</exists>
                </file>
            </activation>
            <build>
                <plugins>
                    <plugin>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok-maven-plugin</artifactId>
                        <version>1.14.8.0</version>
                        <dependencies>
                            <dependency>
                                <groupId>sun.jdk</groupId>
                                <artifactId>tools</artifactId>
                                <version>1.6</version>
                                <scope>system</scope>
                                <systemPath>${java.home}/../lib/tools.jar</systemPath>
                            </dependency>
                        </dependencies>
                    </plugin>
                </plugins>
            </build>
        </profile>
    </profiles>
    <dependencies>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.14.8</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>

</project>
~~~~~~~

配置profile的目的是为了解决非Mac系统下lombok依赖jdk的tools.jar的问题。

# IntelliJ的配置

首先，[这里](https://code.google.com/p/lombok-intellij-plugin/)有一个intellij的插件， 装上！ 但它只负责通过inspect字节码来提供编辑器内代码语法的高亮和提示等功能， 而lombok得以生效的机制确实需要首先执行annotation processor，才会在字节码中生成那些辅助方法， 所以，即使我们已经配置了lombok-maven-plugin， 要想使用到这个intellij插件的好处，还是要每次都`mvn compile`一下 ^[或者执行maven的generate-sources之后任何一个phase]，这显然不胜烦琐，不过，别急，稍微设置一下intellij就可以了。

进intellij的Preferences， 选择Compiler设置， 然后再展开子树，选择Annotation Processors， 勾选“Enable annotation processing” ^[Obtain processors from project classpath默认选择了], OK保存退出， 开始爽吧！







