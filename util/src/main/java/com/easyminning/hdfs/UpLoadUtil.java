package com.easyminning.hdfs;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.util.StringUtils;
import org.apache.log4j.Logger;

public class UpLoadUtil {

    private static Logger log = Logger.getLogger(UpLoadUtil.class);

    private String hadoop_default_name;

    public boolean is_sucessfull = true;

    private int maxpercentage = 95;

    private String destPath;


    private FileSystem fs = null;

    public int init() {
        int ret = 1;
        Configuration con = null;
        try {
            con = new Configuration();
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
            return 0;
        }
        con.set("fs.default.name", "");
        con.set("fs.hdfs.impl.disable.cache", "false");
        con.set("dfs.replication", "1");
        try {
            //fs = FileSystem.get(con);
        } catch (Exception e) {
            log.error("get FileSystem object failed");
            e.printStackTrace();
            log.error(e);
            return 0;
        }
        return ret;
    }

    public void UploadLocalFileToHdfs(String localpath, String dest) {
        Path p_src = null;
        Path p_dest = null;
        Path $p_dest = new Path(dest);
        try {
            p_src = new Path(localpath);
            p_dest = new Path(dest + ".tmp");
            if (fs.exists(p_dest)) {
                fs.delete(p_dest, false);//delete if exists
            }
            if (!fs.exists($p_dest)) {
                fs.copyFromLocalFile(p_src, p_dest);
                fs.rename(p_dest, $p_dest);
            }
        } catch (IOException e) {
            log.error("upload failed" + " fileName=" + localpath);
            log.error(e);
            is_sucessfull = false;
            return;
        } finally {
            p_src = null;
            p_dest = null;
            $p_dest = null;
        }
        log.info("upload file:" + localpath + " to dest:" + dest + " sucessfull");
    }

    public void close() {
        if (fs != null) {
            try {
                fs.close();
            } catch (IOException e) {
                log.error(e);
            }
        }
    }


    public String getHadoop_default_name() {
        return hadoop_default_name;
    }


    public void setHadoop_default_name(String hadoop_default_name) {
        this.hadoop_default_name = hadoop_default_name;
    }


    public String getDestPath() {
        return destPath;
    }


    public void setDestPath(String destPath) {
        this.destPath = destPath;
    }

    public boolean isIs_sucessfull() {
        return is_sucessfull;
    }

    public void setIs_sucessfull(boolean is_sucessfull) {
        this.is_sucessfull = is_sucessfull;
    }

    public boolean Check() {

        DistributedFileSystem dfs = (DistributedFileSystem) fs;
        org.apache.hadoop.hdfs.DistributedFileSystem.DiskStatus ds = null;
        try {
            ds = dfs.getDiskStatus();
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        long used = ds.getDfsUsed();
        long remaining = ds.getRemaining();
        long presentCapacity = used + remaining;
        float use = Float
                .valueOf(StringUtils
                        .limitDecimalTo2(((1.0D * (double) used) / (double) presentCapacity) * 100D));
        return use <= maxpercentage;
    }

    public int getMaxpercentage() {
        return maxpercentage;
    }

    public void setMaxpercentage(int maxpercentage) {
        this.maxpercentage = maxpercentage;
    }

}
