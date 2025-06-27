import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatCardModule }   from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule }   from '@angular/material/icon';
import { MatInputModule }  from '@angular/material/input';
import { Router }          from '@angular/router';
import { of } from 'rxjs';

import { MeComponent }     from './me.component';
import { UserService }     from '../../services/user.service';
import { SessionService }  from '../../services/session.service';
import { User }            from '../../interfaces/user.interface';
import { expect } from '@jest/globals';

describe('MeComponent', () => {
  let fixture: ComponentFixture<MeComponent>;
  let component: MeComponent;
  let sessionService: SessionService;
  let router: Router;
  let userService: jest.Mocked<Partial<UserService>>;

  const fakeUser: User = {
    id: 8,
    email: 'fake@user.com',
    password: '',
    firstName: 'Fake',
    lastName: 'User',
    admin: false,
    createdAt: new Date('2025-06-26'),
    updatedAt: new Date('2025-07-06'),
  };

  const fakeAdmin: User = { ...fakeUser, admin: true };

  const sessionServiceMock = {
    sessionInformation: {
      id: 8,
      admin: false,
      token: '',
      type: '',
      username: '',
      firstName: '',
      lastName: ''
    },
    logOut: jest.fn(),
  };

  const routerStub = { navigate: jest.fn() };
 
  const fakeMatSnackBar = {
    open: (message: string, action?: string | undefined, config?: any) => {},
  } as MatSnackBar;

  beforeEach(async () => {
     userService = {
      getById: jest.fn(() => of(fakeUser)),
      delete:  jest.fn(() => of(null))
    };
    await TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        MatSnackBarModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule
      ],
      declarations: [MeComponent],
      providers: [
        { provide: SessionService, useValue: sessionServiceMock },
        { provide: Router, useValue: routerStub },
        { provide: MatSnackBar, useValue: fakeMatSnackBar },
        { provide: UserService, useValue: userService },
      ]
    }).compileComponents();

    sessionService = TestBed.inject(SessionService);
    router = TestBed.inject(Router);
    userService = TestBed.inject(UserService);
    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should call userService.getById and fill user', () => {
    fixture.detectChanges();
    expect(userService.getById).toHaveBeenCalledWith('8');
    expect(component.user).toEqual(fakeUser);
  });

  it('should display user informations inside the me component', fakeAsync(() => {
    fixture.detectChanges(); 
    expect(component.user).toEqual(fakeUser);

    const el = fixture.nativeElement as HTMLElement;
    const content = el.querySelector('mat-card-content')!;
    const text = content.textContent!;

    expect(text).toContain('Name: Fake USER');
    expect(text).toContain('Email: fake@user.com');
    expect(text).toContain('Delete my account:');
    expect(content.querySelector('button[color="warn"]')).toBeTruthy();

    expect(text).toContain('2025');
    expect(text).toMatch(/Create at:/);
    expect(text).toMatch(/Last update:/);
  }));

  it('should display admin informations inside the me component', fakeAsync(() => {
    (userService.getById as jest.Mock).mockReturnValue(of(fakeAdmin));
    fixture.detectChanges(); 
    expect(component.user).toEqual(fakeAdmin);

    const el = fixture.nativeElement as HTMLElement;
    const content = el.querySelector('mat-card-content')!;
    const text = content.textContent!;

    expect(text).toContain('Name: Fake USER');
    expect(text).toContain('Email: fake@user.com');
    expect(text).toContain('You are admin');

    expect(text).toContain('2025');
    expect(text).toMatch(/Create at:/);
    expect(text).toMatch(/Last update:/);
  }));

  it('should go back to /sessions', () => {
    const spy = jest.spyOn(window.history, 'back');
    component.back();
    expect(spy).toHaveBeenCalled();
    spy.mockRestore();
  });

  it('should delete the fakeUser, display snackbar,logout and go to /', fakeAsync(() => {
    (userService.getById as jest.Mock).mockReturnValue(of(fakeUser));
    fixture.detectChanges(); 
    expect(component.user).toEqual(fakeUser);

    const deleteSpy = jest.spyOn(userService, 'delete');
    const snackSpy  = jest.spyOn(fakeMatSnackBar, 'open');
    const logOutSpy = jest.spyOn(sessionServiceMock, 'logOut');
    const navSpy    = jest.spyOn(routerStub, 'navigate');

    component.delete();
    tick();
    fixture.detectChanges();

    expect(deleteSpy).toHaveBeenCalledWith('8');
    expect(snackSpy).toHaveBeenCalledWith(
      'Your account has been deleted !',
      'Close',
      { duration: 3000 }
    );
    expect(logOutSpy).toHaveBeenCalled();
    expect(navSpy).toHaveBeenCalledWith(['/']);
  }));
});
