# Base DHT Testproject

Base Distributed Hash Table Test Project for learning purpose. 

Try to create a simple chord implementation with SHA-256 in first step with no nat or upnp firewall only plain server/client networking. 

Basic documentation of implementation

https://pdos.csail.mit.edu/papers/ton:chord/paper-ton.pdf

## Status

Basic Protocol handling is working.
Ring Build up is basic working
Finger Tables first are builded up

try now to do the basic get/set operations

shutdown and unregister / leave of a node is missing to

# Basic Protocol Definition

Try to use bidirectonal tcp streams in a connection pool with a req id to handle command -> repsonse 

A request id is a 32 bit integer

All text commands byte encoded in UTF-8

KEY = 256 BIT 

NODE = KEY + PORT + HOSTNAME + 0x00 

## Hello Handshake

- Request:  REQID + "HELLOP2PC" + 0x00 + VER (4 BYTES) + NODE
- Response: REQID + "CP2P" + 0x00 + VER (4 BYTES) + KEY 

## Ping 

- Request:  REQID + "PI" + 0x00
- Response: REQID + "PO" + 0x00

## Lookup Key

- Request:  REQID + "GET" + 0x00 + KEY 
- Response: REQID + "VAL" + 0x00 + LEN (INTEGER) + BYTES 

## Set Value 

- Request:  REQID + "SET" + 0x00 + KEY + LEN (INTEGER) + BYTES 
- Response: REQID + "DONE" + 0x00

## Find Successor

- Request: REQID + "FINDSUCC" + 0x00 + KEY 
- Response REQID + "SUCCESSF" + 0x00 + NODE 

## Notify

- Request:  REQID + "NOTIFY" + 0x00 + NODE 
- Response: REQID + "DONE" + 0x00

## Ask Predecessor

- Request:  REQID + "PRE" + 0x00 
- Response: REQID + "ERP" + 0x00 + NODE

