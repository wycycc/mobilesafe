package com.ycc.mobilesafe;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.ycc.mobilesafe.db.dao.BlackNumberDao;
import com.ycc.mobilesafe.db.dao.BlackNumberDbOpenHelper;
import com.ycc.mobilesafe.domain.BlackNumberInfo;
import com.ycc.mobilesafe.domain.TaskInfo;
import com.ycc.mobilesafe.engine.TaskInfoProvider;

import java.util.List;
import java.util.Random;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testGetTaskInfos() throws Exception{
        List<TaskInfo> infos = TaskInfoProvider.getTaskInfos(getContext());
        for(TaskInfo info:infos){
            System.out.println("============="+info.toString());
        }
    }

    public void testCreateDb() throws Exception{
        BlackNumberDbOpenHelper helper = new BlackNumberDbOpenHelper(getContext());
        helper.getWritableDatabase();
    }

    public void testAdd() throws Exception{
        BlackNumberDao dao = new BlackNumberDao(getContext());
        long basenumber = 13500000000l;
        Random random = new Random();
        for(int i=0;i<100;i++){
            dao.add(String.valueOf(basenumber+i),String.valueOf(random.nextInt(3)+1));
        }
    }

    public void testFindAll() throws Exception{
        BlackNumberDao dao = new BlackNumberDao(getContext());
        List<BlackNumberInfo> infos = dao.findAll();
        for(BlackNumberInfo info:infos){
            System.out.println(info.toString());
        }
    }

    public void testDelete() throws Exception{
        BlackNumberDao dao = new BlackNumberDao(getContext());
        dao.delete("110");
    }

    public void testUpdate() throws Exception{
        BlackNumberDao dao = new BlackNumberDao(getContext());
        dao.update("110","2");
    }

    public void testFind() throws Exception{
        BlackNumberDao dao = new BlackNumberDao(getContext());
        boolean result = dao.find("110");
        assertEquals(true,result);
    }
}