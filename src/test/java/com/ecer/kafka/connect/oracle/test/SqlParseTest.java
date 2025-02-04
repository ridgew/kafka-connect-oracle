/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements. See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache license, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License. You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the license for the specific language governing permissions and
* limitations under the license.
*/
package com.ecer.kafka.connect.oracle.test;

import java.io.File;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.Test;

import static java.util.Objects.isNull;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.drop.Drop;

/**
 *
 */
public class SqlParseTest {

    private static final String CONFIG = "src/test/resources/log4j-console.xml";

    public static void main(final String[] args) throws Exception {
        final File file = new File(CONFIG);
        try (final LoggerContext ctx = Configurator.initialize("LogTest", SqlParseTest.class.getClassLoader(),
                file.toURI())) {
            final Logger logger = LogManager.getLogger("TestLogger");

            for (long i = 0;; i += 1) {
                logger.debug("Sequence: " + i);
                Thread.sleep(250);
            }
        }
    }

    /**
     * 看word是否在lineText中存在，支持正则表达式
     * 
     * @param lineText
     * @param word
     * @return 是否找到
     */
    private static boolean isContains(String lineText, String word) {
        Pattern pattern = Pattern.compile(word, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(lineText);
        return matcher.find();
    }

    /**
     * 
     * @param regex
     * @param text
     * @param group
     * @return
     */
    private static String getMatchedString(String regex, String text, int group) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            return matcher.group(group);
        }
        return null;
    }

    @Test
    public void testOneParent() throws JSQLParserException {
        String errorSql = "drop table T5\n" + "AS \"BIN$yjgthaaz/ejgUBGsAwBtTw==$0\"";
        String dropFix = getMatchedString("drop table ([\\w]+)([\\w\\W]+)AS\\s\"([^\"]+)\"", errorSql, 1);
        if (!isNull(dropFix))
            System.out.println(dropFix);

        String okDrop = "drop table T5";
        Statement stmt = CCJSqlParserUtil.parse(okDrop);
        Drop drop = (Drop) stmt;
        System.out.println(drop.getName().getName());

    }
}
