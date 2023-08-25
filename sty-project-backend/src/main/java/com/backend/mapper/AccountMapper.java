package com.backend.mapper;

import com.backend.entity.dto.Account;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author mqz
 */
@Mapper
public interface AccountMapper extends BaseMapper<Account> {}
