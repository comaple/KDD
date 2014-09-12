package com.easyminning.etl.mahout.docparse2word;

import com.easyminning.etl.mahout.docparse2word.map.SplitAndFilterMapper;
import com.easyminning.etl.mahout.docparse2word.map.SplitMapper;
import com.easyminning.etl.mahout.docparse2word.reduce.SplitReducer;
import com.easyminning.etl.mahout.util.Constant;
import com.easyminning.etl.mahout.writable.DocumentWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.ToolRunner;
import org.apache.mahout.common.AbstractJob;
import org.apache.mahout.common.commandline.DefaultOptionCreator;

/**
 * Created by comaple on 14-08-30.
 */
public class Doc2WordAndFilterJob extends AbstractJob {
    // the final number of doc for the lda input
    private final String finalDocNum = "finalDocNum";

    // the threshold of the similarity for the doc bag
    private final String threshold = "threshold";

    private final String isScrawler = "isScrawler";

    private final String pattern = "pattern";

    public static void main(String[] args) {
        try {
            ToolRunner.run(new Doc2WordAndFilterJob(), args);
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
        Boolean scrawler = Boolean.parseBoolean(getOption(isScrawler));
        System.out.println(getOption(isScrawler));
        System.out.println(String.format(" ------ the scrawler u pass is : %s", scrawler));
        if (scrawler) {
            Job combineJob = prepareJob(getInputPath(), getOutputPath(), TextInputFormat.class, SplitAndFilterMapper.class, Text.class, DocumentWritable.class, SplitReducer.class, Text.class, Text.class, SequenceFileOutputFormat.class);
            combineJob.getConfiguration().set(Constant.THRSHOLD, getOption(threshold, "-1"));
            combineJob.getConfiguration().set(Constant.PATTERN_STR, getOption(pattern));
            System.out.println(getOption(pattern));
            combineJob.getConfiguration().set(Constant.RESULT_NUM, getOption(finalDocNum));
            return combineJob.waitForCompletion(true) == true ? 0 : -1;
        } else {
            Job combineJob_1 = prepareJob(getInputPath(), getOutputPath(), TextInputFormat.class, SplitMapper.class, Text.class, DocumentWritable.class, SplitReducer.class, Text.class, Text.class, SequenceFileOutputFormat.class);
            combineJob_1.getConfiguration().set(Constant.RESULT_NUM, getOption(finalDocNum));
            return combineJob_1.waitForCompletion(true) == true ? 0 : -1;
        }

    }

    /**
     * add the command line parameters to the job
     */
    private void addOptions() {
        addInputOption();
        addOutputOption();
        addOption(finalDocNum, "dn", "the final doc number of the result.");
        addOption(threshold, "td", "the threshold of the similarity for the doc calculate.");
        addOption(isScrawler, "ic", "the document is from scrawler or not.");
        addOption(pattern, "pt", "the pattern which the scrawler doc line use.");
        addOption(DefaultOptionCreator.overwriteOption().create());
    }


}