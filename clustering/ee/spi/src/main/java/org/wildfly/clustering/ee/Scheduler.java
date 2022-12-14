/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2019, Red Hat, Inc., and individual contributors
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

package org.wildfly.clustering.ee;

import java.util.stream.Stream;

/**
 * A task scheduler.
 * @author Paul Ferraro
 */
public interface Scheduler<I, M> extends AutoCloseable {
    /**
     * Schedules a task for the object with the specified identifier, using the specified metaData
     * @param id an object identifier
     * @param metaData the object meta-data
     */
    void schedule(I id, M metaData);

    /**
     * Cancels a previously scheduled task for the object with the specified identifier.
     * @param id an object identifier
     */
    void cancel(I id);

    /**
     * Returns a stream of scheduled item identifiers.
     * @return a stream of scheduled item identifiers.
     */
    Stream<I> stream();

    /**
     * Indicates whether the object with the specified identifier is scheduled.
     * @param id an object identifier
     */
    default boolean contains(I id) {
        return this.stream().anyMatch(id::equals);
    }

    /**
     * Closes any resources used by this scheduler.
     */
    @Override
    void close();
}
