import { Directive, ElementRef } from '@angular/core';

@Directive({
  selector: '[appAutofocusInvalid]'
})
export class AutofocusDirective {
  constructor(private el: ElementRef) {}

  focus() {
    this.el.nativeElement.focus();
  }
}
