# Word count example with seq integration
In this example I will be going through the famous word count example but I need to have the logs sent to seq.

[Seq](https://getseq.net/) is a popular loggin tool in the .net world and it makes it very easy to make use of application logs in a centralised way.

First of all I needed to get my hands dirty with Hadoop and do some real work.
You do not really know about it unless you make mistakes and solve them.
So, other than getting familiar with Haddop I just wanted to add a small piece of .net flavor.
Hadoop MapReduce has its own logging feature and it even supports aggregating logs from all nodes into HDFS.

To summarise, my objective is to get familiar of the MapReduce world and extend it with structured logs by seq.

[Seq Logs][seq1.png]

##Setup 