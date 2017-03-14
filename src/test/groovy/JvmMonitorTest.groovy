import org.junit.Test

class JvmMonitorTest {
    @Test
    void findJUnitTesterVM() {
        JvmMonitor vm = JvmMonitor.getJvmMonitor(
            ['JvmMon.+Test', 'f.*TesterVM'],
            ['-D']
        )

        assert vm, 'Could not find JUnit test JVM instance'
        assert vm.findByName('java.property.java.vm.specification.version').stringValue() == '1.8'
    }
}
