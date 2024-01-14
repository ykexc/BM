package com.backend.entity.dto;

import com.backend.entity.BaseData;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author mqz
 */
@TableName("db_topic_type")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicType implements BaseData {

    @TableId(type = IdType.AUTO)
    Integer id;

    String name;

    @TableField("`desc`")
    String desc;

    String color;

}
