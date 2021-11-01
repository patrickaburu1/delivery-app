/*
 * The MIT License
 *
 * Copyright 2016 Ken Gichia.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.patrick.delivery.utils.db;


import com.patrick.delivery.utils.interfaces.MakerCheckerInterface;
import com.patrick.delivery.utils.interfaces.RowFormatInterface;
import com.patrick.delivery.repositories.AbstractRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Format the content to ensure that the maker checker content has been defined.
 * <p>
 * For more information on the maker checker implementation, refer to the class
 * comment for com.teke.core.db.repository.MakerCheckerRepository
 *
 * @author Ken Gichia
 * @version 1.0.0
 * @category DB/DataTable
 * @package Teke
 * @since 2016-01-25
 */
public class MakerCheckerFormatter implements RowFormatInterface {

    private Long _appUserId;
    private int _iColumns;
    private boolean _inactiveRecordsDisplay;
    MakerCheckerInterface makerChecker;

    /**
     * The class constructor initialises the object with the various parameters
     * used to do the maker checker test
     *
     * @param request
     * @param makerChecker
     */
    public MakerCheckerFormatter(HttpServletRequest request, MakerCheckerInterface makerChecker) {
        // Set the core items
        _appUserId = (Long) request.getSession().getAttribute("_userNo");
        _iColumns = getNumberOfColumns(request);
        _inactiveRecordsDisplay = Boolean.parseBoolean(request.getParameter("inactiveRecordsDisplay"));

        // Get the object used for maker checker purposes
        this.makerChecker = makerChecker;
    }

    //

    /**
     * All formatters will implement this method that will be called by the
     * datatable class to format the content
     *
     * @param row
     * @return Object[]
     */
    @Override
    public Object[] formatRow(Object[] row) {
        // Get the flag
        String flag = row[_iColumns].toString();
//        Long index =  (Long)row[_iColumns+1];
        Long index;

        if (row[_iColumns + 1] instanceof Integer) {
            index = ((Integer) row[_iColumns + 1]).longValue();
        } else index = (Long) row[_iColumns + 1];

        row[_iColumns + 1] = flag;

        // When dealing with inactive records, the information is set differently
        if (_inactiveRecordsDisplay) {
            row[_iColumns] = inactiveFlags(index);
        }

        // When dealing with active records display
        else {
            // If there are no changes to apply, exit here
            if (AbstractRepository.ACTIVE_RECORD.equals(flag) || AbstractRepository.INACTIVE_RECORD.equals(flag) || null == makerChecker)
                return row;

            // Check the flag to kow the appropriate action
            row[_iColumns] = AbstractRepository.ACTIVATE_RECORD.equals(flag) ? sameFlags(flag, index) : differentFlags(flag, index);
        }

        // Show the result
        return row;
    }

    /**
     * The check below are for items that have different approve and decline
     * flag checks
     *
     * @param flag
     * @param index
     * @return String
     */
    private String differentFlags(String flag, Long index) {
        List<String> flags = new ArrayList<String>();

        // Whether one can approve an action
        boolean test = makerChecker.isActive(flag + "-approve");
        if (test == false || !_appUserId.equals(index)) flags.add("approve");

        // Whether one can deny an action
        test = makerChecker.isActive(flag + "-decline");
        if (test == false || !_appUserId.equals(index)) flags.add("deny");

        // If there are no flags set, one can only view
        if (flags.isEmpty()) return "view";

        // Build the flag list
        String result = flags.get(0);

        if (flags.size() > 1)
            result += "-" + flags.get(1);

        // Show the result
        return result;
    }

    /**
     * The check below are for items that have the same approve and decline
     * flag checks
     *
     * @param flag
     * @param index
     * @return String
     */
    private String sameFlags(String flag, Long index) {
        boolean test = makerChecker.isActive(flag);

        // If the test returns false, one can approve
        if (test == false) return "approve-deny";

        // Check if the user can make the change
        return (_appUserId.equals(index)) ? "view" : "approve-deny";
    }

    /**
     * When setting the state for inactive records
     * <p>
     * //     * @param   flag
     *
     * @param index
     * @return String
     */
    private String inactiveFlags(Long index) {
        List<String> flags = new ArrayList<String>();

        // Whether one can activate a record
        boolean test = makerChecker.isActive(AbstractRepository.ACTIVATE_RECORD);
        if (test == false || !_appUserId.equals(index)) flags.add("approve");

        // Whether one can delete a record
        test = makerChecker.isActive(AbstractRepository.DELETE_RECORD);
        if (test == false || !_appUserId.equals(index)) flags.add("deny");

        // If there are no flags set, one can only view
        if (flags.isEmpty()) return "view";

        // Build the flag list
        String result = flags.get(0);

        if (flags.size() > 1)
            result += "-" + flags.get(1);

        // Show the result
        return result;
    }

    /**
     * Count the number of columns on the datatable request
     *
     * @return int
     */
    private int getNumberOfColumns(HttpServletRequest request) {
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
}
