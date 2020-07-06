/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.example;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SocketTextStreamFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.walkthrough.common.entity.Transaction;
import org.apache.flink.walkthrough.common.source.TransactionSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Skeleton for a Flink Streaming Job.
 *
 * <p>For a tutorial how to write a Flink streaming application, check the
 * tutorials and examples on the <a href="http://flink.apache.org/docs/stable/">Flink Website</a>.
 *
 * <p>To package your application into a JAR file for execution, run
 * 'mvn clean package' on the command line.
 *
 * <p>If you change the name of the main class (with the public static void main(String[] args))
 * method, change the respective entry in the POM.xml file (simply search for 'mainClass').
 */
public class StreamingJob {

    public static void main(String[] args) throws Exception {
        // set up the streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

            DataStream<Transaction> text = env.addSource(new TransactionSource());

            DataStream<Tuple2<Long, Integer>> counts =
                    // split up the lines in pairs (2-tuples) containing: (word,1)
                    text.flatMap(new Tokenizer())
                           .keyBy(new KeySelector<Tuple2<Long, Integer>, Object>() {
                               @Override
                               public Object getKey(Tuple2<Long, Integer> value) throws Exception {
                                   return value.f0;
                               }
                           })
                            .sum(1);

            counts.print();

        // execute program
        env.execute("Flink Streaming Java API Skeleton");
    }

    // User-defined functions
    public static class Tokenizer implements FlatMapFunction<Transaction, Tuple2<Long, Integer>> {

        @Override
        public void flatMap(Transaction value, Collector<Tuple2<Long, Integer>> out) throws Exception {
            out.collect(new Tuple2<>( value.getAccountId(), 1));
        }
    }
}


