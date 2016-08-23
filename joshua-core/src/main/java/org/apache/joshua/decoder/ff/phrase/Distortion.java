/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.joshua.decoder.ff.phrase;

import java.util.List;

import org.apache.joshua.decoder.JoshuaConfiguration;
import org.apache.joshua.decoder.chart_parser.SourcePath;
import org.apache.joshua.decoder.ff.FeatureVector;
import org.apache.joshua.decoder.ff.StatelessFF;
import org.apache.joshua.decoder.ff.state_maintenance.DPState;
import org.apache.joshua.decoder.ff.tm.Rule;
import org.apache.joshua.decoder.hypergraph.HGNode;
import org.apache.joshua.decoder.phrase.Hypothesis;
import org.apache.joshua.decoder.segment_file.Sentence;

public class Distortion extends StatelessFF {

  public Distortion(FeatureVector weights, String[] args, JoshuaConfiguration config) {
    super(weights, "Distortion", args, config);
    
    if (! config.search_algorithm.equals("stack")) {
      String msg = "* FATAL: Distortion feature only application for phrase-based decoding. "
          + "Use -search phrase or remove this feature";
      throw new RuntimeException(msg);
    }
  }

  @Override
  public DPState compute(Rule rule, List<HGNode> tailNodes, int i, int j, SourcePath sourcePath,
      Sentence sentence, Accumulator acc) {

    if (rule == Hypothesis.MONO_RULE || rule == Hypothesis.SWAP_RULE) {
//        int start_point = j - rule.getFrench().length + rule.getArity();
//        int jump_size = Math.abs(tailNodes.get(0).j - start_point);

      if (rule == Hypothesis.MONO_RULE) {
        int start_point = j - tailNodes.get(1).getHyperEdges().get(0).getRule().getSource().length;
        int last_point = tailNodes.get(0).j;
        int jump_size = Math.abs(start_point - last_point);
      
//        System.err.println(String.format("DISTORTION_mono(%d -> %d) = %d", 
//            last_point, start_point, jump_size));

        acc.add(featureId, -jump_size);
      } else {
        int start_point = j - tailNodes.get(0).getHyperEdges().get(0).getRule().getSource().length;
        int last_point = tailNodes.get(1).j;
        int jump_size = Math.abs(start_point - last_point);
      
//        System.err.println(String.format("DISTORTION_swap(%d -> %d) = %d", 
//            last_point, start_point, jump_size));

        acc.add(featureId, -jump_size);    
      }
    }
    
    return null;
  }
}
