package com.zeyomir.ocfun.storage.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

@Table(name = "userLog", id = "_id")
public class UserLogModel extends Model {
    @Column(index = true)
    public String cacheCode;
    @Column
    public LogType logtype;
    @Column
    public String comment;
    @Column
    public long date;
    @Column
    public String password;
    @Column
    public Integer rating;
    @Column
    public Boolean recommend;
    @Column
    public Boolean needsMaintenance;
    @Column
    public String error;

    public static int count() {
        return new Select().from(UserLogModel.class).where("error IS NULL").count();
    }

    public static UserLogModel getFirst() {
        return new Select().from(UserLogModel.class).orderBy("_id").limit(1).executeSingle();
    }

    public static void clearErrors(){
        new Update(UserLogModel.class).set("error = NULL").execute();
    }

    public static void setError(String cacheCode, String message) {
        new Update(UserLogModel.class).set("error = ?", message).where("cacheCode LIKE ?", cacheCode).execute();
    }
}
