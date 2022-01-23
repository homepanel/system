package com.homepanel.system.service;

import com.homepanel.core.executor.PriorityThreadPoolExecutor;
import com.homepanel.core.service.PollingService;
import com.homepanel.core.state.Type;
import com.homepanel.system.config.Config;
import com.homepanel.system.config.Topic;
import com.homepanel.system.system.client.DeviceNotFoundException;
import com.homepanel.system.system.client.SystemInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Service extends PollingService<Config, Topic> {

    private final static Logger LOGGER = LoggerFactory.getLogger(Service.class);

    private SystemInfo systemInfo;

    private SystemInfo getSystemInfo() {
        return systemInfo;
    }

    private void setSystemInfo(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    @Override
    public Config getConfig() {
        return (Config) super.getConfig();
    }

    @Override
    protected void startService() throws Exception {

        setSystemInfo(new SystemInfo());

        for (Topic topic : getConfig().getTopics()) {

            switch (topic.getGroup()) {

                case MEMORY:
                    switch (topic.getChannel()) {
                        case AVAILABLE:
                        case TOTAL:
                        case MEMORY_USED:
                            topic.setType(Config.getType(Type.NAME.LONG.name()));
                            break;
                        case AVAILABLE_PERCENT:
                        case USED_PERCENT:
                            topic.setType(Config.getType(Type.NAME.DOUBLE.name()));
                            break;
                    }
                case SWAP:
                    switch (topic.getChannel()) {
                        case AVAILABLE:
                        case TOTAL:
                        case MEMORY_USED:
                            topic.setType(Config.getType(Type.NAME.LONG.name()));
                            break;
                        case AVAILABLE_PERCENT:
                        case USED_PERCENT:
                            topic.setType(Config.getType(Type.NAME.DOUBLE.name()));
                            break;
                    }
                    break;
                case STORAGE:
                    if (topic.getIndex() == null) {
                        topic.setIndex(0);
                    }
                    switch (topic.getChannel()) {
                        case AVAILABLE:
                        case TOTAL:
                        case MEMORY_USED:
                            topic.setType(Config.getType(Type.NAME.LONG.name()));
                            break;
                        case AVAILABLE_PERCENT:
                        case USED_PERCENT:
                            topic.setType(Config.getType(Type.NAME.DOUBLE.name()));
                            break;
                        case NAME:
                        case DESCRIPTION:
                        case TYPE:
                            topic.setType(Config.getType(Type.NAME.STRING.name()));
                            break;
                    }
                    break;
                case DRIVE:
                    if (topic.getIndex() == null) {
                        topic.setIndex(0);
                    }
                    switch (topic.getChannel()) {
                        case NAME:
                        case MODEL:
                        case SERIAL_NUMBER:
                            topic.setType(Config.getType(Type.NAME.STRING.name()));
                            break;
                    }
                    break;
                case DISPLAY:
                    if (topic.getIndex() == null) {
                        topic.setIndex(0);
                    }
                    switch (topic.getChannel()) {
                        case INFORMATION:
                            topic.setType(Config.getType(Type.NAME.STRING.name()));
                            break;
                    }
                    break;
                case BATTERY:
                    if (topic.getIndex() == null) {
                        topic.setIndex(0);
                    }
                    switch (topic.getChannel()) {
                        case NAME:
                            topic.setType(Config.getType(Type.NAME.STRING.name()));
                            break;
                        case REMAINING_CAPACITY:
                        case REMAINING_TIME:
                            topic.setType(Config.getType(Type.NAME.DOUBLE.name()));
                            break;
                    }
                    break;
                case CPU:
                    switch (topic.getChannel()) {
                        case NAME:
                        case DESCRIPTION:
                            topic.setType(Config.getType(Type.NAME.STRING.name()));
                            break;
                        case LOAD:
                            topic.setType(Config.getType(Type.NAME.DOUBLE.name()));
                            break;
                        case LOAD1:
                        case LOAD5:
                        case LOAD15:
                        case UPTIME:
                            topic.setType(Config.getType(Type.NAME.DOUBLE.name()));
                            break;
                        case THREADS:
                            topic.setType(Config.getType(Type.NAME.INTEGER.name()));
                            break;
                    }
                    break;
                case SENSORS:
                    switch (topic.getChannel()) {
                        case CPU_TEMPERATURE:
                        case CPU_VOLTAGE:
                            topic.setType(Config.getType(Type.NAME.DOUBLE.name()));
                            break;
                        case FAN_SPEED:
                            if (topic.getIndex() != null) {
                                topic.setType(Config.getType(Type.NAME.INTEGER.name()));
                            } else {
                                LOGGER.error("attribute \"index\" is required for topic with group \"{}\" and channel \"{}\"", topic.getGroup(), topic.getChannel());
                            }
                            break;
                    }
                    break;
                case NETWORK:
                    if (topic.getIndex() == null) {
                        topic.setIndex(0);
                    }
                    switch (topic.getChannel()) {
                        case IP_ADDRESS:
                        case MAC_ADDRESS:
                        case NETWORK_DISPLAY_NAME:
                        case NETWORK_NAME:
                            topic.setType(Config.getType(Type.NAME.STRING.name()));
                            break;
                        case PACKETS_SENT:
                        case PACKETS_RECEIVED:
                        case DATA_SENT:
                        case DATA_RECEIVED:
                            topic.setType(Config.getType(Type.NAME.LONG.name()));
                            break;
                    }
                    break;
                case PROCESS:
                    switch (topic.getChannel()) {
                        case NAME:
                        case PATH:
                            topic.setType(Config.getType(Type.NAME.STRING.name()));
                            break;
                        case LOAD:
                            topic.setType(Config.getType(Type.NAME.DOUBLE.name()));
                            break;
                        case MEMORY_USED:
                            topic.setType(Config.getType(Type.NAME.LONG.name()));
                            break;
                        case THREADS:
                            topic.setType(Config.getType(Type.NAME.INTEGER.name()));
                            break;
                    }
                    break;
            }

            if (topic.getType() == null) {
                LOGGER.error("topic with group \"{}\" and channel \"{}\" not found", topic.getGroup(), topic.getChannel());
            }
        }
    }

    @Override
    protected void shutdownService() throws Exception {
    }

    private void readData(Topic topic, Long jobRunningTimeInMilliseconds, Long refreshIntervalInMilliseconds) {

        Object value = null;

        try {
            if (topic.getType() != null) {
                switch (topic.getGroup()) {
                    case MEMORY:
                        switch (topic.getChannel()) {
                            case AVAILABLE:
                                value = getSystemInfo().getMemoryAvailable();
                                break;
                            case TOTAL:
                                value = getSystemInfo().getMemoryTotal();
                                break;
                            case MEMORY_USED:
                                value = getSystemInfo().getMemoryUsed();
                                break;
                            case AVAILABLE_PERCENT:
                                value = getSystemInfo().getMemoryAvailablePercent();
                                break;
                            case USED_PERCENT:
                                value = getSystemInfo().getMemoryUsedPercent();
                                break;
                        }
                    case SWAP:
                        switch (topic.getChannel()) {
                            case AVAILABLE:
                                value = getSystemInfo().getSwapAvailable();
                                break;
                            case TOTAL:
                                value = getSystemInfo().getSwapTotal();
                                break;
                            case MEMORY_USED:
                                value = getSystemInfo().getSwapUsed();
                                break;
                            case AVAILABLE_PERCENT:
                                value = getSystemInfo().getSwapAvailablePercent();
                                break;
                            case USED_PERCENT:
                                value = getSystemInfo().getSwapUsedPercent();
                                break;
                        }
                        break;
                    case STORAGE:
                        if (topic.getIndex() != null) {
                            switch (topic.getChannel()) {
                                case AVAILABLE:
                                    value = getSystemInfo().getStorageAvailable(topic.getIndex(), jobRunningTimeInMilliseconds, refreshIntervalInMilliseconds);
                                    break;
                                case TOTAL:
                                    value = getSystemInfo().getStorageTotal(topic.getIndex(), jobRunningTimeInMilliseconds, refreshIntervalInMilliseconds);
                                    break;
                                case MEMORY_USED:
                                    value = getSystemInfo().getStorageUsed(topic.getIndex(), jobRunningTimeInMilliseconds, refreshIntervalInMilliseconds);
                                    break;
                                case AVAILABLE_PERCENT:
                                    value = getSystemInfo().getStorageAvailablePercent(topic.getIndex(), jobRunningTimeInMilliseconds, refreshIntervalInMilliseconds);
                                    break;
                                case USED_PERCENT:
                                    value = getSystemInfo().getStorageUsedPercent(topic.getIndex(), jobRunningTimeInMilliseconds, refreshIntervalInMilliseconds);
                                    break;
                                case NAME:
                                    value = getSystemInfo().getStorageName(topic.getIndex(), jobRunningTimeInMilliseconds, refreshIntervalInMilliseconds);
                                    break;
                                case DESCRIPTION:
                                    value = getSystemInfo().getStorageDescription(topic.getIndex(), jobRunningTimeInMilliseconds, refreshIntervalInMilliseconds);
                                    break;
                                case TYPE:
                                    value = getSystemInfo().getStorageType(topic.getIndex(), jobRunningTimeInMilliseconds, refreshIntervalInMilliseconds);
                                    break;
                            }
                        }
                        break;
                    case DRIVE:
                        if (topic.getIndex() != null) {
                            switch (topic.getChannel()) {
                                case NAME:
                                    value = getSystemInfo().getDriveName(topic.getIndex(), jobRunningTimeInMilliseconds, refreshIntervalInMilliseconds);
                                    break;
                                case MODEL:
                                    value = getSystemInfo().getDriveModel(topic.getIndex(), jobRunningTimeInMilliseconds, refreshIntervalInMilliseconds);
                                    break;
                                case SERIAL_NUMBER:
                                    value = getSystemInfo().getDriveSerialNumber(topic.getIndex(), jobRunningTimeInMilliseconds, refreshIntervalInMilliseconds);
                                    break;
                            }
                        }
                        break;
                    case DISPLAY:
                        if (topic.getIndex() != null) {
                            switch (topic.getChannel()) {
                                case INFORMATION:
                                    value = getSystemInfo().getDisplayInformation(topic.getIndex(), jobRunningTimeInMilliseconds, refreshIntervalInMilliseconds);
                                    break;
                            }
                        }
                        break;
                    case BATTERY:
                        if (topic.getIndex() != null) {
                            switch (topic.getChannel()) {
                                case NAME:
                                    value = getSystemInfo().getBatteryName(topic.getIndex(), jobRunningTimeInMilliseconds, refreshIntervalInMilliseconds);
                                    break;
                                case REMAINING_CAPACITY:
                                    value = getSystemInfo().getBatteryRemainingCapacity(topic.getIndex(), jobRunningTimeInMilliseconds, refreshIntervalInMilliseconds);
                                    break;
                                case REMAINING_TIME:
                                    value = getSystemInfo().getBatteryRemainingTime(topic.getIndex(), jobRunningTimeInMilliseconds, refreshIntervalInMilliseconds);
                                    break;
                            }
                        }
                        break;
                    case CPU:
                        switch (topic.getChannel()) {
                            case NAME:
                                value = getSystemInfo().getCpuName();
                                break;
                            case DESCRIPTION:
                                value = getSystemInfo().getCpuDescription();
                                break;
                            case LOAD:
                                value = getSystemInfo().getCpuLoad();
                                break;
                            case LOAD1:
                                value = getSystemInfo().getCpuLoad1();
                                break;
                            case LOAD5:
                                value = getSystemInfo().getCpuLoad5();
                                break;
                            case LOAD15:
                                value = getSystemInfo().getCpuLoad15();
                                break;
                            case UPTIME:
                                value = getSystemInfo().getCpuUptime();
                                break;
                            case THREADS:
                                value = getSystemInfo().getCpuThreads();
                                break;
                        }
                        break;
                    case SENSORS:
                        switch (topic.getChannel()) {
                            case CPU_TEMPERATURE:
                                value = getSystemInfo().getSensorsCpuTemperature();
                                break;
                            case CPU_VOLTAGE:
                                value = getSystemInfo().getSensorsCpuVoltage();
                                break;
                            case FAN_SPEED:
                                if (topic.getIndex() != null) {
                                    value = getSystemInfo().getSensorsFanSpeed(topic.getIndex());
                                }
                                break;
                        }
                        break;
                    case NETWORK:
                        if (topic.getIndex() != null) {
                            switch (topic.getChannel()) {
                                case IP_ADDRESS:
                                    value = getSystemInfo().getNetworkIpAddress(topic.getIndex(), jobRunningTimeInMilliseconds, refreshIntervalInMilliseconds);
                                    break;
                                case MAC_ADDRESS:
                                    value = getSystemInfo().getNetworkMacAddress(topic.getIndex(), jobRunningTimeInMilliseconds, refreshIntervalInMilliseconds);
                                    break;
                                case NETWORK_DISPLAY_NAME:
                                    value = getSystemInfo().getNetworkDisplayName(topic.getIndex(), jobRunningTimeInMilliseconds, refreshIntervalInMilliseconds);
                                    break;
                                case NETWORK_NAME:
                                    value = getSystemInfo().getNetworkName(topic.getIndex(), jobRunningTimeInMilliseconds, refreshIntervalInMilliseconds);
                                    break;
                                case PACKETS_SENT:
                                    value = getSystemInfo().getNetworkPacketsSent(topic.getIndex(), jobRunningTimeInMilliseconds, refreshIntervalInMilliseconds);
                                    break;
                                case PACKETS_RECEIVED:
                                    value = getSystemInfo().getNetworkPacketsReceived(topic.getIndex(), jobRunningTimeInMilliseconds, refreshIntervalInMilliseconds);
                                    break;
                                case DATA_SENT:
                                    value = getSystemInfo().getNetworkDataSent(topic.getIndex(), jobRunningTimeInMilliseconds, refreshIntervalInMilliseconds);
                                    break;
                                case DATA_RECEIVED:
                                    value = getSystemInfo().getNetworkDataReceived(topic.getIndex(), jobRunningTimeInMilliseconds, refreshIntervalInMilliseconds);
                                    break;
                            }
                        }
                        break;
                    case PROCESS:
                        Integer processId = topic.getProcessId();
                        switch (topic.getChannel()) {
                            case LOAD:
                                value = getSystemInfo().getProcessCpuUsage(processId);
                                break;
                            case MEMORY_USED:
                                value = getSystemInfo().getProcessMemoryUsage(processId);
                                break;
                            case NAME:
                                value = getSystemInfo().getProcessName(processId);
                                break;
                            case THREADS:
                                value = getSystemInfo().getProcessThreads(processId);
                                break;
                            case PATH:
                                value = getSystemInfo().getProcessPath(processId);
                                break;
                        }
                        break;
                }

                publishData(topic, value);
            }
        } catch (DeviceNotFoundException e) {
            LOGGER.error("device not found for topic with group \"{}\", channel \"{}\" and path \"{}\"", topic.getGroup(), topic.getChannel(), topic.getPath());
        }
    }

    @Override
    protected void onInit() {

        for (Topic topic : getConfig().getTopics()) {
            topic.setLastValue(null);
            topic.setLastDateTime(null);
        }
        
        long jobRunningTimeInMilliseconds = ZonedDateTime.now().toInstant().toEpochMilli();
        long refreshIntervalInMilliseconds = TimeUnit.HOURS.toMillis(1);

        for (Topic topic : getConfig().getTopics()) {
            readData(topic, jobRunningTimeInMilliseconds, refreshIntervalInMilliseconds);
        }
    }

    @Override
    protected List<String> getMqttTopics() {
        return new ArrayList<>();
    }

    @Override
    protected Integer getPollingExecutorServicePoolSize() {
        return 3;
    }

    @Override
    public String getTopicNameByTopic(Topic topic) {
        return getTopicNameByParameter(topic.getGroup(), topic.getChannel());
    }

    @Override
    public void pollData(Topic topic, Long jobRunningTimeInMilliseconds, Long refreshIntervalInMilliseconds) {
        try {
            readData(topic, jobRunningTimeInMilliseconds, refreshIntervalInMilliseconds);
        } catch (Exception e) {
            LOGGER.error("error reading data from ow server", e);
        }
    }

    @Override
    protected void onData(Topic topic, Object value, PriorityThreadPoolExecutor.PRIORITY priority) {
        // unused
    }

    @Override
    protected void updateData(Topic topic) {
        // unused
    }

    public static void main(String[] arguments) throws Exception {
        new Service().start(arguments, Config.class);
    }
}