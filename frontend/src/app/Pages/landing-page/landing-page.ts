import { Component } from '@angular/core';
import { Login } from "../../Components/login/login";
import { AuditForm } from "../../Components/audit-form/audit-form";
import { AutofocusDirective } from '../../Directive/autofocus.directive';
@Component({
  selector: 'app-landing-page',
  imports: [Login, AuditForm],
  templateUrl: './landing-page.html',
  styleUrl: './landing-page.css'
})
export class LandingPage {

}
