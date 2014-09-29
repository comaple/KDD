package com.easyminning.etl.mahout.docparse2word;

import com.easyminning.etl.mahout.docparse2word.map.SplitAndFilterMapper;
import com.easyminning.etl.mahout.docparse2word.map.SplitMapper;
import com.easyminning.etl.mahout.docparse2word.reduce.SplitReducer;
import com.easyminning.etl.mahout.util.Constant;
import com.easyminning.etl.mahout.writable.DocumentWritable;
import com.easyminning.tag.LogRecord;
import com.easyminning.tag.LogRecordService;
import com.easyminning.tag.VersionStampService;
import com.easyminning.util.date.DateUtil;
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
        LogRecordService.getInstance().save(new LogRecord("2", DateUtil.getCurrentFriendlyTime(),"分词算法执行开始" ));

        Boolean scrawler = Boolean.parseBoolean(getOption(isScrawler));
        System.out.println(getOption(isScrawler));
        System.out.println(String.format(" ------ the scrawler u pass is : %s", scrawler));
        // 写入版本信息
        VersionStampService.getInstance().genUnFinshedVersionStamp();
        int res = 0;
        if (scrawler) {
            Job combineJob = prepareJob(getInputPath(), getOutputPath(), TextInputFormat.class, SplitAndFilterMapper.class, Text.class, DocumentWritable.class, SplitReducer.class, Text.class, Text.class, SequenceFileOutputFormat.class);
            combineJob.getConfiguration().set(Constant.THRSHOLD, getOption(threshold, "-1"));
            combineJob.getConfiguration().set(Constant.RESULT_NUM, getOption(finalDocNum, "2000"));
            res = combineJob.waitForCompletion(true) == true ? 0 : -1;
        } else {
            Job combineJob_1 = prepareJob(getInputPath(), getOutputPath(), TextInputFormat.class, SplitMapper.class, Text.class, DocumentWritable.class, SplitReducer.class, Text.class, Text.class, SequenceFileOutputFormat.class);
            res = combineJob_1.waitForCompletion(true) == true ? 0 : -1;
        }

        LogRecordService.getInstance().save(new LogRecord("2", DateUtil.getCurrentFriendlyTime(),"分词算法执行结束" ));
        return res;

    }

    /**
     * add the command line parameters to the job
     */
    private void addOptions() {
        addInputOption();
        addOutputOption();
        addOption(threshold, "td", "the threshold of the similarity for the doc calculate.");
        addOption(isScrawler, "ic", "the document is from scrawler or not.");
        addOption(DefaultOptionCreator.overwriteOption().create());
    }


}