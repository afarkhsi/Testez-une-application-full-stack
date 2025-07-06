import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { Router } from '@angular/router';

import { MeComponent } from './me.component';
import { UserService } from '../../services/user.service';
import { SessionService } from '../../services/session.service';
import { User } from '../../interfaces/user.interface';
import { expect } from '@jest/globals';


describe('MeComponent - Integration', () => {
  let fixture: ComponentFixture<MeComponent>;
  let component: MeComponent;
  let httpMock: HttpTestingController;
  let sessionService: SessionService;
  let router: Router;

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
  open: jest.fn(),
} as unknown as MatSnackBar;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        MatSnackBarModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
      ],
      declarations: [MeComponent],
      providers: [
        UserService,
        { provide: SessionService, useValue: sessionServiceMock },
        { provide: Router, useValue: routerStub },
        { provide: MatSnackBar, useValue: fakeMatSnackBar },
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    sessionService = TestBed.inject(SessionService);
    router = TestBed.inject(Router);
  });

  afterEach(() => {
    httpMock.verify(); // s'assure qu'aucune requête en attente
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch user from API and assign it to component', () => {
    fixture.detectChanges();

    const req = httpMock.expectOne('api/user/8');
    expect(req.request.method).toBe('GET');
    req.flush(fakeUser);

    expect(component.user).toEqual(fakeUser);
  });

  it('should display user informations', fakeAsync(() => {
    fixture.detectChanges();

    const req = httpMock.expectOne('api/user/8');
    req.flush(fakeUser);
    tick();
    fixture.detectChanges();

    const el = fixture.nativeElement as HTMLElement;
    const content = el.querySelector('mat-card-content')!;
    const text = content.textContent!;

    expect(text).toContain('Name: Fake USER');
    expect(text).toContain('Email: fake@user.com');
    expect(text).toContain('Delete my account:');
    expect(content.querySelector('button[color="warn"]')).toBeTruthy();
    expect(text).toMatch(/Create at:/);
    expect(text).toMatch(/Last update:/);
  }));

  it('should display admin informations if user is admin', fakeAsync(() => {
    fixture.detectChanges();

    const req = httpMock.expectOne('api/user/8');
    req.flush(fakeAdmin);
    tick();
    fixture.detectChanges();

    const el = fixture.nativeElement as HTMLElement;
    const content = el.querySelector('mat-card-content')!;
    const text = content.textContent!;

    expect(text).toContain('You are admin');
  }));

  it('should go back to /sessions', () => {
    const spy = jest.spyOn(window.history, 'back');
    component.back();
    expect(spy).toHaveBeenCalled();
    spy.mockRestore();
  });

 it('should delete the user and redirect to root', fakeAsync(() => {
  fixture.detectChanges();
  // Simule la récupération de l'utilisateur
  const getReq = httpMock.expectOne('api/user/8');
  expect(getReq.request.method).toBe('GET');
  getReq.flush(fakeUser);
  tick();
  fixture.detectChanges();
  // Spy sur les méthodes du service et du router
  component.delete();

  // Vérifie que la requête de suppression est bien envoyée
  const deleteReq = httpMock.expectOne('api/user/8');
  expect(deleteReq.request.method).toBe('DELETE');
  deleteReq.flush(null);
  tick();
  fixture.detectChanges();

  // Vérifie que le message de succès est affiché
  expect(fakeMatSnackBar.open).toHaveBeenCalledWith(
    'Your account has been deleted !',
    'Close',
    { duration: 3000 }
  );
  expect(sessionServiceMock.logOut).toHaveBeenCalled();
  expect(routerStub.navigate).toHaveBeenCalledWith(['/']);

  // Vérifie qu'il n'y a plus de requêtes en attente
  httpMock.verify();
}));
});
