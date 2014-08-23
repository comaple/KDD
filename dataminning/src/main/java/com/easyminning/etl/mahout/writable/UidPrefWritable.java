package com.easyminning.etl.mahout.writable;

import org.apache.hadoop.io.Text;
import org.apache.mahout.math.VarLongWritable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by ZhangShengtao on 14-8-7.
 */
public class UidPrefWritable extends VarLongWritable {

    /**
     * user data flag to decide what the data is
     */
    private Text flage = new Text();

    /**
     * user id value
     */
    private Text uidValue = new Text();

    /**
     * used to store the doc vector
     */
    private Text vectorWritable = new Text();

    public UidPrefWritable() {
    }

    public UidPrefWritable(long uid) {
        set(uid);
    }

    public UidPrefWritable(long uid, String flage, String uidValue, Text vector) {
        set(uid);
        this.flage.set(flage);
        this.uidValue.set(uidValue);
        this.vectorWritable.set(vector);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        super.write(out);
        this.flage.write(out);
        this.uidValue.write(out);
        this.vectorWritable.write(out);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        super.readFields(in);
        this.flage.readFields(in);
        this.uidValue.readFields(in);
        this.vectorWritable.readFields(in);
    }

    public Text getFlage() {
        return flage;
    }

    public void setFlage(Text flage) {
        this.flage = flage;
    }

    public Text getUidValue() {
        return uidValue;
    }

    public void setUidValue(Text uidValue) {
        this.uidValue = uidValue;
    }

    public Text getVectorWritable() {
        return vectorWritable;
    }

    public void setVectorWritable(Text vectorWritable) {
        this.vectorWritable = vectorWritable;
    }
}
