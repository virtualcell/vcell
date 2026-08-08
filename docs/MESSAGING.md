# VCell messaging

How VCell moves messages today, across two broker technologies and two client stacks, and what
that implies for consolidating onto Artemis.

Written 2026-08-08. Everything marked **(measured)** was verified against a running broker
rather than inferred; the distinction matters, because several long-held beliefs about this
subsystem turned out to be wrong when tested.

---

## 1. The short version

There are **three brokers** and **three client stacks**, and they overlap:

| broker | image / kind | port | protocol(s) | carries |
|---|---|---|---|---|
| `activemqint` | ActiveMQ **Classic** | 61616 | OpenWire | `simReq`, `dataReq`, `dbReq`, `simJob`, topic `clientStatus` |
| `activemqsim` | ActiveMQ **Classic** | 61616 (NodePort 31617 external) | OpenWire | `workerEvent`, topic `serviceControl` |
| `artemismq` | **Artemis** `apache/activemq-artemis:2.42.0` | 61616 | **all protocols on one acceptor** | `opt-request`, `opt-status`, `export.request.queue`, `client.status.topic` |

| client stack | where | talks to |
|---|---|---|
| `VCMessagingService` — the legacy IoC wrapper | `vcell-core` API, `vcell-server` JMS impl | Classic only (`activemqint`, `activemqsim`) |
| raw ActiveMQ OpenWire client | `OptimizationBatchServer` | **Artemis** |
| Quarkus / SmallRye Reactive Messaging (AMQP 1.0) | `vcell-rest` | **Artemis** |

The single most surprising fact, and the one that makes migration tractable: **Artemis exposes
one acceptor that multiplexes every protocol**, so an OpenWire client and an AMQP client talk to
the same broker on the same port.

```xml
<!-- vcell-fluxcd/kustomize/config/shared/broker.xml -->
<acceptor name="artemis">tcp://0.0.0.0:61616</acceptor>
<!-- <acceptor name="amqp">amqp://0.0.0.0:5672?protocols=AMQP</acceptor>  (commented out) -->
```

That is why `%prod.amqp-port` resolves to `61616` rather than AMQP's usual `5672`.

---

## 2. The legacy IoC wrapper (`VCMessagingService`)

A thin inversion-of-control layer over JMS 1.1, so callers never touch `javax.jms` directly.

**Interfaces (`vcell-core`, `cbit.vcell.message`)**

| type | role |
|---|---|
| `VCMessagingService` | entry point; `createProducerSession()`, `addMessageConsumer()`, `removeMessageConsumer()` |
| `VCMessageSession` | send side: `sendQueueMessage`, `sendTopicMessage`, `sendRpcMessage`, `createObjectMessage` |
| `VCQueueConsumer` / `VCTopicConsumer` | a destination + a listener + a thread name + a prefetch limit |
| `VCPooledQueueConsumer` | wraps a listener in a fixed thread pool |
| `VCRpcMessageHandler` | request/reply: invokes a service impl by reflection and replies to `JMSReplyTo` |
| `VCMessagingDelegate` | observability hook (`onMessageSent`, `onException`, …) |
| `VCellQueue` / `VCellTopic` | destination constants, names overridable by property |

**JMS implementation (`vcell-server`, `cbit.vcell.message.jms`)**

| class | role |
|---|---|
| `VCMessagingServiceJms` | abstract base: consumer registry, producer-session registry, blob GC timer |
| `VCMessagingServiceActiveMQ` | the only concrete impl — builds `failover:(tcp://host:port)`, `setTrustAllPackages(true)` |
| `MessageProducerSessionJms` | the send side; owns a connection, a session, and a temporary reply queue |
| `ConsumerContextJms` | one polling thread per consumer: `receive(2000)` in a loop |
| `VCMessageJms` | `VCMessage` over a `javax.jms.Message`, including BLOB offload |
| `JmsFailoverWatchdog` | `TransportListener` that can exit the JVM when failover gives up |

**Destinations** (`VCellQueue` / `VCellTopic`, names overridable via `vcell.jms.queue.*` /
`vcell.jms.topic.*`): `simReq`, `dataReq`, `dbReq`, `simJob`, `workerEvent`, `clientStatus`,
`serviceControl`.

### Who owns which session

This is the part that has caused real bugs, so it is worth stating plainly:

| holder | session | lifetime |
|---|---|---|
| `RpcService` (vcell-api) | **one, shared by every HTTP request thread** | process lifetime, never closed |
| `SimDataServer`, `DatabaseServer`, `HtcSimulationWorker` | one shared, handed to a `VCPooledQueueConsumer` | process lifetime |
| `SimulationDispatcher` | several long-lived (dispatcher queue, client-status topic, sim monitor) | process lifetime |
| `RestDatabaseService` | **a fresh session per request**, closed in `finally` / try-with-resources | one request |
| `ConsumerContextJms` | one per received message, handed to the listener | one message |

Two rules follow, and violating either has produced an incident:

- a **shared** session must never be closed while callers are using it, and
- a `javax.jms.Session` is **not thread-safe**, so shared sessions need per-call sessions
  underneath (see §5).

---

## 3. The Quarkus stack (`vcell-rest`)

Declarative SmallRye Reactive Messaging over **AMQP 1.0** — annotations instead of the wrapper:

```java
@Channel("publisher-opt-request") Emitter<String> optRequestEmitter;   // send
@Incoming("subscriber-opt-status") void onStatus(String json) { … }    // receive
```

Channels are bound to Artemis addresses in `application.properties`:

| channel | address | enabled in |
|---|---|---|
| `publisher-opt-request` | `opt-request` (queue) | **prod** |
| `subscriber-opt-status` | `opt-status` (queue) | **prod** |
| `publisher-export-request` / `subscriber-export-request` | `export-request` | `%test` only |
| `publisher-client-status` / `subscriber-client-status` | `client-status` | `%test` only |

Connection settings: `%prod.amqp-host=${jmshost_artemis_internal}`,
`%prod.amqp-port=${jmsport_artemis_internal}` → `artemismq:61616`.

Two things to check before relying on this (flagged, not diagnosed):

- the two `%test` client-status lines use `.connect=` where every other line uses
  `.connector=`. If that is a typo, those channels are silently in-memory rather than AMQP.
- `broker.xml` declares `export.request.queue` and `client.status.topic`, while the Quarkus
  channels address `export-request` and `client-status`. The dotted names and the hyphenated
  names do not match; whether that is intentional (different generations of wiring) is worth
  confirming before either is treated as live.

---

## 4. How the legacy stack talks to Artemis

It does — but **not through the IoC wrapper**. The single bridge is
`OptimizationBatchServer.initOptimizationQueue(host, port)`, called from `HtcSimulationWorker`:

```java
ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://" + jmsHost + ":" + jmsPort);
factory.setTrustAllPackages(true);
…
Destination requestQueue = session.createQueue("opt-request");   // consume
Destination statusQueue  = session.createQueue("opt-status");    // produce
```

So: a **raw ActiveMQ Classic OpenWire client**, pointed at **Artemis**, exchanging **JSON text
messages** with `vcell-rest`, which addresses the same queues over **AMQP 1.0**. Artemis performs
the protocol translation; neither side knows the other's protocol. It bypasses
`VCMessagingService` entirely — no `VCMessageSession`, no `VCMessagingDelegate`, its own polling
thread.

**This is the template for migration**: it proves the legacy OpenWire client can drive Artemis
unchanged, so destinations can move broker-by-broker without rewriting client code first.

---

## 5. Measured behaviour (don't re-derive this)

Probed against embedded brokers on 2026-08-08 while diagnosing the export-progress incident.
Several results contradicted what the team (and this author) believed.

**Poison messages, Classic (measured).** There is *no* unbounded redelivery loop:

| listener throws | result |
|---|---|
| any generic exception | delivered **once**, never redelivered, never dead-lettered — **silently dropped** |
| `RollbackException` | 1 delivery + 6 redeliveries, then the message lands in `ActiveMQ.DLQ` |

`ConsumerContextJms` only calls `jmsSession.rollback()` for `RollbackException`; every other
failure is logged and the message is simply never committed. ActiveMQ's default
`maximumRedeliveries=6` already bounds the rollback path, so **adding a `RedeliveryPolicy` to the
Classic side would protect against a loop that does not exist.**

Consequence for the 2026-08-06 incident narrative: the ~4,900 errors/minute were **not** one
poisoned message retrying forever. They were many distinct progress events from one long-running
export, each dividing by zero.

**Temporary queues (measured).** A temporary queue is owned by the **connection**, not the
session. It survives:

- the session that created it being closed (3 s+ later, still publishable),
- repeated create/close cycles of consumer sessions on it,
- a *different* connection publishing to it and then closing.

It is removed by `MessageProducerSessionJms.close()` calling `delete()`, or by its owning
connection closing. This matters because a temp queue deleted while an RPC is in flight makes
that RPC wait out its full timeout — the shape of issue #1863.

**Reply routing depends on a name prefix.** `VCMessageJms.getReplyTo()` returns a plain
`VCellQueue` holding the temporary queue's *name*. It reaches the temporary queue only because
ActiveMQ's `createQueue(name)` special-cases names beginning with `ID:` and returns an
`ActiveMQTempQueue`. **Artemis does not share this convention** — see §7.

**"Cannot publish to a deleted Destination" may be a false negative (Classic client).**
`ActiveMQConnection.isDeleted(dest)` does not ask the broker. It answers
`!activeTempDestinations.contains(dest)`, and that set is populated by the connection's own
**advisory consumer**. A connection that has not yet learned about *another* connection's
temporary queue will therefore refuse to publish to it, reporting it as deleted when it is
alive. `ConsumerContextJms` opens a connection per message, so an RPC reply is published from a
connection that may be seconds old — the exposure is real, though unproven (a 25-iteration probe
could not reproduce it locally, where propagation is instant).

Practical consequences when debugging:

- treat that exception as "this connection does not know about the queue", not as proof of
  deletion — confirm with a temp-queue advisory before concluding;
- `ActiveMQConnectionFactory.setWatchTopicAdvisories(false)` disables the client-side check
  entirely (`isDeleted()` then always returns false), which is the one-line lever if this is ever
  confirmed;
- it is **Classic-client behaviour**, not JMS. It does not exist for AMQP or Artemis core
  clients, so it disappears with the migration rather than needing to be ported.

**Temp-queue advisories, and how fast they arrive (measured).** Subscribing to
`ActiveMQ.Advisory.TempQueue` yields a `DestinationInfo` per create/destroy carrying the
**connection id that requested it** — which is how to tell "the owner tore down its own queue"
from "something else deleted it". Measured latency on a healthy local run is about **1 ms**, so
an advisory arriving seconds after the event it should describe is itself evidence. The
recorder lives in `MessageProducerSessionJmsTest` (test-only, deliberately — see §7).

**Artemis already dead-letters.** Contrary to "we don't use DLQs", `broker.xml` configures it for
the export path:

```xml
<address-setting match="export.request.queue">
  <dead-letter-address>export.request.deadLetterQueue</dead-letter-address>
  <max-delivery-attempts>3</max-delivery-attempts>
  <redelivery-delay>5000</redelivery-delay>
  <redelivery-delay-multiplier>2</redelivery-delay-multiplier>
  <expiry-address>export.request.expiryQueue</expiry-address>
</address-setting>
```

`ActiveMQ.DLQ` on the Classic side also exists and already receives messages. **Nobody watches
either.** A dead-letter queue without an alert is a silent leak, not a safety net.

---

## 6. Fixes already made (context for anyone reading the code)

The 2026-08-06 export-progress incident exposed a chain of messaging defects, all now on master:

| what | PR |
|---|---|
| non-finite export progress clamped; the divisions producing `Infinity` guarded | #1837, #1838 |
| `transportResumed` logged at INFO only after a real interruption | #1839 |
| producer session opens its connection on **first use** — five of seven consumers never used the session handed to them, so each message opened a connection, session and temp queue for nothing | #1842 |
| RPC request message built without a second connection | #1844 |
| **one JMS session per RPC call** — all request threads shared one session, so one caller's `commit()` committed another's in-flight send (`Transaction 'TX:…' has not been started`) | #1845 |
| `removeMessageConsumer` actually closes the consumer; poll loop exits cleanly when closed (`bProcessing` made `volatile`) | #1847 |
| `sendTopicMessage` closes its `MessageProducer` | #1850 |

---

## 7. What migration to Artemis-only has to deal with

Ordered roughly by risk.

1. **Temporary-queue reply routing.** The RPC pattern round-trips a temporary queue through a
   *string*: `getReplyTo()` returns its name, and `sendQueueMessage()` turns that name back into
   a destination — which only works because the ActiveMQ Classic client special-cases names
   beginning with `ID:`. That behaviour belongs to the **client library and broker pairing**, not
   to JMS. It may well survive an OpenWire client pointed at Artemis (Artemis emulates OpenWire
   temp destinations), but it will **not** survive a client-stack change to AMQP or Artemis core,
   where the reply address is modelled differently. Untested either way. **Test an RPC round trip
   first** — this is the piece most likely to fail quietly, and every RPC depends on it.
2. **Dead-lettering and redelivery move from client to broker.** Classic bounds redelivery
   client-side (`RedeliveryPolicy`, default 6 → `ActiveMQ.DLQ`). Artemis does it broker-side via
   `address-settings`. Anything relying on the Classic default gets *different* behaviour on
   Artemis unless a matching `address-setting` exists — today only `export.request.queue` has
   one.
3. **Advisories have no equivalent.** Classic publishes `ActiveMQ.Advisory.*`; Artemis publishes
   `activemq.notifications`. Any diagnostic built on advisories must be rewritten, which argues
   for not building one on the Classic side at all. Note this cuts both ways: the client-side
   temp-destination tracking described in §5 — and the spurious "deleted Destination" errors it
   can produce — is a property of the Classic *client*, so it goes away with the migration
   instead of needing to be carried across.
4. **Selectors and correlation IDs.** RPC replies are matched with
   `JMSCorrelationID='<id>'` selectors on a shared reply queue. Artemis supports selectors, but
   this is worth an explicit test rather than an assumption, given it is how every RPC completes.
5. **Address semantics.** Classic distinguishes queue vs topic by object type; Artemis models
   both as *addresses* with `anycast` (queue) or `multicast` (topic) routing, and the two are
   configured, not implied. `clientStatus` is a topic with multiple independent subscribers, so
   it needs `multicast` with a queue per subscriber — `broker.xml` already shows that shape for
   `client.status.topic` with an `export.sub` queue.
6. **The external NodePort.** `activemqsim` is reachable from solvers outside the cluster
   (`jmshost_sim_external=k8s-wn-01.cam.uchc.edu`, port 31617). Any migration of `workerEvent`
   must keep an externally reachable acceptor, and solvers may be running versions built against
   the old endpoint.
7. **Two things that are *not* obstacles.** The wrapper's abstraction means callers do not touch
   `javax.jms`, so most code changes nothing. And OpenWire-against-Artemis already works in
   production (§4), so destinations can be migrated one at a time with the existing client
   library — a broker swap first, a client-stack rewrite later, if ever.

### A pragmatic order

1. Point one low-risk Classic destination at Artemis by configuration only (the wrapper builds
   `tcp://host:port`, so this is a host/port change) and confirm send/receive.
2. Then an RPC destination, to flush out item 1 above.
3. Then `clientStatus`, to flush out item 5 (multiple subscribers).
4. Leave `workerEvent` until last, because of the external NodePort and out-of-cluster solvers.
5. Only after all destinations move, retire `VCMessagingServiceActiveMQ` / the OpenWire client.

Because Artemis is the destination, **do not invest further in Classic-side machinery** —
redelivery policies, advisory watchdogs, DLQ tooling. Effort there has a shelf life.

---

## 8. Operational notes

- Broker pods run **supervisord as PID 1**, so `kubectl logs deployment/activemqint` shows only
  supervisord lifecycle, and promtail does not scrape them. Broker-side events need
  `kubectl exec … tail /var/log/activemq/activemq.log`, which rotates after ~14 h.
  Making these brokers log to stdout is an easy, worthwhile fix.
- `VCMessagingServiceActiveMQ` uses a **bounded** failover URL so a wedged transport cannot
  retry forever; `JmsFailoverWatchdog` runs a terminal action (in production, JVM exit so K8s
  recycles the pod) when failover gives up.
- `transportResumed` fires on a *first connect* as well as after an interruption. A resumed
  count with zero interruptions means new connections, not reconnects — a distinction that once
  cost a misdiagnosis.
- `VCMessagingServiceJms.close()` sleeps two polling intervals (4 s) unconditionally and does not
  cancel its blob GC timer. Harmless at shutdown; surprising in tests.
