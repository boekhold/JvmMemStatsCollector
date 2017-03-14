import sun.jvmstat.monitor.HostIdentifier
import sun.jvmstat.monitor.Monitor
import sun.jvmstat.monitor.MonitoredHost
import sun.jvmstat.monitor.MonitoredVm
import sun.jvmstat.monitor.VmIdentifier

class JvmMonitor {
    private static HostIdentifier thisHostId = new HostIdentifier((String)null)
    private static MonitoredHost monitoredHost = MonitoredHost.getMonitoredHost(thisHostId)

    private MonitoredVm theVm

    private JvmMonitor(MonitoredVm vm) {
        this.theVm = vm
    }

    static JvmMonitor getJvmMonitor(List<String> commandLinePatterns, List<String> vmArgsPatterns) {
        def activeVms = monitoredHost.activeVms()
        def vm = activeVms.findResult { id ->
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

        return vm ? new JvmMonitor(vm) : null
    }

    Monitor findByName(name) {
        theVm.findByName(name)
    }

    List<Monitor> findByPattern(pattern) {
        theVm.findByPattern(pattern)
    }
}
