import { Component } from '@angular/core';
import { Login } from '../../Components/login/login';
import { Form } from '../../Components/form/form';

@Component({
  selector: 'app-landing',
  imports: [Login, Form],
  templateUrl: './landing.html',
  styleUrl: './landing.css',
})
export class Landing {

}
