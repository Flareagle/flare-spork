package org.apache.pig.backend.hadoop.executionengine.spark.converter;

import java.io.Serializable;
import java.util.List;

import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.backend.hadoop.executionengine.physicalLayer.Result;
import org.apache.pig.backend.hadoop.executionengine.physicalLayer.relationalOperators.POForEach;
import org.apache.pig.backend.hadoop.executionengine.spark.ScalaUtil;
import org.apache.pig.backend.hadoop.executionengine.spark.SparkUtil;
import org.apache.pig.data.Tuple;
import org.apache.spark.rdd.RDD;

import scala.collection.Iterator;
import scala.collection.JavaConversions;
import scala.runtime.AbstractFunction1;


/**
 * Convert that is able to convert an RRD to another RRD using a POForEach
 * @author billg
 */
public class ForEachConverter implements POConverter<Tuple, Tuple, POForEach> {

    @Override
    public RDD<Tuple> convert(List<RDD<Tuple>> predecessors, POForEach physicalOperator) {
        SparkUtil.assertPredecessorSize(predecessors, physicalOperator, 1);
        RDD<Tuple> rdd = predecessors.get(0);
        
        ForEachFunction forEachFunction = new ForEachFunction(physicalOperator);
        return rdd.mapPartitions(forEachFunction, false, ScalaUtil.getClassTag(Tuple.class));
    }

    @SuppressWarnings("serial")
	private static class ForEachFunction extends AbstractFunction1<Iterator<Tuple>, Iterator<Tuple>>
            implements Serializable {

        private POForEach poForEach;

        private ForEachFunction(POForEach poForEach) {
            this.poForEach = poForEach;
        }

        public Iterator<Tuple> apply(Iterator<Tuple> i) {
            final java.util.Iterator<Tuple> input = JavaConversions.asJavaIterator(i);
            Iterator<Tuple> output = JavaConversions.asScalaIterator(
            	new POOutputConsumerIterator(input) {
            		protected void attach(Tuple tuple) {
            			poForEach.setInputs(null);
            			poForEach.attachInput(tuple);
            		}

            		protected Result getNextResult() throws ExecException {
            			return poForEach.getNextTuple();
            		}
            	}
            );
            return output;
        }
    }
}
