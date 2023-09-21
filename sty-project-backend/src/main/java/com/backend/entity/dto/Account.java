package com.backend.entity.dto;

import com.backend.entity.BaseData;
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

@Data
@TableName("db_account")
@AllArgsConstructor
@NoArgsConstructor
public class Account implements BaseData {

    @TableId(type = IdType.AUTO)
    private Integer id;

    String username;

    String password;

    String email;

    String role;

    Date registerTime;

    String avatar;

}
