import subprocess
import luz


def bless(device):
    # Send on command
    o, e = mandatum(device,'ON')
    return o,e

def curse(device):
    # Send off command
    o, e = mandatum(device,'OFF')
    return o,e

def mandatum(device, prop):
    # Send command "prop" to device
    command = ['ssh', '-p{}'.format(luz.OPENHAB_SSH_PORT), '-i{}'.format(luz.OPENHAB_ID_FILE),'openhab@localhost', 'smarthome:send {} {}'.format(device,prop)]
    proc = subprocess.Popen(command, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    o, e = proc.communicate()
    return o,e

