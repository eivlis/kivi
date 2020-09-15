package com.eivlis.kivi.command

import com.amazonaws.services.kinesis.AmazonKinesis
import com.eivlis.kivi.{Args, Console}
import net.sourceforge.argparse4j.inf.Subparser

trait Command {

  def name(): String

  def configureArgumentParser(parser: Subparser)

  def execute(kinesis: AmazonKinesis, args: Args, console: Console)

}
