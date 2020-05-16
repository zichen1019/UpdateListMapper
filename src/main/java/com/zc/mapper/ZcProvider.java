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

    private static final String RECORD = "record";

    public ZcProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    /**
     * 批量更新方法：根据每一条的主键进行更新，当主键重复时会自动更新，若主键不存在，则进行保存
     * sql 如下：insert into `test` (id, v) VALUES (null,1),(2,2) on duplicate key update id=VALUES(id), v=VALUES(v);
     *
     * TODO 思考：获取在Weekend中设置的列，以此来设置要更新的列
     */
    public String updateListByPrimarykey(MappedStatement ms) {
        Class<?> entityClass = this.getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.insertIntoTable(entityClass, this.tableName(entityClass)));

        // TODO insert into 后面紧跟着的字段
        sql.append(updateColumns(entityClass, true, false));

        sql.append(" VALUES ");
        sql.append("<foreach collection=\"list\" item=\"" + RECORD + "\" separator=\",\" >");
        // TODO 获取要更新数据的字段
        sql.append(updateColumns(entityClass, false, true));
        sql.append("</foreach>");

        sql.append(" on duplicate key update ");
        // TODO on duplicate key update 后面紧跟着的字段
        sql.append(updateColumns(entityClass, false, false));

//        System.err.println(sql.toString());

        return sql.toString();
    }

    /**
     * 构建字段
     * @param entityClass       实体类
     * @param isInsertColumns   是否是insert into 后面紧跟着的字段
     * @param isEntityField     是否是实体类字段
     * @return
     */
    public String updateColumns(Class<?> entityClass, boolean isInsertColumns, boolean isEntityField) {
        StringBuilder sql = new StringBuilder();
        sql.append("<choose>");
        sql.append("<when test=\"mappingFields != null and mappingFields.size() > 0\">");
        if (isInsertColumns || isEntityField) {
            sql.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        }
        sql.append("<foreach collection=\"mappingFields\" item=\"mappingField\" separator=\",\">");

        if (isInsertColumns) {
            sql.append("${mappingField.column}");
        } else if (isEntityField) {
            sql.append("#{" + RECORD + ".${mappingField.field}}");
        } else {
            // 是on duplicate key update 后面紧跟着的字段
            sql.append("${mappingField.column} = VALUES(${mappingField.column})");
        }

        sql.append("</foreach>");

        if (isInsertColumns || isEntityField) {
            sql.append("</trim>");
        }

        sql.append("</when>");
        //不支持指定列的时候查询全部列
        sql.append("<otherwise>");
        sql.append(getAllColumns(entityClass, isInsertColumns, isEntityField));
        sql.append("</otherwise>");
        sql.append("</choose>");
        return sql.toString();
    }

    /**
     * 获取所有查询列，如id,name,code...
     *
     * @param entityClass
     * @param isInsertColumns   是否是insert into 后面紧跟着的字段
     * @param isEntityField     是否是实体类字段
     * @return
     */
    public String getAllColumns(Class<?> entityClass, boolean isInsertColumns, boolean isEntityField) {
        Set<EntityColumn> columnSet = EntityHelper.getColumns(entityClass);
        StringBuilder sql = new StringBuilder();
        for (EntityColumn entityColumn : columnSet) {
            sql.append(entityColumn.getColumn()).append(",");
        }
        if (isInsertColumns || isEntityField) {
            sql.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        } else {
            sql.append("<trim suffixOverrides=\",\">");
        }

        columnSet.forEach(column -> {
            if (isInsertColumns) {
                sql.append(column.getColumn() + ",");
            } else if (isEntityField) {
                sql.append(column.getColumnHolder(RECORD) + ",");
            } else {
                // on duplicate key update 后面紧跟着的字段
                sql.append(column.getColumn() + "=VALUES(" + column.getColumn() + "),");
            }
        });

        sql.append("</trim>");
        return sql.toString();
    }

}
