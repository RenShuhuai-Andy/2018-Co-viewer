package com.rsh.coviewer.task;

import com.rsh.coviewer.task.entity.MytaskerrorEntity;
import com.rsh.coviewer.task.queue.MyQueue;
import com.rsh.coviewer.task.queue.MyQueueBean;
import com.rsh.coviewer.task.runnable.MyRunnable;
import com.rsh.coviewer.task.service.MyErrorTaskRepository;
import com.rsh.coviewer.task.service.MyTaskRepository;
import com.rsh.coviewer.task.entity.MytaskEntity;
import com.rsh.coviewer.task.entity.MytaskerrorEntity;
import com.rsh.coviewer.task.queue.MyQueue;
import com.rsh.coviewer.task.queue.MyQueueBean;
import com.rsh.coviewer.task.runnable.MyRunnable;
import com.rsh.coviewer.task.service.MyErrorTaskRepository;
import com.rsh.coviewer.task.service.MyTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @DESCRIPTION :随着程序的启动而启动运行,运行定时任务，每5秒查询一次数据库
 * @AUTHOR : WuShukai1103
 * @TIME : 2018/1/24  22:11
 */
@Component
public class MainTask implements CommandLineRunner {

    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newScheduledThreadPool(2);

    private final MyTaskRepository repository;
    private final MyErrorTaskRepository errorTaskRepository;

    @Autowired
    public MainTask(MyTaskRepository repository, MyErrorTaskRepository errorTaskRepository) {
        this.repository = repository;
        this.errorTaskRepository = errorTaskRepository;
    }

    @Override
    public void run(String... strings) throws Exception {
        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {
            List<MytaskEntity> entities = repository.starts();
//            System.out.println("查询数据库:" + TimeTransform.fullDay.format(new Date()) + ",size:" + entities.size());
            for (MytaskEntity e : entities) {
                Timestamp now = new Timestamp(new Date().getTime());
                MyRunnable runnable;
                try {
                    //通过反射获取运行的类
                    runnable = (MyRunnable) Class.forName(e.getClassname()).newInstance();
                    //将runnable存储到队列中
                    MyQueue.getInstance().offer(new MyQueueBean().setEntity(e).setRunnable(runnable));
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                    ex.printStackTrace();
                    MytaskerrorEntity entity = new MytaskerrorEntity();
                    entity.setClassname(e.getClassname());
                    entity.setMsg(ex.getMessage());
                    entity.setRtime(now);
                    entity.setTaskname(e.getTaskname());
                    errorTaskRepository.save(entity);
                }
            }
        }, 0, 5, TimeUnit.SECONDS);
        Thread.sleep(1);
        //加载队列后，立即运行MyTask
        SCHEDULED_EXECUTOR_SERVICE.execute(MyTask.getInstance());
    }
}
