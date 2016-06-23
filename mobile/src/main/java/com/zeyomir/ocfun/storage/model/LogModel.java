package com.zeyomir.ocfun.storage.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "log", id = "_id")
public class LogModel extends Model {
    @Column(unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String uuid;
    @Column(index = true)
    public String code;
    @Column
    public String date;
    @Column
    public LogType type;
    @Column
    public String comment;
    @Column
    public String username;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LogModel logModel = (LogModel) o;

        return uuid.equals(logModel.uuid);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + uuid.hashCode();
        return result;
    }

    public static List<LogModel> getForCode(String code){
        return new Select("_id", "date", "type", "comment", "username").from(LogModel.class).where("code LIKE ?", code).orderBy("date DESC").execute();
    }

    public static void truncate() {
        new Delete().from(LogModel.class).execute();
    }
}
