## 原始数据过滤处理
hadoop fs -rmr /kdd/lda/input_seq
hadoop jar dataminning-jar-with-dependencies.jar  com.easyminning.etl.mahout.docparse2word.Doc2WordAndFilterJob --input /kdd/scraw/201409*  --output /kdd/lda/input_seq  --isScrawler true --threshold 5 -ow

### 将序列文件转化为向量
mahout seq2sparse -i /kdd/lda/input_seq  -o /kdd/lda/vector  -ow --maxDFPercent 60 --namedVector -wt TF-IDF --analyzerName org.apache.lucene.analysis.core.WhitespaceAnalyzer


### 将向量key转化为数字id
mahout rowid -i /kdd/lda/vector/tfidf-vectors -o /kdd/lda/matrix-out/


### 执行lda计算结果
hadoop fs -rmr /kdd/lda/topic
hadoop fs -rmr /kdd/lda/doc
hadoop fs -rmr /kdd/lda/model
mahout cvb -i /kdd/lda/matrix-out/matrix -o /kdd/lda/doc   -dict /kdd/lda/vector/dictionary.file-* -dt /kdd/lda/topic -mt /kdd/lda/model --maxIter 50 --num_topics 100
