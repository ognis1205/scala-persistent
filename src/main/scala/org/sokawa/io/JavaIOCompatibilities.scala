/*
 * Copyright (C) 2016- Shingo OKAWA.
 */
package org.sokawa.io

import java.io.{ File => JavaFile, _ }
import scala.annotation.tailrec
import scala.io.{ BufferedSource, Codec, Source }
import scala.util.control.NonFatal
import org.sokawa.monad.arm._


trait JavaIOCompatibilities {
  implicit class ExtensionOfInputStream(inputStream: InputStream) {

    def >(outputStream: OutputStream): Unit = pipe(outputStream, true, 1 << 10)

    def pipe(outputStream: OutputStream, closeOutputStream: Boolean, bufferSize: Int): Unit = pipe(outputStream, closeOutputStream, Array.ofDim[Byte](bufferSize))

    @tailrec final def pipe(outputStream: OutputStream, closeOutputStream: Boolean = true, buffer: Array[Byte]): Unit = inputStream.read(buffer) match {
      case size if size > 0 =>
        outputStream.write(buffer, 0, size)
        pipe(outputStream, closeOutputStream, buffer)
      case _                =>
        inputStream.close()
        if (closeOutputStream) outputStream.close()
    }

    def buffered: BufferedInputStream = new BufferedInputStream(inputStream)

    def reader(implicit codec: Codec): InputStreamReader = new InputStreamReader(inputStream, codec.name)

    def content(implicit codec: Codec): BufferedSource = Source.fromInputStream(inputStream)(codec)

    def lines(implicit codec: Codec): Iterator[String] = content(codec).getLines()
  }

  implicit class ExtensionOfOutputStream(outputStream: OutputStream) {
    def buffered: BufferedOutputStream = new BufferedOutputStream(outputStream)

    def writer(implicit codec: Codec): OutputStreamWriter = new OutputStreamWriter(outputStream, codec.name)

    def printer(autoFlash: Boolean = false): PrintWriter = new PrintWriter(outputStream, autoFlash)
  }

  implicit class ExtensionOfReader(reader: Reader) {
    def buffered: BufferedReader = new BufferedReader(reader)
  }

  implicit class ExtensionOfWriter(writer: Writer) {
    def buffered: BufferedWriter = new BufferedWriter(writer)
  }
}
