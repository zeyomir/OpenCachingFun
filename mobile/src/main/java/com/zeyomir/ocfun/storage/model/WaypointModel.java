package com.zeyomir.ocfun.storage.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;

@Table(name = "waypoint", id="_id")
public class WaypointModel extends Model{
    @Column (index = true)
    public String code;
    @Column
    public float latitude;
    @Column
    public float longitude;
    @Column
    public String typeName;
    @Column
    public String description;

    public static void truncate() {
        new Delete().from(WaypointModel.class).execute();
    }
}
