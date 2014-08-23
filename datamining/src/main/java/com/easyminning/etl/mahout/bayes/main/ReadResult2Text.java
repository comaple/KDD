package com.easyminning.etl.mahout.bayes.main;

import com.easyminning.etl.mahout.bayes.map.ReadSeq2TextMapper;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.ToolRunner;
import org.apache.mahout.common.AbstractJob;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.common.commandline.DefaultOptionCreator;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: comaple
 * Date: 14-2-28
 * Time: 下午1:21
 * To change this template use File | Settings | File Templates.
 */
public class ReadResult2Text extends AbstractJob {

    public static void main(String[] args) throws Exception {
        ToolRunner.run(new ReadResult2Text(), args);
    }

    @Override
    public int run(String[] args) throws Exception {

        //add the arguments
        addOptions();
        // parse the arguments
        if (parseArguments(args) == null) {
            return -1;
        }
        return runMapReduce(getInputPath(), getOutputPath());
    }

    private int runMapReduce(Path input, Path output) throws IOException, ClassNotFoundException, InterruptedException {
        //delete the path or output
        HadoopUtil.delete(getConf(), output);
        // Prepare Job for submission.
        Job job = prepareJob(input, output, SequenceFileInputFormat.class,
                ReadSeq2TextMapper.class, Text.class, Text.class,
                TextOutputFormat.class, "read-bayes-result-job");

        boolean succeeded = job.waitForCompletion(true);
        if (!succeeded) {
            return -1;
        }
        return 0;
    }


    private void addOptions() {
        addInputOption();
        addOutputOption();
        addOption(DefaultOptionCreator.overwriteOption().create());
    }

}
