# -*- coding: cp1252 -*-

#!/usr/bin/python

import paramiko
import cmd
import os
import sys

class RunCommand(cmd.Cmd):
    """ Simple shell to run a command on the host """
    def __init__(self):
        cmd.Cmd.__init__(self)
        self.hosts = []
        self.connections = []
 
    def add_host(self, args):
        """add_host <host,user,password>
        Add the host to the host list"""
        if args:
            self.hosts.append(args.split(','))
        else:
            print "usage: host <hostip,user,password>"
 
    def connect(self, args):
        """Connect to all hosts in the hosts list"""
        for host in self.hosts:
            client = paramiko.SSHClient()
            client.set_missing_host_key_policy(
                paramiko.AutoAddPolicy())
            client.connect(host[0], 
                username=host[1], 
                password=host[2])
            self.connections.append(client)

    def put(self, fichier):
        if fichier:
            for host, conn in zip(self.hosts, self.connections):
                ftp = conn.open_sftp()
                ftp.put(fichier,'/tmp/MakeDistribAgent.jar')
                ftp.close()
 
    def run(self, command):
        """run <command>
        Execute this command on all hosts in the list"""
        if command:
            for host, conn in zip(self.hosts, self.connections):
               	stdin, stdout, stderr =  conn.exec_command(command)
               	stdin.close()
                for line in stdout.read().splitlines():
                    print 'host: %s: %s' % (host[0], line)
		""""""
        else:
            print "usage: run <command>"
 
    def close(self, args):
        for conn in self.connections:
            conn.close()


ip = sys.argv[1];
port = sys.argv[2];

rc = RunCommand()

file = open('hosts.txt','r')
for line in file:
    print('Ajout de l\'hote : '+line)
    rc.add_host(line)


try:
	rc.connect("")
except:
	print("erreur de config");
else:
	rc.put("../../AgentDistribue/build/MakeDistribAgent.jar")
	rc.run("java -jar /tmp/MakeDistribAgent.jar "+ip+" "+port);
finally:
	rc.close("")
