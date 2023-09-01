package com.backend.entity.dto;

import com.backend.entity.BaseData;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author mqz
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("db_account_privacy")
public class AccountPrivacy implements BaseData {

    @TableId
    Integer id;

    Boolean phone = true;

    Boolean email = true;

    Boolean wx = true;

    Boolean qq = true;

    Boolean gender = true;

}
