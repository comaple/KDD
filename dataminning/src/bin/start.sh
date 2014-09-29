topic=100
APP_PATH=/data/apps/lda
### lda 算法计算
sh $APP_PATH/lda.sh $topic
### lda 话题结果解析
sh $APP_PATH/sufix.sh
### 准备话题数据
cat result/vectordump-* > result/topic_result.txt
### 话题数据上传到 hdfs
hadoop fs -rmr /kdd/lda/topic_map/*
hadoop fs -put result/topic_result.txt  /kdd/lda/topic_map/topic.txt
### lda 结果解析入库
sh $APP_PATH/sufix_process.sh $topic
### aprio 关联分析
sh $APP_PATH/aprio.sh

