package com.easyminning.hdfs;

import org.apache.hadoop.mapreduce.Job;

/**
 * Created with IntelliJ IDEA.
 * User: xdx
 * Date: 14-9-3
 * Time: 下午10:01
 * To change this template use File | Settings | File Templates.
 */
public class JobBeforeDoingAbs {

    protected Job job = null;

    public void init(Job job){
        this.job = job;
    }

    public void doing(){

    }

}
