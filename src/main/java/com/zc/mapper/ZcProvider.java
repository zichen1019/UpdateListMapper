package com.zc.mapper;

import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;
import tk.mybatis.mapper.mapperhelper.SqlHelper;

import java.util.Set;

/**
 * @author zichen
 */
public class ZcProvider extends MapperTemplate {

    public ZcProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    /**
     * 批量更新方法：根据每一条的主键进行更新，当主键重复时会自动更新，若主键不存在，则进行保存，未给定数据的字段都会被置为空
     * sql 如下：insert into `test` (id, v) VALUES (null,1),(2,2) on duplicate key update id=VALUES(id), v=VALUES(v);
     */
    public String updateListByPrimarykey(MappedStatement ms) {
        Class<?> entityClass = this.getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.insertIntoTable(entityClass, this.tableName(entityClass)));

        // insert into 后面紧跟着的字段
        sql.append(getAllColumns(entityClass, true, false));

        sql.append(" VALUES ");
        sql.append("<foreach collection=\"list\" item=\"record\" separator=\",\" >");

        // 获取要获取数据在实体类中对应的字段
        sql.append(getAllColumns(entityClass, false, true));

        sql.append("</foreach>");

        sql.append(" on duplicate key update ");

        // on duplicate key update 后面紧跟着的字段
        sql.append(getAllColumns(entityClass, false, false));

//        System.err.println(sql.toString());

        return sql.toString();
    }

    /**
     * 获取所有查询列，如id,name,code...
     *
     * @param entityClass
     * @param isInsertColumns   是否是insert into 后面紧跟着的字段
     * @param isValueColumns    要获取数据在实体类中对应的字段
     * @return
     */
    public String getAllColumns(Class<?> entityClass, boolean isInsertColumns, boolean isValueColumns) {
        Set<EntityColumn> columnSet = EntityHelper.getColumns(entityClass);
        StringBuilder sql = new StringBuilder();
        if (isInsertColumns || isValueColumns) {
            sql.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        } else {
            sql.append("<trim suffixOverrides=\",\">");
        }
        columnSet.forEach(entityColumn -> {
            if (isInsertColumns) {
                sql.append(entityColumn.getColumn()).append(",");
            } else if (isValueColumns) {
                sql.append(entityColumn.getColumnHolder("record") + ",");
            } else {
                sql.append(entityColumn.getColumn() + "=VALUES(" + entityColumn.getColumn() + "),");
            }
        });
        sql.append("</trim>");
        return sql.toString();
    }

}
