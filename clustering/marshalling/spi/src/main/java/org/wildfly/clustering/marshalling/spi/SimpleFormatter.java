/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2017, Red Hat, Inc., and individual contributors
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

package org.wildfly.clustering.marshalling.spi;

import java.util.function.Function;

/**
 * {@link Formatter} for keys with a simple string representation.
 * @author Paul Ferraro
 */
public class SimpleFormatter<K> implements Formatter<K> {

    private final Class<K> targetClass;
    private final Function<String, K> parser;
    private final Function<K, String> formatter;

    public SimpleFormatter(Class<K> targetClass, Function<String, K> parser) {
        this(targetClass, parser, Object::toString);
    }

    public SimpleFormatter(Class<K> targetClass, Function<String, K> parser, Function<K, String> formatter) {
        this.targetClass = targetClass;
        this.parser = parser;
        this.formatter = formatter;
    }

    @Override
    public Class<K> getTargetClass() {
        return this.targetClass;
    }

    @Override
    public K parse(String value) {
        return this.parser.apply(value);
    }

    @Override
    public String format(K key) {
        return this.formatter.apply(key);
    }
}
