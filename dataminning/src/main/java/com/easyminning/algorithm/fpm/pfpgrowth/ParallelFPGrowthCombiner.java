/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.easyminning.algorithm.fpm.pfpgrowth;

import java.io.IOException;

import com.easyminning.algorithm.fpm.pfpgrowth.*;
import org.apache.mahout.math.list.IntArrayList;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.mahout.common.Pair;

/**
 *  takes each group of dependent transactions and\ compacts it in a
 * TransactionTree structure
 */

public class ParallelFPGrowthCombiner extends Reducer<IntWritable, com.easyminning.algorithm.fpm.pfpgrowth.TransactionTree,IntWritable, com.easyminning.algorithm.fpm.pfpgrowth.TransactionTree> {
  
  @Override
  protected void reduce(IntWritable key, Iterable<com.easyminning.algorithm.fpm.pfpgrowth.TransactionTree> values, Context context)
    throws IOException, InterruptedException {
    com.easyminning.algorithm.fpm.pfpgrowth.TransactionTree cTree = new com.easyminning.algorithm.fpm.pfpgrowth.TransactionTree();
    for (com.easyminning.algorithm.fpm.pfpgrowth.TransactionTree tr : values) {
      for (Pair<IntArrayList,Long> p : tr) {
        cTree.addPattern(p.getFirst(), p.getSecond());
      }
    }
    context.write(key, cTree.getCompressedTree());
  }
  
}
