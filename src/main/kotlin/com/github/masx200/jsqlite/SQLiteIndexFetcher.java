package com.github.masx200.jsqlite;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteIndexFetcher {


    public static List<IndexesData> fetchIndexes(String dbPath, String tableName) throws SQLException {
        List<IndexesData> indexesDataList = new ArrayList<>();
        String url = "jdbc:sqlite:" + dbPath;

        try (Connection conn = DriverManager.getConnection(url)) {
            // 获取表的所有索引
            try (Statement stmt = conn.createStatement(); ResultSet rsIndexList = stmt.executeQuery("PRAGMA index_list('" + tableName + "')")) {

                while (rsIndexList.next()) {
                    String indexName = rsIndexList.getString("name");
                    boolean isUnique = rsIndexList.getInt("unique") == 1;

                    // 获取每个索引的列信息
                    try (ResultSet rsIndexInfo = stmt.executeQuery("PRAGMA index_info('" + indexName + "')")) {
                        List<String> columns = new ArrayList<>();
                        while (rsIndexInfo.next()) {
                            columns.add(rsIndexInfo.getString("name"));
                        }

                        // 构造 IndexesData 并添加到列表
                        indexesDataList.add(new IndexesData(isUnique, indexName, columns));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            throw e;
        }

        return indexesDataList;
    }
}
