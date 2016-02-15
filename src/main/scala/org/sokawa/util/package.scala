/*
 * Copyright (C) 2016- Shingo OKAWA.
 */
package org.sokawa

import java.lang.{ ClassLoader => JavaClassLoader }
import java.lang.reflect.{ Constructor, Modifier, Method }
import java.io.{ File => JavaFile }

package object util extends JavaClassCompatibilities {
  type ScalaClassLoader = JavaClassLoader

  @inline private[util] def when[A](condition: Boolean)(f: => A): Option[A] = if (condition) Some(f) else None

  @inline private[util] def repeat(until: Int)(f: => Unit): Unit = (1 to until).foreach(_ => f)
}
