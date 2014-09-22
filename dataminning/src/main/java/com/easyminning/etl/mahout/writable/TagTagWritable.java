package com.easyminning.etl.mahout.writable;

import org.apache.hadoop.hive.serde2.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by comaple on 14-8-31.
 */
public class TagTagWritable implements WritableComparable {

    private Text tagItem = new Text();

    private Text tagItem1 = new Text();

    private DoubleWritable weight = new DoubleWritable();


    public TagTagWritable() {
    }

    @Override
    public void write(DataOutput out) throws IOException {
       this.tagItem.write(out);
       this.tagItem1.write(out);
       this.weight.write(out);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
       this.tagItem.readFields(in);
       this.tagItem1.readFields(in);
       this.weight.readFields(in);
    }


    public Text getTagItem() {
        return tagItem;
    }

    public void setTagItem(Text tagItem) {
        this.tagItem = tagItem;
    }

    public DoubleWritable getWeight() {
        return weight;
    }

    public void setWeight(DoubleWritable weight) {
        this.weight = weight;
    }

    public Text getTagItem1() {
        return tagItem1;
    }

    public void setTagItem1(Text tagItem1) {
        this.tagItem1 = tagItem1;
    }

    @Override
    public int compareTo(Object o) {
        TagTagWritable tagTagWritable = (TagTagWritable)o;
        if (this.tagItem.compareTo(tagTagWritable.getTagItem()) == 0
                && this.tagItem1.compareTo(tagTagWritable.getTagItem1()) == 0) {
            return 0;
        }
        if (this.tagItem.compareTo(tagTagWritable.getTagItem1()) == 0
                && this.tagItem1.compareTo(tagTagWritable.getTagItem()) == 0) {
            return 0;
        }

        if(this.tagItem.compareTo(tagTagWritable.getTagItem()) != 0){
            return this.tagItem.compareTo(tagTagWritable.getTagItem());
        }else{
            return this.tagItem1.compareTo(tagTagWritable.getTagItem1());
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * ((this.tagItem == null) ? 0 : this.tagItem.hashCode()) *((this.tagItem1 == null) ? 0 : this.tagItem1.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TagTagWritable other = (TagTagWritable) obj;
        if (this.tagItem.equals(other.getTagItem())
                && this.tagItem1.equals(other.getTagItem1())) {
            return true;
        }
        if (this.tagItem1.equals(other.getTagItem())
                && this.tagItem.equals(other.getTagItem1())) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        TagTagWritable tagTagWritable = new TagTagWritable();
        tagTagWritable.setTagItem(new Text("句子"));
        tagTagWritable.setTagItem1(new Text("答案"));

        TagTagWritable tagTagWritable2 = new TagTagWritable();
        tagTagWritable2.setTagItem(new Text("句子"));
        tagTagWritable2.setTagItem1(new Text("答案"));

        System.out.println(tagTagWritable.compareTo(tagTagWritable2) );





    }

}
