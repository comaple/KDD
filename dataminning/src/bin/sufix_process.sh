
if [ $# != 0 ];then
topic=$1
else
topic=100
fi

hadoop fs -rmr /kdd/pfp/input
hadoop jar  dataminning-jar-with-dependencies.jar com.easyminning.etl.mahout.lda.ParseLDAJob --input /kdd/lda/topic  --output /kdd/pfp/input --matrix /kdd/lda/matrix-out/docIndex  --topicK $topic --topn 75 --topicPath /kdd/lda/topic_map/topic.txt


