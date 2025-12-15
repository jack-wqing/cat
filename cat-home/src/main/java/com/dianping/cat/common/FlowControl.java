package com.dianping.cat.common;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.nacos.api.PropertyKeyConst;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.lookup.annotation.Named;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

@Named
public class FlowControl implements Initializable, LogEnabled {

    private boolean initialized = false;

    private Logger m_logger = null;

    @Override
    public void initialize() throws InitializationException {
        Properties props = new Properties();
        String filePath = "/META-INF/sentinel/" + System.getProperty("sentinel.env") + "/sentinel.properties";
        InputStream inputStream = this.getClass().getClassLoader()
            .getResourceAsStream(filePath);
        if (inputStream != null) {
            try {
                props.load(inputStream);
                Properties nacosProps = new Properties();
                System.setProperty("csp.sentinel.dashboard.server", props.getProperty("sentinel.server"));
                System.setProperty("project.name", props.getProperty("project.name"));
                nacosProps.put(PropertyKeyConst.SERVER_ADDR, props.getProperty("sentinel.nacos.remoteAddress"));
                nacosProps.put(PropertyKeyConst.NAMESPACE, props.getProperty("sentinel.nacos.namespace"));
                ReadableDataSource<String, List<FlowRule>> flowRuleDataSource = new NacosDataSource<>(
                    nacosProps, props.getProperty("sentinel.nacos.groupId"), props.getProperty("sentinel.nacos.dataId"),
                    source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {}));
                FlowRuleManager.register2Property(flowRuleDataSource.getProperty());
                initialized = true;
            } catch (Exception e) {
                m_logger.error("打开文件 " + filePath + " 失败", e);
            }
        } else {
            m_logger.error("打开文件流失败：" + filePath);
        }
    }

    @Override
    public void enableLogging(Logger logger) {
        m_logger = logger;
    }

    public boolean canPass(String resourceName) {
        if (!initialized) {
            return true;
        }
        Entry entry = null;
        try {
            entry = SphU.entry(resourceName);
            return true;
        } catch (BlockException e1) {
			m_logger.error("触发限流：" + resourceName);
            return false;
        } catch (Exception e2) {
            m_logger.error("sentinel失败！", e2);
            return true;
        } finally {
            if (entry != null) {
                entry.exit();
            }
        }
    }

}