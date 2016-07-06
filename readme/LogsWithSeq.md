# Word count example with seq integration
In this example I will be going through the famous word count example but I need to have the logs sent to seq.

[Seq](https://getseq.net/) is a popular logging tool in the .net world and it makes it very easy to make use of application logs in a centralised way.

First of all I needed to get my hands dirty with Hadoop and do some real work.
You do not really know about it unless you make mistakes and solve them.
So, other than getting familiar with Haddop I just wanted to add a small piece of .net flavor.
Hadoop MapReduce has its own logging feature and it even supports aggregating logs from all nodes into HDFS.

To summarise, my objective is to get familiar of the MapReduce world and extend it with structured logs by seq.

My objective is to compare mapper or reducer performance across nodes in a cluster from a central log repository to allows structured queries like seq.

So, it would be nice to issue query to find the slowest node doing mapping/reducing in case a job is slow beyond normal.
The root cause can be anything from a hardware issue to a data pattern and hadoop itself might try to help and re-schdule the work on another node but it is also nice to have other clues.


![Seq Logs](../images/seq1.png)

##Initial Setup
The setup here is simple. We need to grab Horton Works sandbox. It is available for VMware and VirtualBox.
You can get it from : http://hortonworks.com/downloads/#sandbox

After you extract the VM file into your preferred VM tool, you can play with the installed Hadoop copy either using the Shell or via a web console.

The VM OS is CentOS 6.7 and it does not include a GUI so you will need to install any GUI like GNOME or KDE.

##Java version switch
The installed Hadoop copy on this VM is configured with Java 7 (1.7). There is no problem with that other than a small thing.

The java port of Serliog (libray that will allow us to write logs to seq and elsewhere) is written in java 8.
A nice guy has already did the hard work of porting the .net serilog code into java and named it [serilogj](https://github.com/80dB/serilogj).

So, our first task here is to switch hadoop from java 7 (1.7) to java 8(1.8).
I am no java expert so I just tried some googling here and there to make it happen and I do not claim this is reliable but it is enough for our example.

In brief the steps to switch to java 8 are:
+ Install java 8 from Oracle site.
+ Run the following command to select default java runtime : **alternatives --config java**
+ Update the environment variables for JAVA_HOME
   - export JAVA_HOME=**your java 8 folder**
   - export PATH=$JAVA_HOME/bin:$PATH
+ I followed [this link](http://unix.stackexchange.com/questions/110512/uninstall-jdk-rpm-to-reinstall) to uninstall java 7
+ Perform the few steps mentioned in [Horton Works docs](https://docs.hortonworks.com/HDPDocuments/Ambari-2.2.1.1/bk_ambari_reference_guide/content/ch_changing_the_jdk_version_on_an_existing_cluster.html) to switch JDK version of ambari.
+ Restart your VM and try to make sure Ambari is still alive and you can browse and view HDFS folders at least.


##Eclipse and serilogj

Next step is to install Eclipse to be able to write our basic word count sample. Please note that we are using hadoop v2 distribution in case you will be copying word count from pages for v1.
Generally speaking it should work but many package names and JARs are different so you should be prepared.

I am including full source code of the working sample in folder [LogsWithSeq](./LogsWithSeq) so this should not be a big deal.

Back to Eclipse, go to the official site and download Luna for example (we do not need high end EE things, the basic java projects will be fine).

 Next head to github and download a zipped copy of [serilogj](https://github.com/80dB/serilogj) repo.
 Extract it into the default workspace folder of Eclipse and create another java project called WordCount into same workspace.

 In the new project **WordCount**, you will need to do the following:
 + Reference JAR files for hadoop
 + Reference the serilog project

For **serilogj** project you will need to:
+ Download a bunch of dependencies from Maven repo (boon-json-0.5.7.jar,boon-reflekt-0.5.7.jar,boon-util-0.5.7.jar)
+ Add them to the project external reference JARs
+ Copy them to some class path visible folder like /usr/lib

As I mentioned before, I am no java geek so the above might not be the best way to do things but it worked for me.

Next grab some source code for Word Count canonical example on hadoop 2 from anywhere and dump it into the WordCount project.

Make sure that all projects compile and try to export both projects as a single JAR and run it in the most basic way as below:

**$ hadoop jar WordCount.jar WordCount.Counter /input-folder /output-folder**

Input and output folders are HDFS folders created using the shell (**hdfs dfs -mkdir**) or using HDFS view on ambari.
Also the input folder should contain any text file with a bunch of words. You can put anything but the classic example is to grab a novel in text format from project gutenberg.

## Integrating with seq

You will need to have seq installed somewhere accessible from your VM and probably this should be your windows host.

Next you need to modify the word count reducer class and add the following few lines:

 ```java
     private ArrayList<String> keys = new ArrayList<String>();
     private ArrayList<Instant> times = new ArrayList<Instant>();

     @Override
     protected void cleanup(org.apache.hadoop.mapreduce.Reducer<Text,IntWritable,Text,IntWritable>.Context context) throws IOException ,InterruptedException
     {
     	try
     	{
     	    Collections.sort(times);
             Instant first = times.get(0);
             Instant last = times.get(times.size()-1);
             Duration duration = Duration.between(first, last);
     	    StringBuilder sb = new StringBuilder();
     	    for(String key:keys)
     	    	sb.append(key + ",");
     	    ILogger logger = new LoggerConfiguration().writeTo(seq("http://192.168.1.6:5341/")).setMinimumLevel(LogEventLevel.Verbose)
         			.createLogger();
     	    logger.information("Reduced : {duration} in milliseconds" ,  duration.toMillis());
     	    logger.information("Reduced : {keys}" ,  sb.toString());
     	}
     	catch(Exception ex)
     	{
     		System.out.println("Cleanup exception:" + ex.toString());
     	}
     }
 ```

The above will just declare some variables to collect the keys fed to the reducer and the times at which those keys are fed.

We are overriding the cleanup method that is called after the reducer is done to log the keys processed and duration it to the reducer to process all of them.

I know that this is big data and it is probably bad idea to log all keys (they will not be in millions per reducer) but this is fine for our naive scenario here.

You can see that we hardcoded a URL for seq server but this can be sent as a parameter to the job in the command line or so.


The reduce method is also updated slightly to collect keys and timestamps into the member variables above

```java
public void reduce(Text key, Iterable<IntWritable> values,Context context) throws IOException, InterruptedException
{
    int sum = 0;
    for (IntWritable val : values) {
      sum += val.get();
    }
    result.set(sum);
    context.write(key, result);

    keys.add(key.toString());
    times.add(Instant.now());
}
```

After compiling and exporting the new JAR and using runnign a new job for a sample book like Wizard of Oz we will get something like the below

You can expand any line to see more details and you can use seq features to filter for reducers with slow processing times.

**Rough edges ahead!!** this is just a quick POC plus the serilogj library is pretty fresh.

![Seq Logs](../images/durations.png)

![Seq Logs](../images/keys.png)


