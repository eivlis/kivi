package com.eivlis.kivi.command

import java.nio.charset.StandardCharsets

import com.amazonaws.services.kinesis.AmazonKinesis
import com.amazonaws.services.kinesis.clientlibrary.types.UserRecord
import com.eivlis.kivi.support.utils.ArgumentImprovement
import com.eivlis.kivi.{Args, Console}
import net.sourceforge.argparse4j.inf.Subparser
import com.amazonaws.services.kinesis.model.{GetRecordsRequest, GetShardIteratorRequest, ShardIteratorType}

class TailStreamCommand extends Command {
  override def name(): String = "tail"

  override def configureArgumentParser(parser: Subparser): Unit = {
    parser.addStreamArgument()
    parser.addShardArgument()
  }

  override def execute(kinesis: AmazonKinesis, args: Args, console: Console): Unit = {
    val streamName = args.getStreamName()
    val shardId = args.getShardId()

    val getShardIteratorResult = kinesis.getShardIterator(new GetShardIteratorRequest()
      .withStreamName(streamName)
      .withShardId(shardId)
      .withShardIteratorType(ShardIteratorType.LATEST))

    var shardIterator = getShardIteratorResult.getShardIterator

    while (true) {
      task()
      Thread.sleep(1000)
    }

    def task(): Unit = {
      val getRecordsResult = kinesis.getRecords(new GetRecordsRequest()
        .withShardIterator(shardIterator))
      shardIterator = getRecordsResult.getNextShardIterator

      val userRecords = UserRecord.deaggregate(getRecordsResult.getRecords)

      userRecords.forEach { userRecord =>
        val decodedRecordData = StandardCharsets.UTF_8.decode(userRecord.getData)
        console.println(decodedRecordData.toString())
      }
    }

  }
}

