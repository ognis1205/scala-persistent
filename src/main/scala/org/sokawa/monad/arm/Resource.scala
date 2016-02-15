/*
 * Copyright (C) 2016- Shingo OKAWA.
 */
package org.sokawa.monad.arm


trait Resource[+A] {
  def apply[B](fetch: A => B): B

  def acquire[B](fetch: A => B): B

  def tryToAcquire[B](fetch: A => B): Either[List[Throwable], B]

  def map[B](f: A => B): NullableResource[B]

  def flatMap[B](f: A => Resource[B]): Resource[B]

  def foreach(f: A => Unit): Unit

  def product[B](that: Resource[B]): Resource[(A, B)]
}
