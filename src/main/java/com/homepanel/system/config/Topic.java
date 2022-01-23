package com.homepanel.system.config;

import com.homepanel.core.config.InterfaceTopic;
import com.homepanel.core.config.InterfaceTopicPolling;
import com.homepanel.core.config.InterfaceTopicValue;
import com.homepanel.core.config.TypeAdapter;
import com.homepanel.core.state.Type;
import com.homepanel.system.service.SystemConstants;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlValue;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class Topic implements InterfaceTopic, InterfaceTopicValue, InterfaceTopicPolling {

    private String path;
    private Type type;
    private SystemConstants.GROUP group;
    private SystemConstants.CHANNEL channel;
    private Integer index;
    private Integer processId;
    private Object lastValue;
    private LocalDateTime lastDateTime;
    private Integer refreshIntervalValue;
    private TimeUnit refreshIntervalUnit;

    @XmlValue
    @Override
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @XmlAttribute
    @XmlJavaTypeAdapter(TypeAdapter.class)
    @Override
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @XmlAttribute
    public SystemConstants.GROUP getGroup() {
        return group;
    }

    private void setGroup(SystemConstants.GROUP group) {
        this.group = group;
    }

    @XmlAttribute
    public SystemConstants.CHANNEL getChannel() {
        return channel;
    }

    private void setChannel(SystemConstants.CHANNEL channel) {
        this.channel = channel;
    }

    @XmlAttribute
    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    @XmlAttribute
    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }

    @XmlTransient
    @Override
    public Object getLastValue() {
        return lastValue;
    }

    public void setLastValue(Object lastValue) {
        this.lastValue = lastValue;
    }

    @XmlTransient
    @Override
    public LocalDateTime getLastDateTime() {
        return lastDateTime;
    }

    @Override
    public void setLastDateTime(LocalDateTime lastDateTime) {
        this.lastDateTime = lastDateTime;
    }

    @XmlAttribute
    @Override
    public Integer getRefreshIntervalValue() {
        return refreshIntervalValue;
    }

    @Override
    public void setRefreshIntervalValue(Integer refreshIntervalValue) {
        this.refreshIntervalValue = refreshIntervalValue;
    }

    @XmlAttribute
    @Override
    public TimeUnit getRefreshIntervalUnit() {
        return refreshIntervalUnit;
    }

    @Override
    public void setRefreshIntervalUnit(TimeUnit refreshIntervalUnit) {
        this.refreshIntervalUnit = refreshIntervalUnit;
    }
}