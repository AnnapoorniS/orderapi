# Retail order processing API.


* API to post retail order request.

1. Async Order requests are pushed to Kafka message queue and consumed for processing.
2. Each order is inserted into Mongo(NoSQL) database with the status of each order updated uniquely by processor.
3. Customer access to api is secured through JWT token generted through login username and password.

* API to get each Order status request.

1. Customer can fetch the status of there order.
2. Order can be any status like, PLACED, PROCESSED, SHIPPED, FAILED or COMPLETED.
