import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { expect } from '@jest/globals';
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { BrowserAnimationsModule }   from '@angular/platform-browser/animations';
import { Router }                    from '@angular/router';
import { of, throwError }            from 'rxjs';

import { RegisterComponent }         from './register.component';
import { AuthService }               from '../../services/auth.service';

describe('RegisterComponent', () => {
  let fixture: ComponentFixture<RegisterComponent>;
  let component: RegisterComponent;
  let authServiceMock: Partial<AuthService>;
  let routerStub: Partial<Router>;

  beforeEach(async () => {
    authServiceMock = {
      register: jest.fn()
    };
    routerStub = {
      navigate: jest.fn()
    };

    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      imports: [
        ReactiveFormsModule,
        BrowserAnimationsModule
      ],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerStub }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  const newUserData = {
    email: 'jacksparrow@gmail.com',
    firstName: 'Jack',
    lastName: 'Sparrow',
    password: 'jacksparrow',
  };

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should unvalidate informations if form is empty', () => {
    expect(component.form.valid).toBe(false);
    expect(component.form.controls.email.hasError('required')).toBe(true);
    expect(component.form.controls.firstName.hasError('required')).toBe(true);
    expect(component.form.controls.lastName.hasError('required')).toBe(true);
    expect(component.form.controls.password.hasError('required')).toBe(true);
  });

  it('should validate informations if form is completed', () => {
    component.form.setValue({
      email: newUserData.email,
      firstName: newUserData.firstName,
      lastName: newUserData.lastName,
      password: newUserData.password
    });
    expect(component.form.valid).toBe(true);
  });

  it('should success on register and navigate to login page', fakeAsync(() => {
    // arrange
    (authServiceMock.register as jest.Mock).mockReturnValue(of(void 0));
    component.form.setValue({
      email: newUserData.email,
      firstName: newUserData.firstName,
      lastName: newUserData.lastName,
      password: newUserData.password
    });

    // act
    component.submit();
    tick();

    // assert
    expect(authServiceMock.register).toHaveBeenCalledWith({
      email: newUserData.email,
      firstName: newUserData.firstName,
      lastName: newUserData.lastName,
      password: newUserData.password
    });
    expect(routerStub.navigate).toHaveBeenCalledWith(['/login']);
    expect(component.onError).toBe(false);
  }));

  it('should indicate an error if the email does not contain and "@"', fakeAsync(() => {
    (authServiceMock.register as jest.Mock)
      .mockReturnValue(throwError(() => new Error('wrong email format')));
    component.form.setValue({
      email:     'userexample.com', // email sans '@'
      firstName: 'user',
      lastName:  'exemple',
      password:  'password'
    });

    component.submit();
    tick();

    expect(component.onError).toBe(true);

    const emailValue = component.form.get('email')!.value;
    expect(emailValue).not.toContain('@');
  }));
});
