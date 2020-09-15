package com.eivlis.kivi.support

import java.time.{LocalDateTime, ZoneId}
import java.util.Date

import com.amazonaws.services.kinesis.model.GetRecordsResult
import net.sourceforge.argparse4j.inf.{Argument, Subparser}

package object utils {

  implicit class ArgumentImprovement(parser: Subparser) {

    def addStreamArgument() = parser.addArgument("--stream")
      .required(true)
      .help("The stream to interact with")

    def addShardArgument() = parser.addArgument("--shard")
      .required(false)
      .setDefault("shardId-000000000000")
      .help("The shard to interact with")
  }

  implicit class LocalDateTimeImprovement(date: LocalDateTime) {

    def toDate() = Date
      .from(date.atZone(ZoneId.systemDefault())
        .toInstant())
  }

  implicit class DateImprovement(date: Date) {

    def toLocalDateTime() = date.toInstant.atZone(ZoneId.systemDefault())
      .toLocalDateTime
  }

  implicit class GetRecordsResultImprovement(result: GetRecordsResult) {

    def last() = {
      val records = result.getRecords
      records.get(records.size() - 1)
    }
  }


}


