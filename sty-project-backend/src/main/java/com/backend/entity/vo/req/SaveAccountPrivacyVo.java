package com.backend.entity.vo.req;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * @author mqz
 */
@Data
public class SaveAccountPrivacyVo {
    @Pattern(regexp = "(phone|qq|wx|email|gender)")
    String type;

    Boolean flag;

}
