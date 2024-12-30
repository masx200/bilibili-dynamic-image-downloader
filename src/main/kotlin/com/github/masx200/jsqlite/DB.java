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

//import lombok.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public interface DB extends AutoCloseable {
    static DB connect(String path) {
        return new Core(path);
    }

    @Override
    void close();

    List<String> findDifferenceTypeColumns(@NotNull Class<?> classes);


    List<String> createColumns(@NotNull Class<?> classes, String... columnNames);

    List<String> dropColumns(@NotNull Class<?> classes, String... columnNames);

    boolean checkTableDifferenceInPrimaryKeyAndAutoIncrement(@NotNull Class<?> classes);

    List<String> dropUnusedColumns(@NotNull Class<?>... classes);

    List<String> tables(@NotNull Class<?>... classes);

    List<String> drop(@NotNull Class<?>... classes);

    List<String> create(@NotNull Class<?>... classes);

    String version();

    <T extends DataSupport<T>> List<String> insert(T t);

    <T extends DataSupport<T>> List<String> update(T t, String predicate, Object... args);

    <T extends DataSupport<T>> List<String> update(T t);

    <T extends DataSupport<T>> void delete(@NotNull Class<T> tClass, String predicate, Object... args);

    <T extends DataSupport<T>> void delete(@NotNull Class<T> tClass, List<Long> ids);

    <T extends DataSupport<T>> void delete(@NotNull Class<T> tClass, Long... ids);

    <T extends DataSupport<T>> void deleteAll(@NotNull Class<T> tClass);

    <T extends DataSupport<T>> List<T> find(@NotNull Class<T> tClass, Consumer<Options> consumer);

    <T extends DataSupport<T>> List<T> find(@NotNull Class<T> tClass, List<Long> ids);

    <T extends DataSupport<T>> List<T> find(@NotNull Class<T> tClass, Long... ids);

    <T extends DataSupport<T>> List<T> findAll(@NotNull Class<T> tClass);

    <T extends DataSupport<T>> T findOne(@NotNull Class<T> tClass, String predicate, Object... args);

    <T extends DataSupport<T>> T findOne(@NotNull Class<T> tClass, Long id);

    <T extends DataSupport<T>> T first(@NotNull Class<T> tClass, String predicate, Object... args);

    <T extends DataSupport<T>> T first(@NotNull Class<T> tClass);

    <T extends DataSupport<T>> T last(@NotNull Class<T> tClass, String predicate, Object... args);

    <T extends DataSupport<T>> T last(@NotNull Class<T> tClass);

    <T extends DataSupport<T>> long count(@NotNull Class<T> tClass, String predicate, Object... args);

    <T extends DataSupport<T>> long count(@NotNull Class<T> tClass);

    <T extends DataSupport<T>> double average(@NotNull Class<T> tClass, String column, String predicate, Object... args);

    <T extends DataSupport<T>> double average(@NotNull Class<T> tClass, String column);

    <T extends DataSupport<T>> Number sum(@NotNull Class<T> tClass, String column, String predicate, Object... args);

    <T extends DataSupport<T>> Number sum(@NotNull Class<T> tClass, String column);

    <T extends DataSupport<T>> Number max(@NotNull Class<T> tClass, String column, String predicate, Object... args);

    <T extends DataSupport<T>> Number max(@NotNull Class<T> tClass, String column);

    <T extends DataSupport<T>> Number min(@NotNull Class<T> tClass, String column, String predicate, Object... args);

    <T extends DataSupport<T>> Number min(@NotNull Class<T> tClass, String column);
}