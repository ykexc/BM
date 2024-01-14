package com.backend.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author mqz
 */
@TableName("db_topic")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Topic {

    @TableId(type = IdType.AUTO)
    Integer id;

    String title;

    String content;

    Integer uid;

    Integer type;

    Date time;

}
