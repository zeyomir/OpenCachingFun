package com.zeyomir.ocfun.storage.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

import java.util.List;

@Table(name = "image", id = "_id")
public class ImageModel extends Model {
    @Column(index = true)
    public String code;
    @Column
    public String caption;
    @Column(unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String name;
    @Column
    public boolean downloaded = false;
    @Column
    public boolean isSpoiler;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageModel that = (ImageModel) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    public static void markDownloaded(String name) {
        new Update(ImageModel.class).set("downloaded = ?", true).where("name LIKE ?", name).execute();
    }

    public static int countNotDownloaded() {
        return new Select("downloaded").from(ImageModel.class).where("downloaded = ?", false).count();
    }

    public static ImageModel getFirstNotDownloaded() {
        return new Select().from(ImageModel.class).where("downloaded = ?", false).orderBy("_id").limit(1).executeSingle();
    }

    public static List<ImageModel> getForCode(String code){
        return new Select().from(ImageModel.class).where("code LIKE ?", code).execute();
    }

    public static void truncate() {
        new Delete().from(ImageModel.class).execute();
    }
}
