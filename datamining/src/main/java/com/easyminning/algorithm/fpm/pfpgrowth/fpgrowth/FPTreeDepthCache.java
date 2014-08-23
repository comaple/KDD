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

package com.easyminning.algorithm.fpm.pfpgrowth.fpgrowth;

import com.google.common.collect.Lists;
import com.easyminning.algorithm.fpm.pfpgrowth.fpgrowth.*;
import com.easyminning.algorithm.fpm.pfpgrowth.fpgrowth.FPTree;

import java.util.List;

/**
 * Caches large FPTree {@link Object} for each level of the recursive
 * {@link com.easyminning.algorithm.fpm.pfpgrowth.fpgrowth.FPGrowth} algorithm to reduce allocation overhead.
 */

public class FPTreeDepthCache {

  private final com.easyminning.algorithm.fpm.pfpgrowth.fpgrowth.LeastKCache<Integer,com.easyminning.algorithm.fpm.pfpgrowth.fpgrowth.FPTree> firstLevelCache = new com.easyminning.algorithm.fpm.pfpgrowth.fpgrowth.LeastKCache<Integer,com.easyminning.algorithm.fpm.pfpgrowth.fpgrowth.FPTree>(5);
  private int hits;
  private int misses;
  private final List<com.easyminning.algorithm.fpm.pfpgrowth.fpgrowth.FPTree> treeCache = Lists.newArrayList();
  
  public final com.easyminning.algorithm.fpm.pfpgrowth.fpgrowth.FPTree getFirstLevelTree(Integer attr) {
    com.easyminning.algorithm.fpm.pfpgrowth.fpgrowth.FPTree tree = firstLevelCache.get(attr);
    if (tree != null) {
      hits++;
      return tree;
    } else {
      misses++;
      com.easyminning.algorithm.fpm.pfpgrowth.fpgrowth.FPTree conditionalTree = new com.easyminning.algorithm.fpm.pfpgrowth.fpgrowth.FPTree();
      firstLevelCache.set(attr, conditionalTree);
      return conditionalTree;
    }
  }
  
  public final int getHits() {
    return hits;
  }
  
  public final int getMisses() {
    return misses;
  }
  
  public final com.easyminning.algorithm.fpm.pfpgrowth.fpgrowth.FPTree getTree(int level) {
    while (treeCache.size() < level + 1) {
      com.easyminning.algorithm.fpm.pfpgrowth.fpgrowth.FPTree cTree = new com.easyminning.algorithm.fpm.pfpgrowth.fpgrowth.FPTree();
      treeCache.add(cTree);
    }
    FPTree conditionalTree = treeCache.get(level);
    conditionalTree.clear();
    return conditionalTree;
  }
  
}
