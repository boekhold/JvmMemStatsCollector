import org.junit.Test
import sun.jvmstat.monitor.HostIdentifier
import sun.jvmstat.monitor.MonitoredHost
import sun.jvmstat.monitor.MonitoredVm
import sun.jvmstat.monitor.VmIdentifier

class MonitoredHostTest {
    @Test
    void monitoredHostTest() {
        HostIdentifier thisHostId = new HostIdentifier((String)null)
        MonitoredHost monitoredHost = MonitoredHost.getMonitoredHost(thisHostId)

        monitoredHost.activeVms().each { id ->
            VmIdentifier jvmId = new VmIdentifier("//${id}?mode=r")
            MonitoredVm vm = monitoredHost.getMonitoredVm(jvmId)
            println vm.findByName("sun.rt.javaCommand").stringValue()
        }
    }
}
