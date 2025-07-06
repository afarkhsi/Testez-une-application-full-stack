import { Component } from '@angular/core';
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ReactiveFormsModule }       from '@angular/forms';
import { Router }                    from '@angular/router';
import { RouterTestingModule }       from '@angular/router/testing';

import { LoginComponent }            from './login.component';
import { AuthService }               from '../../services/auth.service';
import { SessionService }            from 'src/app/services/session.service';
import { SessionInformation }        from 'src/app/interfaces/sessionInformation.interface';
import { expect } from '@jest/globals';

@Component({ template: '' }) // composant vide utilisé pour la navigation simulée
class DummyComponent {}

describe('LoginComponent', () => {
  let fixture: ComponentFixture<LoginComponent>;
  let component: LoginComponent;
  let httpMock: HttpTestingController;
  let sessionService: SessionService;
  let router: Router;

  const fakeSessionInfo: SessionInformation = {
    id: 42, 
    admin: false, 
    token: 'jwt', 
    type: 'session', 
    username: 'jacksparrow@gmail.com',
    firstName: 'Jack', 
    lastName: 'Sparow'
  };

  beforeEach(async () => {
      await TestBed.configureTestingModule({
      declarations: [LoginComponent, DummyComponent],
      imports: [
        ReactiveFormsModule,
        HttpClientTestingModule,
        RouterTestingModule.withRoutes([
          { path: 'sessions', component: DummyComponent }
        ]),
      ],
      providers: [AuthService, SessionService]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    sessionService = TestBed.inject(SessionService);
    router = TestBed.inject(Router);
    httpMock = TestBed.inject(HttpTestingController);

    fixture.detectChanges();
  });

  afterEach(() => {
    // Vérification des requêtes HTTP restantes
    httpMock.verify();
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should unvalidate informations if form is not completed', () => {
    expect(component.form.valid).toBe(false);
    expect(component.form.controls.email.hasError('required')).toBe(true);
    expect(component.form.controls.password.hasError('required')).toBe(true);
  });

  it('should validate informations if form is completed', () => {
    component.form.setValue({
      email:    'test@example.com',
      password: '12345'
    });
    expect(component.form.valid).toBe(true);
  });

  it('should success on submit', fakeAsync(() => {
    const navSpy = jest.spyOn(router, 'navigate');
    const sessionSpy = jest.spyOn(sessionService, 'logIn');

    const expectedData = {
      email: 'jacksparrow@gmail.com',
      password: 'jacksparrow',
    };


    component.form.setValue({
      email: expectedData.email,
      password: expectedData.password
    });

    component.submit();
    tick();

    const req = httpMock.expectOne('api/auth/login');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({
      email: expectedData.email,
      password: expectedData.password
    });

    req.flush(fakeSessionInfo);
    tick();
 
    expect(sessionSpy).toHaveBeenCalledWith(fakeSessionInfo);
    expect(navSpy).toHaveBeenCalledWith(['/sessions']);
    expect(component.onError).toBe(false);
  }));

  it('should indicate an error if credentials are invalid', fakeAsync(() => {
    const formData = {
      email:    'wrong@user.com',
      password: 'badpassword'
    };

    component.form.setValue({
      email: formData.email,
      password: formData.password
    });

    component.submit();
    tick();

    const req = httpMock.expectOne('api/auth/login');
    req.flush({ message: 'Auth failed' }, { status: 401, statusText: 'Unauthorized' });

    tick();
    expect(component.onError).toBe(true);
  }));
});