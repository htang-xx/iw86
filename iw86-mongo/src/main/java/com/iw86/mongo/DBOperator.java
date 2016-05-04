/**
 * 
 */
package com.iw86.mongo;

/**
 * @author tanghuang
 *
 */
public interface DBOperator {
	
	String EQ = "$eq";
	String LT = "$lt";
	String LTE = "$lte";
	String GT = "$gt";
	String GTE = "$gte";
	String IN = "$in";
	String EXISTS = "$exists";
	String OR = "$or";
	String ELEMMATCH = "$elemMatch";
	String SLICE = "$slice";
	String NE = "$ne";
	String SUM = "$sum";
	String AVG = "$avg";
	String EACH = "$each";
	String SORT = "$sort";
	String MATCH = "$match";
	String PROJECT = "$project";
	String GROUP = "$group";
	String SET = "$set";
	String UNSET = "$unset";
	String INC = "$inc";
	String PUSH = "$push";
	String PULL = "$pull";

}
