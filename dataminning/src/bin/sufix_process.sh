
hadoop fs -rmr /kdd/pfp/input
hadoop jar  dataminning-jar-with-dependencies.jar com.easyminning.etl.mahout.lda.ParseLDAJob --input /kdd/lda/doc  --output /kdd/pfp/input --matrix /kdd/lda/matrix-out/docIndex  --topicK 100 --topn 75 --topicPath /kdd/lda/topic_map/topic.txt


