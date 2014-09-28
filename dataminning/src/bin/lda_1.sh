## 原始数据过滤处理

hadoop fs -rmr /kdd/lda_new/input_seq
hadoop jar dataminning-jar-with-dependencies.jar com.easyminning.etl.mahout.docparse2word.Doc2WordAndFilterJob --input /kdd/scraw/201409*  --output /kdd/lda_new/input_seq  --isScrawler true -ow --threshold 4
#hadoop jar dataminning-jar-with-dependencies.jar  --input /kdd/scraw_bak/201409*  --output /kdd/lda/input_seq  --isScrawler true -ow 

### 将序列文件转化为向量
mahout seq2sparse -i /kdd/lda_new/input_seq  -o /kdd/lda_new/vector  -ow --maxDFPercent 80 --namedVector -wt TFIDF --analyzerName org.apache.lucene.analysis.core.WhitespaceAnalyzer -ow


### 将向量key转化为数字id
hadoop fs -rmr /kdd/lda_new/matrix-out
mahout rowid -i /kdd/lda_new/vector/tfidf-vectors -o /kdd/lda_new/matrix-out/


### 执行lda_new计算结果
hadoop fs -rmr /kdd/lda_new/topic
hadoop fs -rmr /kdd/lda_new/doc
hadoop fs -rmr /kdd/lda_new/model
mahout cvb -i /kdd/lda_new/matrix-out/matrix -o /kdd/lda_new/doc   -dict /kdd/lda_new/vector/dictionary.file-* -dt /kdd/lda_new/topic -mt /kdd/lda_new/model --maxIter 20 --num_topics 100
