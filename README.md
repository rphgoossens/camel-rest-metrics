# camel-rest-metrics
Camel REST API exposing metrics


## Dependencies
The application has several dependencies to other systems. At the moment it depends on Artemis and, if you use 
hazelcast database persistence, a mysql datbase. Both dependencies can be resolved using Docker instances

### Running Artemis
docker run -p 61616:61616 -p 8161:8161 --detach -e ARTEMIS_USERNAME=admin -e ARTEMIS_PASSWORD=admin vromero/activemq-artemis

### Running a database (for the BeerMapDBStore)
docker run --name=hc-mysql --detach --env="MYSQL_ROOT_PASSWORD=root" --env="MYSQL_USER=hc_admin" --env="MYSQL_PASSWORD=password" --env="MYSQL_DATABASE=hazelcast" -p 3306:3306 mysql
