package com.easyminning.etl.mahout.bayes.main;

import com.easyminning.etl.mahout.bayes.map.SequenceFilesFromDirMapper;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.ToolRunner;
import org.apache.mahout.common.AbstractJob;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.common.commandline.DefaultOptionCreator;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: comaple
 * Date: 14-2-25
 * Time: 上午9:43
 * To change this template use File | Settings | File Templates.
 */
public class SeqFromTextFile extends AbstractJob {
    public static String JOB_NAME = "SequenceFilesFromDir-job";

    /**
     * 入口，调用
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        ToolRunner.run(new SeqFromTextFile(), args);

    }

    @Override
    public int run(String[] args) throws Exception {
        //add the arguments
        addOptions();
        // parse the arguments
        if (parseArguments(args) == null) {
            return -1;
        }

        return runMapReduce(inputPath, outputPath);

    }


    private int runMapReduce(Path input, Path output) throws IOException, ClassNotFoundException, InterruptedException {
        //delete the path or output
        FileSystem fs = FileSystem.get(getConf());

        if (fs.exists(outputPath)) {
            HadoopUtil.delete(getConf(), output);
            System.out.println("delete the out put path :" + output.toString());
        }

        if (fs.exists(inputPath)) {
            System.out.println("the input path is exists : " + inputPath.toString());
            System.out.println("the input is :" + input.toString());
        } else {
            System.out.println("the input path is not exists :" + inputPath.toString());
            System.out.println("the input is :" + input.toString());
        }
        // Prepare Job for submission.
        Job job = prepareJob(input, output, TextInputFormat.class,
                SequenceFilesFromDirMapper.class, Text.class, Text.class,
                SequenceFileOutputFormat.class, JOB_NAME);

        boolean succeeded = job.waitForCompletion(true);
        if (!succeeded) {
            return -1;
        }
        return 0;
    }


    /**
     * Override this method in order to add additional options to the command line of the SequenceFileFromDirectory job.
     * Do not forget to call super() otherwise all standard options (input/output dirs etc) will not be available.
     */
    protected void addOptions() {
        addInputOption();
        addOutputOption();
        addOption(DefaultOptionCreator.overwriteOption().create());

    }
}
