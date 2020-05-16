package com.zc.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.UpdateProvider;
import tk.mybatis.mapper.annotation.RegisterMapper;

import java.util.List;

/**
 * @author zichen
 */
@RegisterMapper
public interface UpdateListByPrimarykeyMapper<T> {

    /**
     * 批量更新方法：根据每一条的主键进行更新，当主键重复时会自动更新，若主键不存在，则进行保存
     *  sql 如：insert into `test` (id, v) VALUES (null,1),(2,2) on duplicate key update id=VALUES(id), v=VALUES(v);
     * @param var1          要批量更新的数据
     * @param mappingFields 要更新的字段
     * @return      var1中如果主键存在，返回的Affected rows则为2，否则则为1。
     */
    @UpdateProvider(
            type = ZcProvider.class,
            method = "dynamicSQL"
    )
    int updateListByPrimarykey(@Param("list") List<? extends T> var1, @Param("mappingFields") List<MappingField> mappingFields);

}