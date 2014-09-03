package com.easyminning.etl.mahout.lda;

import org.apache.hadoop.util.ToolRunner;
import org.apache.mahout.common.AbstractJob;

/**
 * Created by comaple on 14-9-4.
 */
public class ParseLDAJob extends AbstractJob {
    String docInput = "docinput";
    String indexInput = "indexinput";
    String topicK = "topicK";

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

    private int runMapReduce() {
        return 0;
    }

    private void addOptions() {


    }

}
