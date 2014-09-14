
hadoop fs -rmr /kdd/pfp/input
hadoop jar  dataminning-jar-with-dependencies.jar --input /kdd/lda/doc  --output /kdd/pfp/input --matrix /kdd/lda/matrix-out/docIndex  --topicK 20 --topn 100 --topicPath /kdd/lda/topic_map/topic.txt


