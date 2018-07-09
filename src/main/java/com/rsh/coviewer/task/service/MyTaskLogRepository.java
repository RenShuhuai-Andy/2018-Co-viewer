package com.rsh.coviewer.task.service;

import com.rsh.coviewer.task.entity.MytasklogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @DESCRIPTION :平时日志记录
 * @AUTHOR : WuShukai1103
 * @TIME : 2018/1/27  22:39
 */
public interface MyTaskLogRepository extends JpaRepository<MytasklogEntity, Integer> {
}
