// package org.hablapps.puretest

// import cats.{Monad, MonadError}
// import cats.syntax.all._

// class PureMatchers2[P[_], E, A](self: TestProgram[P, E, A])(implicit
//   ME: MonadError[TestProgram[P, E, ?], E], loc: Location) {

//   type TP[X] = TestProgram[P, E, X]

//   // Basic

//   private def shouldFail(p: E => Boolean)(
//       errorIfSuccess: A => PuretestError[E],
//       errorIfFailure: E => PuretestError[E]): TP[Unit] =
//     ME.handleError(
//       ME.flatMap(self) { a: A =>
//         Left(errorIfSuccess(a))
//       }) {
//         case error =>
//           if (p(error)) ().pure[TP].right
//           else Left(errorIfFailure(error))
//       }

//   private def shouldSucceed(p: A => Boolean)(
//       errorIfSuccess: A => PuretestError[E],
//       errorIfFailure: E => PuretestError[E]): TP[A] =
//     ME.flatMap {
//       ME.handleErrorWith(self) {
//         case error =>
//           Left(errorIfFailure(error))
//       }
//     } { a =>
//       if (p(a)) ME.pure(a)
//       else Left(errorIfSuccess(a))
//     }

//   // Failure

//   def shouldMatchFailure(p: E => Boolean): TP[Unit] =
//     shouldFail(p)(NotFailed(_), NotMatchedFailure(_))

//   def shouldFail: TP[Unit] =
//     shouldFail(_ => true)(NotFailed(_), _ => ShouldNotHappen())

//   def shouldFailWith(e: E): TP[Unit] =
//     shouldFail(_ == e)(NotError(_, e), OtherError(_, e))

//   // Success

//   def shouldMatch(p: A => Boolean): TP[A] =
//     shouldSucceed(p)(NotMatched(_), NotSucceeded(_))

//   def shouldBe(a: A): TP[A] =
//     shouldSucceed(_ == a)(NotEqualTo(_, a), NotValue(_, a))

//   def shouldSucceed: TP[A] =
//     shouldSucceed(_ => true)(_ => ShouldNotHappen(), NotSucceeded(_))
// }
