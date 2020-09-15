# kivi
kinesis cli viewer

It currently supports:

* Tail a stream from `LATEST`
* Scan records within a given interval

It authenticates with AWS using a profile from your `.aws/credentials` file.

##Usage:

###Tail
--profile profile_name --region region_name tail --stream stream_name --shard shardId-000000000000

###Scan
--profile profile_name --region region_name scan --from 2020-09-13T00:00:00 --to 2020-09-15T00:00:00 --stream stream_name --shard shardId-000000000000



