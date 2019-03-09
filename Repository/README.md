# Repository #

NOTE: Currently for this PoC, the repository is file-based, and any interfaces pull information from this filesystem-accessed "repository".

## Introduction
TBD
## Requirements
Query for content
Request Content Delivery
Deliver Content
## Glossary
## Use Cases
### Content Type Listing
### Content Listing
### Content Requests
### Content Delivery
## Determining Support
If an entity supports the SACM Repository protocol, it MUST advertise that fact in its responses to Service Discovery (XEP-0030) [3] information ("disco#info") requests by returning a feature of "http://cisecurity.org/sacm/repository".

**Requesting entity queries responding entity regarding protocol support**
```
<iq from='orchestrator@cisecurity.org/sacm'
    id='disco1'
    to='repository@cisecurity.org/sacm'
    type='get'>
  <query xmlns='http://jabber.org/protocol/disco#info'/>
</iq>
```

**Responding entity communicates protocol support**
```
<iq from='repository@cisecurity.org/sacm'
    id='disco1'
    to='orchestrator@cisecurity.org/sacm'
    type='result'>
  <query xmlns='http://jabber.org/protocol/disco#info'>
    <feature var='http://cisecurity.org/sacm/repository'/>
  </query>
</iq>
```

## Implementation Notes
## Security Considerations
The repository XMPP entity is intended to be implemented within an enterprise, and not necessarily federated to entities external to that enterprise.

## XML Schema
```
<?xml version="1.0" encoding="UTF-8"?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" targetNamespace="http://cisecurity.org/sacm/repository" xmlns="http://cisecurity.org/sacm/repository" elementFormDefault="qualified">

	<!-- TBD: Schema Forthcoming... -->

</xs:schema>
```

