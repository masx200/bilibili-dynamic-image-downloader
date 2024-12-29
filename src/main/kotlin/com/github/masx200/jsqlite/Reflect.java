/**
 * Copyright 2023 Zhang Guanhu
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.masx200.jsqlite;

import kotlin.jvm.functions.Function1;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.github.masx200.jsqlite.Core.gson;
import static com.github.masx200.jsqlite.GetTableNameFromClassKt.getTableNameFromClass;

final class Reflect<T> {

    final Map<String, Field> fieldMap = new LinkedHashMap<>();
    private Class<?> tClass;
    private T t;

    Reflect(Class<?> tClass) {
        this.tClass = tClass;
        newInstance(tClass);
    }

    Reflect(T t) {
        this.t = t;
        newInstance(t.getClass());
    }

    static <T> T toEntity(Class<T> tClass, Options options, ResultSet resultSet) {
        try {
            Map<String, Boolean> columnsMap = new HashMap<>();
            if (options != null && options.selectColumns != null && !Objects.equals(options.selectColumns, "*")) {
                String[] columns = options.selectColumns.split(", ");
                for (String column : columns) {
                    columnsMap.put(column, true);
                }
            }
            T t;
            try {
                // 尝试使用无参构造函数创建对象
                t = tClass.getConstructor().newInstance();
            } catch (NoSuchMethodException e) {
                // 如果没有无参构造函数，则使用带 Consumer 参数的构造函数
                try {
                    t = tClass.getConstructor(Function1.class).newInstance((Function1<T, Object>) (c -> null));
                } catch (NoSuchMethodException e2) {
                    t = tClass.getConstructor(Consumer.class).newInstance((Consumer<T>) (c -> {
                    }));
                }

            }

            Reflect<T> reflect = new Reflect<>(t);
            for (Field field : reflect.fieldMap.values()) {
                String name = field.getName();
                if (!columnsMap.isEmpty() && !columnsMap.getOrDefault(name, false)) {
                    continue;
                }
                if (isJson(field)) {
                    reflect.setValue(name, gson.fromJson(resultSet.getString(name), field.getType()));
                    continue;
                }
                String type = field.getType().getSimpleName().toLowerCase();
                switch (type) {
                    case "int":
                    case "integer":
                        reflect.setValue(name, resultSet.getInt(name));
                        break;
                    case "byte":
                        reflect.setValue(name, resultSet.getByte(name));
                        break;
                    case "short":
                        reflect.setValue(name, resultSet.getShort(name));
                        break;
                    case "long":
                        reflect.setValue(name, resultSet.getLong(name));
                        break;
                    case "float":
                        reflect.setValue(name, resultSet.getFloat(name));
                        break;
                    case "double":
                        reflect.setValue(name, resultSet.getDouble(name));
                        break;
                    case "char":
                    case "character":
                    case "string":
                        reflect.setValue(name, resultSet.getString(name));
                        break;
                    case "boolean":
                        reflect.setValue(name, resultSet.getBoolean(name));
                        break;
                }
            }
            return reflect.get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    static boolean isIgnore(Field field) {
        if (field.isAnnotationPresent(Column.class)) {
            Column column = field.getAnnotation(Column.class);
            return column.ignore();
        }
        return false;
    }

    static boolean isIndex(Field field) {
        if (field.isAnnotationPresent(Column.class)) {
            Column column = field.getAnnotation(Column.class);
            return column.index();
        }
        return false;
    }

    static boolean isJson(Field field) {
        if (field.isAnnotationPresent(Column.class)) {
            Column column = field.getAnnotation(Column.class);
            return column.json();
        }
        return false;
    }

    static Column getColumn(Field field) {
        if (field.isAnnotationPresent(Column.class)) {
            return field.getAnnotation(Column.class);
        } else {
            return null;
        }
    }

    private void newInstance(Class<?> tClass) {
        Class<?> clazz = tClass;
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                if (!isIgnore(field)) {
                    fieldMap.put(field.getName().toLowerCase(), field);
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    void setValue(String fieldName, Object value) {
        try {
            Field field = fieldMap.getOrDefault(fieldName.toLowerCase(), null);
            if (field != null) {
                field.set(t, value);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    Object getValue(String fieldName) {
        try {
            Field field = fieldMap.getOrDefault(fieldName.toLowerCase(), null);
            return (field != null) ? field.get(t) : null;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    Class<?> getType(String fieldName) {
        Field field = fieldMap.getOrDefault(fieldName.toLowerCase(), null);
        return field.getType();
    }

    String getDatabaseType(String fieldName) {
        switch (getType(fieldName.toLowerCase()).getSimpleName().toLowerCase()) {
            case "int":
            case "integer":
            case "byte":
            case "short":
            case "long":
                return "integer";
            case "float":
            case "double":
                return "real";
            case "char":
            case "character":
            case "string":
                return "text";
            case "boolean":
                return "blob";
            default:
                throw new NullPointerException();
        }
    }

    Object getDBValue(Field field) {
        try {
            String fieldName = field.getName().toLowerCase();
            Field dbField = fieldMap.getOrDefault(fieldName, null);
            Object dbValue = (dbField != null) ? dbField.get(t) : null;
            if (dbField != null && dbValue != null) {
                if (isJson(field)) {
                    return String.format("'%s'", gson.toJson(dbValue));
                }
                switch (getDatabaseType(fieldName)) {
                    case "text":
                        return String.format("'%s'", dbValue);
                    case "blob":
                        return (Objects.equals(dbValue, true)) ? 1 : 0;
                    default:
                        return dbValue;
                }
            }
            return null;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    void getDBColumnsWithValue(BiConsumer<String, Object> consumer) {
        for (Field field : fieldMap.values()) {
            consumer.accept(field.getName(), getDBValue(field));
        }
    }

    void getDBColumnsWithType(BiConsumer<String, String> consumer) {
        for (Field field : fieldMap.values()) {
            if (isJson(field)) {
                consumer.accept(field.getName().toLowerCase(), "text");
                continue;
            }
            consumer.accept(field.getName().toLowerCase(), getDatabaseType(field.getName()));
        }
    }

    void getIndexList(BiConsumer<String, String> consumer) {
        String table = getTableNameFromClass(tClass);
        fieldMap.values().forEach(field -> {
            if (isIndex(field)) {
                String column = field.getName().toLowerCase();
                String index = String.format("idx_%s_%s", table, column);
                consumer.accept(index, column);
            }
        });
    }

    T get() {
        return t;
    }

}