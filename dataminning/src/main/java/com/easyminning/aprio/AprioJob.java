package com.easyminning.aprio;

import com.easyminning.etl.mahout.writable.TagTagWritable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.serde2.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.ToolRunner;
import org.apache.mahout.common.AbstractJob;
import org.apache.mahout.common.commandline.DefaultOptionCreator;

/**
 * Created by Administrator on 2014/9/6.
 */
public class AprioJob extends AbstractJob {

    private final String param1 = "param1";

    private final String param2 = "param2";

    public static void main(String[] args) {
        try {
            ToolRunner.run(new Configuration(), new AprioJob(), args);
            //ToolRunner.run(new AprioJob(), args);
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
        this.getConf().set("mapred.map.tasks","10");
        this.getConf().set("mapred.reduce.tasks","10");
        Job job = prepareJob(getInputPath(), getOutputPath(), TextInputFormat.class,
                AprioMaper.class, TagTagWritable.class, DoubleWritable.class, AprioReducer.class,
                Text.class, NullWritable.class, TextOutputFormat.class);
        int res = job.waitForCompletion(true) == true ? 0 : -1;
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
