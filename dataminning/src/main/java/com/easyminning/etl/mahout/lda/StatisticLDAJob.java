package com.easyminning.etl.mahout.lda;


import com.easyminning.etl.mahout.lda.map.LDADocMapper;
import com.easyminning.etl.mahout.lda.map.MatrixMapper;
import com.easyminning.etl.mahout.lda.map.StatisticsTopicMapper;
import com.easyminning.etl.mahout.lda.reduce.ParseLDAReducer;
import com.easyminning.etl.mahout.lda.reduce.StatisticsReducer;
import com.easyminning.etl.mahout.writable.UidPrefWritable;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
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

/**
 * Created by ZhangShengtao on 14-8-7.
 */
public class StatisticLDAJob extends AbstractJob {
    /**
     * 处理matrix的输入
     */
    private String MATRIX = "matrix";

    private String NUM_REDUCE = "numReduce";

    public static void main(String[] args) throws Exception {
        ToolRunner.run(new StatisticLDAJob(), args);
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
        Path matrix = new Path(getOption(MATRIX));
        int numReduce = Integer.parseInt(getOption(NUM_REDUCE));
        Job ldaParseJob = new Job(getConf());
        ldaParseJob.setReducerClass(ParseLDAReducer.class);
        ldaParseJob.setMapOutputKeyClass(Text.class);
        ldaParseJob.setMapOutputValueClass(UidPrefWritable.class);
        ldaParseJob.setOutputKeyClass(LongWritable.class);
        ldaParseJob.setOutputValueClass(Text.class);
        ldaParseJob.setOutputFormatClass(SequenceFileOutputFormat.class);
        ldaParseJob.setNumReduceTasks(numReduce);
        ldaParseJob.setJobName("PARSE-LDA-JOB-I");
        FileOutputFormat.setOutputPath(ldaParseJob, output);
        MultipleInputs.addInputPath(ldaParseJob, input, SequenceFileInputFormat.class, LDADocMapper.class);
        MultipleInputs.addInputPath(ldaParseJob, matrix, SequenceFileInputFormat.class, MatrixMapper.class);
        ldaParseJob.setJarByClass(StatisticLDAJob.class);
        boolean phrase_1 = ldaParseJob.waitForCompletion(true);

        Job statisticsJob = prepareJob(output, new Path(output.getParent(), "statistics_result"), SequenceFileInputFormat.class, StatisticsTopicMapper.class, Text.class, Text.class, StatisticsReducer.class, Text.class, Text.class, TextOutputFormat.class);
        statisticsJob.setJobName("PARSE-LDA-JOB-II");
        boolean phrase_2 = statisticsJob.waitForCompletion(true);
        return phrase_1 && phrase_2 ? 0 : -1;
    }

    private void addOptions() {
        addInputOption();
        addOutputOption();
        addOption(MATRIX, "mx", "the input of matrix for lda.");
        addOption(NUM_REDUCE, "nr", "the number of task of reduce.");
        addOption(DefaultOptionCreator.overwriteOption().create());
    }
}
