package com.homepanel.system.config;

import com.homepanel.core.type.DefaultDouble;
import com.homepanel.core.type.DefaultString;

import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "config")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Config extends com.homepanel.core.config.ConfigTopic<Topic> {

    static {
        addTypes(new DefaultDouble(), new DefaultString());
    }
    private List<Topic> topics;

    @XmlElementWrapper(name = "topics")
    @XmlElement(name = "topic")
    public List<Topic> getTopics() {
        return topics;
    }

    @Override
    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }
}