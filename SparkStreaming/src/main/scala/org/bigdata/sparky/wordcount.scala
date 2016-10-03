package org.bigdata.sparky

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._

object wordcount {

    def main(args: Array[String]) {
        val conf = new SparkConf().setAppName("WordCountApp")

        val sc = new SparkContext(conf)
        val input = sc.parallelize(List("The difference between stupidity and genius is that genius has its limits", "We cannot solve our problems with the same thinking we used when we created them"))
        val words = input.flatMap(line => line.split(" "))
        val counts = words.map(word => (word, 1)).reduceByKey{case (x, y) => x + y}
        counts.collect().foreach(println)
    } 

}   