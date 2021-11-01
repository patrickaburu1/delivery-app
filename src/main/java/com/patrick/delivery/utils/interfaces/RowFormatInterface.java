package com.patrick.delivery.utils.interfaces;

public interface RowFormatInterface {
    /**
     * All formater will implement this method that will be called by the
     * datatable class to format the content
     *
     * @param   row -
     * @return  Object[]
     */
    public Object[] formatRow(Object[] row);
}
