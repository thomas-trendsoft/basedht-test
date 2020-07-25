# Base DHT Testproject

Base Distributed Hash Table Test Project for learning purpose. 

Try to create a simple chord implementation with SHA-256 in first step with no nat or upnp firewall only plain server/client networking. 

Basic documentation of implementation

https://pdos.csail.mit.edu/papers/ton:chord/paper-ton.pdf

# Basic Protocol Definition

Try to use bidirectonal tcp streams in a connection pool with a req id to handle command -> repsonse 

A request id is a 32 bit integer

All text commands byte encoded in UTF-8

KEY = 256 BIT = 

## Ping 

- Request: REQID + "PI" + 0x00

- Response: REQID + "PO" + 0x00

## Find Successor

- Request: REQID + "FINDSUCC" + KEY + 0x00
- Response REQID + "FINDSUCC" + NODE + 0x00 / OR instead NODE 0x00 directly if nothing found

## Notify

- Request: REQID + "NOTIFY" + NODE + 0x00
- Response: REQID + "X" + 0x00

## lookup 

- Request: REQID + "GET" + KEY + 0x00
