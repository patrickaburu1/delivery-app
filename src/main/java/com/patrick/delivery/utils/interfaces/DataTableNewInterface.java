package com.patrick.delivery.utils.interfaces;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Edudev on 5/17/2017.
 */
public interface DataTableNewInterface {

    /**
     * When one just wants to return an empty result set
     *
     * @return Map<String       ,               Object>
     */
    Object emptyResultSet();


    /**
     * When one just wants to return an empty result set
     *
     * @return Map<String       ,               Object>
     */
    Object customResultSet(Map<String, Object> map);

    /**
     * Indicate whether we have prepared a native sql statement
     *
     * @param state
     * @return DataTableNewInterface
     */
    DataTableNewInterface nativeSQL(boolean state);

    /**
     * Indicate whether ordering should be avoided (like in the case of an extremely large table
     *
     * @param noOrder
     * @return DataTableNewInterface
     */
    DataTableNewInterface noOrder(boolean noOrder);


    /**
     * Called to re-initialise the parameters as the class is ever on
     */

    /**
     * Specify the columns that will appear in the final result set in order to
     * assist this class in building the information needed to render the
     * datatable result map
     *
     * @param select
     * @return DataTableNewInterface
     */
    DataTableNewInterface select(String select);

    /**
     * Specify the tables where the information will be fetched from i.e. the
     * parent table and all the respective joins
     *
     * @param from
     * @return DataTableNewInterface
     */
    DataTableNewInterface from(String from);

    /**
     * Specify the conditions that will be applied to the query. This will help
     * in building the filter used by datatables
     *
     * @param where
     * @return DataTableNewInterface
     */

    DataTableNewInterface where(String where);

    /**
     * Get the HQL that will be used to generate the result set
     *
     * @param setting
     * @return String
     */
    String getSQL(String setting);

    /**
     * Apply the join clause in order to include LEFT JOINS in the query
     * functions
     *
     * @param join
     * @return DataTableNewInterface
     */
    DataTableNewInterface join(String join);

    /**
     * Apply the join clause in order to include INNER JOINS in the query
     * functions
     *
     * @param innerJoin
     * @return DataTableNewInterface
     */
    DataTableNewInterface innerJoin(String innerJoin);

    /**
     * Apply the group by clause in order to properly support the aggregate
     * functions
     *
     * @param groupBy
     * @return DataTableNewInterface
     */
    DataTableNewInterface groupBy(String groupBy);

    /**
     * Apply the havingBy clause in order to properly support the aggregate
     * functions
     *
     * @param havingCount
     * @return DataTableNewInterface
     */
    DataTableNewInterface havingCount(String havingCount);

    /**
     * Apply the order by clause in order to specify the column and direction of ordring
     * functions
     *
     * @param orderBy
     * @return DataTableNewInterface
     */
    DataTableNewInterface orderBy(String orderBy);


    /**
     * Set the parameter bound to the parameterised query passed in the
     * conditions
     *
     * @param key
     * @param value
     * @return DataTableInterface
     */
    DataTableNewInterface setParameter(String key, Object value);


    /**
     * Render the table
     */
    Object showTable();

    /**
     * Set the formatter that will be used to format the response generated by
     * the class
     *
     * @param formatter
     * @return DataTableInterface
     */
    DataTableNewInterface setFormatter(RowFormatInterface formatter);

    /**
     * Bind multiple values to a named query parameter.
     *
     * @param key
     * @param value
     * @return DataTableInterface
     */
    DataTableNewInterface setParameterList(String key, Collection value);

    /**
     * Allow one to add multiple parameters
     *
     * @param map
     * @return DataTableInterface
     */
    DataTableNewInterface setParameters(Map<String, Object> map);

    /**
     * Get the HQL that will be used to generate the result set
     *
     * @param setting
     * @return String
     */
    String getHQL(String setting);

    /**
     * Get the parameters used to generate the result set
     *
     * @return Map<String   ,       Object>
     */
    Map<String, Object> getParameters();

    /**
     * Get parameters for the export function
     *
     * @param
     * @return List<Map   <   String   ,       Object>>
     */
    List<Map<String, Object>> getExportParams();

    Map<String, Object> showTable(Function<Object[], Object[]> func);

    /**
     * Allow one to specify the columns for footer totals
     *
     * @param key
     * @param columnName
     * @return DataTableInterface
     */
    DataTableNewInterface setFooterColumn(String key, String columnName);

    /**
     * Allow one to specify the columns to order
     *
     * @param columns
     * @param order
     * @return
     */
    DataTableNewInterface setOrderingColumns(String columns, String order);

    /**
     * Determin that datatable filters exist
     */
    List<Map<String, Object>> filtersExist(HttpServletRequest request);

    /**
     * Determin that datatable filters exist
     */
    List<Map<String, Object>> filtersExist2(HttpServletRequest request);

}