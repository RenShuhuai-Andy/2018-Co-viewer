package com.rsh.coviewer.springdata;

import com.rsh.coviewer.springdata.entity.WangyicontentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @DESCRIPTION :
 * @AUTHOR : WuShukai1103
 * @TIME : 2018/5/1  18:13
 */
public interface WangYiContentRepository extends JpaRepository<WangyicontentEntity, Integer> {
    List<WangyicontentEntity> getAllBySongid(int songId);
}
