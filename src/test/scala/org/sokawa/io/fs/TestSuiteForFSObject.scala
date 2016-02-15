/*
 * Copyright (C) 2016- Shingo OKAWA.
 */

import org.junit.Assert._
import org.junit.Before
import org.junit.After
import org.junit.Test
import org.scalatest.junit.AssertionsForJUnit
import org.sokawa.io._
import org.sokawa.io.fs._
import org.sokawa.util._


class TestSuiteForFSObject extends AssertionsForJUnit with Debuggable {
  val debugging: Boolean = true
  val fileExtension: String = ".txt"
  val fileName: String = "testForFSObject" + fileExtension
  val lines: Map[Int, String] = Map(
    0 -> "abcdefghijklmnopqrstuvwxyz",
    1 -> "1234567890",
    2 -> "あいうえお"
  )
  var file: File = _

  def getLine(index: Int) = lines get index match {
    case Some(result) => result
    case None         => ""
  }

  @Before def initialize() {
    file = FSObject(fileName)
    for (out <- file.bufferedWriter) {
      for (i <- 0 until 3) {
        var line = getLine(i)
        out.write(line, 0, line.size)
        out.newLine
      }
    }
  }

  @Test def verifyIO() {
    file.extension match {
      case Some(extension) => debug(extension, debugging); assertEquals(extension, fileExtension)
      case _               => debug("NO EXTENSION FOUND", debugging)
    }

    for (in <- file.bufferedSource) in.getLines.zipWithIndex.foreach {
      case (line, i) => debug(line, debugging); assertEquals(line, getLine(i))
    }
  }

  @After def terminate() {
    file.delete
  }
}
