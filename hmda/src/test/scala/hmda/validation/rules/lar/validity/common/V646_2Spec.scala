package hmda.validation.rules.lar.validity

import hmda.model.filing.lar.LarGenerators._
import hmda.model.filing.lar.LoanApplicationRegister
import hmda.model.filing.lar.enums._
import hmda.validation.rules.EditCheck
import hmda.validation.rules.lar.LarEditCheckSpec

class V646_2Spec extends LarEditCheckSpec {
  override def check: EditCheck[LoanApplicationRegister] = V646_2

  property("Co-Applicant sex observed value must be valid") {
    forAll(larGen) { lar =>
      lar.mustPass
      val invalidLar = lar.copy(
        coApplicant = lar.coApplicant.copy(sex =
          lar.coApplicant.sex.copy(sexObservedEnum = new InvalidSexObservedCode)))
      invalidLar.mustFail
    }
  }
}
