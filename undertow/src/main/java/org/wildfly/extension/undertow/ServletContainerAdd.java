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

package org.wildfly.extension.undertow;

import io.undertow.connector.ByteBufferPool;
import io.undertow.server.handlers.cache.DirectBufferCache;
import io.undertow.servlet.api.CrawlerSessionManagerConfig;
import io.undertow.servlet.api.ServletStackTraces;
import io.undertow.servlet.api.SessionPersistenceManager;

import org.jboss.as.controller.AbstractBoottimeAddStepHandler;
import org.jboss.as.controller.CapabilityServiceBuilder;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.registry.Resource;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.Property;
import org.jboss.msc.service.ServiceController;
import org.xnio.XnioWorker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author <a href="mailto:tomaz.cerar@redhat.com">Tomaz Cerar</a> (c) 2013 Red Hat Inc.
 * @author <a href="mailto:ropalka@redhat.com">Richard Opalka</a>
 */
final class ServletContainerAdd extends AbstractBoottimeAddStepHandler {
    static final ServletContainerAdd INSTANCE = new ServletContainerAdd();

    ServletContainerAdd() {
        super(ServletContainerDefinition.ATTRIBUTES);
    }

    @Override
    protected void performBoottime(OperationContext context, ModelNode operation, Resource resource) throws OperationFailedException {
        installRuntimeServices(context, resource.getModel(), context.getCurrentAddressValue());
    }

    void installRuntimeServices(OperationContext context, ModelNode model, String name) throws OperationFailedException {
        final ModelNode fullModel = Resource.Tools.readModel(context.readResource(PathAddress.EMPTY_ADDRESS), 2);

        final SessionCookieConfig config = SessionCookieDefinition.INSTANCE.getConfig(context, fullModel.get(SessionCookieDefinition.INSTANCE.getPathElement().getKeyValuePair()));
        final CrawlerSessionManagerConfig crawlerSessionManagerConfig = CrawlerSessionManagementDefinition.INSTANCE.getConfig(context, fullModel.get(CrawlerSessionManagementDefinition.INSTANCE.getPathElement().getKeyValuePair()));
        final boolean persistentSessions = PersistentSessionsDefinition.isEnabled(context, fullModel.get(PersistentSessionsDefinition.INSTANCE.getPathElement().getKeyValuePair()));
        final boolean allowNonStandardWrappers = ServletContainerDefinition.ALLOW_NON_STANDARD_WRAPPERS.resolveModelAttribute(context, model).asBoolean();
        final boolean proactiveAuth = ServletContainerDefinition.PROACTIVE_AUTHENTICATION.resolveModelAttribute(context, model).asBoolean();
        final String bufferCache = ServletContainerDefinition.DEFAULT_BUFFER_CACHE.resolveModelAttribute(context, model).asString();
        final boolean disableFileWatchService = ServletContainerDefinition.DISABLE_FILE_WATCH_SERVICE.resolveModelAttribute(context, model).asBoolean();
        final boolean disableSessionIdReususe = ServletContainerDefinition.DISABLE_SESSION_ID_REUSE.resolveModelAttribute(context, model).asBoolean();

        JSPConfig jspConfig = JspDefinition.INSTANCE.getConfig(context, fullModel.get(JspDefinition.INSTANCE.getPathElement().getKeyValuePair()));

        final String stackTracesString = ServletContainerDefinition.STACK_TRACE_ON_ERROR.resolveModelAttribute(context, model).asString();
        final ModelNode defaultEncodingValue = ServletContainerDefinition.DEFAULT_ENCODING.resolveModelAttribute(context, model);
        final String defaultEncoding = defaultEncodingValue.isDefined()? defaultEncodingValue.asString() : null;
        final boolean useListenerEncoding = ServletContainerDefinition.USE_LISTENER_ENCODING.resolveModelAttribute(context, model).asBoolean();
        final boolean ignoreFlush = ServletContainerDefinition.IGNORE_FLUSH.resolveModelAttribute(context, model).asBoolean();
        final boolean eagerFilterInit = ServletContainerDefinition.EAGER_FILTER_INIT.resolveModelAttribute(context, model).asBoolean();
        final boolean disableCachingForSecuredPages = ServletContainerDefinition.DISABLE_CACHING_FOR_SECURED_PAGES.resolveModelAttribute(context, model).asBoolean();
        final int sessionIdLength = ServletContainerDefinition.SESSION_ID_LENGTH.resolveModelAttribute(context, model).asInt();
        final int fileCacheMetadataSize = ServletContainerDefinition.FILE_CACHE_METADATA_SIZE.resolveModelAttribute(context, model).asInt();
        final int fileCacheMaxFileSize = ServletContainerDefinition.FILE_CACHE_MAX_FILE_SIZE.resolveModelAttribute(context, model).asInt();
        final ModelNode fileCacheTtlNode = ServletContainerDefinition.FILE_CACHE_TIME_TO_LIVE.resolveModelAttribute(context, model);
        final Integer fileCacheTimeToLive = fileCacheTtlNode.isDefined()  ? fileCacheTtlNode.asInt() : null;
        final int defaultCookieVersion = ServletContainerDefinition.DEFAULT_COOKIE_VERSION.resolveModelAttribute(context, model).asInt();
        final boolean preservePathOnForward = ServletContainerDefinition.PRESERVE_PATH_ON_FORWARD.resolveModelAttribute(context, model).asBoolean();

        Boolean directoryListingEnabled = null;
        if(model.hasDefined(Constants.DIRECTORY_LISTING)) {
            directoryListingEnabled = ServletContainerDefinition.DIRECTORY_LISTING.resolveModelAttribute(context, model).asBoolean();
        }
        Integer maxSessions = null;
        if(model.hasDefined(Constants.MAX_SESSIONS)) {
            maxSessions = ServletContainerDefinition.MAX_SESSIONS.resolveModelAttribute(context, model).asInt();
        }

        final int sessionTimeout = ServletContainerDefinition.DEFAULT_SESSION_TIMEOUT.resolveModelAttribute(context, model).asInt();

        WebsocketsDefinition.WebSocketInfo webSocketInfo = WebsocketsDefinition.INSTANCE.getConfig(context, fullModel.get(WebsocketsDefinition.INSTANCE.getPathElement().getKeyValuePair()));

        final Map<String, String> mimeMappings = new HashMap<>();
        if (fullModel.hasDefined(Constants.MIME_MAPPING)) {
            for (final Property mapping : fullModel.get(Constants.MIME_MAPPING).asPropertyList()) {
                mimeMappings.put(mapping.getName(), MimeMappingDefinition.VALUE.resolveModelAttribute(context, mapping.getValue()).asString());
            }
        }

        List<String> welcomeFiles = new ArrayList<>();
        if (fullModel.hasDefined(Constants.WELCOME_FILE)) {
            for (final Property welcome : fullModel.get(Constants.WELCOME_FILE).asPropertyList()) {
                welcomeFiles.add(welcome.getName());
            }
        }

        final CapabilityServiceBuilder<?> sb = context.getCapabilityServiceTarget().addCapability(ServletContainerDefinition.SERVLET_CONTAINER_CAPABILITY);
        final Consumer<ServletContainerService> sConsumer = sb.provides(ServletContainerDefinition.SERVLET_CONTAINER_CAPABILITY, UndertowService.SERVLET_CONTAINER.append(name));
        final Supplier<SessionPersistenceManager> spmSupplier = persistentSessions ? sb.requires(AbstractPersistentSessionManager.SERVICE_NAME) : null;
        final Supplier<DirectBufferCache> bcSupplier = bufferCache != null ? sb.requires(BufferCacheService.SERVICE_NAME.append(bufferCache)) : null;
        final Supplier<ByteBufferPool> bbpSupplier = webSocketInfo != null ? sb.requiresCapability(Capabilities.CAPABILITY_BYTE_BUFFER_POOL, ByteBufferPool.class, webSocketInfo.getBufferPool()) : null;
        final Supplier<XnioWorker> xwSupplier = webSocketInfo != null ? sb.requiresCapability(Capabilities.REF_IO_WORKER, XnioWorker.class, webSocketInfo.getWorker()) : null;
        final ServletContainerService container = new ServletContainerService(sConsumer, spmSupplier, bcSupplier, bbpSupplier, xwSupplier,
                allowNonStandardWrappers, ServletStackTraces.valueOf(stackTracesString.toUpperCase().replace('-', '_')), config,
                jspConfig, defaultEncoding, useListenerEncoding, ignoreFlush, eagerFilterInit, sessionTimeout,
                disableCachingForSecuredPages, webSocketInfo != null, webSocketInfo != null && webSocketInfo.isDispatchToWorker(),
                webSocketInfo != null && webSocketInfo.isPerMessageDeflate(), webSocketInfo == null ? -1 : webSocketInfo.getDeflaterLevel(), mimeMappings,
                welcomeFiles, directoryListingEnabled, proactiveAuth, sessionIdLength, maxSessions, crawlerSessionManagerConfig, disableFileWatchService, disableSessionIdReususe, fileCacheMetadataSize, fileCacheMaxFileSize, fileCacheTimeToLive, defaultCookieVersion, preservePathOnForward);
        sb.setInstance(container);
        sb.setInitialMode(ServiceController.Mode.ON_DEMAND);
        sb.install();
    }
}
