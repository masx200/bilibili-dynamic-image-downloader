import argparse
import sqlite3


def main():
    # 创建ArgumentParser对象
    parser = argparse.ArgumentParser(
        description="Process some SQL file with a database and output the result to a file."
    )

    # 添加命令行参数
    parser.add_argument(
        "--output_file",
        type=str,
        required=True,
        help="The path to the output file where the results will be saved.",
    )
    parser.add_argument(
        "--database",
        type=str,
        help="The database connection string or name.",
        required=True,
    )
    parser.add_argument(
        "--sql_file",
        required=True,
        type=str,
        help="The path to the SQL file that contains the query to execute.",
    )

    # 解析命令行参数
    args = parser.parse_args()

    # 使用解析后的参数
    print(f"Output file: {args.output_file}")
    print(f"Database: {args.database}")
    print(f"SQL file: {args.sql_file}")

    # 这里可以添加你的逻辑，例如连接数据库、执行SQL文件中的查询，并将结果保存到输出文件中

    output_file = (
        args.output_file
        # "C:/Users/msx/Documents/bilibili-dynamic-image-downloader测试数据库-286509323.txt"
    )
    # 连接到数据库
    conn = sqlite3.connect(
        args.database
        # "C:/Users/msx/Documents/bilibili-dynamic-image-downloader测试数据库-286509323.db"
    )

    # 创建游标对象
    cursor = conn.cursor()

    # 读取脚本文件
    with open(
            args.sql_file, encoding="utf-8"
            # "C:/Users/msx/Documents/bilibili-dynamic-image-downloader/SELECT picturesrc   FROM dynamicpictures.sql",
            ,
            mode="r",
    ) as f:
        script = f.read()

    # 执行脚本
    cursor.execute(script)
    rows = cursor.fetchall()
    # print(rows)
    # 提交更改
    conn.commit()

    # 关闭连接
    conn.close()
    with open(output_file, "w", newline="", encoding="utf-8") as file:
        for row in rows:
            file.write(",".join(str(item) for item in row) + "\n")


if __name__ == "__main__":
    main()
