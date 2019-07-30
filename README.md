# Consul Microservices Simulator
A simple registration simulator for [Consul](https://www.consul.io/) discovery service

### Download

Install [git](http://git-scm.com/)

```
git clone git://github.com/ggdio/consul-microservices-simulator.git
```

..or you can download [sources as a zip](https://github.com/ggdio/consul-microservices-simulator/archive/master.zip)

### Building and Running

Install [Maven](http://maven.apache.org/)
Install [Consul](https://www.consul.io/)


*//start consul*
<p>

```bash
nohup $CONSUL_HOME/consul agent -dev &
```

*// build simulator and run*

```bash
mvn clean package assembly:assembly
cd target/
java -jar consul-microservices-simulator-1.0-jar-with-dependencies.jar
```

*// use menu to interact with the simulator*


## License

* [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)
