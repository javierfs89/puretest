// package org.hablapps.puretest


// trait PureMatchersTC[P[_], E] {

//   protected def shouldFail[A](pa: P[A])(p: E => Boolean)(
//     errorIfSuccess: A => PuretestError[E],
//     errorIfFailure: E => PuretestError[E]): P[Unit]

//   protected def shouldSucceed[A](pa: P[A])(p: A => Boolean)(
//     errorIfSuccess: A => PuretestError[E],
//     errorIfFailure: E => PuretestError[E]): P[A]

// }

// object PureMatchersTC {
//   object syntax extends Syntax
//   trait Syntax {
//     implicit class PureMatchersOps[P[_], E, A](self: P[A])(implicit PM: PureMatchersTC[P, E]) {
//       // Failure

//       def shouldMatchFailure(p: E => Boolean): P[Unit] =
//         PM.shouldFail(self)(p)(NotFailed(_), NotMatchedFailure(_))

//       def shouldFail: P[Unit] =
//         PM.shouldFail(self)(_ => true)(NotFailed(_), _ => ShouldNotHappen())

//       def shouldFailWith(e: E): P[Unit] =
//         PM.shouldFail(self)(_ == e)(NotError(_, e), OtherError(_, e))

//       // Success

//       def shouldMatch(p: A => Boolean): P[A] =
//         PM.shouldSucceed(self)(p)(NotMatched(_), NotSucceeded(_))

//       def shouldBe(a: A): P[A] =
//         PM.shouldSucceed(self)(_ == a)(NotEqualTo(_, a), NotValue(_, a))

//       def shouldSucceed: P[A] =
//         PM.shouldSucceed(self)(_ => true)(_ => ShouldNotHappen(), NotSucceeded(_))
//     }
//   }
// }
