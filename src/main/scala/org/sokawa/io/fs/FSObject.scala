/*
 * Copyright (C) 2016- Shingo OKAWA.
 */
package org.sokawa.io.fs

import java.io.{ File => JavaFile, BufferedWriter => JavaBufferedWriter }
import java.nio.file.{ Files => JavaFiles }
import java.nio.file.{ LinkOption => JavaLinkOption }
import java.nio.file.{ Path => JavaPath, Paths => JavaPaths }
import java.nio.file.{ StandardCopyOption => JavaStandardCopyOption }
import scala.collection.JavaConversions._
import scala.io.{ BufferedSource, Codec, Source }
import org.sokawa.io._
import org.sokawa.monad.arm._


sealed trait FSContexts {
  object LinkOptions {
    val Follow: LinkOptions   = Seq.empty
    val NoFollow: LinkOptions = Seq(JavaLinkOption.NOFOLLOW_LINKS)
    val Default: LinkOptions  = Follow
  }

  type LinkOptions = Seq[JavaLinkOption]

  implicit val DefaultLinkOptions = LinkOptions.Default

  object CopyOptions {
    def apply(overwrite: Boolean): CopyOptions = if (overwrite) JavaStandardCopyOption.REPLACE_EXISTING +: Default else Default
    val Default: CopyOptions = Seq.empty
  }

  type CopyOptions = Seq[JavaStandardCopyOption]

  implicit val DefaultCopyOptions = CopyOptions.Default

  sealed trait Type[Content] {
    def unapply(fsObject: FSObject): Option[Content]
  }

  object Type {
    case object RegularFile extends Type[BufferedSource] {
      override def unapply(fsObject: FSObject) = when(fsObject.isRegular) (fsObject.newBufferedSource)
    }
  }

  implicit val DefaultType = Type.RegularFile
}

final class FSObject(override val path: String, override val delimiter: String = "/") extends File(path, delimiter) with FileOperations {
  import FSObject._

  private def javaPath: JavaPath = JavaPaths.get(path)

  private def toJava: JavaFile = javaPath.toFile

  override def isRegular(): Boolean = isRegular(DefaultLinkOptions)
 
  def isRegular(linkOptions: LinkOptions): Boolean = JavaFiles.isRegularFile(javaPath, linkOptions: _*)

  override def isEmpty: Boolean = this match {
    case Type.RegularFile(content) => content.isEmpty
    case _                         => notExists
  }

  override def notExists() : Boolean = notExists(DefaultLinkOptions)

  def notExists(linkOptions: LinkOptions) : Boolean = JavaFiles.notExists(javaPath, linkOptions: _*)

  override def delete(): File = returning(this) {
    JavaFiles.delete(javaPath)
  }

  override def newBufferedWriter(implicit codec: Codec): JavaBufferedWriter = JavaFiles.newOutputStream(javaPath).writer.buffered

  override def newBufferedSource(implicit codec: Codec): BufferedSource = Source.fromFile(toJava)(codec)
}

object FSObject extends FSContexts {
  def apply(path: String): FSObject = new FSObject(JavaPaths.get(path).normalize.toAbsolutePath.toString)

  def apply(path: JavaPath): FSObject = new FSObject(path.normalize.toAbsolutePath.toString)
}
