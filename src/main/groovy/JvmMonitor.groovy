import sun.jvmstat.monitor.*
import sun.jvmstat.monitor.event.HostEvent
import sun.jvmstat.monitor.event.HostListener
import sun.jvmstat.monitor.event.VmStatusChangeEvent

class JvmMonitor implements HostListener {
    private static HostIdentifier thisHostId = new HostIdentifier((String)null)
    private static MonitoredHost monitoredHost = MonitoredHost.getMonitoredHost(thisHostId)

    private MonitoredVm vm
    private List<String> commandLinePatterns
    private List<String> vmArgsPatterns

    private JvmMonitor() {
    }

    static JvmMonitor getJvmMonitor(List<String> commandLinePatterns, List<String> vmArgsPatterns) {
        JvmMonitor jvmMonitor = new JvmMonitor()
        jvmMonitor.commandLinePatterns = commandLinePatterns
        jvmMonitor.vmArgsPatterns = vmArgsPatterns
        monitoredHost.addHostListener(jvmMonitor)

        return jvmMonitor
    }

    private static MonitoredVm getMonitoredVm(List<String> commandLinePatterns, List<String> vmArgsPatterns) {
        def vm = monitoredHost.activeVms().findResult { id ->
            VmIdentifier jvmId = new VmIdentifier("//${id}?mode=r")
            MonitoredVm vm = monitoredHost.getMonitoredVm(jvmId)

            def javaCommandLine = vm.findByName('sun.rt.javaCommand').stringValue()
            def vmArgs = vm.findByName('java.rt.vmArgs').stringValue()

            def match = commandLinePatterns.every { pattern ->
                javaCommandLine =~ pattern
            }
            if (match) {
                match = vmArgsPatterns.every { pattern ->
                    vmArgs =~ pattern
                }
            }

            return match ? vm : null
        }
        //println "getMonitoredVm: ${vm ? vm.vmIdentifier.localVmId : 'n/a'}"
        return vm
    }


    Monitor findByName(name) throws MonitorException {
        if (vm)
            return vm.findByName(name)

        // not connected yet, or disconnected
        vm = getMonitoredVm(commandLinePatterns, vmArgsPatterns)
        return vm?.findByName(name)
    }

    List<Monitor> findByPattern(pattern) throws MonitorException {
        if (vm)
            return vm.findByPattern(pattern)

        // not connected yet, or disconnected
        vm = getMonitoredVm(commandLinePatterns, vmArgsPatterns)
        return vm?.findByPattern(pattern)
    }

    @Override
    void vmStatusChanged(VmStatusChangeEvent vmStatusChangeEvent) {
        if (vm && vmStatusChangeEvent.terminated.contains(vm.vmIdentifier.localVmId as Integer)) {
            // our VM has died, need to explicitly clean up, because the current
            // vm object seems to remain valid if we don't do this!
            vm.detach()
            vm = null
        }
    }

    @Override
    void disconnected(HostEvent hostEvent) {
        // we're monitoring only the local host, should never
        // get disconnected
    }
}
