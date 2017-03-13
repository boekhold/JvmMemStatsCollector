import org.junit.Ignore
import org.junit.Test
import sun.jvmstat.monitor.HostIdentifier
import sun.jvmstat.monitor.IntegerMonitor
import sun.jvmstat.monitor.LongMonitor
import sun.jvmstat.monitor.MonitoredHost
import sun.jvmstat.monitor.MonitoredVm
import sun.jvmstat.monitor.StringMonitor
import sun.jvmstat.monitor.VmIdentifier

class MonitoredHostTest {
    @Ignore
    @Test
    void monitoredHostTest() {
        HostIdentifier thisHostId = new HostIdentifier((String)null)
        MonitoredHost monitoredHost = MonitoredHost.getMonitoredHost(thisHostId)

        monitoredHost.activeVms().each { id ->
            // 'id' is the OS PID...
            println "ID: $id --------------------------------------------------------------------------"

            VmIdentifier jvmId = new VmIdentifier("//${id}?mode=r")
            MonitoredVm vm = monitoredHost.getMonitoredVm(jvmId)

            /**
             * Interesting "monitors" useful for identifying a specific JVM instance are:
             * - java.rt.vmArgs
             * - sun.rt.javaCommand
             */
            vm.findByPattern('.*').each { monitor ->
                if (monitor instanceof StringMonitor)
                    println "${monitor.name} : ${monitor.stringValue()}"
                if (monitor instanceof IntegerMonitor)
                    println "${monitor.name} : ${monitor.intValue()}"
                if (monitor instanceof LongMonitor)
                    println "${monitor.name} : ${monitor.longValue()}"

            }
        }
    }
}
