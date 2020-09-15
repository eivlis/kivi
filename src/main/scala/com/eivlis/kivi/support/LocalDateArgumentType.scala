package com.eivlis.kivi.support

import java.time.LocalDateTime

import net.sourceforge.argparse4j.inf.{Argument, ArgumentParser, ArgumentType}

class LocalDateArgumentType extends ArgumentType[LocalDateTime]{

  override def convert(parser: ArgumentParser, arg: Argument, value: String): LocalDateTime = {
     LocalDateTime.parse(value)
  }
}
