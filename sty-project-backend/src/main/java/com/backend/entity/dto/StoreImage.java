package com.backend.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * @author mqz
 */
@Data
@TableName("db_image_store")
@AllArgsConstructor
public class StoreImage {



    Integer uid;

    String name;

    Date time;

}
