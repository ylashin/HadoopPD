package org.bigdata.sparky

import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient

class HttpSender()
{
    def SendData(tuples: Array[(String,Int)])
    {   
        // convert it to a JSON string
        val items = tuples.map(a => "{\"Hashtag\" :\"" + a._1 + "\",\"Count\":" + a._2 +"}")    
        val json = "[" + items.mkString(",") + "]"    
        // create an HttpPost object - REPLACE WITH YOUR OWN KEY
        val post = new HttpPost("PUT YOUR POWER BI STREAMING API URL")  
        // set the Content-type
        post.setHeader("Content-type", "application/json")  
        // add the JSON as a StringEntity, UTF-8 is important for non ASCII tweets
            post.setEntity(new StringEntity(json, "UTF-8")) 
        // send the post request
        val response = (new DefaultHttpClient).execute(post) 

    }
}