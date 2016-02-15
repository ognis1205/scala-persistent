/*
 * Copyright (C) 2016- Shingo OKAWA.
 */
package org.sokawa

import java.lang.{ VirtualMachineError => JavaVirtualMachineError }
import java.nio.file.LinkOption
import java.nio.file.StandardCopyOption
import scala.util.control.ControlThrowable


package object io extends JavaIOCompatibilities {
  @inline private[io] def when[A](condition: Boolean)(f: => A): Option[A] = if (condition) Some(f) else None

  @inline private[io] def returning[A](instance: A)(f: => Unit): A = {f; instance}
}
