xd-utils
===================

Repository with various Spring XD utilities 

* job-status - groovy script used to parse Job Status from an job listener Tap
* nulljob - this job does nothing and fails every other run. If you launch with shouldFail parameter it always fails.

## Build and Installation

Set the environment variable `XD_HOME` to the Spring-XD installation directory

    export XD_HOME=<root-install-dir>/spring-xd/xd

create nulljob job module folders:

    mkdir ${XD_HOME}/modules/job/nulljob
    mkdir ${XD_HOME}/modules/job/nulljob/lib
    mkdir ${XD_HOME}/modules/job/nulljob/config
  
Build the Jdbc job jar:

    mvn clean package

Copy the result `xdutils-0.0.1-SNAPSHOT.jar` into `${XD_HOME}/modules/job/nulljob/lib`  
    
    cp target/xxdutils-0.0.1-SNAPSHOT.jar `${XD_HOME}/modules/job/nulljob/lib`

Copy the `nulljob.xml` module definition into `${XD_HOME}/modules/job/nulljob/config` 

    cp src/main/resources/nulljob/* `${XD_HOME}/modules/job/nulljob/config`

Copy the scripts `src/main/resources/scripts/*.groovy` into `${XD_HOME}/modules/processor/scripts`  
    
    cp src/main/resources/*.groovy `${XD_HOME}/modules/processor/scripts`

## Usage

#### nulljob

    xd>job create j1 --definition "nulljob --listener=job" --deploy 

##### job-status script      

    xd>job create j1-to-j1 --definition "tap:job:j1.job > script --location=job-status.groovy > queue:job:j1" --deploy 
  
  
 
