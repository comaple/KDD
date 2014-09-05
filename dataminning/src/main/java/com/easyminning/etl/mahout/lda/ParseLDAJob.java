package com.easyminning.etl.mahout.lda;

import com.easyminning.etl.mahout.lda.map.LDADocMapper;
import com.easyminning.etl.mahout.lda.map.MatrixMapper;
import com.easyminning.etl.mahout.lda.reduce.ParseLDANewReducer;
import com.easyminning.etl.mahout.util.Constant;
import com.easyminning.etl.mahout.writable.UidPrefWritable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.ToolRunner;
import org.apache.mahout.common.AbstractJob;
import org.apache.mahout.common.commandline.DefaultOptionCreator;

import java.net.URI;


/**
 * Created by comaple on 14-9-4.
 */
public class ParseLDAJob extends AbstractJob {
    /**
     * 处理matrix的输入
     */
    private String MATRIX = "matrix";
    String TOPIC_K = "topicK";
    String TOPIC_PATH = "topicPath";

    public static void main(String[] args) throws Exception {
        ToolRunner.run(new ParseLDAJob(), args);
    }

    @Override
    public int run(String[] args) throws Exception {
        //add the arguments
        addOptions();
        // parse the arguments
        if (parseArguments(args) == null) {
            return -1;
        }
        return runMapReduce();
    }

    private int runMapReduce() throws Exception {
        Path input = getInputPath();
        Path output = getOutputPath();
        int k = Integer.parseInt(getOption(TOPIC_K));
        Path topicPath = new Path(getOption(TOPIC_PATH));
        Configuration conf = getConf();
        Path indexPath = new Path(getOption(MATRIX));
        System.out.println("topic path is :" + topicPath.toString());

        DistributedCache.createSymlink(conf);
        DistributedCache.addCacheFile(new URI(topicPath.toUri().toString() + "#" + Constant.TOPIC_PATH), conf);
        Job ldaParseJob = new Job(conf);
        ldaParseJob.setReducerClass(ParseLDANewReducer.class);
        ldaParseJob.setMapOutputKeyClass(Text.class);
        ldaParseJob.setMapOutputValueClass(UidPrefWritable.class);
        ldaParseJob.setOutputKeyClass(Text.class);
        ldaParseJob.setOutputValueClass(NullWritable.class);
        ldaParseJob.setOutputFormatClass(TextOutputFormat.class);
        ldaParseJob.setJobName("PARSE-LDA-JOB-I");
        FileOutputFormat.setOutputPath(ldaParseJob, output);
        MultipleInputs.addInputPath(ldaParseJob, input, SequenceFileInputFormat.class, LDADocMapper.class);
        MultipleInputs.addInputPath(ldaParseJob, indexPath, SequenceFileInputFormat.class, MatrixMapper.class);
        ldaParseJob.setJarByClass(ParseLDAJob.class);
        ldaParseJob.getConfiguration().setInt(Constant.TOPIC_K, k);
        boolean phrase_1 = ldaParseJob.waitForCompletion(true);
        if (phrase_1) {
            return 0;
        } else {
            return -1;
        }
    }

    private void addOptions() {
        addInputOption();
        addOutputOption();
        addOption(MATRIX, "mx", "the input of matrix for lda.");
        addOption(TOPIC_K, "tk", "the input num of topic");
        addOption(TOPIC_PATH, "tp", "the input path of topic");
        addOption(DefaultOptionCreator.overwriteOption().create());

    }

}
