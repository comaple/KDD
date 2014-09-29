num=`hadoop fs -ls /kdd/lda/doc |grep -v _|grep -v Found|wc -l`
num=$(( num -1 ))
echo $num

for i in $(seq 0 $num) ; do
mahout hadoop  org.apache.mahout.utils.vectors.VectorDumper   -i /kdd/lda/doc/part-m-0000$i \
-o result/vectordump-$i -vs 150 -p true \
-d /kdd/lda/vector/dictionary.file-* \
-dt sequencefile \
-sort /kdd/lda/doc/part-m-0000$i

done
