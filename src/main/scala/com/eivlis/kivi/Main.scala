package com.eivlis.kivi

import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.eivlis.kivi.command.{Command, ScanStreamCommand, TailStreamCommand}
import net.sourceforge.argparse4j.ArgumentParsers
import net.sourceforge.argparse4j.impl.Arguments
import net.sourceforge.argparse4j.inf.{ArgumentParser, Namespace}
import java.io.PrintStream

import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder


object Main extends App {

  val COMMANDS: List[Command] = List(new TailStreamCommand(), new ScanStreamCommand())

  val argumentParser: ArgumentParser = ArgumentParsers.newArgumentParser("kivi")
    .description("Kinesis stream viewer")

  argumentParser.version(getVersion())
  argumentParser.addArgument("-v", "--version").action(Arguments.version())

  argumentParser.addArgument("-p", "--profile")
    .required(true)
    .help("The AWS credential profile to use")

  argumentParser.addArgument("-r", "--region")
    .setDefault("eu-west-2")
    .help("The AWS region to use")

  initCommandSpecificArgs(argumentParser)

  val namespace = argumentParser.parseArgs(args)

  val progArgs = Args(namespace)

  val profileName = progArgs.getProfile()
  val region = progArgs.getRegion()
  val kinesis = AmazonKinesisClientBuilder.standard()
    .withCredentials(new ProfileCredentialsProvider(profileName))
    .withRegion(region)
    .build()

  val console = Console(System.out, System.err)

  val commandToExecute: Option[Command] = COMMANDS.find {
    _.name() == progArgs.getCommand()
  }

  commandToExecute match {
    case Some(command) => command.execute(kinesis, progArgs, console)
    case None =>  console.printError("command not found! :-O")
  }


  private def getVersion() = getClass.getPackage.getImplementationVersion

  private def initCommandSpecificArgs(argumentParser: ArgumentParser) {
    val subparsers = argumentParser.addSubparsers()
    COMMANDS.foreach { command =>
      val subparser = subparsers.addParser(command.name())
        .setDefault("command", command.name())
      command.configureArgumentParser(subparser)
    }
  }

}

case class Args(val namespace: Namespace) {
  def getCommand(): String = namespace.getString("command")

  def getProfile(): String = namespace.getString("profile")

  def getRegion(): String = namespace.getString("region")

  def getStreamName(): String = namespace.getString("stream")

  def getShardId(): String = namespace.getString("shard")

  def getArg[T](argKey: String): T = namespace.get(argKey)

}

case class Console(val out: PrintStream, error: PrintStream) {

  def printError(message: String) {
    error.println("ERROR: $message")
  }

  def println(message: String) {
    out.println(message)
  }
}
