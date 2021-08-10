# Retail order processing API. [DOC](http://localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config)


### * API to post retail order request.

  1. Async Order requests are pushed to Kafka order message queue and consumed for processing.
  2. Each order is inserted into Mongo(NoSQL) database with the status of each order updated uniquely by processor.
  3. Customer access to api is secured through JWT token generted through login username and password.

### * API to get each Order status request.

  1. Customer can fetch the status of there order.
  2. Order can be any status like, PLACED, PROCESSED, SHIPPED, FAILED or COMPLETED.

### * [Order Processor](https://github.com/AnnapoorniS/orderprocessor)

  1. Listens for orders in Kafka order message queue to process.
  2. Update the process status in the Order (Mongo) Database.
