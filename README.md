Project replicating issue seen when using Futures via defFutureAny

Project is a maven project, to run execute `mvn jetty:run` and point browser at http://localhost:8081

As seen in terminal out and via web page processing of non future and future service generate different output

Non Future Output:
Data: [{"name":"a","age":1},{"name":"b","age":2}]

Future Based Output:
FutureData: {"value":[{"name":"aa","age":11},{"name":"bb","age":22}]}