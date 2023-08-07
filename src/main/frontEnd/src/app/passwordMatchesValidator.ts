import {AbstractControl, ValidationErrors, ValidatorFn} from "@angular/forms";

export const passwordMatchesValidator: ValidatorFn = (
  control: AbstractControl
): ValidationErrors | null => {
  const password = control.get('password')?.value;
  const passwordAgain = control.get('passwordAgain')?.value;

  return password && passwordAgain && password === passwordAgain
    ? null
    : { passwordMatch: true };
}
