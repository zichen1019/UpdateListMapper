package com.zc.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.UpdateProvider;
import tk.mybatis.mapper.annotation.RegisterMapper;

import java.util.List;

/**
 * @author zichen
 */
@RegisterMapper
public interface UpdateListByConditionMapper<T> {

    /**
     * 批量更新数据：多条update sql
     * mysql在连接时，在最后面追加&allowMultiQueries=true
     * 根据条件进行更新指定字段或全部字段
     * @param var1          要批量更新的数据
     * @param mappingFields 要更新的字段
     * @param condition     根据当前字段更新
     * @return
     */
    @UpdateProvider(
            type = ZcExampleProvider.class,
            method = "dynamicSQL"
    )
    int updateListByCondition(@Param("list") List<? extends T> var1, @Param("mappingFields") List<MappingField> mappingFields, @Param("condition") MappingField condition);

}