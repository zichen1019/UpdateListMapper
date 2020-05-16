package com.zc.mapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zichen
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class MappingField {

    /**
     * 数据库表中的字段
     */
    private String column;

    /**
     * 实体类中的字段
     */
    private String field;

}
