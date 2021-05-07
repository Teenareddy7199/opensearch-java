/*
 * Licensed to Elasticsearch B.V. under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch B.V. licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package co.elastic.clients.elasticsearch.experiments.inheritance;

import co.elastic.clients.elasticsearch.experiments.inheritance.child.ChildClass;
import co.elastic.clients.elasticsearch.experiments.inheritance.final_.FinalClass;
import co.elastic.clients.json.JsonpValueParser;
import co.elastic.clients.json.ToJsonp;
import org.junit.Assert;
import org.junit.Test;

import javax.json.spi.JsonProvider;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonParser;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;

public class InheritanceTest extends Assert {

    @Test
    public void testSerialization() {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonProvider provider = JsonProvider.provider();

        FinalClass fc = new FinalClass.Builder()
            // Start fields from the top of the hierarchy to test setter return values
            .baseField("baseValue")
            .childField("childValue")
            .finalField("finalValue")
            .build();

        JsonGenerator generator = provider.createGenerator(baos);
        fc.toJsonp(generator, ToJsonp.DEFAULT_PARAMS);

        generator.close();
        String str = baos.toString();

        assertEquals("{\"baseField\":\"baseValue\",\"childField\":\"childValue\",\"finalField\":\"finalValue\"}", str);

        baos.reset();

        ChildClass cc = new ChildClass.Builder()
            // Start fields from the top of the hierarchy to test setter return values
            .baseField("baseValue")
            .childField("childValue")
            .build();

        generator = provider.createGenerator(baos);
        cc.toJsonp(generator, ToJsonp.DEFAULT_PARAMS);

        generator.close();
        str = baos.toString();

        assertEquals("{\"baseField\":\"baseValue\",\"childField\":\"childValue\"}", str);
    }

    @Test
    public void testDeserialization() {
        JsonProvider provider = JsonProvider.provider();

        JsonParser parser = provider.createParser(new StringReader(
            "{\"baseField\":\"baseValue\",\"childField\":\"childValue\",\"finalField\":\"finalValue\"}"));

        FinalClass fc = FinalClass.JSONP_PARSER.parse(parser, JsonpValueParser.DEFAULT_PARAMS);

        assertEquals("baseValue", fc.baseField());
        assertEquals("childValue", fc.childField());
        assertEquals("finalValue", fc.finalField());


        parser = provider.createParser(new StringReader(
            "{\"baseField\":\"baseValue\",\"childField\":\"childValue\"}"));

        ChildClass cc = ChildClass.JSONP_PARSER.parse(parser, JsonpValueParser.DEFAULT_PARAMS);

        assertEquals("baseValue", cc.baseField());
        assertEquals("childValue", cc.childField());
    }
}