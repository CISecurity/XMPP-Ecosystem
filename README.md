# XMPP-Ecosystem
An XMPP-based implementation of the [SACM architecture](https://datatracker.ietf.org/doc/draft-ietf-sacm-arch/), enabling an ecosystem of connected components

A number of XMPP client implementations form the components of the architecture.  XMPP provides a secure communication channel between components, and its inherent extensibility allows clients to perform many SACM-related functions, such as:

- Orchestrator
- Collector
- Evaluator
- Repository

## Architecture ##

```
                 +--------------------------------------------------------------------------------+                                               +--------+
                 |                                                                                |                                               |        |
                 |                                    Orchestrator                                |   +------------------------------------------->        |
                 |                                                                                |   |    Query/Retrieve                         |        |
                 +--+--^--------------------------+------------------------------------------+----+   |    Evaluation Policy                      |        |
                    |  |                          |                                          |        |                                           |        |
Discover Collection |  |                          | Initiate                                 |        |                                           |        |
   Capabilities     |  |                          | Posture                   Initiate       |        |                                           |        |
    (XEP-0030)      |  |                          | Collection                Evaluation     |        |                          Store Posture    |        |
                    |  |                          |                                          |        |                          Attributes       |        |
                 +---------------------------------------------------------------------------------------------- -------------+    (XEP-0060)     |        |
                 |  |  |                          |                                          |        |                       +------------------->        |
                 |  |  |                          |             XMPP Server                  |        |                       |                   |        |Repository
                 |  |  |                          |                                          |        |                       <-------------------+        |
                 +--------------------------------------------^------------------------------------------------^-------+------+ Query/Retrieve    |        |
                    |  |                          |           |                              |        |        |       |        Posture Attributes|        |
                    |  |                          |           |                              |        |        |       |                          |        |
                    |  | Advertise Collection     |           | Publish Collected            |        |        |       |                          |        |
                    |  |     Capabilities         |           | Posture Attributes           |        |        |       |    Store Evaluation      |        |
                    |  |      (XEP-0030)          |           |    (XEP-0060)                |        |        |       |    Results (XEP+0060)    |        |
                    |  |                          |           |                              |        |        |       +-------------------------->        |
                 +--v--+--------------------------v-----------+------+                       |        |        |                                  |        |
                 |                                                   |                       |        |        |                                  |        |
                 |                     Endpoint                      |                       |        |        |                                  |        |
                 |                                                   |                       |        |        | Publish                          +--------+
                 +--------------------------------+-----------^------+                       |        |        | Evaluation Results
                                                  |           |                              |        |        | (XEP-0060)
                                        Perform   |           | Posture                      |        |        |
                                       Collection |           | Attributes                   |        |        |
                                                  |           |                              |        |        |
                                                +-v-----------+------------------------------v--------v--------+-+
                                                |                                                                |
                                                |                       Collector / Evaluator                    |
                                                |                         (aka, an Assessor)                     |
                                                |                                                                |
                                                +----------------------------------------------------------------+

```

## Orchestrator ##
TBD

## Collector ##
TBD; either agent/host-based or remote capable

## Evaluator ##
TBD

## Repository ##
TBD; Policy repository, posture attribute repository, evaluation results repository, etc.

## XMPP Extension Protocols (XEPs) ##
TBD
- Service Discovery
- Entity Capabilities
- Publish/Subscribe
- File Transfer
- (?) File Repository & Sharing
- (?) Personal Eventing Protocol