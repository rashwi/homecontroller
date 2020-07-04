#!/usr/bin/python3
# Defining project to listen in for on and off commands on 'la luz'
# Basing code on guide provided at https://pymotw.com/2/socket/tcp.html

import socket
import sys, signal, time
import luz, deus


connection = None


def main():
    # Create a TCP/IP socket
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)    

    # Bind the socket to the port
    server_address = (luz.SERVER, luz.PORT)

    print('starting up on %s port %d ' %server_address)
    sock.bind(server_address)



    # Listen for incoming connections
    sock.listen(1)

    while True:
        # Wait for a connection
        sys.stdout.write('Waiting for a connection.. \n')
        connection, client_address = sock.accept()

        try:
            #print >>sys.stderr, 'connection from', client_address
            sys.stdout.write('Connection from: %s\n' %client_address[0])

            while True:
                data = connection.recv(luz.PACKET_SIZE)
                data_str = data.decode(luz.ENCODING).strip()
                sys.stdout.write('Received "%s"\n' %data_str)


                    
                if data:
                    # Check for "DEVICE COMMAND" Formatted string
                    split_str = data_str.split()
                    if len(split_str) > 1:
                        device=split_str[0]
                    
                        command=split_str[1]
                    else:
                        # Find Default device
                        device=luz.SWITCH_1

                        # Keeping around for legacy reasons
                        command=data_str

                    
                    if command == luz.LUX:
                        o, e = deus.bless(device)
                    elif command == luz.TENEBRIS:
                        o,e = deus.curse(device)
                    else:
                        #outStr = 'Unknown message %s! Cannot Process!' %data_str
                        #e = b''
                        #o = outStr.encode(luz.ENCODING)
                        # TODO: Add error checking?
                        o,e = deus.mandatum(device, command)

                    full_str = 'SERV.OUT: %s\n' %o.decode(luz.ENCODING).strip()
                    sys.stdout.write('SENDING "%s"\n' %full_str)
                    connection.sendall(full_str.encode(luz.ENCODING))
                    full_str = 'SERV.ERR: %s\n' %e.decode(luz.ENCODING).strip()
                    sys.stdout.write('SENDING "%s"\n' %full_str)
                    connection.sendall(full_str.encode(luz.ENCODING))
                    full_str = '%s\n' %luz.BUFFER_END
                    sys.stdout.write('SENDING "%s"\n' %full_str)
                    connection.sendall(full_str.encode(luz.ENCODING))

                    sys.stdout.write('Client should stop listening now..\n')
                else:
                    sys.stderr.write('no more data from %s\n' %client_address[0])
                    break
                # Sleep a second, just to catch sigint
                time.sleep(1)
           
        finally:
            # Clean up the connection
            connection.close()


if __name__ ==  '__main__':
    main()

