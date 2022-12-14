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

package org.wildfly.extension.batch.jberet.deployment;

import org.jboss.as.controller.parsing.ParseUtils;
import org.jboss.staxmapper.XMLExtendedStreamReader;
import org.wildfly.extension.batch.jberet.Attribute;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

/**
 * A parser for batch deployment descriptors ({@code batch-jberet:3.0}) in {@code jboss-all.xml}.
 */
public class BatchDeploymentDescriptorParser_3_0 extends BatchDeploymentDescriptorParser_2_0 {

    public static final String NAMESPACE = "urn:jboss:domain:batch-jberet:3.0";
    public static final QName ROOT_ELEMENT = new QName(NAMESPACE, "batch");

    @Override
    Integer parseExecutionRecordsLimit(final XMLExtendedStreamReader reader) throws XMLStreamException {
        Integer executionRecordsLimit = null;
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            if (Attribute.EXECUTION_RECORDS_LIMIT.getLocalName().equals(reader.getAttributeLocalName(0))) {
                try {
                    executionRecordsLimit = Integer.valueOf(reader.getAttributeValue(0));
                } catch (NumberFormatException e) {
                    throw ParseUtils.invalidAttributeValue(reader, 0);
                }
            } else {
                throw ParseUtils.unexpectedAttribute(reader, 0);
            }
        }
        return executionRecordsLimit;
    }
}
