package com.dianping.cat.report.service;

import com.dianping.cat.config.server.ServerConfigManager;
import com.dianping.cat.configuration.NetworkInterfaceManager;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.helper.Splitters;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author:limaozhan
 * @Date:2021/1/26
 */
@Named
public class CompositeRemoteRefreshService implements Initializable {

    @Inject
    protected ServerConfigManager m_configManager;

    private List<RemoteRefreshService> m_refreshServices = new ArrayList<>();

    public void refreshProjectConfig(final String domain) {
        for (final RemoteRefreshService remoteRefreshService : m_refreshServices) {
            m_configManager.getModelServiceExecutorService().execute(new Runnable() {
                @Override
                public void run() {
                    remoteRefreshService.refreshProjectConfig(domain);
                }
            });

        }
    }

    public void refreshExceptionConfig(final String domain, final String exceptionName, final String exceptionLimit) {
        for (final RemoteRefreshService remoteRefreshService : m_refreshServices) {
            m_configManager.getModelServiceExecutorService().execute(new Runnable() {
                @Override
                public void run() {
                    remoteRefreshService.refreshExceptionConfig(domain,exceptionName,exceptionLimit);
                }
            });

        }
    }

    public void refreshExceptionExcludeConfig(final String domain, final String exceptionName, final String methodType) {
        for (final RemoteRefreshService remoteRefreshService : m_refreshServices) {
            m_configManager.getModelServiceExecutorService().execute(new Runnable() {
                @Override
                public void run() {
                    remoteRefreshService.refreshExceptionExcludeConfig(domain,exceptionName,methodType);
                }
            });

        }
    }

    @Override
    public void initialize() throws InitializationException {
        String remoteServers = m_configManager.getConsoleRemoteServers();
        List<String> endpoints = Splitters.by(',').noEmptyItem().trim().split(remoteServers);

        for (String endpoint : endpoints) {
            int pos = endpoint.indexOf(':');
            String host = buildHost(endpoint, pos);
            int port = (pos > 0 ? Integer.parseInt(endpoint.substring(pos + 1)) : 2281);
            RemoteRefreshService remote = createRefreshService();
            remote.setM_host(host);
            remote.setM_port(port);
            m_refreshServices.add(remote);
        }
    }

    private RemoteRefreshService createRefreshService() {
        return new RemoteRefreshService();
    }

    private String buildHost(String endpoint, int pos) {
        String host = (pos > 0 ? endpoint.substring(0, pos) : endpoint);

        if ("127.0.0.1".equals(host)) {
            host = NetworkInterfaceManager.INSTANCE.getLocalHostAddress();
        }
        return host;
    }
}
