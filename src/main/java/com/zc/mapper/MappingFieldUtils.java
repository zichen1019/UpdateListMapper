package com.zc.mapper;

import cn.hutool.core.util.StrUtil;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.entity.EntityTable;
import tk.mybatis.mapper.mapperhelper.EntityHelper;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 构建映射字段
 * @author zichen
 */
public class MappingFieldUtils<A, B> {

    /**
     * 实体类字段和sql字段
     */
    private Map<String, EntityColumn> propertyMap;

    private List<MappingField> mappingFields = new LinkedList<>();

    public MappingFieldUtils(Class<?> entityClass) {
        EntityTable table = EntityHelper.getEntityTable(entityClass);
        propertyMap = table.getPropertyMap();
    }

    public MappingFieldUtils<A, B> put(Fn<A, B> fn) {
        try {
            Method method = fn.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(Boolean.TRUE);
            SerializedLambda serializedLambda = (SerializedLambda)method.invoke(fn);
            String getter = serializedLambda.getImplMethodName();
            String field = StrUtil.lowerFirst(getter.replaceFirst("get|is", ""));
            String column = propertyMap.get(field).getColumn();
            mappingFields.add(MappingField.builder().column(column).field(field).build());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return this;
    }

    public List<MappingField> toMappingFieldList() {
        return mappingFields;
    }

}
