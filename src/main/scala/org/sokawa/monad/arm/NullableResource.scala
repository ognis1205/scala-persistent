/*
 * Copyright (C) 2016- Shingo OKAWA.
 */
package org.sokawa.monad.arm


trait NullableResource[+A] extends Resource[A] {
  def opt: Option[A]

  def either: Either[Seq[Throwable], A]
}
