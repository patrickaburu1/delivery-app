package com.patrick.delivery.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author patrick on 6/18/20
 * @project sprintel-delivery
 */
public class DataTableNew {

    private String draw;
    private long recordsTotal;
    private long recordsFiltered;
    private List<Object[]> data;

    public DataTableNew() {
        this.data = new ArrayList<>();
    }

    public DataTableNew(int draw, long recordsTotal, long recordsFiltered) {
        this(draw + "", recordsTotal, recordsFiltered);
    }

    public DataTableNew(String draw, long recordsTotal, long recordsFiltered) {
        this();
        this.draw = draw;
        this.recordsTotal = recordsTotal;
        this.recordsFiltered = recordsFiltered;
    }

    public DataTableNew(String draw, long recordsTotal, long recordsFiltered, List<Object[]> data) {
        this.draw = draw;
        this.recordsTotal = recordsTotal;
        this.recordsFiltered = recordsFiltered;
        this.data = data;
    }

    public String getDraw() {
        return draw;
    }

    public void setDraw(String draw) {
        this.draw = draw;
    }

    public long getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(long recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public long getRecordsFiltered() {
        return recordsFiltered;
    }

    public void setRecordsFiltered(long recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }

    public List<Object[]> getData() {
        return data;
    }

    public void setData(List<Object[]> data) {
        this.data = data;
    }

}
