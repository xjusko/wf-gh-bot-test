/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2020, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.wildfly.clustering.infinispan.metadata;

import org.infinispan.metadata.EmbeddedMetadata;
import org.infinispan.metadata.EmbeddedMetadata.EmbeddedExpirableMetadata;
import org.infinispan.metadata.EmbeddedMetadata.EmbeddedLifespanExpirableMetadata;
import org.infinispan.metadata.EmbeddedMetadata.EmbeddedMaxIdleExpirableMetadata;
import org.infinispan.protostream.SerializationContext;
import org.wildfly.clustering.marshalling.protostream.AbstractSerializationContextInitializer;

/**
 * @author Paul Ferraro
 */
public class MetadataSerializationContextInitializer extends AbstractSerializationContextInitializer {

    public MetadataSerializationContextInitializer() {
        super("org.infinispan.metadata.proto");
    }

    @Override
    public void registerMarshallers(SerializationContext context) {
        context.registerMarshaller(new EmbeddedMetadataMarshaller<>(EmbeddedMetadata.class));
        context.registerMarshaller(new EmbeddedMetadataMarshaller<>(EmbeddedExpirableMetadata.class));
        context.registerMarshaller(new EmbeddedMetadataMarshaller<>(EmbeddedLifespanExpirableMetadata.class));
        context.registerMarshaller(new EmbeddedMetadataMarshaller<>(EmbeddedMaxIdleExpirableMetadata.class));
    }
}
