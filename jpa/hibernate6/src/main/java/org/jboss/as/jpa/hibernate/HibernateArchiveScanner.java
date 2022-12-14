/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2022 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.jpa.hibernate;


import org.hibernate.boot.archive.scan.spi.AbstractScannerImpl;
import org.hibernate.boot.archive.scan.spi.Scanner;

/**
 * Annotation scanner for Hibernate.  Essentially just passes along the VFS-based ArchiveDescriptorFactory
 *
 * @author Steve Ebersole
 */
public class HibernateArchiveScanner extends AbstractScannerImpl implements Scanner {
    public HibernateArchiveScanner() {
        super( VirtualFileSystemArchiveDescriptorFactory.INSTANCE );
    }
}
