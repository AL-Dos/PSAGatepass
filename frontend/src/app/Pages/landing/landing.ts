import { Component } from '@angular/core';
import { Login } from '../../Components/login/login';
import { Form } from '../../Components/form/form';
import { MatCard } from "@angular/material/card";

@Component({
  selector: 'app-landing',
  imports: [Login, Form, MatCard],
  templateUrl: './landing.html',
  styleUrl: './landing.css',
})
export class Landing {

}
