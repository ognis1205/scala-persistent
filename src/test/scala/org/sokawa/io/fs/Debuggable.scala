/*
 * Copyright (C) 2016- Shingo OKAWA.
 */

trait Debuggable {
  def debug(testee: String, enable: Boolean = false): Unit = enable match {
    case debugging if debugging == true => Console.println("[DEBUG] " + testee)
    case _                              => Unit
  }
}
