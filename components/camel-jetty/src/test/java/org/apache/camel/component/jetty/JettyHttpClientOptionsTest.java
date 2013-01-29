/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.jetty;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http.HttpEndpoint;
import org.apache.camel.component.http.HttpProducer;
import org.junit.Test;

/**
 * Unit test for http client options.
 */
public class JettyHttpClientOptionsTest extends BaseJettyTest {

    @Test
    public void testCustomHttpBinding() throws Exception {
        // assert jetty was configured with our timeout
        HttpEndpoint jettyEndpoint = context.getEndpoint("http://localhost:{{port}}/myapp/myservice?httpClient.soTimeout=5555", HttpEndpoint.class);
        assertNotNull("Jetty endpoint should not be null ", jettyEndpoint);
        HttpProducer producer = (HttpProducer)jettyEndpoint.createProducer();
        assertEquals("Get the wrong http client parameter", 5555, producer.getHttpClient().getParams().getSoTimeout());

        // send and receive
        Object out = template.requestBody("http://localhost:{{port}}/myapp/myservice", "Hello World");
        assertEquals("Bye World", context.getTypeConverter().convertTo(String.class, out));
    }
    
    @Test
    public void testProxySettingOfJettyHttpClient() throws Exception {
        // setup the Proxy setting through the URI
        HttpEndpoint jettyEndpoint = context.getEndpoint("jetty://http://localhost:{{port}}/proxy/setting?proxyHost=192.168.0.1&proxyPort=9090", HttpEndpoint.class);
        assertNotNull("Jetty endpoint should not be null ", jettyEndpoint);
        JettyHttpProducer producer = (JettyHttpProducer)jettyEndpoint.createProducer();
        assertEquals("Get the wrong http proxy host parameter", "192.168.0.1", producer.getClient().getProxy().getHost());
        assertEquals("Get the wrong http proxy port paramerter", 9090, producer.getClient().getProxy().getPort());
        
        // setup the context properties
        context.getProperties().put("http.proxyHost", "192.168.0.2");
        context.getProperties().put("http.proxyPort", "8080");
        jettyEndpoint = context.getEndpoint("jetty://http://localhost:{{port}}/proxy2/setting", HttpEndpoint.class);
        producer = (JettyHttpProducer)jettyEndpoint.createProducer();
        assertEquals("Get the wrong http proxy host parameter", "192.168.0.2", producer.getClient().getProxy().getHost());
        assertEquals("Get the wrong http proxy port paramerter", 8080, producer.getClient().getProxy().getPort());
        context.getProperties().clear();

    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("jetty:http://localhost:{{port}}/myapp/myservice?httpClient.soTimeout=5555").transform().constant("Bye World");
            }
        };
    }

}