package com.easyminning.etl.mahout.docparse2word;

import com.easyminning.etl.mahout.util.Constant;
import org.apache.hadoop.util.ToolRunner;
import org.apache.mahout.common.AbstractJob;
import org.apache.mahout.common.commandline.DefaultOptionCreator;

/**
 * Created by comaple on 14-8-30.
 */
public class Doc2WordAndFilterJob extends AbstractJob {

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
    private int runMapReduce() {

        return 1;
    }

    /**
     * add the command line parameters to the job
     */
    private void addOptions() {
        addInputOption();
        addOutputOption();
        addOption(DefaultOptionCreator.overwriteOption().create());
    }

}