package com.easyminning.algorithm.fpm.pfpgrowth.result;

import com.easyminning.algorithm.fpm.pfpgrowth.convertors.string.TopKStringPatterns;
import com.easyminning.tag.StepTagSimilarity;
import com.easyminning.tag.VersionStampService;
import com.easyminning.util.simhash.DuplicateDocFilter;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.ToolRunner;
import org.apache.mahout.common.AbstractJob;
import org.apache.mahout.common.commandline.DefaultOptionCreator;

/**
 * Created by Administrator on 2014/9/6.
 */
public class FpgrowthResultJob extends AbstractJob {

    private final String param1 = "param1";

    private final String param2 = "param2";

    public static void main(String[] args) {
        try {
            ToolRunner.run(new FpgrowthResultJob(), args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int run(String[] args) throws Exception {
        addOptions();
        if (parseArguments(args) == null) {
            return -1;
        }
        return runMapReduce();
    }

    // run mapreduce to parse doc to word and split
    private int runMapReduce() throws Exception {

        // this.getConf().set("fs.default.name", "hdfs://db1:9000");
        Job job = prepareJob(getInputPath(), getOutputPath(), SequenceFileInputFormat.class,
                FpgrowthResultMaper.class, Text.class, TopKStringPatterns.class, Reducer.class,
                Text.class, Text.class, SequenceFileOutputFormat.class);
//        job.getConfiguration().set(param1, getOption(param1));。。，。。，
//        job.getConfiguration().set(param2, getOption(param2));


        int res = job.waitForCompletion(true) == true ? 0 : -1;
        StepTagSimilarity.getInstance().analysis();

        // 删除重复数据
        DuplicateDocFilter.filter();

        //  更新版本号为已经完成
        VersionStampService.getInstance().updateUnFinishedVersion();

        return res;

    }

    /**
     * add the command line parameters to the job
     */
    private void addOptions() {
        addInputOption();
        addOutputOption();
        addOption(param1, "p1", "p1", false);
        addOption(param2, "p2", "p2", false);

        addOption(DefaultOptionCreator.overwriteOption().create());
    }

}
