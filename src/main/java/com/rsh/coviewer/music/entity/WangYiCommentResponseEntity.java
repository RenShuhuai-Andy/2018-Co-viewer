package com.rsh.coviewer.music.entity;

import com.rsh.coviewer.music.bean.WangYiAllCommentBean;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @DESCRIPTION :
 * @AUTHOR : WuShukai1103
 * @TIME : 2018/1/21  14:21
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class WangYiCommentResponseEntity extends BaseEntity {
    private long songid;
    private WangYiAllCommentBean data;
}
