/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.search.aggregations.bucket.histogram;

import org.elasticsearch.common.xcontent.ObjectParser;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.search.DocValueFormat;
import org.elasticsearch.search.aggregations.ParsedMultiBucketAggregation;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ParsedHistogram extends ParsedMultiBucketAggregation implements Histogram {

    @Override
    protected String getType() {
        return HistogramAggregationBuilder.NAME;
    }

    @Override
    public List<? extends Histogram.Bucket> getBuckets() {
        return buckets.stream().map(bucket -> (Histogram.Bucket) bucket).collect(Collectors.toList());
    }

    private static ObjectParser<ParsedHistogram, Void> PARSER =
            new ObjectParser<>(ParsedHistogram.class.getSimpleName(), true, ParsedHistogram::new);
    static {
        declareMultiBucketAggregationFields(PARSER,
                parser -> ParsedBucket.fromXContent(parser, false),
                parser -> ParsedBucket.fromXContent(parser, true));
    }

    public static ParsedHistogram fromXContent(XContentParser parser, String name) throws IOException {
        ParsedHistogram aggregation = PARSER.parse(parser, null);
        aggregation.setName(name);
        return aggregation;
    }

    static class ParsedBucket extends ParsedMultiBucketAggregation.ParsedBucket<Double> implements Histogram.Bucket {

        @Override
        public String getKeyAsString() {
            String keyAsString = super.getKeyAsString();
            if (keyAsString != null) {
                return keyAsString;
            } else {
                return DocValueFormat.RAW.format((Double) getKey());
            }
        }

        static ParsedBucket fromXContent(XContentParser parser, boolean keyed) throws IOException {
            return parseXContent(parser, keyed, ParsedBucket::new, XContentParser::doubleValue);
        }
    }
}