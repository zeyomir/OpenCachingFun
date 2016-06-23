package com.zeyomir.ocfun.storage.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

@Table(name = "personalNote", id = "_id")
public class PersonalNoteModel extends Model {
    @Column (unique = true, onUniqueConflict = Column.ConflictAction.REPLACE, index = true)
    public String cacheCode;
    @Column
    public String value;
    @Column
    public String oldValue;

    public static int count() {
        return new Select().from(PersonalNoteModel.class).count();
    }

    public static PersonalNoteModel getFirst() {
        return new Select().from(PersonalNoteModel.class).orderBy("_id").limit(1).executeSingle();
    }

    public static void remove(String cacheCode) {
        new Delete().from(PersonalNoteModel.class).where("cacheCode LIKE ?", cacheCode).execute();
    }
}
