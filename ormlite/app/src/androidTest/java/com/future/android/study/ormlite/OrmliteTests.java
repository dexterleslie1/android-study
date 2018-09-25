package com.future.android.study.ormlite;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.sql.SQLException;

/**
 * @author Dexterleslie.Chan
 */
@RunWith(AndroidJUnit4.class)
public class OrmliteTests {
    @Test
    public void multipleDatabase() throws Exception {
        DatabaseHelper databaseHelper1=null;
        try {
            databaseHelper1 = new DatabaseHelper(InstrumentationRegistry.getTargetContext(), 1);
            Dao<User, Integer> userDao = databaseHelper1.getDao(User.class);
            String loginname = "user1";
            String password = "aa112233";
            PreparedQuery<User> query =
                    userDao.queryBuilder()
                            .where()
                            .eq("loginname", loginname)
                            .prepare();
            User user = userDao.queryForFirst(query);
            if (user == null) {
                user = new User();
                user.setLoginname(loginname);
                user.setPassword(password);
                userDao.create(user);
            }
            user = userDao.queryForFirst(query);
            Assert.assertNotNull(user);
            Assert.assertEquals(loginname, user.getLoginname());
            Assert.assertEquals(password, user.getPassword());
        }catch(Exception ex){
            throw ex;
        }finally{
            if(databaseHelper1!=null){
                databaseHelper1.close();
            }
        }

        DatabaseHelper databaseHelper2=null;
        try {
            databaseHelper2 = new DatabaseHelper(InstrumentationRegistry.getTargetContext(), 2);
            Dao<User, Integer> userDao = databaseHelper2.getDao(User.class);
            String loginname = "user2";
            String password = "aa112233k";
            PreparedQuery<User> query =
                    userDao.queryBuilder()
                            .where()
                            .eq("loginname", loginname)
                            .prepare();
            User user = userDao.queryForFirst(query);
            if (user == null) {
                user = new User();
                user.setLoginname(loginname);
                user.setPassword(password);
                userDao.create(user);
            }
            user = userDao.queryForFirst(query);
            Assert.assertNotNull(user);
            Assert.assertEquals(loginname, user.getLoginname());
            Assert.assertEquals(password, user.getPassword());
        }catch(Exception ex){
            throw ex;
        }finally{
            if(databaseHelper2!=null){
                databaseHelper2.close();
            }
        }
    }
}
