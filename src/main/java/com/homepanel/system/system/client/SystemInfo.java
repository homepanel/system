/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package com.homepanel.system.system.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.hardware.*;
import oshi.software.os.OSFileStore;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import oshi.util.EdidUtil;

import java.util.HashMap;
import java.util.Map;


public class SystemInfo {

    private final static Logger LOGGER = LoggerFactory.getLogger(SystemInfo.class);

    private oshi.SystemInfo systemInfo;
    private long[] oldSystemCpuLoadTicks;
    private OSFileStore[] osFileSores;
    private Map<Integer, NetworkIF> networks;
    private Display[] displays;
    private PowerSource[] powerSources;
    private HWDiskStore[] hardwareDiskStores;
    private Long osFileSoresCacheExpireTimeInMilliseconds;
    private Long networksCacheExpireTimeInMilliseconds;
    private Long displaysCacheExpireTimeInMilliseconds;
    private Long powerSourcesCacheExpireTimeInMilliseconds;
    private Long hardwareDiskStoresCacheExpireTimeInMilliseconds;

    private oshi.SystemInfo getSystemInfo() {
        return systemInfo;
    }

    private void setSystemInfo(oshi.SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    private long[] getOldSystemCpuLoadTicks() {
        return oldSystemCpuLoadTicks;
    }

    private void setOldSystemCpuLoadTicks(long[] oldSystemCpuLoadTicks) {
        this.oldSystemCpuLoadTicks = oldSystemCpuLoadTicks;
    }

    private OSFileStore[] getOsFileSores() {
        return osFileSores;
    }

    private void setOsFileSores(OSFileStore[] osFileSores) {
        this.osFileSores = osFileSores;
    }

    private Map<Integer, NetworkIF> getNetworks() {
        return networks;
    }

    private void setNetworks(Map<Integer, NetworkIF> networks) {
        this.networks = networks;
    }

    private Display[] getDisplays() {
        return displays;
    }

    private void setDisplays(Display[] displays) {
        this.displays = displays;
    }

    private PowerSource[] getPowerSources() {
        return powerSources;
    }

    private void setPowerSources(PowerSource[] powerSources) {
        this.powerSources = powerSources;
    }

    private HWDiskStore[] getHardwareDiskStores() {
        return hardwareDiskStores;
    }

    private void setHardwareDiskStores(HWDiskStore[] hardwareDiskStores) {
        this.hardwareDiskStores = hardwareDiskStores;
    }

    private Long getOsFileSoresCacheExpireTimeInMilliseconds() {
        return osFileSoresCacheExpireTimeInMilliseconds;
    }

    private void setOsFileSoresCacheExpireTimeInMilliseconds(Long osFileSoresCacheExpireTimeInMilliseconds) {
        this.osFileSoresCacheExpireTimeInMilliseconds = osFileSoresCacheExpireTimeInMilliseconds;
    }

    private Long getNetworksCacheExpireTimeInMilliseconds() {
        return networksCacheExpireTimeInMilliseconds;
    }

    private void setNetworksCacheExpireTimeInMilliseconds(Long networksCacheExpireTimeInMilliseconds) {
        this.networksCacheExpireTimeInMilliseconds = networksCacheExpireTimeInMilliseconds;
    }

    private Long getDisplaysCacheExpireTimeInMilliseconds() {
        return displaysCacheExpireTimeInMilliseconds;
    }

    private void setDisplaysCacheExpireTimeInMilliseconds(Long displaysCacheExpireTimeInMilliseconds) {
        this.displaysCacheExpireTimeInMilliseconds = displaysCacheExpireTimeInMilliseconds;
    }

    private Long getPowerSourcesCacheExpireTimeInMilliseconds() {
        return powerSourcesCacheExpireTimeInMilliseconds;
    }

    private void setPowerSourcesCacheExpireTimeInMilliseconds(Long powerSourcesCacheExpireTimeInMilliseconds) {
        this.powerSourcesCacheExpireTimeInMilliseconds = powerSourcesCacheExpireTimeInMilliseconds;
    }

    public Long getHardwareDiskStoresCacheExpireTimeInMilliseconds() {
        return hardwareDiskStoresCacheExpireTimeInMilliseconds;
    }

    private void setHardwareDiskStoresCacheExpireTimeInMilliseconds(Long hardwareDiskStoresCacheExpireTimeInMilliseconds) {
        this.hardwareDiskStoresCacheExpireTimeInMilliseconds = hardwareDiskStoresCacheExpireTimeInMilliseconds;
    }

    private HardwareAbstractionLayer getHardware() {
        return getSystemInfo().getHardware();
    }

    private GlobalMemory getMemory() {
        return getHardware().getMemory();
    }

    private CentralProcessor getProcessor() {
        return getHardware().getProcessor();
    }

    private Sensors getSensors() {
        return getHardware().getSensors();
    }

    private ComputerSystem getComputerSystem() {
        return getHardware().getComputerSystem();
    }

    private OperatingSystem getOperatingSystem() {
        return getSystemInfo().getOperatingSystem();
    }

    public SystemInfo() {
        setSystemInfo(new oshi.SystemInfo());
        setOldSystemCpuLoadTicks(getProcessor().getSystemCpuLoadTicks());
    }

    private synchronized OSFileStore getOsFileStore(int index, long timeInMilliseconds, long refreshIntervalInMilliseconds) throws DeviceNotFoundException {

        if (getOsFileSores() == null || getOsFileSoresCacheExpireTimeInMilliseconds() == null || getOsFileSoresCacheExpireTimeInMilliseconds() < timeInMilliseconds - refreshIntervalInMilliseconds) {
            setOsFileSores(getOperatingSystem().getFileSystem().getFileStores());
            setOsFileSoresCacheExpireTimeInMilliseconds(timeInMilliseconds);
        }

        if (getOsFileSores() == null || getOsFileSores().length <= index) {
            throw new DeviceNotFoundException("filesystem with index: " + index + " can not be found");
        }

        return getOsFileSores()[index];
    }

    private synchronized NetworkIF getNetwork(int index, long timeInMilliseconds, long refreshIntervalInMilliseconds) throws DeviceNotFoundException {

        if (getNetworks() == null || getNetworksCacheExpireTimeInMilliseconds() == null || getNetworksCacheExpireTimeInMilliseconds() <= timeInMilliseconds - refreshIntervalInMilliseconds) {
            setNetworks(new HashMap<>());
            setNetworksCacheExpireTimeInMilliseconds(timeInMilliseconds);
        }

        if (!getNetworks().containsKey(index)) {
            NetworkIF[] networkIFS = getHardware().getNetworkIFs();
            if (networkIFS == null || networkIFS.length <= index) {
                throw new DeviceNotFoundException("network with index: " + index + " can not be found");
            }
            getNetworks().put(index, networkIFS[index]);
            getNetworks().get(index).updateAttributes();

        }

        return getNetworks().get(index);
    }

    private synchronized Display getDisplay(int index, long timeInMilliseconds, long refreshIntervalInMilliseconds) throws DeviceNotFoundException {

        if (getDisplays() == null || getDisplaysCacheExpireTimeInMilliseconds() == null || getDisplaysCacheExpireTimeInMilliseconds() <= timeInMilliseconds - refreshIntervalInMilliseconds) {
            setDisplays(getHardware().getDisplays());
            setDisplaysCacheExpireTimeInMilliseconds(timeInMilliseconds);
        }

        if (getDisplays() == null || getDisplays().length <= index) {
            throw new DeviceNotFoundException("display with index: " + index + " can not be found");
        }

        return getDisplays()[index];
    }

    private synchronized PowerSource getPowerSource(int index, long timeInMilliseconds, long refreshIntervalInMilliseconds) throws DeviceNotFoundException {

        if (getPowerSources() == null || getPowerSourcesCacheExpireTimeInMilliseconds() == null || getPowerSourcesCacheExpireTimeInMilliseconds() <= timeInMilliseconds - refreshIntervalInMilliseconds) {
            setPowerSources(getHardware().getPowerSources());
            setPowerSourcesCacheExpireTimeInMilliseconds(timeInMilliseconds);
        }

        if (getPowerSources() == null || getPowerSources().length <= index) {
            throw new DeviceNotFoundException("power source with index: " + index + " can not be found");
        }

        return getPowerSources()[index];
    }

    private synchronized HWDiskStore getHardwareDiskStore(int index, long timeInMilliseconds, long refreshIntervalInMilliseconds) throws DeviceNotFoundException {

        if (getHardwareDiskStores() == null || getHardwareDiskStoresCacheExpireTimeInMilliseconds() == null || getHardwareDiskStoresCacheExpireTimeInMilliseconds() <= timeInMilliseconds - refreshIntervalInMilliseconds) {
            setHardwareDiskStores(getHardware().getDiskStores());
            setPowerSourcesCacheExpireTimeInMilliseconds(timeInMilliseconds);
        }

        if (getHardwareDiskStores() == null || getHardwareDiskStores().length <= index) {
            throw new DeviceNotFoundException("hardware disk stores with index: " + index + " can not be found");
        }

        return getHardwareDiskStores()[index];
    }

    public Integer getProcessId() {
        return getOperatingSystem().getProcessId();
    }

    private OSProcess getProcess(int pid) throws DeviceNotFoundException {

        OSProcess process = getOperatingSystem().getProcess(pid);

        if (process == null) {
            throw new DeviceNotFoundException("error while getting information for process with PID " + pid);
        }

        return process;
    }

    private Double getAvarageCpuLoad(int timeInMinutes) {

        int index;

        switch (timeInMinutes) {
            case 1:
                index = 0;
                break;
            case 5:
                index = 1;
                break;
            case 15:
                index = 2;
                break;
            default:
                index = 2;
        }

        double processorLoads[] = getProcessor().getSystemLoadAverage(index + 1);

        return processorLoads[index];
    }

    private Long getSizeInMb(Long sizeInBytes) {
        return Math.round(sizeInBytes / (1024D * 1024));
    }

    private Double getPercentsValue(Double decimalFraction) {
        return decimalFraction * 100;
    }

    private Double getTimeInMinutes(Double timeInSeconds) {
        return timeInSeconds / 60;
    }

    public String getOsFamily() {
        return getOperatingSystem().getFamily();
    }

    public String getOsManufacturer() {
        return getOperatingSystem().getManufacturer();
    }

    public String getOsVersion() {
        return getOperatingSystem().getVersionInfo().getVersion();
    }

    public String getCpuName() {
        return getProcessor().getProcessorIdentifier().getName();
    }

    public String getCpuDescription() {
        return String.format("Model: %s %s, family: %s, vendor: %s, sn: %s, identifier: %s ", getProcessor().getProcessorIdentifier().getModel(), getProcessor().getProcessorIdentifier().isCpu64bit() ? "64 bit" : "32 bit", getProcessor().getProcessorIdentifier().getFamily(), getProcessor().getProcessorIdentifier().getVendor(), getComputerSystem().getSerialNumber(), getProcessor().getProcessorIdentifier().getIdentifier());
    }

    public Integer getCpuLogicalCores() {
        return getProcessor().getLogicalProcessorCount();
    }

    public Integer getCpuPhysicalCores() {
        return getProcessor().getPhysicalProcessorCount();
    }

    public Long getMemoryTotal() {
        return getSizeInMb(getMemory().getTotal());
    }

    public Long getMemoryAvailable() {
        return getSizeInMb(getHardware().getMemory().getAvailable());
    }

    public Long getMemoryUsed() {
        return getSizeInMb(getMemory().getTotal() - getMemory().getAvailable());
    }

    public Long getStorageTotal(int index, long timeInMilliseconds, long refreshIntervalInMilliseconds) throws DeviceNotFoundException {
        return getSizeInMb(getOsFileStore(index, timeInMilliseconds, refreshIntervalInMilliseconds).getTotalSpace());
    }

    public Long getStorageAvailable(int index, long timeInMilliseconds, long refreshIntervalInMilliseconds) throws DeviceNotFoundException {

        return getSizeInMb(getOsFileStore(index, timeInMilliseconds, refreshIntervalInMilliseconds).getUsableSpace());
    }

    public Long getStorageUsed(int index, long timeInMilliseconds, long refreshIntervalInMilliseconds) throws DeviceNotFoundException {

        OSFileStore fileStore = (OSFileStore) getOsFileStore(index, timeInMilliseconds, refreshIntervalInMilliseconds);
        return getSizeInMb(fileStore.getTotalSpace() - fileStore.getUsableSpace());
    }

    public Double getStorageAvailablePercent(int index, long timeInMilliseconds, long refreshIntervalInMilliseconds) throws DeviceNotFoundException {

        OSFileStore fileStore = (OSFileStore) getOsFileStore(index, timeInMilliseconds, refreshIntervalInMilliseconds);

        Long totalSpace = fileStore.getTotalSpace();
        Long freeSpace = fileStore.getUsableSpace();

        if (totalSpace > 0) {
            return getPercentsValue(freeSpace.doubleValue() / totalSpace.doubleValue());
        } else {
            return null;
        }
    }

    public Double getStorageUsedPercent(int index, long timeInMilliseconds, long refreshIntervalInMilliseconds) throws DeviceNotFoundException {

        OSFileStore fileStore = (OSFileStore) getOsFileStore(index, timeInMilliseconds, refreshIntervalInMilliseconds);

        Long totalSpace = fileStore.getTotalSpace();
        Long freeSpace = fileStore.getUsableSpace();
        Long usedSpace = totalSpace - freeSpace;

        if (totalSpace > 0) {
            return getPercentsValue(usedSpace.doubleValue() / totalSpace.doubleValue());
        } else {
            return null;
        }
    }

    public String getStorageName(int index, long timeInMilliseconds, long refreshIntervalInMilliseconds) throws DeviceNotFoundException {

        return getOsFileStore(index, timeInMilliseconds, refreshIntervalInMilliseconds).getName();
    }

    public String getStorageType(int index, long timeInMilliseconds, long refreshIntervalInMilliseconds) throws DeviceNotFoundException {

        return getOsFileStore(index, timeInMilliseconds, refreshIntervalInMilliseconds).getType();
    }

    public String getStorageDescription(int index, long timeInMilliseconds, long refreshIntervalInMilliseconds) throws DeviceNotFoundException {

        return getOsFileStore(index, timeInMilliseconds, refreshIntervalInMilliseconds).getDescription();
    }

    public String getNetworkIpAddress(int index, long timeInMilliseconds, long refreshIntervalInMilliseconds) throws DeviceNotFoundException {

        String[] ipAddresses = getNetwork(index, timeInMilliseconds, refreshIntervalInMilliseconds).getIPv4addr();

        if (ipAddresses != null && ipAddresses.length > 0) {
            return ipAddresses[0];
        }

        ipAddresses = getNetwork(index, timeInMilliseconds, refreshIntervalInMilliseconds).getIPv6addr();

        if (ipAddresses != null && ipAddresses.length > 0) {
            return ipAddresses[0];
        }

        return null;
    }

    public String getNetworkName(int index, long timeInMilliseconds, long refreshIntervalInMilliseconds) throws DeviceNotFoundException {

        return getNetwork(index, timeInMilliseconds, refreshIntervalInMilliseconds).getName();
    }

    public String getNetworkDisplayName(int index, long timeInMilliseconds, long refreshIntervalInMilliseconds) throws DeviceNotFoundException {

        return getNetwork(index, timeInMilliseconds, refreshIntervalInMilliseconds).getDisplayName();
    }

    public String getDisplayInformation(int index, long timeInMilliseconds, long refreshIntervalInMilliseconds) throws DeviceNotFoundException {

        byte[] edid = getDisplay(index, timeInMilliseconds, refreshIntervalInMilliseconds).getEdid();

        return String.format("product %s, manufacturer %s, SN: %s, width: %d, height: %d", EdidUtil.getProductID(edid), EdidUtil.getManufacturerID(edid), EdidUtil.getSerialNo(edid), EdidUtil.getHcm(edid), EdidUtil.getVcm(edid));
    }

    public Double getSensorsCpuTemperature() {

        double cpuTemperature = getSensors().getCpuTemperature();

        return cpuTemperature > 0 ? cpuTemperature : null;
    }

    public Double getSensorsCpuVoltage() {

        double cpuVoltage = getSensors().getCpuVoltage();

        return cpuVoltage > 0 ? cpuVoltage : null;
    }

    public Integer getSensorsFanSpeed(int index) throws DeviceNotFoundException {

        int[] fanSpeeds = getSensors().getFanSpeeds();

        if (fanSpeeds.length > index) {
            return fanSpeeds[index] > 0 ? fanSpeeds[index]  : null;
        }

        return null;
    }

    public Double getBatteryRemainingTime(int index, long timeInMilliseconds, long refreshIntervalInMilliseconds) throws DeviceNotFoundException {

        double remainingTime = getTimeInMinutes(getPowerSource(index, timeInMilliseconds, refreshIntervalInMilliseconds).getTimeRemainingEstimated());
        return remainingTime > 0 ? remainingTime : null;
    }

    public Double getBatteryRemainingCapacity(int index, long timeInMilliseconds, long refreshIntervalInMilliseconds) throws DeviceNotFoundException {

        return getPowerSource(index, timeInMilliseconds, refreshIntervalInMilliseconds).getRemainingCapacityPercent();
    }

    public String getBatteryName(int index, long timeInMilliseconds, long refreshIntervalInMilliseconds) throws DeviceNotFoundException {

        return getPowerSource(index, timeInMilliseconds, refreshIntervalInMilliseconds).getName();
    }

    public Double getMemoryAvailablePercent() {

        Long availableMemory = getMemory().getAvailable();
        Long totalMemory = getMemory().getTotal();

        if (totalMemory > 0) {
            return getPercentsValue(availableMemory.doubleValue() / totalMemory.doubleValue());
        } else {
            return null;
        }
    }

    public Double getMemoryUsedPercent() {

        Long totalMemory = getMemory().getTotal();
        Long usedMemory = totalMemory - getMemory().getAvailable();

        if (totalMemory > 0) {
            return getPercentsValue(usedMemory.doubleValue() / totalMemory.doubleValue());
        } else {
            return null;
        }
    }

    public String getDriveName(int index, long timeInMilliseconds, long refreshIntervalInMilliseconds) throws DeviceNotFoundException {

        return getHardwareDiskStore(index, timeInMilliseconds, refreshIntervalInMilliseconds).getName();
    }

    public String getDriveModel(int index, long timeInMilliseconds, long refreshIntervalInMilliseconds) throws DeviceNotFoundException {

        return getHardwareDiskStore(index, timeInMilliseconds, refreshIntervalInMilliseconds).getModel();
    }

    public String getDriveSerialNumber(int index, long timeInMilliseconds, long refreshIntervalInMilliseconds) throws DeviceNotFoundException {

        return getHardwareDiskStore(index, timeInMilliseconds, refreshIntervalInMilliseconds).getSerial();
    }

    public Long getSwapTotal() {

        long swapTotal = getSizeInMb(getMemory().getVirtualMemory().getSwapTotal());

        return swapTotal > 0 ? swapTotal : null;
    }

    public Long getSwapAvailable() {

        long swapAvaialble = getSizeInMb(getMemory().getVirtualMemory().getSwapTotal() - getMemory().getVirtualMemory().getSwapUsed());

        return swapAvaialble > 0 ? swapAvaialble : null;
    }

    public Long getSwapUsed() {

        long swapTotal = getSizeInMb(getMemory().getVirtualMemory().getSwapUsed());
        return swapTotal > 0 ? swapTotal : null;
    }

    public Double getSwapAvailablePercent() {

        Long usedSwap = getMemory().getVirtualMemory().getSwapUsed();
        Long totalSwap = getMemory().getVirtualMemory().getSwapTotal();
        Long freeSwap = totalSwap - usedSwap;

        if (totalSwap > 0) {
            return getPercentsValue(freeSwap.doubleValue() / totalSwap.doubleValue());
        } else {
            return null;
        }
    }

    public Double getSwapUsedPercent() {

        Long usedSwap = getMemory().getVirtualMemory().getSwapUsed();
        Long totalSwap = getMemory().getVirtualMemory().getSwapTotal();

        if (totalSwap > 0) {
            return getPercentsValue(usedSwap.doubleValue() / totalSwap.doubleValue());
        } else {
            return null;
        }
    }

    public Double getCpuLoad() {

        double cpuUsageRaw = getProcessor().getSystemCpuLoadBetweenTicks(getOldSystemCpuLoadTicks());

        setOldSystemCpuLoadTicks(getProcessor().getSystemCpuLoadTicks());

        return getPercentsValue(cpuUsageRaw);
    }

    public Double getCpuLoad1() {

        double avarageCpuLoad = getAvarageCpuLoad(1);

        return avarageCpuLoad >= 0 ? avarageCpuLoad : null;
    }

    public Double getCpuLoad5() {

        double avarageCpuLoad = getAvarageCpuLoad(5);

        return avarageCpuLoad >= 0 ? avarageCpuLoad : null;
    }

    public Double getCpuLoad15() {

        double avarageCpuLoad = getAvarageCpuLoad(15);

        return avarageCpuLoad >= 0 ? avarageCpuLoad : null;
    }

    public Double getCpuUptime() {

        Long seconds = getOperatingSystem().getSystemUptime();

        return getTimeInMinutes(seconds.doubleValue());
    }

    public Integer getCpuThreads() {

        return getOperatingSystem().getThreadCount();
    }

    public String getNetworkMacAddress(int index, long timeInMilliseconds, long refreshIntervalInMilliseconds) throws DeviceNotFoundException {

        return getNetwork(index, timeInMilliseconds, refreshIntervalInMilliseconds).getMacaddr();
    }

    public Long getNetworkPacketsReceived(int index, long timeInMilliseconds, long refreshIntervalInMilliseconds) throws DeviceNotFoundException {

        return getNetwork(index, timeInMilliseconds, refreshIntervalInMilliseconds).getPacketsRecv();
    }

    public Long getNetworkPacketsSent(int index, long timeInMilliseconds, long refreshIntervalInMilliseconds) throws DeviceNotFoundException {

        return getNetwork(index, timeInMilliseconds, refreshIntervalInMilliseconds).getPacketsSent();
    }

    public Long getNetworkDataSent(int index, long timeInMilliseconds, long refreshIntervalInMilliseconds) throws DeviceNotFoundException {

        return getSizeInMb(getNetwork(index, timeInMilliseconds, refreshIntervalInMilliseconds).getBytesSent());
    }

    public Long getNetworkDataReceived(int index, long timeInMilliseconds, long refreshIntervalInMilliseconds) throws DeviceNotFoundException {

        return getSizeInMb(getNetwork(index, timeInMilliseconds, refreshIntervalInMilliseconds).getBytesRecv());
    }

    public String getProcessName(Integer pid) throws DeviceNotFoundException {

        if (pid != null && pid > 0) {
            OSProcess process = getProcess(pid);
            return process.getName();
        } else {
            return null;
        }
    }

    public Double getProcessCpuUsage(Integer pid) throws DeviceNotFoundException {

        if (pid != null && pid > 0) {
            OSProcess process = getProcess(pid);
            double cpuUsageRaw = (process.getKernelTime() + process.getUserTime()) / process.getUpTime();

            return getPercentsValue(cpuUsageRaw);
        } else {
            return null;
        }
    }

    public Long getProcessMemoryUsage(Integer pid) throws DeviceNotFoundException {

        if (pid != null && pid > 0) {
            return getSizeInMb(getProcess(pid).getResidentSetSize());
        } else {
            return null;
        }
    }

    public String getProcessPath(Integer pid) throws DeviceNotFoundException {

        if (pid != null && pid > 0) {
            return getProcess(pid).getPath();
        } else {
            return null;
        }
    }

    public Integer getProcessThreads(Integer pid) throws DeviceNotFoundException {

        if (pid != null && pid > 0) {
            return getProcess(pid).getThreadCount();
        } else {
            return null;
        }
    }
}