package org.hablapps.puretest

import cats.MonadError

trait SimpleSpec[P[_], E] {

  implicit val ME: MonadError[P, E]
  implicit val Fi: Filter[P]

}
