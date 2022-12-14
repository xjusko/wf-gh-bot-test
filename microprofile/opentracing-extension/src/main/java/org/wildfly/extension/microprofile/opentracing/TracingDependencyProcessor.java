/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2018, Red Hat, Inc., and individual contributors
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
package org.wildfly.extension.microprofile.opentracing;

import static org.wildfly.extension.microprofile.opentracing.Constants.INTERCEPTOR_PACKAGE;
import static org.wildfly.extension.microprofile.opentracing.SubsystemDefinition.*;
import static org.wildfly.microprofile.opentracing.smallrye.TracerConfigurationConstants.SMALLRYE_OPENTRACING_TRACER_CONFIGURATION;

import org.jboss.as.controller.capability.CapabilityServiceSupport;
import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.server.deployment.module.ModuleDependency;
import org.jboss.as.server.deployment.module.ModuleSpecification;
import org.jboss.as.web.common.WarMetaData;
import org.jboss.metadata.javaee.spec.ParamValueMetaData;
import org.jboss.metadata.web.jboss.JBossWebMetaData;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleLoader;
import org.jboss.msc.service.ServiceName;
import org.wildfly.microprofile.opentracing.smallrye.WildFlyTracerFactory;

public class TracingDependencyProcessor implements DeploymentUnitProcessor {

    @Override
    public void deploy(DeploymentPhaseContext phaseContext) {
        DeploymentUnit deploymentUnit = phaseContext.getDeploymentUnit();
        addDependencies(deploymentUnit);
        final CapabilityServiceSupport support = deploymentUnit.getAttachment(Attachments.CAPABILITY_SERVICE_SUPPORT);
        if(support.hasCapability(DEFAULT_TRACER_CAPABILITY_NAME)) {
            phaseContext.getServiceTarget().addDependency(DEFAULT_TRACER_CAPABILITY.getCapabilityServiceName());
        }
        ServiceName tracerConfiguration = getTracerConfigurationServiceDependency(phaseContext);
        if(tracerConfiguration != null) {
            phaseContext.getServiceTarget().addDependency(tracerConfiguration);
        }
    }

    private JBossWebMetaData getJBossWebMetaData(DeploymentUnit deploymentUnit) {
        WarMetaData warMetaData = deploymentUnit.getAttachment(WarMetaData.ATTACHMENT_KEY);
        if (null == warMetaData) {
            // not a web deployment, nothing to do here...
            return null;
        }
        return warMetaData.getMergedJBossWebMetaData();
    }

    private ServiceName getTracerConfigurationServiceDependency(DeploymentPhaseContext deploymentPhaseContext) {
        DeploymentUnit deploymentUnit = deploymentPhaseContext.getDeploymentUnit();
        JBossWebMetaData jbossWebMetaData = getJBossWebMetaData(deploymentUnit);
        if (null == jbossWebMetaData || null == jbossWebMetaData.getContextParams()) {
            return null;
        }
        for (ParamValueMetaData param : jbossWebMetaData.getContextParams()) {
            if (SMALLRYE_OPENTRACING_TRACER_CONFIGURATION.equals(param.getParamName())) {
                String value = param.getParamValue();
                if (value != null && !value.isEmpty()) {
                    return TRACER_CAPABILITY.getCapabilityServiceName(value);
                }
            }
        }
        return null;
    }
    private void addDependencies(DeploymentUnit deploymentUnit) {
        ModuleSpecification moduleSpecification = deploymentUnit.getAttachment(Attachments.MODULE_SPECIFICATION);
        ModuleLoader moduleLoader = Module.getBootModuleLoader();
        for (String module : MODULES) {
            if (!module.contains(INTERCEPTOR_PACKAGE)) {
                moduleSpecification.addSystemDependency(new ModuleDependency(moduleLoader, module, false, false, true, false));
            }
        }
        for (String module : EXPORTED_MODULES) {
            ModuleDependency modDep = new ModuleDependency(moduleLoader, module, false, true, true, false);
            // io.opentracing.contrib.opentracing-interceptors needs to be processed by ExternalBeanArchiveProcessor
            if (module.contains(INTERCEPTOR_PACKAGE)) {
                modDep.addImportFilter(s -> s.equals("META-INF"), true);
            }
            moduleSpecification.addSystemDependency(modDep);
        }
        for (String module : WildFlyTracerFactory.getModules()) {
            moduleSpecification.addSystemDependency(new ModuleDependency(moduleLoader, module, true, false, true, false));
        }
    }
}
