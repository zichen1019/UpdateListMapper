# UpdateListMapper

通用mapper扩展-批量更新

## 更新方式

> 推荐第一种更新方式

## UpdateListByPrimarykeyMapper

### 更新方式

> 根据每一条的主键进行更新，当主键重复时会自动更新，若主键不存在，则进行保存，未给定数据的字段都会被置为空

### 使用方式

#### 将要需要使用此功能的mapper继承当前mapper

```java
public interface BaseMapper<T> extends UpdateListByPrimarykeyMapper<T> {
    // FIXME 特别注意，该接口不能被扫描到，否则会出错
}
```

#### 参数说明

> @param list          要批量更新的数据

#### 接口调用示例

```java
List<Test> list = new LinkedList<>();
testMapper.updateListByPrimarykey(list);
```

### 执行sql

```mysql
insert into `test` (id, v) VALUES (null,1),(2,2) 
on duplicate key update id=VALUES(id), v=VALUES(v);
```

## UpdateListByConditionMapper

### 更新方式

> 多条update sql

### 使用方式

#### mysql在连接时，在最后面追加：&allowMultiQueries=true

```yaml
url: jdbc:mysql://127.0.0.1:3306/mytest?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true
```

#### 将要需要使用此功能的mapper继承当前mapper

```java
public interface BaseMapper<T> extends UpdateListByConditionMapper<T> {
    // FIXME 特别注意，该接口不能被扫描到，否则会出错
}
```

#### 参数说明

> @param list          要批量更新的数据 
>
> @param mappingFields 要更新的字段
>
> @param condition     根据当前字段更新

#### 接口调用示例

```java
List<Test> list = new LinkedList<>();
List<MappingField> mappingFields = new MappingFieldUtils<Test, Object>(Test.class).put(Test::getId).put(Test::getV).toMappingFieldList();
MappingField condition = MappingField.builder().column("id").field("id").build();
testMapper.updateListByCondition(list, mappingFields, condition);
```

### 执行sql

```mysql
update `test` set v = '1' where id = 1;
update `test` set v = '2' where id = 2;
```
