package com.future.android.study.ormlite;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author Dexterleslie.Chan
 */
@DatabaseTable(tableName = "t_user")
public class User {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(unique = true)
    private String loginname;
    @DatabaseField
    private String password;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLoginname() {
        return loginname;
    }

    public void setLoginname(String loginname) {
        this.loginname = loginname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
