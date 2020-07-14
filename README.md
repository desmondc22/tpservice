# Transaction Processor Service

This service can receive a list of financial transactions from external sources, apply them to accounts, and ultimately report eh balance of each account.

This service exposes two HTTP APIs: Transaction API "/transactions" and Account API "/accounts".

## Built with

* [Spring](https://spring.io/) - Java framework used for the backend
* [Gradle](https://gradle.org/) - Java dependency management
* [Docker](https://docker.org/) - Running the service in a container 

###  Build and Start the server with Docker

The project has a DockerFile that takes care of running the service in a container. Execute the following commands in the home directory of the project to get the service up and running.

`
docker build -t tpservice .
`

`
docker run -p 8080:8080 docker build -t tpservice .
`

### Build and Start the server with Gradle

If there are any issues running using docker, try using Gradle.

First build this application's JAR file on a system that has both Java and Gradle installed using the commands 

`./gradlew build`

`java -jar build/libs/tpservice-0.0.1.jar`

## Developer Reference

### Transactions API

* Method: POST
* EndPoint: /transactions
* Request Body: A JSON array of transaction, with "cmd" key to identify the type of transaction. Please see the command reference below parameters for each type of transactions. ( [Example of transactions](https://github.com/desmondc22/tpservice/blob/master/transactions.json) in JSON)
* Response: A JSON array of failed and invalid transactions.

Deposit

| Method | EndPoint            | Parameters              | Type | Description                                           |
|--------|---------------------|-------------------------|------|-------------------------------------------------------|
| POST   | /notifications      | cmd                     |String| "DEPOSIT"                                             |
|        |                     | accountId               |String| The account id to deposit money into                  |

Withdraw

| Method | EndPoint            | Parameters              | Type | Description                                           |
|--------|---------------------|-------------------------|------|-------------------------------------------------------|
| POST   | /notifications      | cmd                     |String| "WITHDRAW"                                            |
|        |                     | accountId               |String| The account id to withdraw money from                 |
|        |                     | amount                  |Double| The amount in dollars & cents to remove to the account|

Freeze

| Method | EndPoint            | Parameters              | Type | Description                                           |
|--------|---------------------|-------------------------|------|-------------------------------------------------------|
| POST   | /notifications      | cmd                     |String| "FREEZE"                                              |
|        |                     | accountId               |String| The account id to deposit money into                  |

Thaw

| Method | EndPoint            | Parameters              | Type | Description                                           |
|--------|---------------------|-------------------------|------|-------------------------------------------------------|
| POST   | /notifications      | cmd                     |String| "THAW"                                                |
|        |                     | accountId               |String| The account id to deposit money into                  |

Xfer

| Method | EndPoint            | Parameters              | Type | Description                                           |
|--------|---------------------|-------------------------|------|-------------------------------------------------------|
| POST   | /notifications      | cmd                     |String| "XFER"                                                |
|        |                     | fromId                  |String| The account id to transfer money from                 |
|        |                     | toId                    |String| The account id to transfer money into                 |
|        |                     | amount                  |Double| The amount to remove from fromId and add to toId      |

### Account API

* Method: GET
* EndPoint: /accounts
* Request Body: Lists of accountIds (at least one is required)
* Response: A JSON array of accounts.

### Controllers

TransactionsController

Transactions Controller exposes REST API to take a JSON array of transactions, with basic validation to ensure the JSON format is correct. The controller will convert the JSON Objects to Java Objects for Service.

AccountsController

Accounts Controller exposes REST API to take a JSON array of accounts, with basic validation to ensure the JSON has an accountId and call on Service to get the account information.

### Services

TransactionsService

Transaction Service processes the list of Transactions Objects from Controller and execute base on the command "cmd". The service handle rollback if required in the transaction execution and return the all the failed transactions. Failed transactions would be skipped and would not affect other transactions.

AccountsService

Accounts Service execute all the individual account operations: Deposit, Withdraw, Freeze, and Thaw. It also create new accounts if a new accountId is found in any of the execution, with a default balance of 0.

### Models

* Account
* Transactions
    * Deposit
    * Freeze
    * Thaw
    * Withdraw
    * Xfer (Transfer)

## Tests

Unit test are in "src/test/java/". 

Run the JUnit test using the command 

`
 ./gradlew test 
`

Summary of Tests

* Create new Account 
* Deposit amount to account
* Freeze Account
* Thaw Account
* Deposit amount into a frozen account
* Deposit invalid amount into account
* Withdraw amount from account
* Withdraw amount from a frozen account
* Withdraw invalid amount from account
* Transfer from one account to another 
* Transfer from one account to another with invalid amount
* Batch transactions with all transaction type
* Validity of transaction with valid attributes 
* Validity of transaction with invalid attributes 
* Retrieve the balance of an existing account
* Retrieve the balance of an unknown account