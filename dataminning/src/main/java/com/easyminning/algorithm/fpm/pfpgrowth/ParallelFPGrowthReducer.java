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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.mutable.MutableLong;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.mahout.common.Pair;
import org.apache.mahout.common.Parameters;
import com.easyminning.algorithm.fpm.pfpgrowth.*;
import com.easyminning.algorithm.fpm.pfpgrowth.PFPGrowth;
import com.easyminning.algorithm.fpm.pfpgrowth.convertors.ContextStatusUpdater;
import com.easyminning.algorithm.fpm.pfpgrowth.convertors.ContextWriteOutputCollector;
import com.easyminning.algorithm.fpm.pfpgrowth.convertors.integer.IntegerStringOutputConverter;
import com.easyminning.algorithm.fpm.pfpgrowth.convertors.string.TopKStringPatterns;
import com.easyminning.algorithm.fpm.pfpgrowth.fpgrowth.FPGrowth;
import com.easyminning.algorithm.fpm.pfpgrowth.fpgrowth2.FPGrowthIds;
import org.apache.mahout.math.list.IntArrayList;
import org.apache.mahout.math.list.LongArrayList;

/**
 *  takes each group of transactions and runs Vanilla FPGrowth on it and
 * outputs the the Top K frequent Patterns for each group.
 * 
 */

public final class ParallelFPGrowthReducer extends Reducer<IntWritable, com.easyminning.algorithm.fpm.pfpgrowth.TransactionTree,Text,TopKStringPatterns> {

  private final List<String> featureReverseMap = Lists.newArrayList();
  private final LongArrayList freqList = new LongArrayList();
  private int maxHeapSize = 50;
  private int minSupport = 3;
  private int numFeatures;
  private int maxPerGroup;
  private boolean useFP2;

  private static final class IteratorAdapter implements Iterator<Pair<List<Integer>,Long>> {
    private final Iterator<Pair<IntArrayList,Long>> innerIter;

    private IteratorAdapter(Iterator<Pair<IntArrayList,Long>> transactionIter) {
      innerIter = transactionIter;
    }

    @Override
    public boolean hasNext() {
      return innerIter.hasNext();
    }

    @Override
    public Pair<List<Integer>,Long> next() {
      Pair<IntArrayList,Long> innerNext = innerIter.next();
      return new Pair<List<Integer>,Long>(innerNext.getFirst().toList(), innerNext.getSecond());
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  @Override
  protected void reduce(IntWritable key, Iterable<com.easyminning.algorithm.fpm.pfpgrowth.TransactionTree> values, Context context) throws IOException {
    com.easyminning.algorithm.fpm.pfpgrowth.TransactionTree cTree = new com.easyminning.algorithm.fpm.pfpgrowth.TransactionTree();
    for (com.easyminning.algorithm.fpm.pfpgrowth.TransactionTree tr : values) {
      for (Pair<IntArrayList,Long> p : tr) {
        cTree.addPattern(p.getFirst(), p.getSecond());
      }
    }
    
    List<Pair<Integer,Long>> localFList = Lists.newArrayList();
    for (Entry<Integer,MutableLong> fItem : cTree.generateFList().entrySet()) {
      localFList.add(new Pair<Integer,Long>(fItem.getKey(), fItem.getValue().toLong()));
    }
    
    Collections.sort(localFList, new CountDescendingPairComparator<Integer,Long>());
    
    if (useFP2) {
      FPGrowthIds.generateTopKFrequentPatterns(
          cTree.iterator(),
          freqList,
          minSupport,
          maxHeapSize,
          com.easyminning.algorithm.fpm.pfpgrowth.PFPGrowth.getGroupMembers(key.get(), maxPerGroup, numFeatures),
          new IntegerStringOutputConverter(
              new ContextWriteOutputCollector<IntWritable, com.easyminning.algorithm.fpm.pfpgrowth.TransactionTree, Text, TopKStringPatterns>(context),
              featureReverseMap),
          new ContextStatusUpdater<IntWritable, com.easyminning.algorithm.fpm.pfpgrowth.TransactionTree, Text, TopKStringPatterns>(context));
    } else {
      FPGrowth<Integer> fpGrowth = new FPGrowth<Integer>();
      fpGrowth.generateTopKFrequentPatterns(
          new IteratorAdapter(cTree.iterator()),
          localFList,
          minSupport,
          maxHeapSize,
          Sets.newHashSet(com.easyminning.algorithm.fpm.pfpgrowth.PFPGrowth.getGroupMembers(key.get(),
                  maxPerGroup,
                  numFeatures).toList()),
          new IntegerStringOutputConverter(
              new ContextWriteOutputCollector<IntWritable, com.easyminning.algorithm.fpm.pfpgrowth.TransactionTree,Text,TopKStringPatterns>(context),
              featureReverseMap),
          new ContextStatusUpdater<IntWritable, com.easyminning.algorithm.fpm.pfpgrowth.TransactionTree,Text,TopKStringPatterns>(context));
    }
  }
  
  @Override
  protected void setup(Context context) throws IOException, InterruptedException {
    
    super.setup(context);
    Parameters params = new Parameters(context.getConfiguration().get(com.easyminning.algorithm.fpm.pfpgrowth.PFPGrowth.PFP_PARAMETERS, ""));
    
    for (Pair<String,Long> e : com.easyminning.algorithm.fpm.pfpgrowth.PFPGrowth.readFList(context.getConfiguration())) {
      featureReverseMap.add(e.getFirst());
      freqList.add(e.getSecond());
    }
    
    maxHeapSize = Integer.valueOf(params.get(com.easyminning.algorithm.fpm.pfpgrowth.PFPGrowth.MAX_HEAPSIZE, "50"));
    minSupport = Integer.valueOf(params.get(com.easyminning.algorithm.fpm.pfpgrowth.PFPGrowth.MIN_SUPPORT, "3"));

    maxPerGroup = params.getInt(com.easyminning.algorithm.fpm.pfpgrowth.PFPGrowth.MAX_PER_GROUP, 0);
    numFeatures = featureReverseMap.size();
    useFP2 = "true".equals(params.get(PFPGrowth.USE_FPG2));
  }
}
