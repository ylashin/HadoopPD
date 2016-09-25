# Spark streaming to a Power BI dashboard

## Overview

This example will show how to connect streaming data aggregated using Apache Spark to a Power BI dashboard.
Spark is a general purpose data processing engine and it has specific libraries for machine learning, streaming and graph processing as well.
I will be using Spark Streaming to stream tweets, do some basic aggregation and push the results to Power BI.
So the overall objective is to do some basic **realtime** Power BI dashboard showing trending hashtags in twitter at the moment.

The final output should be as below:

<TODO - image>


1. [Preparing Spark Environment](spark-streaming-part1.md)
2. [Work count in Scala](spark-streaming-part2.md)
3. [Streaming tweets to console](spark-streaming-part3.md)
4. [Configrue Power Bi Dashbaord with streaming dataset](spark-streaming-part4.md)
5. [Connecting Spark aggregated data to Power BI](spark-streaming-part5.md)


## Other Resources:

* [Spark Overview](http://spark.apache.org/docs/1.6.2/index.html)
* [Pluralsight course - Apache Spark Fundamentals](https://www.pluralsight.com/courses/apache-spark-fundamentals)
* [Building a Real-time IoT Dashboard with Power BI: A Step-by-Step Tutorial](https://powerbi.microsoft.com/en-us/blog/using-power-bi-real-time-dashboards-to-display-iot-sensor-data-a-step-by-step-tutorial/)
* [Book - Learning Spark](http://shop.oreilly.com/product/0636920028512.do)
