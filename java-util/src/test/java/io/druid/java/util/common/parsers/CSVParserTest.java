/*
 * Licensed to Metamarkets Group Inc. (Metamarkets) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. Metamarkets licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.druid.java.util.common.parsers;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class CSVParserTest
{

  @Test
  public void testValidHeader()
  {
    String csv = "time,value1,value2";
    final Parser<String, Object> csvParser;
    boolean parseable = true;
    try {
      csvParser = new CSVParser(Optional.<String>fromNullable(null), csv);
    }
    catch (Exception e) {
      parseable = false;
    }
    finally {
      Assert.assertTrue(parseable);
    }
  }

  @Test
  public void testInvalidHeader()
  {
    String csv = "time,value1,value2,value2";
    final Parser<String, Object> csvParser;
    boolean parseable = true;
    try {
      csvParser = new CSVParser(Optional.<String>fromNullable(null), csv);
    }
    catch (Exception e) {
      parseable = false;
    }
    finally {
      Assert.assertFalse(parseable);
    }
  }

  @Test
  public void testCSVParserWithHeader()
  {
    String header = "time,value1,value2";
    final Parser<String, Object> csvParser = new CSVParser(Optional.<String>fromNullable(null), header);
    String body = "hello,world,foo";
    final Map<String, Object> jsonMap = csvParser.parse(body);
    Assert.assertEquals(
        "jsonMap",
        ImmutableMap.of("time", "hello", "value1", "world", "value2", "foo"),
        jsonMap
    );
  }

  @Test
  public void testCSVParserWithoutHeader()
  {
    final Parser<String, Object> csvParser = new CSVParser(Optional.<String>fromNullable(null), false, 0);
    String body = "hello,world,foo";
    final Map<String, Object> jsonMap = csvParser.parse(body);
    Assert.assertEquals(
        "jsonMap",
        ImmutableMap.of("column_1", "hello", "column_2", "world", "column_3", "foo"),
        jsonMap
    );
  }

  @Test
  public void testCSVParserWithSkipHeaderRows()
  {
    final int skipHeaderRows = 2;
    final Parser<String, Object> csvParser = new CSVParser(
        Optional.absent(),
        false,
        skipHeaderRows
    );
    csvParser.startFileFromBeginning();
    final String[] body = new String[] {
        "header,line,1",
        "header,line,2",
        "hello,world,foo"
    };
    int index;
    for (index = 0; index < skipHeaderRows; index++) {
      Assert.assertNull(csvParser.parse(body[index]));
    }
    final Map<String, Object> jsonMap = csvParser.parse(body[index]);
    Assert.assertEquals(
        "jsonMap",
        ImmutableMap.of("column_1", "hello", "column_2", "world", "column_3", "foo"),
        jsonMap
    );
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testCSVParserWithoutStartFileFromBeginning()
  {
    final int skipHeaderRows = 2;
    final Parser<String, Object> csvParser = new CSVParser(
        Optional.absent(),
        false,
        skipHeaderRows
    );
    final String[] body = new String[] {
        "header\tline\t1",
        "header\tline\t2",
        "hello\tworld\tfoo"
    };
    csvParser.parse(body[0]);
  }
}
