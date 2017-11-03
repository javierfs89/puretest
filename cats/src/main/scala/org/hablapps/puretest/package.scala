package org.hablapps

package object puretest
  extends StateValidatedMonad
  with MonadErrorUtils
  with TestProgramModule {

  type Location = (sourcecode.File, sourcecode.Line)

  implicit def loc(implicit f: sourcecode.File, l: sourcecode.Line) = (f, l)

  /* matchers and ops */

  import cats.MonadError

  // implicit def toPureMatchers[P[_], E, A](self: TestProgram[P, E, A])(implicit
  //   M: MonadError[TestProgram[P, E, ?], E],
  //   loc: Location) = new PureMatchers2(self)

  implicit def toBooleanOps[P[_]](p: P[Boolean]) =
    new BooleanOps(p)

}
