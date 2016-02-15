/*
 * Copyright (C) 2016- Shingo OKAWA.
 */
package org.sokawa.io

import java.io.{ BufferedWriter => JavaBufferedWriter }
import scala.io.{ BufferedSource, Codec }
import org.sokawa.monad.arm._


trait FileOperations extends File {
  override def name: String = path.split(delimiter).reverse(0)

  override def extension: Option[String] = when(hasExtension) (name.substring(name lastIndexOf ".", name.size).toLowerCase)

  override def hasExtension: Boolean = (isRegular || notExists) && (name contains ".")

  def newBufferedWriter(implicit codec: Codec): JavaBufferedWriter

  override def bufferedWriter(implicit codec: Codec): Resource[JavaBufferedWriter] = IOMonad(newBufferedWriter(codec))

  def newBufferedSource(implicit codec: Codec): BufferedSource

  override def bufferedSource(implicit codec: Codec): Resource[BufferedSource] = IOMonad(newBufferedSource(codec))
}
