hadoop fs -rmr /kdd/test10
hadoop jar dataminning-jar-with-dependencies.jar  com.easyminning.aprio.AprioJob  -i /kdd/pfp/input -o /kdd/test10
# -Dmapred.reduce.tasks=4 -Dmapred.reduce.tasks=10
