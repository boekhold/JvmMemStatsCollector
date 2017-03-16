// TODO: parse cmdPatterns and argPatterns from "String[] args"
// TODO: print usage

def vm = JvmMonitor.getJvmMonitor('', cmdPatterns, argPatterns)
if (vm) {
    println "Found VM with PID ${vm.vm.vmIdentifier.localVmId}"
} else {
    println "Did not find a VM using specified patterns"
}
