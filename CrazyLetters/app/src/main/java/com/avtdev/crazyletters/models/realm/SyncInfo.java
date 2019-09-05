package com.avtdev.crazyletters.models.realm;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmField;

public class SyncInfo extends RealmObject {

    public static final class PROPERTIES{
        public static final String TABLE = "table";
        public static final String SYNC_DATE = "syncDate";
    }

    @PrimaryKey
    @RealmField(PROPERTIES.TABLE)
    private String table;

    @RealmField(PROPERTIES.SYNC_DATE)
    private Date syncDate;

    public SyncInfo() {
    }

    public SyncInfo(String table, Date syncDate) {
        this.table = table;
        this.syncDate = syncDate;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Date getSyncDate() {
        return syncDate;
    }

    public void setSyncDate(Date syncDate) {
        this.syncDate = syncDate;
    }
}
