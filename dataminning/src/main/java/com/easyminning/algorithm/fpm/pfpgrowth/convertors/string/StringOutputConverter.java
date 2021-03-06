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

package com.easyminning.algorithm.fpm.pfpgrowth.convertors.string;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.mahout.common.Pair;
import com.easyminning.algorithm.fpm.pfpgrowth.convertors.string.*;

/**
 * Collects a string pattern in a MaxHeap and outputs the top K patterns
 * 
 */

public final class StringOutputConverter implements OutputCollector<String,List<Pair<List<String>,Long>>> {
  
  private final OutputCollector<Text, com.easyminning.algorithm.fpm.pfpgrowth.convertors.string.TopKStringPatterns> collector;
  
  public StringOutputConverter(OutputCollector<Text, com.easyminning.algorithm.fpm.pfpgrowth.convertors.string.TopKStringPatterns> collector) {
    this.collector = collector;
  }
  
  @Override
  public void collect(String key,
                      List<Pair<List<String>,Long>> value) throws IOException {
    collector.collect(new Text(key), new com.easyminning.algorithm.fpm.pfpgrowth.convertors.string.TopKStringPatterns(value));
  }
}
