import { ComponentFixture, TestBed, fakeAsync, tick, flush } from '@angular/core/testing';
import { ReactiveFormsModule }       from '@angular/forms';
import { Router, ActivatedRoute }    from '@angular/router';
import { RouterTestingModule }       from '@angular/router/testing';
import { HttpClientTestingModule }   from '@angular/common/http/testing';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatCardModule }    from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule }    from '@angular/material/icon';
import { MatInputModule }   from '@angular/material/input';
import { MatSelectModule }  from '@angular/material/select';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { of, BehaviorSubject } from 'rxjs';

import { FormComponent }        from './form.component';
import { SessionApiService }    from '../../services/session-api.service';
import { SessionService }       from 'src/app/services/session.service';
import { TeacherService }       from 'src/app/services/teacher.service';
import { Session }              from '../../interfaces/session.interface';
import { SessionInformation }   from 'src/app/interfaces/sessionInformation.interface';
import { expect } from '@jest/globals';


describe('FormComponent', () => {
  let fixture: ComponentFixture<FormComponent>;
  let component: FormComponent;
  let router: Router;
  let snackBar: MatSnackBar;

  const activatedFakeRoute = {
    snapshot: { paramMap: { get: jest.fn().mockReturnValue('1') } }
  } as unknown as ActivatedRoute;


  const sessionInformation: SessionInformation = {
    admin: true,
    id: 69, 
    token: '',
    type: '', 
    username: '', 
    firstName: '', 
    lastName: ''
  };

  const fakeSession: Session = {
    id: 1,
    name: 'Session Test',
    description: 'Description de la session',
    date: new Date('2025-06-27'),
    teacher_id: 2,
    users: [],
    createdAt: new Date(),
    updatedAt: new Date()
  };

  const sessionServiceMock: Partial<SessionService> = {
    sessionInformation,
    isLogged: true,
    //@ts-ignore
    isLoggedSubject: new BehaviorSubject(true),
    $isLogged: () => of(true)
  };

  const sessionApiMock: Partial<SessionApiService> = {
    detail: jest.fn(() => of(fakeSession)),
    create: jest.fn(() => of(fakeSession)),
    update: jest.fn(() => of(fakeSession))
  };

  const teacherServiceMock: Partial<TeacherService> = {
    all: jest.fn(() => of([]))
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        NoopAnimationsModule,
        RouterTestingModule,
        HttpClientTestingModule,
        ReactiveFormsModule,
        MatSnackBarModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        MatSelectModule
      ],
      declarations: [ FormComponent ],
      providers: [
        { provide: ActivatedRoute,    useValue: activatedFakeRoute },
        { provide: Router,            useValue: { navigate: jest.fn(), url: '/sessions' } },
        { provide: SessionService,    useValue: sessionServiceMock },
        { provide: SessionApiService, useValue: sessionApiMock },
        { provide: TeacherService,    useValue: teacherServiceMock }
      ]
    }).compileComponents();

    router   = TestBed.inject(Router);
    snackBar = TestBed.inject(MatSnackBar);
    fixture  = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
  });

  it('Should create component', () => {
    expect(component).toBeTruthy();
  });


  describe('CREATE SESSION', () => {
    beforeEach(() => {
      Object.defineProperty(router, 'url', { get: () => '/sessions/create' });
      (activatedFakeRoute.snapshot!.paramMap!.get as jest.Mock).mockReturnValue(null);

      fixture.detectChanges(); 
    });

    it('should initialise an empty form', () => {
      expect(component.onUpdate).toBe(false);
      expect(component.sessionForm!.value).toEqual({
        name: '', date: '', teacher_id: '', description: ''
      });
    });

    it('should submit and create a session, after that, call snackBar and navigate ', fakeAsync(() => {
      const form = component.sessionForm!;
      form.setValue({
        name: 'Nouvelles session',
        date: '2025-08-01',
        teacher_id: 2,
        description: 'Test sessions'
      });

      const createSpy   = jest.spyOn(sessionApiMock, 'create');
      const snackSpy    = jest.spyOn(snackBar, 'open');
      const navSpy      = jest.spyOn(router, 'navigate');

      component.submit();
      tick();
      flush();  

      expect(createSpy).toHaveBeenCalledWith({
        name: 'Nouvelles session',
        date: '2025-08-01',
        teacher_id: 2,
        description: 'Test sessions'
      });
      expect(snackSpy).toHaveBeenCalledWith('Session created !', 'Close', { duration: 3000 });
      expect(navSpy).toHaveBeenCalledWith(['sessions']);
    }));
  });


  describe('UPDATE SESSION', () => {
    beforeEach(fakeAsync(() => {
      // Simuler l'update de la session 1"
      (activatedFakeRoute.snapshot!.paramMap!.get as jest.Mock).mockReturnValue('1');
      Object.defineProperty(router, 'url', { get: () => '/sessions/update/1' });
    

      fixture.detectChanges();
      tick();
    }));

    it('should fill the form with actual session values', () => {
      expect(sessionApiMock.detail).toHaveBeenCalledWith('1');
      expect(component.onUpdate).toBe(true);

      expect(component.sessionForm!.value).toEqual({
        name: fakeSession.name,
        date: "2025-06-27",
        teacher_id: fakeSession.teacher_id,
        description: fakeSession.description
      });
    });

    it('should update session with a new name value', fakeAsync(() => {
      component.sessionForm!.patchValue({ name: 'Session Modifiée' });

      const updateSpy = jest.spyOn(sessionApiMock, 'update');
      const snackSpy  = jest.spyOn(snackBar, 'open');
      const navSpy    = jest.spyOn(router, 'navigate');

      component.submit();
      tick();
      flush()

      expect(updateSpy).toHaveBeenCalledWith('1', {
        name: 'Session Modifiée',
        date: component.sessionForm!.value.date,
        teacher_id: component.sessionForm!.value.teacher_id,
        description: fakeSession.description
      });
      expect(snackSpy).toHaveBeenCalledWith('Session updated !', 'Close', { duration: 3000 });
      expect(navSpy).toHaveBeenCalledWith(['sessions']);
    }));
  });
});
