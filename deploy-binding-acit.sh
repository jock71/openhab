scp  ./bundles/binding/org.openhab.binding.acit/target/org.openhab.binding.acit-1.13.0-SNAPSHOT.jar pi@192.168.10.23:/tmp

# after that go to openhab machine and
# ssh -p 8101 openhab@localhost 
# bundle:list
# bundle:uninstall 267  ' use the right number
# bundle:install "file:///tmp/org.openhab.binding.acit-1.13.0-SNAPSHOT.jar"
# bundle:start 268

# if required stop and start openhab2
# sudo systemctl stop openhab2.service
# sudo systemctl start openhab2.service
