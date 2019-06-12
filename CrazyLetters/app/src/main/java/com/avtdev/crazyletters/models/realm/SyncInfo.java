package com.avtdev.crazyletters.models.realm;

import com.avtdev.crazyletters.models.response.DictionaryResponse;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SyncInfo extends RealmObject {

    public static final class PROPERTIES{
        public static final String TABLE = "table";
        public static final String SYNC_DATE = "syncDate";
    }

    @PrimaryKey
    @SerializedName(PROPERTIES.TABLE)
    private String table;

    @SerializedName(PROPERTIES.SYNC_DATE)
    private Date syncDate;

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
