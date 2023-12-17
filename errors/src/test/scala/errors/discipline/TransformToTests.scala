package errors.discipline

import cats.{Applicative, Eq, Monad}
import errors.{Raise, TransformTo, catsLawsIsEqToProp}
import errors.laws.TransformToLaws
import org.scalacheck.{Arbitrary, Prop}
import org.scalacheck.Prop.forAll
import org.typelevel.discipline.Laws

trait TransformToTests[F[_], G[_], E1, E2] extends HandleToTests[F, G, E1] with RaiseTests[G, E2] {

  def laws: TransformToLaws[F, G, E1, E2]

  def transformTo[A](implicit
    ApplicativeF: Applicative[F],
    MonadG: Monad[G],
    EqFA: Eq[F[A]],
    EqGA: Eq[G[A]],
    EqGOptionA: Eq[G[Option[A]]],
    EqGEitherE1A: Eq[G[Either[E1, A]]],
    ArbitraryA: Arbitrary[A],
    ArbitraryE1: Arbitrary[E1],
    ArbitraryE2: Arbitrary[E2],
    ArbitraryAToGA: Arbitrary[A => G[A]],
    ArbitraryE1ToGA: Arbitrary[E1 => G[A]],
    ArbitraryE1ToA: Arbitrary[E1 => A],
    ArbitraryE1ToE2: Arbitrary[E1 => E2],
    RaiseFE1: Raise[F, E1]
  ): RuleSet = {
    new RuleSet {
      override def name: String = "TransformTo"
      override def bases: Seq[(String, Laws#RuleSet)] = Nil
      override def parents: Seq[RuleSet] = Seq(raise[A], handleTo[A])
      override def props: Seq[(String, Prop)] = Seq(
        "transform f . pure a = pure a" -> forAll(laws.pureTransform[A] _),
        "transform f . raise e = raise (f e)" -> forAll(laws.raiseTransform[A] _),
      )
    }
  }

}

object TransformToTests {
  def apply[F[_], G[_], E1, E2](implicit ev: TransformTo[F, G, E1, E2]): TransformToTests[F, G, E1, E2] =
    new TransformToTests[F, G, E1, E2] { def laws: TransformToLaws[F, G, E1, E2] = TransformToLaws[F, G, E1, E2] }
}