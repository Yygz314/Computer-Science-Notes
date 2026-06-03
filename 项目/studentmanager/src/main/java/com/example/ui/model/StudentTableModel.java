package com.example.ui.model;

import com.example.bean.StudentBean;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class StudentTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;

    private static final String[] COLUMNS = {"学号", "姓名", "性别", "班级", "分数"};

    private List<StudentBean> students;

    public StudentTableModel(List<StudentBean> students) {
        this.students = students;
    }

    public void setStudents(List<StudentBean> students) {
        this.students = students;
        fireTableDataChanged();
    }

    public StudentBean getStudent(int row) {
        return students.get(row);
    }

    @Override public int getRowCount()    { return students.size(); }
    @Override public int getColumnCount() { return COLUMNS.length; }
    @Override public String getColumnName(int col) { return COLUMNS[col]; }

    @Override
    public Object getValueAt(int row, int col) {
        StudentBean s = students.get(row);
        switch (col) {
            case 0: return s.getID();
            case 1: return s.getName();
            case 2: return s.getGender();
            case 3: return s.getClazz();
            case 4: return s.getScore();
        }
        return null;
    }

    @Override
    public Class<?> getColumnClass(int col) {
        if (col == 0) return Long.class;
        if (col == 4) return Integer.class;
        return String.class;
    }
}
