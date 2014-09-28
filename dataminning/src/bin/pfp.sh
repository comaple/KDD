

hadoop fs -rmr /kdd/pfp/result
#hadoop jar dataminning-jar-pfp-job.jar -i /kdd/pfp/input -o /kdd/pfp/result/ -k 50 -method mapreduce -regex '[,]' -s 5 --numGroups 160 
#hadoop jar /data/apps/mahout-extend/examples/target/mahout-examples-0.9-SNAPSHOT-job.jar \
#org.apache.mahout.driver.MahoutDriver \
#com.easyminning.algorithm.fpm.pfpgrowth.FPGrowthDriver \
#hadoop jar dataminning-jar-with-dependencies.jar \
mahout com.easyminning.algorithm.fpm.pfpgrowth.FPGrowthDriver \
-i /kdd/pfp/input -o /kdd/pfp/result/ -k 50 -method mapreduce -regex '[,]' -s 5 --numGroups 1000 \
-Dmapred.reduce.tasks=20 -Dmapred.map.child.java.opts=-Xmx8192m  -Dmapred.reduce.child.java.opts=-Xmx8192m

 

