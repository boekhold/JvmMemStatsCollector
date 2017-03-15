import org.junit.Test

class JvmMonitorTest {
    private static String VERSION_PROP = 'java.property.java.vm.specification.version'
    @Test
    void findJUnitTesterVM() {
        JvmMonitor vm = JvmMonitor.getJvmMonitor(
            ['JvmMon.+Test', 'f.*TesterVM'],
            ['-D']
        )

        assert vm.findByName(VERSION_PROP).stringValue() == '1.8'
    }

    @Test
    void testJvmStartedLater() {
        JvmMonitor vm = JvmMonitor.getJvmMonitor(
            ['BackgroundJvmMain'],
            ['-Dsom.*ing']
        )

        assert vm.findByName(VERSION_PROP) == null

        def subVm = launchJvm('BackgroundJvmMain', ['-Dsomething'])

        sleep 2000
        assert vm.findByName(VERSION_PROP).stringValue() == '1.8'
        subVm.destroy()
    }

    @Test
    void testJvmRestarted() {
        JvmMonitor vm = JvmMonitor.getJvmMonitor(
            ['BackgroundJvmMain'],
            ['-Dsom.*ing']
        )

        def subVm = launchJvm('BackgroundJvmMain', ['-Dsomething'])
        sleep 2000
        assert vm.findByName(VERSION_PROP).stringValue() == '1.8'
        subVm.destroy()
        // sleep more than the default interval of 1000ms of MonitoredHost/MonitoredVm
        sleep 1100

        assert vm.findByName(VERSION_PROP) == null

        subVm = launchJvm('BackgroundJvmMain', ['-Dsomething'])
        sleep 2000
        assert vm.findByName(VERSION_PROP).stringValue() == '1.8'
        subVm.destroy()
    }

    private static Process launchJvm(String clazzName, List<String> vmArgs) {
        def sep = System.properties['file.separator']
        def cmd = [System.properties['java.home'] + sep + 'bin' + sep + 'java']
        cmd += '-cp'
        cmd += System.properties['java.class.path']
        if (vmArgs)
            cmd += vmArgs
        cmd += clazzName

        ProcessBuilder builder = new ProcessBuilder(cmd)
        builder.redirectError(ProcessBuilder.Redirect.INHERIT)
        builder.redirectOutput(ProcessBuilder.Redirect.INHERIT)
        Process proc = builder.start()
        return proc
    }
}
