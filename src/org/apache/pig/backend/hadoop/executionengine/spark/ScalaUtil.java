package org.apache.pig.backend.hadoop.executionengine.spark;

import java.util.List;

import org.apache.pig.data.Tuple;

import scala.Tuple2;
import scala.collection.JavaConversions;
import scala.collection.Seq;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;

/**
 * 
 * @author zhangbaofeng
 *
 */
public class ScalaUtil {

	/**
	 * Scala 2.10 use ClassTag to replace ClassManifest
	 */
    public static <T> ClassTag<T> getClassTag(Class<T> clazz) {
    	return ClassTag$.MODULE$.apply(clazz);
    }
    
    @SuppressWarnings("unchecked")
	public static <K, V> ClassTag<Tuple2<K, V>> getTuple2ClassTag() {
    	return (ClassTag<Tuple2<K, V>>)(Object) getClassTag(Tuple2.class);
    }
    
    public static <T> Seq<T> toScalaSeq(List<T> list) {
        return JavaConversions.asScalaBuffer(list);
    }
    
    public static void main(String[] args) {
		System.out.print(ClassTag$.MODULE$.apply(Tuple.class));
	}
}
