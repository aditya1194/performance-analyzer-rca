/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 */

package com.amazon.opendistro.elasticsearch.performanceanalyzer.rca.framework.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Stats {
  private long totalNodesCount;
  private long leafNodesCount;
  private long leavesAddedToAnalysisFlowField;
  private Map<Integer, ConnectedComponent> graphs;

  private Set<String> mutedGraphNodes;

  private static volatile Stats instance = null;

  public void incrementLeavesAddedToAnalysisFlowField() {
    ++this.leavesAddedToAnalysisFlowField;
  }

  private Stats() {
    this.totalNodesCount = 0;
    this.leafNodesCount = 0;
    this.leavesAddedToAnalysisFlowField = 0;
    this.graphs = new HashMap<>();
  }

  public static Stats getInstance() {
    if (instance == null) {
      instance = new Stats();
    }
    return instance;
  }

  public long getTotalNodesCount() {
    return totalNodesCount;
  }

  public long getLeafNodesCount() {
    return leafNodesCount;
  }

  public long getLeavesAddedToAnalysisFlowField() {
    return leavesAddedToAnalysisFlowField;
  }

  void incrementTotalNodesCount() {
    ++this.totalNodesCount;
  }

  void incrementLeafNodesCount() {
    ++this.leafNodesCount;
    incrementTotalNodesCount();
  }

  public int getGraphsCount() {
    return graphs.size();
  }

  public void addNewGraph(ConnectedComponent connectedComponent) {
    this.graphs.put(connectedComponent.getGraphId(), connectedComponent);
  }

  void removeGraph(int graphId) {
    this.graphs.remove(graphId);
  }

  public ConnectedComponent getGraphById(int graphId) {
    return this.graphs.get(graphId);
  }

  public List<ConnectedComponent> getConnectedComponents() {
    mutedGraphNodes =
            ConcurrentHashMap.newKeySet((int)getTotalNodesCount());
    return new ArrayList<>(graphs.values());
  }

  public boolean updateMutedGraphNodes(Set<String> nodeNames) {
    // Add all the rcaNodes if the mutedGraphNodes is empty
    // else, retain the ones provided in param value
    if (mutedGraphNodes.isEmpty()) {
      return mutedGraphNodes.addAll(nodeNames);
    } else {
      return mutedGraphNodes.retainAll(nodeNames);
    }
  }

  public boolean addToMutedGraphNodes(String nodeName) {
    return mutedGraphNodes.add(nodeName);
  }

  public boolean isNodeMuted(String nodeName) {
    return mutedGraphNodes.contains(nodeName);
  }

  public Set<String> getMutedGraphNodes() {
    return mutedGraphNodes;
  }

  public int getMutedGraphNodesCount() {
    return mutedGraphNodes.size();
  }

  public static void clear() {
    instance = null;
  }

  public void reset() {
    totalNodesCount = 0;
    leafNodesCount = 0;
    leavesAddedToAnalysisFlowField = 0;
    graphs = null;
  }
}
