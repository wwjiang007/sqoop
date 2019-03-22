/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.sqoop.importjob.splitby;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.sqoop.SqoopOptions;
import org.apache.sqoop.importjob.DatabaseAdapterFactory;
import org.apache.sqoop.importjob.configuration.GenericImportJobSplitByTestConfiguration;
import org.apache.sqoop.importjob.configuration.ParquetTestConfiguration;
import org.apache.sqoop.testutil.ArgumentArrayBuilder;
import org.apache.sqoop.testutil.ImportJobTestCase;
import org.apache.sqoop.testutil.adapter.DatabaseAdapter;
import org.apache.sqoop.util.ParquetReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public abstract class SplitByImportTestBase extends ImportJobTestCase implements DatabaseAdapterFactory {

  public static final Log LOG = LogFactory.getLog(SplitByImportTestBase.class.getName());

  private Configuration conf = new Configuration();

  private final ParquetTestConfiguration configuration;
  private final DatabaseAdapter adapter;

  public SplitByImportTestBase() {
    this.adapter = createAdapter();
    this.configuration =  new GenericImportJobSplitByTestConfiguration();
  }

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Override
  protected Configuration getConf() {
    return conf;
  }

  @Override
  protected boolean useHsqldbTestServer() {
    return false;
  }

  @Override
  protected String getConnectString() {
    return adapter.getConnectionString();
  }

  @Override
  protected SqoopOptions getSqoopOptions(Configuration conf) {
    SqoopOptions opts = new SqoopOptions(conf);
    adapter.injectConnectionParameters(opts);
    return opts;
  }

  @Override
  protected void dropTableIfExists(String table) throws SQLException {
    adapter.dropTableIfExists(table, getManager());
  }

  @Before
  public void setUp() {
    super.setUp();
    String[] names = configuration.getNames();
    String[] types = configuration.getTypes();
    createTableWithColTypesAndNames(names, types, new String[0]);
    List<String[]> inputData = configuration.getSampleData();
    for (String[] input  : inputData) {
      insertIntoTable(names, types, input);
    }
  }

  @After
  public void tearDown() {
    try {
      dropTableIfExists(getTableName());
    } catch (SQLException e) {
      LOG.warn("Error trying to drop table on tearDown: " + e);
    }
    super.tearDown();
  }

  private ArgumentArrayBuilder getArgsBuilder() {
    return new ArgumentArrayBuilder()
        .withCommonHadoopFlags(true)
        .withProperty("org.apache.sqoop.splitter.allow_text_splitter","true")
        .withOption("warehouse-dir", getWarehouseDir())
        .withOption("num-mappers", "2")
        .withOption("table", getTableName())
        .withOption("connect", getConnectString())
        .withOption("split-by", GenericImportJobSplitByTestConfiguration.NAME_COLUMN)
        .withOption("as-parquetfile");
  }

  @Test
  public void testSplitBy() throws IOException {
    ArgumentArrayBuilder builder = getArgsBuilder();
    String[] args = builder.build();
    runImport(args);
    verifyParquetFile();
  }

  private void verifyParquetFile() {
    ParquetReader reader = new ParquetReader(new Path(getWarehouseDir() + "/" + getTableName()), getConf());
    assertEquals(asList(configuration.getExpectedResultsForParquet()), reader.readAllInCsvSorted());
  }
}
