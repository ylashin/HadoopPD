package WordCount;

import static serilogj.sinks.coloredconsole.ColoredConsoleSinkConfigurator.coloredConsole;
import static serilogj.sinks.rollingfile.RollingFileSinkConfigurator.rollingFile;
import static serilogj.sinks.seq.SeqSinkConfigurator.seq;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.function.BiConsumer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import serilogj.ILogger;
import serilogj.Log;
import serilogj.LoggerConfiguration;
import serilogj.events.LogEventLevel;

public class Counter {

  public static class TokenizerMapper
       extends Mapper<Object, Text, Text, IntWritable>{

    private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();

    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
      StringTokenizer itr = new StringTokenizer(value.toString());
      while (itr.hasMoreTokens()) {
        word.set(itr.nextToken());
        context.write(word, one);
      }
    }
  }

  public static class IntSumReducer
       extends Reducer<Text,IntWritable,Text,IntWritable> {
	  
    private IntWritable result = new IntWritable();
    
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
    public void reduce(Text key, Iterable<IntWritable> values,
                       Context context
                       ) throws IOException, InterruptedException {  
    	int sum = 0;	    
	    for (IntWritable val : values) {		  
	      sum += val.get();	     
	    }
	    result.set(sum);
	    context.write(key, result);
	    
	    keys.add(key.toString());
	    times.add(Instant.now());
    }
  }

  public static void main(String[] args) throws Exception {	
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "word count");
    job.setJarByClass(Counter.class);
    job.setMapperClass(TokenizerMapper.class);
    job.setCombinerClass(IntSumReducer.class);
    job.setReducerClass(IntSumReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}