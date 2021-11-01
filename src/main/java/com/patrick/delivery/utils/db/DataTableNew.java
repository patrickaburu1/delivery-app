package com.patrick.delivery.utils.db;

import com.patrick.delivery.utils.interfaces.DataTableNewInterface;
import com.patrick.delivery.utils.interfaces.RowFormatInterface;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Transactional(readOnly = true)
@Service("dataTableNewService")
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class DataTableNew implements DataTableNewInterface {
    private static final Logger logger = LoggerFactory.getLogger(DataTableNew.class);
    private String[] columns;
    private final List<RowFormatInterface> _formatChain = new ArrayList<>();
    private final Map<String, Collection> _listParams = new HashMap<>();
    private final Map<String, Object> _params = new HashMap<>();
    private final Map<String, Object> _footerColumns = new LinkedHashMap<>();
    private final Map<String, Object> _orderingColumns = new HashMap<>();
    private final List<String> _whereParams = new ArrayList<>();
    private final List<String> _columnNames = new ArrayList<>();
    private boolean _nativeSQL = false;
    private boolean _noOrder = false;
    private boolean _noGroup = false;
    private String _groupBy = "";
    private String _orderBy = "";
    private String _havingCount = "";

    private final StringBuilder _selectParams = new StringBuilder();
    private final StringBuilder _fromParams = new StringBuilder();
    private final StringBuilder _joinParams = new StringBuilder();
    private final StringBuilder _innerJoinParams = new StringBuilder();

    @Autowired
    private HttpServletRequest request;

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Object emptyResultSet() {
        Map<String, Object> map = new HashMap<>();
        String requestDraw = request.getParameter("draw");
        int draw = Integer.parseInt((null != requestDraw)?requestDraw:"0");

        map.put("draw", draw);
        map.put("data", new HashMap<>());
        map.put("recordsTotal", 0);
        map.put("recordsFiltered", 0);
        return map;
    }

    @Override
    public Object customResultSet(Map<String, Object> map) {
//        Map<String,Object>resultMap = new HashMap<>();
//        map.forEach((k,v) -> resultMap.put(k,v));
        return map;

    }


    @Override
    public Object showTable() {

        //StringQueryBuilder nativeQueryBuilder = new StringQueryBuilder(true,session,StringQueryBuilder.NATIVE_QUERY);
        Map<String, Object> map = new HashMap<>();
        String draw = request.getParameter("draw");
        map.put("draw", Integer.parseInt((null != draw)? draw : "0"));
        map.put("data", buildResultSet(""));
        map.put("recordsTotal", buildResultSet("total"));
        map.put("recordsFiltered", buildResultSet("filtered-total"));

        return map;
    }

    @Override
    public Map<String, Object> showTable(Function<Object[], Object[]> func) {
        Map<String, Object> map = new HashMap<>();

        // The result set
        String draw = request.getParameter("draw");
        map.put("draw", Integer.parseInt((null != draw)?draw:"0"));
        map.put("data", buildResultSet(""));
        map.put("recordsTotal", buildResultSet("total").iterator().next());
        map.put("recordsFiltered", buildResultSet("filtered-total").iterator().next());

        if (!_footerColumns.isEmpty()) {
            map.put("footerTotals", buildResultSet("footer-totals"));
        }

        List<Object> data = new ArrayList<>();

        // If the formatter has not been set
        if (_formatChain.isEmpty()) {
            for (Object[] row : buildResultSet("")) {
                // Set the formated data
                data.add(func.apply(row));
            }
        } // When formatting the row
        else {
            for (Object[] row : buildResultSet("")) {
                Object[] row2;

                row2 = func.apply(row);
                // Loop through the chain
                for (RowFormatInterface fmt : _formatChain) {
                    row2 = fmt.formatRow(row2);
                }
                // Set the formated data
                data.add(row2);
            }
        }

        map.put("data", data);

        // Return the result set
        return map;
    }

    /**
     * Allow one to specify the columns for footer totals
     *
     * @param key
     * @param columnName
     * @return DataTableInterface
     */
    @Override
    public DataTableNewInterface setFooterColumn(String key, String columnName) {
        //Place the collection in the footer bag
        _footerColumns.put(key, columnName);
        //Allow the chaining of the params
        return this;
    }

    /**
     * Allow one to specify the columns to order
     *
     * @param columns
     * @param order
     * @return
     */
    @Override
    public DataTableNewInterface setOrderingColumns(String columns, String order) {
        _orderingColumns.put(columns, order);
        //Allow the chaining of the params
        return this;
    }

    /**
     * Specify the columns that will appear in the final result set in order to
     * assist this class in building the information needed to render the
     * datatable result map
     *
     * @param select
     * @return DataTableNewInterface
     */
    @Override
    public DataTableNewInterface select(String select) {
        //get string array from select statement, with (,) as the delimeter
        int result = 0;
        for (String column : select.split(",")) {
            //replace the <;> used in functions with <,> to make a valid sql statement
            column = column.replace(";", ",");

            //get the column used (decide if it has an alias) and set it
            int c;
            c = column.toLowerCase().lastIndexOf(" as ");
            if (c > 0 && !column.toLowerCase().contains("cast"))
                column = column.substring(0, c);

            _columnNames.add(column);

            //append the column to the select statement builder
            if (_selectParams.length() > 0)
                _selectParams.append(", ").append(column);
            else
                _selectParams.append(column);

            //append the column identifier
            _selectParams.append(" AS col_").append(result);

            result++;

        }
        return this;
    }

    @Override
    public DataTableNewInterface from(String from) {
        // Set the table stuff
        if (_fromParams.length() > 0)
            _fromParams.append(" ").append(from);
        else _fromParams.append(from);

        return this;
    }

    @Override
    public DataTableNewInterface where(String where) {
        _whereParams.add(where);

        return this;
    }

    @Override
    public DataTableNewInterface groupBy(String groupBy) {
        _groupBy = groupBy;
        return this;
    }

    @Override
    public DataTableNewInterface havingCount(String havingCount) {
        _havingCount = havingCount;
        return this;
    }


    @Override
    public DataTableNewInterface orderBy(String orderBy) {
        _orderBy = orderBy;
        return this;
    }


    @Override
    public DataTableNewInterface join(String join) {
        _joinParams.append(" LEFT JOIN ").append(join);
        return this;
    }

    @Override
    public DataTableNewInterface innerJoin(String innerJoin) {
        _innerJoinParams.append(" INNER JOIN ").append(innerJoin);
        return this;
    }


    @Override
    public DataTableNewInterface nativeSQL(boolean state) {
        _nativeSQL = true;
        return this;
    }

    @Override
    public DataTableNewInterface noOrder(boolean noOrder) {
        _noOrder = true;
        return this;
    }

    /**
     * Set the parameter bound to the parameterised query passed in the
     * conditions
     *
     * @param key
     * @param value
     * @return DataTableInterface
     */
    @Override
    public DataTableNewInterface setParameter(String key, Object value) {
        _params.put(key, value);
        return this;
    }


    private List<Object[]> buildResultSet(String setting) {

        Session session = entityManager.unwrap(Session.class);
        StringBuilder queryBuilder = buildQuery(setting);
        StringBuilder hql = buildHQL(setting);

        Query query = _nativeSQL ? session.createNativeQuery(queryBuilder.toString()) : session.createQuery(hql.toString());
        if (!setting.equals("filtered-total") && !setting.equals("total"))
            query = setLimit(queryBuilder, session);
        if (!_params.isEmpty()) {
            for (Map.Entry<String, Object> param : _params.entrySet()) {
                try {
                    assert query != null;
                    if (!query.getParameterMetadata().getNamedParameterNames().isEmpty())
                        if (query.getParameterMetadata().getNamedParameterNames().contains(param.getKey()))
                            query.setParameter(param.getKey(), param.getValue());
                } catch (Exception e) {
                    logger.info("Params {} ", query.getParameterMetadata().getNamedParameterNames());
                    logger.error(new Object() {
                    }.getClass().getEnclosingMethod().getName(), e);
                }
            }
        }
        if (!_listParams.isEmpty()) {
            for (Map.Entry<String, Collection> p : _listParams.entrySet()) {
                try {
                    query.setParameterList(p.getKey(), p.getValue());
                } catch (Exception e) {
                    logger.error(new Object() {
                    }.getClass().getEnclosingMethod().getName(), e);
                }
            }
        }
        if (query != null)
            return query.getResultList();

        return new ArrayList<>();
    }


    private StringBuilder buildQuery(String setting) {
        StringBuilder query = new StringBuilder();
        StringBuilder params = new StringBuilder();
        StringBuilder generalBuilder = new StringBuilder();
        if (setting.equals("filtered-total") || setting.equals("total")) {
            String column = "";
            if (!_nativeSQL) {
                //hql here
            } else {
                query.append("SELECT ").append(_selectParams).append(" FROM ").append(_fromParams);
            }
        }
        //generating the statement
        else
            query.append("SELECT ").append(_selectParams).append(" FROM  ").append(_fromParams);

        if (_joinParams.length() > 0)
            query.append(_joinParams);
        else if (_innerJoinParams.length() > 0)
            query.append(_innerJoinParams);

        //add the conditions
        if (_whereParams.size() > 0) {
            for (String item : _whereParams) {
                if (params.length() > 0)
                    params.append(" AND ");

                params.append("(").append(item).append(")");
            }

            query.append(" WHERE ").append(params);
        }

        //ADD THE FILTER HERE
        generalBuilder = setFilter(setting);
        if (generalBuilder.length() > 0)
            query.append((_whereParams.size() > 0) ? " AND " : " WHERE ").append(generalBuilder);

        //Set the group by clause
        if (!_groupBy.isEmpty())
            query.append(" GROUP BY ").append(_groupBy);

        //set the having by clause
        if (!_havingCount.isEmpty())
            query.append(" HAVING COUNT").append(_havingCount);

        //Add the order that will be applied to the result set
        if (!setting.equals("filtered-total") && !setting.equals("total") && !setting.equals("footer-totals")) {
            generalBuilder = setOrder();
            if (generalBuilder != null && generalBuilder.length() > 0) {
//                //System.err.println("No order is set to " + _noOrder);
                if (!_noOrder) {
                    if (!_orderBy.isEmpty())
                        query.append(" ORDER BY ").append(_orderBy);

                    else
                        query.append(" ORDER BY ").append(generalBuilder);
                }
            }
        }


        //if we're implementing a native query
        if (_nativeSQL && (setting.equals("filtered-total") || setting.equals("total"))) {
            generalBuilder = new StringBuilder();
            generalBuilder.append("SELECT COUNT(col_0) FROM (").append(query).append(") count");
            query = generalBuilder;
        }
        return query;
    }

    // Take care of spaces in the search string
    private String getGlobalSearchString(String searchString){
        String[] arr = searchString.split(" ");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("%");
        for (String s : arr) {
            stringBuilder.append(s).append("%");
        }
        if(arr.length==0) stringBuilder.append("%");

        return stringBuilder.toString();
    }

    /**
     * Create the filter query used to filter the information in the result set
     *
     * @return String
     */
    private StringBuilder setFilter(String setting) {
        //columns in which the filter is applied
        HttpSession session = request.getSession();
        StringBuilder searchColumns = new StringBuilder();
        StringBuilder filter = new StringBuilder();
        if (!setting.equals("total")) {
            String globalSearch = request.getParameter("search[value]");
            if (null != globalSearch && !globalSearch.isEmpty()) {
                String theSearchString = getGlobalSearchString(globalSearch);
                for (int i = 0; i < getNumberOfColumns(); i++) {
                    //the searchable parameter returns a string of either true or false
                    if (request.getParameter("columns[" + i + "][searchable]").equals("true")) {
                        if (columnIsNotAggregate(_columnNames.get(i))) {
                            if (!_columnNames.get(i).contains("COUNT(")
                                    && !_columnNames.get(i).contains("SUM(")
                                    && !_columnNames.get(i).contains("avg(")
                                    && !_columnNames.get(i).contains("COUNT (")
                                    && !_columnNames.get(i).contains("avg (")
                                    && !_columnNames.get(i).contains("SUM (")) {
                                if (searchColumns.length() > 0)
                                    searchColumns.append(" OR ");

                                searchColumns.append("lower(").append(_columnNames.get(i)).append(")").append(" LIKE lower(:searchTerm)");
                            }
                        }
                    }
                }
                session.setAttribute("searchParam", theSearchString);
                _params.put("searchTerm", theSearchString);
                filter.append("(").append(searchColumns).append(")");
            }
        }
        return filter;
    }

    /**
     * Count the number of columns on the datatable request
     *
     * @return int
     */
    public int getNumberOfColumns() {
        Pattern p = Pattern.compile("columns\\[[0-9]+\\]\\[data\\]");
        @SuppressWarnings("rawtypes")
        Enumeration params = request.getParameterNames();
        List<String> lstOfParams = new ArrayList<>();
        while (params.hasMoreElements()) {
            String paramName = ((String) params.nextElement()).replace("[]", "");
            Matcher m = p.matcher(paramName);
            if (m.matches()) {
                lstOfParams.add(paramName);
            }
        }
        return lstOfParams.size();
    }


    /**
     * Omit the aggregate functions as you set the filter
     *
     * @param column
     * @return Boolean
     */
    private boolean columnIsNotAggregate(String column) {
        String temp = column.toUpperCase().replace(" ", "");
        return !(temp.contains("MIN(") || temp.contains("MAX(") || temp.contains("SUM(") || temp.contains("COUNT("));
    }


    /**
     * Define the limit that will be applied to the result set
     *
     * @param session
     * @return Query
     */
    private Query setLimit(StringBuilder queryBuilder, Session session) {
        Query query = _nativeSQL ? session.createNativeQuery(queryBuilder.toString()) : session.createQuery(queryBuilder.toString
                ());
        String requestStart = request.getParameter("start");
        String requestLength = request.getParameter("length");
        int start = Integer.parseInt((null != requestStart)?requestStart:"0");
        int length = Integer.parseInt((null != requestLength)?requestLength:"0");

        if (null != requestStart && !requestStart.isEmpty() && start != -1) {
            int offset = start / length;


            //coz the query can end up being null because of it being defined as an alternative in the code above
            if (query != null) {
                query.setFirstResult(offset * length);
                query.setMaxResults(length);
            }

        }

        return query;
    }

    /**
     * Set the aux conditions specified in the request parameters used to limit
     * and order the result set
     *
     * @return String
     */
    private StringBuilder setOrder() {
        // If there are no parameters defined
        String requestOrder = request.getParameter("order[0][column]");
        if (null == requestOrder || requestOrder.isEmpty())
            return null;

        //build the order
        StringBuilder order = new StringBuilder();

        if (!_orderingColumns.isEmpty()) {
            for (Map.Entry<String, Object> p : _params.entrySet()) {
                order.append(p.getKey()).append(" ").append(p.getValue());
                break;
            }
        } else {
            String orderColumn = request.getParameter("order[0][column]");
            int sortingColumn = Integer.parseInt((null != orderColumn)?orderColumn:"0");

            if ("true".equals(request.getParameter("columns[" + sortingColumn + "][orderable]"))) {
                order.append(_columnNames.get(sortingColumn)).append(" ");
                order.append((request.getParameter("order[0][dir]")).equals("asc") ? "ASC" : "DESC");
            }
        }

        return order;
    }

    /**
     * Get the SQL that will be used to generate the result set
     *
     * @param setting
     * @return String
     */
    @Override
    public String getSQL(String setting) {
        return buildQuery((null == setting) ? "" : setting).toString();
    }

    /**
     * Get the HQL that will be used to generate the result set
     *
     * @param setting
     * @return String
     */
    @Override
    public String getHQL(String setting) {
        return buildHQL((null == setting) ? "" : setting).toString();
    }

    /**
     * Build the HQL used when building the query
     *
     * @param setting
     * @return String
     */
    private StringBuilder buildHQL(String setting) {
        // The HQL to build
        StringBuilder hql = new StringBuilder();
        StringBuilder ss = new StringBuilder();

        // If we are getting the number of records
        switch (setting) {
            case "filtered-total":
            case "total":
                if (!_nativeSQL) {
                    String col = "";

                    for (String _columnName : _columnNames) {
                        if (!_columnName.contains("(")) {
                            col = _columnName;
                            break;
                        }
                    }

                    if (!col.isEmpty()) {
                        col = (_groupBy.contains(col)) ? "DISTINCT " + col : col;
                        hql.append("SELECT COUNT(").append(col).append(") FROM ").append(_fromParams);
                    }
                } else {
                    hql.append("SELECT ").append(_selectParams).append(" FROM ").append(_fromParams);
                }
                break;
            case "footer-totals":
                if (!_footerColumns.isEmpty()) {
                    StringBuilder columns = new StringBuilder();
                    for (Map.Entry<String, Object> p : _footerColumns.entrySet()) {
                        try {
                            if (columns.length() > 0) {
                                columns.append(", ");
                            }
                            columns.append(p.getValue()).append("(").append(p.getKey()).append(") ");

                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }

                    hql.append("SELECT ").append(columns).append("FROM ").append(_fromParams);
                }
                break;
            default:
                hql.append("SELECT ").append(_selectParams).append(" FROM ").append(_fromParams);
                break;
        }

        // Add the conditions
        if (_whereParams.size() > 0) {
            for (String item : _whereParams) {
                if (ss.length() > 0) {
                    ss.append(" AND ");
                }
                ss.append("(").append(item).append(")");
            }

            hql.append(" WHERE ").append(ss);
        }

        // Add the filter
        ss = setFilter(setting);
        if (ss.length() > 0) {
            hql.append((_whereParams.size() > 0) ? " AND " : " WHERE ").append(ss);
        }

        // Set the group by clause
        if (!_groupBy.isEmpty() && !setting.equals("footer-totals")) {
            hql.append(" GROUP BY ").append(_groupBy);
        }

        // Add the order that will be applied to the result set
        if (!setting.equals("filtered-total") && !setting.equals("total") && !setting.equals("footer-totals")) {
            ss = setOrder();
            if (null != ss && ss.length() > 0) {
                hql.append(" ORDER BY ").append(ss);
            }
        }
        // If we are implementing a native sql
        if (_nativeSQL && (setting.equals("filtered-total") || setting.equals("total"))) {
            ss = new StringBuilder();
            ss.append("SELECT COUNT(cc.dtcol_0) FROM (").append(hql).append(") cc");
            hql = ss;
        }
        // The string built
        return hql;
    }

    /**
     * /**
     * Set the formatter that will be used to format the response generated by
     * the class
     *
     * @param formatter
     * @return DataTableInterface
     */
    @Override
    public DataTableNewInterface setFormatter(RowFormatInterface formatter) {
        // Check if the filter is in the chain
        if (!_formatChain.contains(formatter)) {
            _formatChain.add(formatter);
        }

        // Set the
        return this;
    }


    /**
     * Allow one to add multiple parameters
     *
     * @param map
     * @return DataTableInterface
     */
    @Override
    public DataTableNewInterface setParameters(Map<String, Object> map) {
        for (Map.Entry<String, Object> p : map.entrySet()) {
            _params.put(p.getKey(), p.getValue());
        }
        // Allow the chaining of the params
        return this;
    }

    /**
     * Bind multiple values to a named query parameter.
     *
     * @param key
     * @param value
     * @return DataTableInterface
     */
    @Override
    public DataTableNewInterface setParameterList(String key, Collection value) {
        // Place the collection in the parameter bag
        _listParams.put(key, value);

        // Allow chaining
        return this;
    }

    /**
     * Get the parameters used to generate the result set
     *
     * @return
     */
    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> map = new HashMap<>();

        map.put("params", _params);
        map.put("listParams", _listParams);

        return map;
    }

    @Override
    public List<Map<String, Object>> getExportParams() {
        List<Map<String, Object>> mapList = new ArrayList<>();
        mapList.add(_params);
        return mapList;
    }

    @Override
    public List<Map<String, Object>> filtersExist(HttpServletRequest request) {

        List<Pattern> patterns = new ArrayList<>();
        Pattern p2 = Pattern.compile("columns\\[[0-9]+\\]\\[data\\]");
        Pattern p = Pattern.compile("[0-9]?\\[filters]\\[[0-9]]\\[name]");
        //0[filters][6][start-date]
        Pattern startDate = Pattern.compile("[0-9]?\\[filters]\\[[0-9]]\\[start-date]");
        Pattern endDate = Pattern.compile("[0-9]?\\[filters]\\[[0-9]]\\[end-date]");

        patterns.add(p);
        patterns.add(startDate);
        patterns.add(endDate);

        List<String> names = new ArrayList<>();
        Enumeration rparams = request.getParameterNames();
        while (rparams.hasMoreElements()) {
            String paramName = ((String) rparams.nextElement()).replace("[]", "");
            for (Pattern pat : patterns) {
                Matcher m = pat.matcher(paramName);
                if (m.matches()) {
                    names.add(paramName);
                }
            }
        }

        List<Map<String, Object>> params = new ArrayList<>();
        if (names.size() > 0) {
            for (String filter : names) {
                Map<String, Object> map = new HashMap<>();
                String sval = filter.replace("name", "value");
                String paramName = request.getParameter(filter);
                String paramValue = request.getParameter(sval);
                map.put("name", paramName);
                map.put("value", paramValue);
                params.add(map);
            }
        }
        return params;
    }

    @Override
    public List<Map<String, Object>> filtersExist2(HttpServletRequest request) {

        List<Pattern> patterns = new ArrayList<>();
        Pattern p = Pattern.compile("[0-9]?filters\\[[0-9]]\\[name]");
        //0[filters][6][start-date]
        Pattern startDate = Pattern.compile("[0-9]?filters\\[[0-9]]\\[start-date]");
        Pattern endDate = Pattern.compile("[0-9]?filters\\[[0-9]]\\[end-date]");

        patterns.add(p);
        patterns.add(startDate);
        patterns.add(endDate);

        List<String> names = new ArrayList<>();
        Enumeration rparams = request.getParameterNames();
        while (rparams.hasMoreElements()) {
            String paramName = ((String) rparams.nextElement()).replace("[]", "");
            for (Pattern pat : patterns) {
                Matcher m = pat.matcher(paramName);
                if (m.matches()) {
                    names.add(paramName);
                }
            }
        }

        List<Map<String, Object>> params = new ArrayList<>();
        if (names.size() > 0) {
            for (String filter : names) {
                Map<String, Object> map = new HashMap<>();
                String sval = filter.replace("name", "value");
                String paramName = request.getParameter(filter);
                String paramValue = request.getParameter(sval);
                map.put("name", paramName);
                map.put("value", paramValue);
                params.add(map);
            }
        }
        return params;
    }

}
