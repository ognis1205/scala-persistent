/*
 * Copyright (C) 2016- Shingo OKAWA.
 */
package org.sokawa.io

import java.io.{ BufferedWriter => JavaBufferedWriter }
import scala.io.{ BufferedSource, Codec }
import org.sokawa.monad.arm._


abstract class File (val path: String, val delimiter: String = "/") {
  def name: String

  def extension: Option[String]

  def hasExtension: Boolean

  def isRegular: Boolean

  def isEmpty: Boolean

  def notExists: Boolean

  def delete: File

  def bufferedWriter(implicit codec: Codec): Resource[JavaBufferedWriter]

  def bufferedSource(implicit codec: Codec): Resource[BufferedSource]
}
