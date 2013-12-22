/*
 * Copyright 2011 The Apache Software Foundation
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package my.test.cql3.statements;

import my.test.TestBase;

public class TableTest extends TestBase {
    public static void main(String[] args) throws Exception {
        new TableTest().start();
    }

    @Override
    public void startInternal() throws Exception {
        tableName = "TableTest3";

        //test_CQL3Type();

        //test_RawStatement_prepare();
        //
        //        test_CFPropDefs_validate();
        //
        //        test_CreateTableStatement_applyPropertiesTo();

        //test_getColumns();
        test_AlterTableStatement();
    }

    void test_AlterTableStatement() throws Exception {
        //test_Alter_Add();
        //test_Alter_Alter();
        //test_Alter_Drop();
        test_Alter_With();
        //test_Alter_Rename();
    }

    void test_Alter_Add() throws Exception {
        tableName = "TableTest_Alter_Add";
        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName //
                + " ( block_id uuid, f1 int, short_hair boolean," //
                + "PRIMARY KEY (block_id, f1)) WITH COMPACT STORAGE");

        //错误是: Cannot add new column to a compact CF
        tryExecute("ALTER TABLE " + tableName + " ADD f2 int");

        tableName = "TableTest_Alter_Add2";
        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName //
                + " ( block_id uuid, f1 int, short_hair boolean," //
                + "PRIMARY KEY (block_id, f1))");

        //错误是:Invalid column name block_id because it conflicts with a PRIMARY KEY part
        tryExecute("ALTER TABLE " + tableName + " ADD block_id int");
        //错误是:Invalid column name block_id because it conflicts with a PRIMARY KEY part
        tryExecute("ALTER TABLE " + tableName + " ADD f1 int");
        //错误是:Invalid column name short_hair because it conflicts with an existing column
        tryExecute("ALTER TABLE " + tableName + " ADD short_hair int");

        tableName = "TableTest_Alter_Add3";
        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName //
                + " ( block_id uuid, f1 int, short_hair boolean," //
                + "PRIMARY KEY (block_id)) WITH COMPACT STORAGE");

        //错误是:Cannot use collection types with non-composite PRIMARY KEY
        tryExecute("ALTER TABLE " + tableName + " ADD f2 list<int>");
    }

    void test_Alter_Alter() throws Exception {
        tableName = "TableTest_Alter_Alter";
        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName //
                + " ( block_id uuid, f1 int, " //
                + "PRIMARY KEY (block_id))");

        //错误是:Cell f2 was not found in table tabletest_alter_alter
        tryExecute("ALTER TABLE " + tableName + " ALTER f2 TYPE bigint");

        tableName = "TableTest_Alter_Alter2";
        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName //
                + " ( block_id uuid, f1 int, f2 int,  " //
                + "PRIMARY KEY (block_id, f1))");

        //错误是:counter type is not supported for PRIMARY KEY part block_id
        tryExecute("ALTER TABLE " + tableName + " ALTER block_id TYPE counter");

        tableName = "TableTest_Alter_Alter3";
        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName //
                + " ( block_id uuid, f1 int, f2 int,  " //
                + "PRIMARY KEY ((block_id, f1)))");

        //错误是:Cannot change block_id from type uuid to type int: types are incompatible.
        tryExecute("ALTER TABLE " + tableName + " ALTER block_id TYPE int");
    }

    void test_Alter_Drop() throws Exception {
        tableName = "TableTest_Alter_Drop";
        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName //
                + " ( block_id uuid, f1 int, " //
                + "PRIMARY KEY (block_id)) WITH COMPACT STORAGE");

        //错误是:Cannot drop columns from a non-CQL3 CF
        tryExecute("ALTER TABLE " + tableName + " DROP block_id");

        tableName = "TableTest_Alter_Drop2";
        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName //
                + " ( block_id uuid, f1 int, " //
                + "PRIMARY KEY (block_id))");

        //错误是:Cell f2 was not found in table tabletest_alter_drop2
        tryExecute("ALTER TABLE " + tableName + " DROP f2");

        //错误是:Cannot drop PRIMARY KEY part block_id
        tryExecute("ALTER TABLE " + tableName + " DROP block_id");
    }

    void test_Alter_With() throws Exception {
        tableName = "TableTest_Alter_With";
        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName //
                + " ( block_id uuid, f1 int, " //
                + "PRIMARY KEY (block_id)) WITH COMPACT STORAGE");

        //tryExecute("ALTER TABLE " + tableName + " WITH read_repair_chance=0.9");

        //语法错误是:line 0:-1 no viable alternative at input '<EOF>'
        //不能只加WITH
        tryExecute("ALTER TABLE " + tableName + " WITH");
    }

    void test_Alter_Rename() throws Exception {
        tableName = "TableTest_Alter_Rename";
        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName //
                + " ( block_id uuid, f1 int, " //
                + "PRIMARY KEY (block_id))");

        //错误是:Cannot rename non PRIMARY KEY part f1
        //只能重命名PRIMARY KEY
        tryExecute("ALTER TABLE " + tableName + " RENAME f1 TO f2");

        tryExecute("ALTER TABLE " + tableName + " RENAME block_id TO f2");
    }

    void test_getColumns() throws Exception {
        //        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName //
        //                + " ( block_id uuid, f1 int, short_hair boolean," //
        //                + "PRIMARY KEY (block_id)) WITH COMPACT STORAGE");
        //        
        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName //
                + " ( block_id uuid, f1 int, short_hair boolean," //
                + "PRIMARY KEY (block_id))");
    }

    void test_CQL3Type() throws Exception {
        //自定义一个叫"name"的类型
        execute("CREATE TYPE IF NOT EXISTS name (firstname text, lastname text)");

        cql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" //
                //对应所有org.apache.cassandra.cql3.CQL3Type.Native
                //16个本地类型
                + "f_ascii ascii primary key," //
                + "f_bigint bigint," //
                + "f_blob blob," //
                + "f_boolean boolean," //
                //+ "f_counter counter," //counter类列的列不能跟其他列一起定义
                + "f_decimal decimal," //
                + "f_double double," //
                + "f_float float," //
                + "f_inet inet," //
                + "f_int int," //
                + "f_text text," //
                + "f_timestamp timestamp," //
                + "f_uuid uuid," //
                + "f_varchar varchar," //
                + "f_varint varint," //
                + "f_timeuuid timeuuid," //
                //对应org.apache.cassandra.cql3.CQL3Type.Custom
                + "f_custom1 'org.apache.cassandra.db.marshal.UTF8Type'," //
                + "f_custom2 'UTF8Type'," //
                //对应org.apache.cassandra.cql3.CQL3Type.Collection
                + "f_map map<int, int>," //
                + "f_list list<int>," //
                + "f_set set<int>," //
                //对应org.apache.cassandra.cql3.CQL3Type.UserDefined
                + "f_UserDefined name," //最后一个字段后面的逗号可以有也可以没有
                + ")";
        tryExecute();
    }

    //按org.apache.cassandra.cql3.statements.CreateTableStatement.RawStatement.prepare()中的代码测试
    public void test_RawStatement_prepare() throws Exception {
        //tryExecute("DROP TABLE IF EXISTS " + tableName);

        //表名不能使用中文，在语法分析阶段就能检查出来了，这里是IDENT的场景
        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName + "中 ( block_id uuid)");

        //表名不能使用中文，但是不能在语法分析阶段检查出来，这里是QUOTED_NAME的场景(用双引号括起来)
        //在RawStatement_prepare的if (!columnFamily().matches("\\w+"))中检查
        tryExecute("CREATE TABLE IF NOT EXISTS \"" + tableName + "中\" ( block_id uuid)");

        //下划线可以
        //tryExecute("CREATE TABLE IF NOT EXISTS " + tableName + "_ddd ( block_id uuid)");

        //表名不能超过48个字符
        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName + "toolonglonglonglonglonglonglong ( block_id uuid)");

        //定义了重复的字段
        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName + " ( block_id uuid, block_id uuid)");

        //min_threshold属性不支持 (Unknown property 'min_threshold')
        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName + " ( block_id uuid ) WITH min_threshold=2");

        //Create Table语句不能使用占位符
        //session.prepare("CREATE TABLE IF NOT EXISTS " + tableName + " ( block_id uuid ) WITH gc_grace_seconds=?");

        //每个表必须有主键
        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName + " ( block_id uuid, breed text, emails set<text>)");

        //定义了多个PRIMARY KEY
        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName
                + " ( block_id uuid PRIMARY KEY, breed text, color text, short_hair boolean,"
                + "PRIMARY KEY ((block_id, breed), color, short_hair))");

        //partition key和clustering key中的字段类型不能是CollectionType
        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName
                + " ( block_id uuid, breed set<text>, color set<text>, short_hair boolean,"
                + "PRIMARY KEY ((block_id, breed), color, short_hair))");

        //主键字段不能是counter类型
        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName + " ( block_id counter PRIMARY KEY, breed text)");

        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName + " ( block_id counter PRIMARY KEY, breed text)"
                + "WITH CLUSTERING ORDER BY (block_id DESC)");

        //没有clustering key、使用COMPACT STORAGE、且所有的字段都是partition key时是不允许的
        //错误是: No definition found that is not part of the PRIMARY KEY
        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName //
                + " ( block_id uuid, breed text," //
                + "PRIMARY KEY ((block_id, breed))) WITH COMPACT STORAGE");

        //没有clustering key、使用COMPACT STORAGE、且包含Collection类型的字段时是不允许的
        //错误是: Collection types are not supported with COMPACT STORAGE
        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName //
                + " ( block_id uuid, breed text, emails set<text>," //
                + "PRIMARY KEY ((block_id, breed))) WITH COMPACT STORAGE");

        //这样就正常了，org.apache.cassandra.cql3.statements.CreateTableStatement.comparator默认使用UTF8Type
        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName //
                + " ( block_id uuid, breed text, short_hair boolean," //
                + "PRIMARY KEY ((block_id, breed))) WITH COMPACT STORAGE");

        //测试if (columnAliases.isEmpty()) 且 useCompactStorage=false的场景
        execute("CREATE TABLE IF NOT EXISTS " + tableName //
                + " ( block_id uuid, breed text, short_hair boolean, emails set<text>," //
                + "PRIMARY KEY ((block_id, breed)))");

        //clustering key只有一个、使用COMPACT STORAGE、且包含Collection类型的字段时是不允许的
        //错误是: Collection types are not supported with COMPACT STORAGE
        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName //
                + " ( block_id uuid, breed text, short_hair boolean, emails set<text>," //
                + "PRIMARY KEY ((block_id, breed), short_hair)) WITH COMPACT STORAGE");

        execute("CREATE TABLE IF NOT EXISTS " + tableName //
                + " ( block_id uuid, breed text, short_hair boolean, emails text," //
                + "PRIMARY KEY ((block_id, breed), short_hair)) WITH COMPACT STORAGE");

        //clustering key中的字段不能是counter类型
        //错误是: counter type is not supported for PRIMARY KEY part java.nio.HeapByteBuffer[pos=0 lim=6 cap=6]
        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName //
                + " ( block_id uuid, breed text, emails counter," //
                + "PRIMARY KEY ((block_id, breed), emails)) WITH COMPACT STORAGE");

        //但是加了CLUSTERING ORDER后就可绕过这个问题(这是一个bug)
        //不过也可以正常添加和查询记录，见my.test.cql3.statements.BugCreateTableStatementTest
        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName //
                + " ( block_id uuid, breed text, emails counter," //
                + "PRIMARY KEY ((block_id, breed), emails)) WITH COMPACT STORAGE " //
                + "AND CLUSTERING ORDER BY (emails DESC)");

        //测试org.apache.cassandra.cql3.statements.CreateTableStatement.RawStatement.prepare()中的
        //if (useCompactStorage && !stmt.columnAliases.isEmpty()) if (stmt.columns.isEmpty())都为true的场景
        execute("CREATE TABLE IF NOT EXISTS " + tableName //
                + " ( block_id uuid, breed text, short_hair boolean, f1 text," //
                + "PRIMARY KEY ((block_id, breed), short_hair, f1)) WITH COMPACT STORAGE");

        //使用COMPACT STORAGE时，除了partition key和clustering key中的字段外，剩下的字段不能大于1个
        //错误是: 
        //COMPACT STORAGE with composite PRIMARY KEY allows no more than one column not part of the PRIMARY KEY (got: f2, f1)
        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName //
                + " ( block_id uuid, breed text, short_hair boolean, f1 text, f2 text," //
                + "PRIMARY KEY ((block_id, breed), short_hair)) WITH COMPACT STORAGE");

        //正常
        execute("CREATE TABLE IF NOT EXISTS " + tableName //
                + " ( block_id uuid, breed text, short_hair boolean, f1 text," //
                + "PRIMARY KEY ((block_id, breed), short_hair)) WITH COMPACT STORAGE");

        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName //
                + " ( block_id uuid, breed text," //
                + "PRIMARY KEY ((block_id, breed))) WITH COMPACT STORAGE");

        //ORDER BY (color DESC, short_hair)少了ASC
        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName + " ( block_id uuid, breed text, color text, short_hair boolean,"
                + "PRIMARY KEY ((block_id, breed), color)) " //
                + "WITH CLUSTERING ORDER BY (color DESC, short_hair)");

        //排序key中的字段比clustering key中的字段多
        //错误是:Only clustering key columns can be defined in CLUSTERING ORDER directive
        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName + " ( block_id uuid, breed text, color text, short_hair boolean,"
                + "PRIMARY KEY ((block_id, breed), color)) " //
                + "WITH CLUSTERING ORDER BY (color DESC, short_hair ASC)");

        //排序key中的字段与clustering key中的字段顺序必须一样
        //错误是:
        //The order of columns in the CLUSTERING ORDER directive 
        //must be the one of the clustering key (short_hair must appear before color)
        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName + " ( block_id uuid, breed text, color text, short_hair boolean,"
                + "PRIMARY KEY ((block_id, breed),short_hair, color)) " //
                + "WITH CLUSTERING ORDER BY (color DESC, short_hair ASC)");

        //排序key中的字段必须是clustering key中的字段
        //错误是:Missing CLUSTERING ORDER for column color
        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName + " ( block_id uuid, breed text, color text, short_hair boolean,"
                + "PRIMARY KEY ((block_id, breed), color)) " //
                + "WITH CLUSTERING ORDER BY (short_hair ASC)");

        //CLUSTERING ORDER中的字段只能是clustering key包含的字段，并且不能是partition key的字段
        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName + " ( block_id uuid, breed text, color text, short_hair boolean,"
                + "PRIMARY KEY ((block_id, breed), color)) " //
                + "WITH CLUSTERING ORDER BY (block_id ASC)");

        execute("CREATE TABLE IF NOT EXISTS " + tableName + " ( block_id uuid, breed text, color text, short_hair boolean,"
                + "PRIMARY KEY ((block_id, breed), color, short_hair))");

        execute("CREATE TABLE IF NOT EXISTS " + tableName + " ( block_id uuid, breed text, color text, short_hair boolean,"
                + "PRIMARY KEY (block_id, breed, color, short_hair))");

        execute("CREATE TABLE IF NOT EXISTS " + tableName + " ( block_id uuid, breed text, color text, short_hair boolean,"
                + "PRIMARY KEY ((block_id, breed, color, short_hair)))");

        cql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + //
                "block_id uuid," + //
                "species text," + //
                "alias text," + //
                "population varint," + //
                "PRIMARY KEY (block_id, population)" + //

                ") WITH compression = { 'sstable_compression' : 'DeflateCompressor', 'chunk_length_kb' : 64 }" + //
                "AND compaction = { 'class' : 'SizeTieredCompactionStrategy', 'min_threshold' : 6 }" + //
                " AND CLUSTERING ORDER BY (population DESC)" + //
                " AND COMPACT STORAGE";
        tryExecute();

        //使用全限定类名也是可以的
        execute("CREATE TABLE IF NOT EXISTS " + tableName
                + " ( block_id uuid PRIMARY KEY, breed 'org.apache.cassandra.db.marshal.UTF8Type')");

        //使用简单类名也是可以的
        execute("CREATE TABLE IF NOT EXISTS " + tableName + " ( block_id uuid PRIMARY KEY, breed 'UTF8Type')");

        //UTF8Type必须加引号
        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName + " ( block_id uuid PRIMARY KEY, breed UTF8Type)");

        //测试org.apache.cassandra.cql3.CQL3Type.Collection类
        execute("CREATE TABLE IF NOT EXISTS " + tableName
                + " ( block_id uuid PRIMARY KEY, s set<int>, l list<int>, m map<text,int>)");

        //Collection类型的元素类型不能是couter类型和Collection类型
        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName
                + " ( block_id uuid PRIMARY KEY, s set<int>, l list<int>, m map<set<text>,int>)");

        //PRIMARY KEY中的字段未定义
        //测试org.apache.cassandra.cql3.statements.CreateTableStatement.RawStatement.getTypeAndRemove中的if (type == null)
        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName + " ( block_id uuid, breed text, color text, short_hair boolean,"
                + "PRIMARY KEY (unknownField))");

        //测试org.apache.cassandra.cql3.statements.CreateTableStatement.RawStatement.getTypeAndRemove中的isReversed=true
        execute("CREATE TABLE IF NOT EXISTS " + tableName + " ( block_id uuid, breed text, color text, short_hair boolean,"
                + "PRIMARY KEY (block_id, breed)) WITH CLUSTERING ORDER BY (breed DESC)");

        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName + " ( block_id uuid, breed text, color text, short_hair boolean,"
                + "PRIMARY KEY (block_id, breed, short_hair)) WITH COMPACT STORAGE");

        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName + " ( block_id uuid, breed text, color text, short_hair boolean,"
                + "PRIMARY KEY (block_id))");

        tryExecute("CREATE TABLE IF NOT EXISTS " + tableName + " ( block_id uuid, breed text, color text, short_hair counter,"
                + "PRIMARY KEY (block_id))");
    }

    public void test_CFPropDefs_validate() throws Exception {
        //合法的属性有这些:
        //read_repair_chance, bloom_filter_fp_chance, index_interval, speculative_retry, 
        //gc_grace_seconds, dclocal_read_repair_chance, compaction, memtable_flush_period_in_ms, 
        //default_time_to_live, compression, comment, replicate_on_write, caching, populate_io_cache_on_flush
        cql = "CREATE TABLE IF NOT EXISTS " + tableName + " (block_id uuid PRIMARY KEY, species text)" + //
                "WITH UnknownProperty = 1";

        tryExecute();

        //使用compaction时必须指定'class'子属性
        //否则出错: Missing sub-option 'class' for the 'compaction' option.
        cql = "CREATE TABLE IF NOT EXISTS " + tableName + " (block_id uuid PRIMARY KEY, species text)" + //
                "WITH compaction = {'min_threshold' : 6 }";

        tryExecute();

        //'class'必须是org.apache.cassandra.db.compaction.AbstractCompactionStrategy的子类
        //否则出错: Specified compaction strategy class (java.lang.Object) is not derived from AbstractReplicationStrategy
        cql = "CREATE TABLE IF NOT EXISTS " + tableName + " (block_id uuid PRIMARY KEY, species text)" + //
                "WITH compaction = { 'class' : 'java.lang.Object', 'min_threshold' : 6 }";

        tryExecute();

        cql = "CREATE TABLE IF NOT EXISTS " + tableName + " (block_id uuid PRIMARY KEY, species text)" + //
                "WITH compaction = { 'class' : 'SizeTieredCompactionStrategy', 'min_threshold' : 6 }";
        tryExecute();

        cql = "CREATE TABLE IF NOT EXISTS " + tableName + " (block_id uuid PRIMARY KEY, species text)" + //
                "WITH compaction = { 'class' : 'LeveledCompactionStrategy', 'sstable_size_in_mb' : 50 }";
        tryExecute();

        cql = "CREATE TABLE IF NOT EXISTS " + tableName + " (block_id uuid PRIMARY KEY, species text)" + //
                "WITH compaction = { 'class' : 'LeveledCompactionStrategy', 'bucket_low' : 0.9 }";
        tryExecute();

        //
        cql = "CREATE TABLE IF NOT EXISTS " + tableName + " (block_id uuid PRIMARY KEY, species text)" + //
                "WITH compaction = { 'class' : 'SizeTieredCompactionStrategy', 'min_threshold' : 1, 'max_threshold' : 5  }";
        tryExecute();

        cql = "CREATE TABLE IF NOT EXISTS " + tableName + " (block_id uuid PRIMARY KEY, species text)" + //
                "WITH compaction = { 'class' : 'LeveledCompactionStrategy', 'min_threshold' : 1, 'max_threshold' : 5  }";
        tryExecute();

        //crc_check_chance的取值是闭区间[0.0, 1.0]
        //否则出错: crc_check_chance should be between 0.0 and 1.0
        cql = "CREATE TABLE IF NOT EXISTS " + tableName + " (block_id uuid PRIMARY KEY, species text)" + //
                "WITH compression = " + //
                "{ 'sstable_compression' : 'DeflateCompressor', 'chunk_length_kb' : 64, 'crc_check_chance' : 1.5 }";
        tryExecute();

        //chunk_length_kb必须是power of 2
        //否则出错: chunk_length_kb must be a power of 2
        cql = "CREATE TABLE IF NOT EXISTS " + tableName + " (block_id uuid PRIMARY KEY, species text)" + //
                "WITH compression = " + //
                "{ 'sstable_compression' : 'DeflateCompressor', 'chunk_length_kb' : 3 }";
        tryExecute();

        //测试speculative_retry
        //对应org.apache.cassandra.config.CFMetaData.SpeculativeRetry.fromString(String)
        cql = "CREATE TABLE IF NOT EXISTS " + tableName + " (block_id uuid PRIMARY KEY, species text)" + //
                "WITH speculative_retry = '90percentile'";
        tryExecute();

        cql = "CREATE TABLE IF NOT EXISTS " + tableName + " (block_id uuid PRIMARY KEY, species text)" + //
                "WITH speculative_retry = '60ms'";
        tryExecute();

        cql = "CREATE TABLE IF NOT EXISTS " + tableName + " (block_id uuid PRIMARY KEY, species text)" + //
                "WITH speculative_retry = 'ALWAYS'";
        tryExecute();

        cql = "CREATE TABLE IF NOT EXISTS " + tableName + " (block_id uuid PRIMARY KEY, species text)" + //
                "WITH speculative_retry = 'NONE'";
        tryExecute();

        cql = "CREATE TABLE IF NOT EXISTS " + tableName + " (block_id uuid PRIMARY KEY, species text)" + //
                "WITH speculative_retry = 'invalid speculative_retry type'";
        tryExecute();
    }

    public void test_CreateTableStatement_applyPropertiesTo() throws Exception {
        //execute("CREATE TABLE IF NOT EXISTS " + tableName + " ( block_id uuid PRIMARY KEY, breed text, emails set<text>)");

        execute("CREATE TABLE IF NOT EXISTS " + tableName //
                + " ( block_id uuid, breed text, short_hair boolean, f1 text, f2 int, " //
                + "PRIMARY KEY ((block_id, breed), short_hair, f1)) WITH COMPACT STORAGE");
    }
}
