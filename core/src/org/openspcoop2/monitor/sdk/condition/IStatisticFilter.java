package org.openspcoop2.monitor.sdk.condition;

import org.openspcoop2.monitor.sdk.constants.LikeMode;
import org.openspcoop2.monitor.sdk.constants.MessageType;
import org.openspcoop2.monitor.sdk.exceptions.SearchException;
import org.openspcoop2.monitor.sdk.statistic.StatisticResourceFilter;
import org.openspcoop2.monitor.sdk.statistic.StatisticFilter;

import java.util.Collection;
import java.util.List;

/**
 * IStatisticFilter
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IStatisticFilter extends IFilter {

	
	/* ************ ID STATISTICA *********** */
		
	/**
	 * optional statistic id
	 * 
	 * @param idStatistic statistic id
	 */
	public void setIdStatistic(String idStatistic);
	
	
	
	
	/* ************ STATE OF EXPRESSION *********** */
	
	/**
	 * Make the negation of the expression
	 * 
	 * @return the instance of itself
	 * @throws ExpressionNotImplementedException Method not implemented
	 * @throws ExpressionException Error Processing
	 */
	@Override
	public IStatisticFilter not() throws SearchException;
	
	/**
	 * Use the conjunction "AND" for all expressions
	 * 
	 * @return the instance of itself
	 * @throws ExpressionNotImplementedException Method not implemented
	 * @throws ExpressionException Error Processing
	 */
	@Override
	public IStatisticFilter and() throws SearchException;

	/**
	 * Use the conjunction "OR" for all expressions
	 * 
	 * @return the instance of itself
	 * @throws ExpressionNotImplementedException Method not implemented
	 * @throws ExpressionException Error Processing
	 */
	@Override
	public IStatisticFilter or() throws SearchException;
	
	
	
	/* ************ COMPARATOR *********** */
	
	/**
	 * Create an expression of equality
	 * Example:  (field = value)
	 * 
	 * @param resourceID Resource identifier
	 * @param value Value
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IStatisticFilter equals(StatisticResourceFilter resourceID, Object value) throws SearchException;

	/**
	 * Create an expression of inequality
	 * Example:  (field <> value)
	 * 
	 * @param resourceID Resource identifier
	 * @param value Value
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IStatisticFilter notEquals(StatisticResourceFilter resourceID, Object value) throws SearchException;

	/**
	 * Create an expression of equality to each resource in the collection of the keys of the Map
	 * Use the conjunction "AND" for all expressions
	 * Example:  ( field[0]=values[0] AND field[1]=values[1] ..... AND field[N]=values[N] )
	 * 
	 * @param propertyNameValues Map that contains identifiers and their values
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IStatisticFilter allEquals(List<StatisticFilter> propertyNameValues) throws SearchException;

	/**
	 * Create an expression of inequality to each resource in the collection of the keys of the Map
	 * Use the conjunction "AND" for all expressions
	 * Example:  ( field[0]<>values[0] AND field[1]<>values[1] ..... AND field[N]<>values[N] )
	 * 
	 * @param propertyNameValues Map that contains identifiers and their values
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IStatisticFilter allNotEquals(List<StatisticFilter> propertyNameValues)
	throws SearchException;
	
	/**
	 * Create an expression of equality to each resource in the collection of the keys of the Map
	 * Use the conjunction defined by <var>andConjunction</var> for all expressions
	 * Example:  ( field[0]=values[0] <var>andConjunction</var> field[1]=values[1] ..... <var>andConjunction</var> field[N]=values[N] )
	 * 
	 * @param propertyNameValues Map that contains identifiers and their values
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IStatisticFilter allEquals(List<StatisticFilter> propertyNameValues,boolean andConjunction) throws SearchException;

	/**
	 * Create an expression of inequality to each resource in the collection of the keys of the Map
	 * Use the conjunction defined by <var>andConjunction</var> for all expressions
	 * Example:  ( field[0]<>values[0] <var>andConjunction</var> field[1]<>values[1] ..... <var>andConjunction</var> field[N]<>values[N] )
	 * 
	 * @param propertyNameValues Map that contains identifiers and their values
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IStatisticFilter allNotEquals(List<StatisticFilter> propertyNameValues,boolean andConjunction)
	throws SearchException;

	/**
	 * Create an expression "greaterThan"
	 * Example:  (field > value)
	 * 
	 * @param resourceID Resource identifier
	 * @param value Value
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IStatisticFilter greaterThan(StatisticResourceFilter resourceID, Object value)
	throws SearchException;
	
	/**
	 * Create an expression "greaterEquals"
	 * Example:  (field >= value)
	 * 
	 * @param resourceID Resource identifier
	 * @param value Value
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IStatisticFilter greaterEquals(StatisticResourceFilter resourceID, Object value)
	throws SearchException;
	
	/**
	 * Create an expression "lessThan"
	 * Example:  (field < value)
	 * 
	 * @param resourceID Resource identifier
	 * @param value Value
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IStatisticFilter lessThan(StatisticResourceFilter resourceID, Object value)
	throws SearchException;
	
	/**
	 * Create an expression "lessEquals"
	 * Example:  (field <= value)
	 * 
	 * @param resourceID Resource identifier
	 * @param value Value
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IStatisticFilter lessEquals(StatisticResourceFilter resourceID, Object value)
	throws SearchException;
	
	
	
	
	
	
	
	
	
	/* ************ SPECIAL COMPARATOR *********** */
	
	/**
	 * Create an expression "is null"
	 * Example:  ( field is null )
	 * 
	 * @param resourceID Resource identifier
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IStatisticFilter isNull(StatisticResourceFilter resourceID) throws SearchException;
	
	/**
	 * Create an expression "is not null"
	 * Example:  ( field is not null )
	 * 
	 * @param resourceID Resource identifier
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IStatisticFilter isNotNull(StatisticResourceFilter resourceID) throws SearchException;
	
	/**
	 * Create an expression "is empty"
	 * Example:  ( field = '' )
	 * 
	 * @param resourceID Resource identifier
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IStatisticFilter isEmpty(StatisticResourceFilter resourceID) throws SearchException;
	
	/**
	 * Create an expression "is not empty"
	 * Example:  ( field <> '' )
	 * 
	 * @param resourceID Resource identifier
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IStatisticFilter isNotEmpty(StatisticResourceFilter resourceID) throws SearchException;
	
	
	
	
	
	
	
	
	/* ************ BETWEEN *********** */
	
	/**
	 * Create an expression "between"
	 * Example:  ( field BEETWEEN lower AND high )
	 * 
	 * @param resourceID Resource identifier
	 * @param lower Lower limit value to be applied to the constraint
	 * @param high Higher limit value to be applied to the constraint
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IStatisticFilter between(StatisticResourceFilter resourceID, Object lower, Object high)
	throws SearchException;
	
	
	
	
	
	
	
	/* ************ LIKE *********** */
	
	/**
	 * Create an expression "like"
	 * Example:  ( field like '%value%' )
	 * 
	 * @param resourceID Resource identifier
	 * @param value Value
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IStatisticFilter like(StatisticResourceFilter resourceID, String value) throws SearchException;
	
	/**
	 * Create an expression "like"
	 * Example:  
	 * 	 LikeMode.ANYWHERE -> ( field like '%value%' )
	 *   LikeMode.EXACT -> ( field like 'value' )
	 *   LikeMode.END -> ( field like '%value' )
	 *   LikeMode.START -> ( field like 'value%' )
	 * 
	 * @param resourceID Resource identifier
	 * @param value Value
	 * @param mode LikeMode
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IStatisticFilter like(StatisticResourceFilter resourceID, String value, LikeMode mode) throws SearchException;
	
	/**
	 * Create an expression "like" case-insensitive
	 * Example:  ( toLower(field) like '%toLower(value)%' )  
	 * 
	 * @param resourceID Resource identifier
	 * @param value Value
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IStatisticFilter ilike(StatisticResourceFilter resourceID, String value) throws SearchException;
	
	/**
	 * Create an expression "like" case-insensitive
	 * Example:  
	 *   LikeMode.ANYWHERE -> ( toLower(field) like '%toLower(value)%' )
	 *   LikeMode.EXACT -> ( toLower(field) like 'toLower(value)' )
	 *   LikeMode.END -> ( toLower(field) like '%toLower(value)' )
	 *   LikeMode.START -> ( toLower(field) like 'toLower(value)%' )
	 * 
	 * @param resourceID Resource identifier
	 * @param value Value
	 * @param mode LikeMode
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IStatisticFilter ilike(StatisticResourceFilter resourceID, String value, LikeMode mode) throws SearchException;

	
	
	
	
	

	/* ************ IN *********** */
	
	/**
	 * Create an expression "in" verifying that the value of <var>field</var> is one of those given in the param <var>values</var>
	 * Example:  ( field IN ('values[0]', 'values[1]', ... ,'values[n]')  )
	 * 
	 * @param resourceID Resource identifier
	 * @param values Values
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IStatisticFilter in(StatisticResourceFilter resourceID, Object ... values)
	throws SearchException;
		
	/**
	 * Create an expression "in" verifying that the value of <var>field</var> is one of those given in the param <var>values</var>
	 * Example:  ( field IN ('values[0]', 'values[1]', ... ,'values[n]')  )
	 * 
	 * @param resourceID Resource identifier
	 * @param values Values
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	public IStatisticFilter in(StatisticResourceFilter resourceID, Collection<?> values)
	throws SearchException;
	
	
	
	
	
	/* ************ CONJUNCTION *********** */
	
	/**
	 *  Adds to the expression in the conjunction "AND" of the espressions given as a parameters
	 *  Example: ( exp1 AND exp2  )
	 *  
	 * @param exp1 Filter combined in AND with <var>exp1</var>
	 * @param exp2 Filter combined in AND with <var>exp2</var>
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	@Override
	public IStatisticFilter and(IFilter exp1, IFilter exp2) throws SearchException;
	
	/**
	 *  Adds to the expression in the conjunction "AND" of the espressions given as a parameters
	 *  Example: ( expressions[1] AND expressions[2] AND ... expressions[N] )
	 *  
	 * @param expressions Expressions combined in AND
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	@Override
	public IStatisticFilter and(IFilter... expressions) throws SearchException;
	
	/**
	 *  Adds to the expression in the conjunction "OR" of the espressions given as a parameters
	 *  Example: ( exp1 OR exp2  )
	 *  
	 * @param exp1 Filter combined in OR with <var>exp1</var>
	 * @param exp2 Filter combined in OR with <var>exp2</var>
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	@Override
	public IStatisticFilter or(IFilter exp1, IFilter exp2) throws SearchException;
	
	/**
	 *  Adds to the expression in the conjunction "OR" of the espressions given as a parameters
	 *  Example: ( expressions[1] OR expressions[2] OR ... expressions[N] )
	 *  
	 * @param expressions Expressions combined in OR
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	@Override
	public IStatisticFilter or(IFilter... expressions) throws SearchException;
	
	
	
	
	
	
	
	
	/* ************ NEGATION *********** */
	
	/**
	 * Adding to the negation of the expression passed as parameter
	 * Example: ( NOT expression )
	 * 
	 * @param expression Expression
	 * @return the instance of itself enriched with expression that represents the constraint
	 * @throws SearchException
	 */
	@Override
	public IStatisticFilter not(IFilter expression) throws SearchException;

	
	
	
	
	
	
	/* ************ MESSAGE TYPE *********** */

	@Override
	public IStatisticFilter setMessageType(MessageType messageType) throws SearchException;
	
	
	
	
	/* ************ NEW FILTER *********** */
	
	@Override
	public IStatisticFilter newFilter() throws SearchException;

}