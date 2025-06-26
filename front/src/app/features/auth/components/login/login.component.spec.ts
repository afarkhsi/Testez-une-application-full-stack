import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ReactiveFormsModule }       from '@angular/forms';
import { Router }                    from '@angular/router';
import { RouterTestingModule }       from '@angular/router/testing';
import { of, throwError }            from 'rxjs';

import { LoginComponent }            from './login.component';
import { AuthService }               from '../../services/auth.service';
import { SessionService }            from 'src/app/services/session.service';
import { SessionInformation }        from 'src/app/interfaces/sessionInformation.interface';
import { expect } from '@jest/globals';

describe('LoginComponent', () => {
  let fixture: ComponentFixture<LoginComponent>;
  let component: LoginComponent;

  // stubs
  let authServiceMock: Partial<AuthService>;
  let sessionServiceMock: Partial<SessionService>;
  const routerStub = { navigate: jest.fn() } as unknown as Router;

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
    authServiceMock = {
      login: jest.fn()
    };
    sessionServiceMock = {
      logIn: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        RouterTestingModule
      ],
      declarations: [LoginComponent],
      providers: [
        { provide: AuthService,    useValue: authServiceMock },
        { provide: SessionService, useValue: sessionServiceMock },
        { provide: Router,         useValue: routerStub }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
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

  it('should succes on submit', fakeAsync(() => {
    const loginSpy = jest
      .spyOn(authServiceMock, 'login' as any)
      .mockReturnValue(of(fakeSessionInfo));
    const sessionSpy = jest.spyOn(sessionServiceMock, 'logIn');
    const navSpy = jest.spyOn(routerStub, 'navigate');

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

    expect(loginSpy).toHaveBeenCalledWith({
      email: expectedData.email,
      password: expectedData.password
    });
    expect(sessionSpy).toHaveBeenCalledWith(fakeSessionInfo);
    expect(navSpy).toHaveBeenCalledWith(['/sessions']);
    expect(component.onError).toBe(false);
  }));

  it('should indicate an error if credentials are invalid', fakeAsync(() => {
    const loginSpy = jest
      .spyOn(authServiceMock, 'login')
      .mockReturnValue(throwError(() => new Error('Auth failed')));

    const logInSpy = jest.spyOn(sessionServiceMock, 'logIn');
    const navSpy   = jest.spyOn(routerStub, 'navigate');

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

    expect(loginSpy).toHaveBeenCalledWith(formData);
    expect(component.onError).toBe(true);
    expect(logInSpy).not.toHaveBeenCalled();
  }));
});