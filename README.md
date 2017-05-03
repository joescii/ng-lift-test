Project replicating issue seen when using Futures via defFutureAny

Project is a maven project, to run execute `mvn jetty:run` and point browser at http://localhost:8081

As seen in terminal out and via web page processing of non future and future service generate different output

Non Future Output:
Data: [{"name":"a","age":1},{"name":"b","age":2}]
failData: forced failure *(expected)*

Future Based Output:
FutureData: {"value":[{"name":"aa","age":11},{"name":"bb","age":22}]} *(value field not expected)*
futureFailData is undefined which is not expected
Oops2 has return structure which is not expected, should be null. This implies Future(Failure) is being processed as valid data in js controller.