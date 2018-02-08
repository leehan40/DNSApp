# DNSApp
Simple DNS java app

This app will search for an IP address associated with a given URL,
it will also attempt to fix a broken URL input

Server requires a port# argument when started 
Example: java DNSServer 5901

Client requires two argruments
1. IP of Server with port number 
2. URL of a website
Example: java DNSClient 127.0.0.1:5901 google.com
