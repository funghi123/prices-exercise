## Assumptions

### Input validation

There is no sophisticated input validation, we try only to preserve most important invariants like not-nullness

### Dependency injection

No DI framework was used for the sake of simplicity

### Architecture

It is very simple (as the exercise), there is only a separate adapter layer containing csv feed subscriber which is
an entry to the application. 