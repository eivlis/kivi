package com.eivlis.kivi.command

import java.nio.charset.StandardCharsets
import java.time.LocalDateTime

import com.amazonaws.services.kinesis.AmazonKinesis
import com.amazonaws.services.kinesis.clientlibrary.types.UserRecord
import com.amazonaws.services.kinesis.model.{GetRecordsRequest, GetRecordsResult, GetShardIteratorRequest, ShardIteratorType}
import com.eivlis.kivi.support.LocalDateArgumentType
import com.eivlis.kivi.support.utils.{ArgumentImprovement, DateImprovement, GetRecordsResultImprovement, LocalDateTimeImprovement}
import com.eivlis.kivi.{Args, Console}
import net.sourceforge.argparse4j.inf.Subparser

import scala.util.control.Breaks.break

class ScanStreamCommand extends Command {
  override def name(): String = "scan"

  override def configureArgumentParser(parser: Subparser): Unit = {
    parser.addStreamArgument()
    parser.addShardArgument()
    parser.addArgument("--from")
      .required(true)
      .`type`(new LocalDateArgumentType)

    parser.addArgument("--to")
      .required(true)
      .`type`(new LocalDateArgumentType)
  }

  override def execute(kinesis: AmazonKinesis, args: Args, console: Console): Unit = {
    val streamName = args.getStreamName()
    val shardId = args.getShardId()

    val from = args.getArg[LocalDateTime]("from")
    val to = args.getArg[LocalDateTime]("to")

    if (from.isAfter(to)) {
      console.printError("'to' must be before 'from'")
      System.exit(1)
    }

    val fromTimestamp = from.toDate()

    val getShardIteratorResult = kinesis.getShardIterator(new GetShardIteratorRequest()
      .withStreamName(streamName)
      .withShardId(shardId)
      .withShardIteratorType(ShardIteratorType.AT_TIMESTAMP)
      .withTimestamp(fromTimestamp))


    var shardIterator = getShardIteratorResult.getShardIterator

    while (true) {
      val getRecordsResult = task()
      if (shouldHaltConsumption(getRecordsResult, to)) {
        break
      }
      Thread.sleep(1000)
    }

    def task(): GetRecordsResult = {
      val getRecordsResult = kinesis.getRecords(new GetRecordsRequest()
        .withShardIterator(shardIterator))
      shardIterator = getRecordsResult.getNextShardIterator

      val userRecords = UserRecord.deaggregate(getRecordsResult.getRecords)

      userRecords.forEach { userRecord =>
        val decodedRecordData = StandardCharsets.UTF_8.decode(userRecord.getData)
        console.println(decodedRecordData.toString())
      }
      getRecordsResult
    }

    def shouldHaltConsumption(result: GetRecordsResult, to: LocalDateTime): Boolean = {
        val latestArrivalInResults = result.last().getApproximateArrivalTimestamp.toLocalDateTime()
        result.getMillisBehindLatest == 0L || latestArrivalInResults.isAfter(to)
    }

  }
}
