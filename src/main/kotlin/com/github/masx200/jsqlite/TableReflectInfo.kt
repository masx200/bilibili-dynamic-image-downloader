package com.github.masx200.jsqlite

data class TableReflectInfo(
    val tablesMapTypes: HashMap<String, HashMap<String, String>>,
    val tablesMapPrimaryKeys: HashMap<String, String>,
    val tablesMapIsAutoIncrement: HashMap<String, HashMap<String, Boolean>>,

    val tablesMapIndexesData1: HashMap<String, List<IndexesData1>>,
)