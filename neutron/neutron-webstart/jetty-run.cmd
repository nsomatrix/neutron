@echo off
rem @version $Revision$ ($Author$)  $Date$
title *Jetty:neutron-webstart

call mvn -o -P debug webstart:jnlp
echo Go to http://localhost:8080/neutron-webstart/
call mvn %* jetty:run

title Jetty:neutron-webstart - ended

pause
