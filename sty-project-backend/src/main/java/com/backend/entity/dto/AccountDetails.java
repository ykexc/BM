package com.backend.entity.dto;

import com.backend.entity.BaseData;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author mqz
 */
@TableName("db_account_details")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetails implements BaseData {

    @TableId
    Integer id;

    Integer gender;

    String phone;

    String qq;

    String wx;

    @TableField("`desc`")
    String desc;
}
