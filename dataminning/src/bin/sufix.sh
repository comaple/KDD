#########################################
### Aouthor : comaple.zhang
### Create Time : 20140902
### Usage : to sufix process the lda result
###########################################

num=$(hadoop fs -ls /kdd/lda/topic |grep -v _|grep -v Found|wc -l)
num=$(( num -1 ))
for i in $(seq 0 $num) ;do
mahout hadoop  org.apache.mahout.utils.vectors.VectorDumper   -i /kdd/lda/topic/part-m-0000$i \
-o result/vectordump-$i -vs 150 -p true \
-d /kdd/lda/vector/dictionary.file-0 \
-dt sequencefile \
-sort /kdd/lda/topic/part-m-0000$i

done
