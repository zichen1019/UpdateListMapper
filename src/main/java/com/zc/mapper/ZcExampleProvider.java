package com.zc.mapper;

import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;
import tk.mybatis.mapper.mapperhelper.SqlHelper;

/**
 * @author zichen
 */
public class ZcExampleProvider extends MapperTemplate {

    public ZcExampleProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    /**
     * 批量更新数据：多个update sql
     * 根据条件进行更新指定字段或全部字段
     * sql 如：<foreach item="item" index="index" collection="list" open="" close="" separator=";">
     *       <if test="item.id != ''">
     *       UPDATE biz_base_info <set> se_id = #{item.seId,jdbcType=VARCHAR} </set> WHERE id = #{item.id,jdbcType=INTEGER}
     *       </if>
     *     </foreach>
     */
    public String updateListByCondition(MappedStatement ms) {
        Class<?> entityClass = this.getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append("<foreach collection=\"list\" item=\"record\" separator=\";\" >");

        sql.append(" UPDATE " + this.tableName(entityClass) + " ");

        sql.append(updateColumn(entityClass));

        sql.append("<where>" +
                "<if test=\"condition != null\">" +
                "${condition.column} = #{record.${condition.field}}" +
                "</if>" +
                "</where>");

        sql.append("</foreach>");

        System.err.println(sql.toString());

        return sql.toString();
    }

    public String updateColumn(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("<choose>");
        sql.append("<when test=\"mappingFields != null and mappingFields.size() > 0\">");
        sql.append("<set>" +
                "<foreach collection=\"mappingFields\" item=\"mappingField\" separator=\",\">" +
                "${mappingField.column} = #{record.${mappingField.field}}" +
                "</foreach>" +
                "</set>");
        sql.append("</when>");
        //不支持指定列的时候查询全部列
        sql.append("<otherwise>");
        // 根据条件更新所有字段
        sql.append(SqlHelper.updateSetColumns(entityClass, "record", false, false));
        sql.append("</otherwise>");
        sql.append("</choose>");
        return sql.toString();
    }

}
