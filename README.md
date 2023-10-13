# assignment

## Performance

On my Apple M1 CPU using a single core the `Service` class handles around 400k queries/sec. The throughput will not scale with the number of cores due to writes on every signle query.