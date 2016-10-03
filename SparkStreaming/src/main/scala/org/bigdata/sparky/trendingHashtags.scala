package org.bigdata.sparky

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.streaming.twitter._
import twitter4j.Status
    
object trendingHashtags {
  def main(args: Array[String]): Unit = {

      val consumerKey = "YOUR CONSUMER KEY"
      val consumerSecret = "YOUR CONSUMER SECRET"
      val accessToken= "YOUR ACCESS TOKEN"
      val accessTokenSecret = "YOUR ACCESS TOKEN SECRET"

      System.setProperty("twitter4j.oauth.consumerKey", consumerKey)
      System.setProperty("twitter4j.oauth.consumerSecret", consumerSecret)
      System.setProperty("twitter4j.oauth.accessToken", accessToken)
      System.setProperty("twitter4j.oauth.accessTokenSecret", accessTokenSecret)
      
        
      val batchInterval = 10
      val sc = new SparkContext(new SparkConf().setAppName("TrendingHashTags"))   
      val ssc = new StreamingContext(sc, Seconds(batchInterval.toInt))
      ssc.checkpoint("hdfs:///tmp/trendingHashtags")
      
      val tweets = TwitterUtils.createStream(ssc, None)
      val tweetContents = tweets.map{ tweet => tweet.getText()}
      val words = tweetContents.flatMap{content => content.split("""\s+""")}
      val hashTags = words.filter{word => word.startsWith("#")}
    

      val hashTagPairs = hashTags.map{hashtag => (hashtag, 1)}
      val tagsWithCounts = hashTagPairs.updateStateByKey(
          (counts: Seq[Int], prevCount: Option[Int]) =>
          prevCount.map{c => c + counts.sum}.orElse{Some(counts.sum)}
      )        
        
      val minThreshold = 10
      val topHashTags = tagsWithCounts.filter{ case(t, c) =>
        c > minThreshold.toInt
      }

      val sortedTopHashTags = topHashTags.transform{ rdd =>
        rdd.sortBy({case(w, c) => c}, false)
      }

      val showCount = 10
      sortedTopHashTags.print(showCount.toInt)

      sortedTopHashTags.foreachRDD((rdd) => {
        val data = rdd.take(showCount)
        val httpSender = new HttpSender()
        httpSender.SendData(data)
      })

      ssc.start()
      ssc.awaitTermination()
    }
}