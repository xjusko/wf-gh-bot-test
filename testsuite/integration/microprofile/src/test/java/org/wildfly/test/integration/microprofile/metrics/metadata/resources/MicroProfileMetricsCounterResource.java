/*
 * Copyright 2019 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wildfly.test.integration.microprofile.metrics.metadata.resources;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricType;

@Path("/counter")
public class MicroProfileMetricsCounterResource {

   @Inject
   MetricRegistry registry;

   @Inject
   CustomCounterMetric customCounter;

   @PostConstruct
   public void init() {
      registry.register(Metadata.builder()
                      .withName("customCounter")
                      .withType(MetricType.COUNTER)
                      .build(),
              customCounter);
   }

   @GET
   @Path("/hello")
   public Response hello() {
      Metadata counterMetadata = Metadata.builder()
              .withName("helloCounter")
              .withType(MetricType.COUNTER)
              .build();

      registry.counter(counterMetadata).inc();
      registry.counter("customCounter").inc();
      return Response.ok("Hello World!").build();
   }
}
