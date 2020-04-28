# Dynamic Twitter Command

Dynamic Twitter Command is an application of dynamic search of tweets using Kafka to produce data.

Doing a search using the Twitter API based on the topics informed on the endpoints. These endpoints are asynchronous, so you are going to get a response while KAFKA produces data from twitter based on the topic.
A Future Consumer will read all the data sent to the broker and save the bulk into ElasticSearch 

## Technologies
- Java 11
- Springboot
- Kafka
- MongoDB
- Twitter HBC

## Swagger 

```bash
http://localhost:8181/dynamic/swagger-ui.html
```

