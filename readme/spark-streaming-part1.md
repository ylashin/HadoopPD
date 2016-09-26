# 1. Preparing Spark Environment

[Apache Spark](http://spark.apache.org/docs/1.6.2/index.html) is a fast and general-purpose cluster computing system. 
It provides high-level APIs in Java, Scala, Python and R, and an optimized engine that supports general execution graphs. 
It also supports a rich set of higher-level tools including Spark SQL for SQL and structured data processing, MLlib for machine learning, GraphX for graph processing, and Spark Streaming.

I will be using Spark 1.6.2 although Spark 2.0 has been released and it provides a bunch of improvments but 1.6.2 should be very enough for our scenario.
Spark is a Scala application and can be installed standalone but generally speaking it will be bundled in a Hadoop installation.
I will use Hortonworks Sandbox becuase it is easy to use, contains other bits installed like HDFS/Zeppelin and comes with Ambari to view and monitor the whole cluster. 

##Installing Hortonworks Sandbox

1. Download Hortonworks HDP 2.5 sandbox from [Hortonworks sandbox downloads](https://cran.r-project.org/mirrors.html).
    * Sandbox can be used with VirtualBox/Vmware/Docker.
    * I will use VirtualBox in this demo but you are free to pick another type of virtualization you like.
    * Sandbox is more than 10GB size so prepare for a bit of waiting if you do not have fast connection.
2. Once downloaded, double click the **.ova** file downloaded and follow the VirtualBox wizard to import the VM.
3. The VM will be imported with some default settings but I recommend to tweak the following settings if you have some HW capacity:
    * CPU : verify it has minimum 4 CPUs.
    * Memory : 8GB should be good but you can add more if you are going deep with other stuff like machine learning. 
    * Display : Give it 128MB of video memory as we will be installing a linux Desktop later.
4. Start the VM and you should be getting the below:

  ![sandbox-started](../images/spark-streaming-01-vm-console.png)

## Verify Hadoop installation

Once the sandbox is started you can try the below links in the browser to make sure it is running as expected:

1. [Sandbox dashboard page](http://localhost:8888) will give you access to useful links, documentation and tutorials.
2. [Ambari](http://localhost:8080/#/login) can be used to navigate Hadoop cluster components and do some stuff like upload files to HDFS and start/stop services.
    * You can use user and password **maria_dev/maria_dev** as a first step although sandbox dashboard page provides documentation for a bunch of other user types.
    * You can also [reset ambari admin password](http://hortonworks.com/hadoop-tutorial/learning-the-ropes-of-the-hortonworks-sandbox/#setup-ambari-admin-password) in case you need to need full control on stuff like hadoop service settings.

    ![Ambari home page](../images/spark-streaming-01-ambari.png)

3. [Zeppelin](http://localhost:9995/#/) is a web-based notebook that enables interactive data analytics. We can use it to verify Spark instllation in the sandbox works fine.
    * Click on the above link to open Zeppelin home page.
    * Click on the link **Create new note** to open a new blank notbook where we will be writing some basic Spark program. Provide any name for the new notebook.

    ![Zeppelin](../images/spark-streaming-01-zeppelin.png)
    
    * In the new notebook write down the below snippt of code which basically does a simple word count for two lines of Albert Einstein quotes.

    ```
    val input = sc.parallelize(List("The difference between stupidity and genius is that genius has its limits", "We cannot solve our problems with the same thinking we used when we created them"))
    val words = input.flatMap(line => line.split(" "))
    val counts = words.map(word => (word, 1)).reduceByKey{case (x, y) => x + y}
    counts.collect().foreach(println)
    ```

    * The above simply creates an RDD (RDD is an array of objects) of strings, splits them into an RDD of words. Think of .NET linq SelectMany
    * Then words array is mapped to a tuple of (word,1) and reduced to a tuple of the word value and how many times it appeared.
    * Click the run button for the current paragraph or the whole notebook and you should get the below meaning Spark works as expected

    ![Zeppelin](../images/spark-streaming-01-spark-word-count.png)

    * Close Zeppelin tab in the browser as it might interfer with Spark program we will be running later.
    * For most realistic use cases, Spark programs will be written in Scala/Java/Python and run on the shell using **spark-submit** command.

## Install desktop system

1. Sandbox comes with no GUI where Hadoop cluster is installed as a Docker container.
2. Go to your VM running within VirtualBox and hit **Alt F5** (on Windows) to login to linux shell.
3. Use the credentials root/hadoop to login.
4. Follow instructions in this [page](http://www.itzgeek.com/how-tos/linux/centos-how-tos/install-gnome-gui-on-centos-7-rhel-7.html) to install Gnome desktop but use the commands for **Cent OS** as this is the OS for our VM.
5. Shut down the VM using the command **shutdown now**
6. Start the VM from VirtualBox and you should get a GUI login page as below
    ![GENOME](../images/spark-streaming-01-GENOME.png)
7. Click **Finish Configuration** button and after a while you will get a login screen with a pre-selected user name **packer**.
8. Click **not selected** link to login as another user and login with our lovely root/hadoop account.
9. Follow the wizard to choose some OS settings like language and keyboard preferences.

    ![GENOME](../images/spark-streaming-01-GENOME-logged-in.png)

**Side Note** : You can configure VirtualBox guest additions on the VM to allow stuff like copy/paste from the host OS or to allow running the VM in full screen mode. The VM comes with no CD configured so you will need to add a SCSI controller with empty CD drive.

## Install Scala IDE
1. We will be using Scala to develop our streaming application so we need an IDE. Scala is a functional language running on JVM and it is the defacto standard for Spark applications.
2. Open Firefox **inside the VM** and download Scala IDE from [this page](http://scala-ide.org/download/sdk.html) 
3. Pick the link for 64bit linux.
4. Download the tar file and extract it to some folder of your choice.
5. Once extracted, there will be a folder called Eclipse containing an executable with the same name.
6. Right click that executable and select **Make link** then copy the new link file to desktop.
7. You can rename the new link to Scala IDE.
8. Double click the new link to Open Scala IDE.
9. You will be asked to select a workspace location. You are free to change it or proceed with the default */root/workspace* location.


Phew, you are ready now to jump into the big data ocean.


## End of Part 1

You now have a VM with Spark installation & Scala IDE, and ready to get started. Proceed to [Word count in Scala](spark-streaming-part2.md)
