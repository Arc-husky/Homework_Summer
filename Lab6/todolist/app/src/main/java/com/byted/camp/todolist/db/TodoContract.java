package com.byted.camp.todolist.db;

import android.provider.BaseColumns;

public final class TodoContract {

    // TODO 1. 定义创建数据库以及升级的操作
    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE "+TodoNote.TABLE_NAME+"("
            +TodoNote._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
            +TodoNote.COLUMN_NAME_DATE+" INTEGER,"
            +TodoNote.COLUMN_NAME_STATE+" INTEGER,"
            +TodoNote.COLUMN_NAME_CONTENT+" TEXT,"
            +TodoNote.COLUMN_NAME_PRIORITY+" INTEGER)";
    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "+TodoNote.TABLE_NAME;
    private TodoContract() {}

    public static class TodoNote implements BaseColumns {
        // TODO 2.此处定义表名以及列明
        public static final String TABLE_NAME = "TODO";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_STATE = "state";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_PRIORITY = "priority";
    }

}
