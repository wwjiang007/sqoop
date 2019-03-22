/**
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

package org.apache.sqoop.metastore.postgres;

import static org.apache.sqoop.manager.postgresql.PostgresqlTestUtil.CONNECT_STRING;
import static org.apache.sqoop.manager.postgresql.PostgresqlTestUtil.DATABASE_USER;
import static org.apache.sqoop.manager.postgresql.PostgresqlTestUtil.PASSWORD;

import org.apache.sqoop.metastore.MetaConnectIncrementalImportTestBase;
import org.apache.sqoop.testcategories.thirdpartytest.PostgresqlTest;
import org.junit.experimental.categories.Category;

/**
 * Test that Incremental-Import values are stored correctly in PostgreSQL
 *
 * This uses JDBC to store and retrieve metastore data from a Postgres server
 *
 * Since this requires a Postgres installation,
 * this class is named in such a way that Sqoop's default QA process does
 * not run it. You need to run this manually with
 * -Dtestcase=PostgresMetaConnectIncrementalImportTest or -Dthirdparty=true.
 *
 *   Once you have a running Postgres database,
 *   Set server URL, database name, username, and password with system variables
 *   -Dsqoop.test.postgresql.connectstring.host_url, -Dsqoop.test.postgresql.database,
 *   -Dsqoop.test.postgresql.username and -Dsqoop.test.postgresql.password respectively
 */
@Category(PostgresqlTest.class)
public class PostgresMetaConnectIncrementalImportTest extends MetaConnectIncrementalImportTestBase {

    public PostgresMetaConnectIncrementalImportTest() {
        super(CONNECT_STRING, DATABASE_USER, PASSWORD);
    }
}
