import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { of, BehaviorSubject } from 'rxjs';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';

import { DetailComponent } from './detail.component';
import { SessionApiService } from '../../services/session-api.service';
import { TeacherService } from 'src/app/services/teacher.service';
import { SessionService } from 'src/app/services/session.service';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import { Session } from '../../interfaces/session.interface';
import { Teacher } from 'src/app/interfaces/teacher.interface';
import { expect } from '@jest/globals';

describe('DetailComponent (intégration)', () => {
  let fixture: ComponentFixture<DetailComponent>;
  let component: DetailComponent;

  // Mocks
  const activatedRoute = {
    snapshot: { paramMap: { get: jest.fn().mockReturnValue('123') } }
  } as unknown as ActivatedRoute;

  const sessionInformation: SessionInformation = {
    admin: true,
    id: 13,
    token: '',
    type: '',
    username: '',
    firstName: '',
    lastName: ''
  };

  const fakeSession: Session = {
    id: 123,
    name: 'Test Session',
    description: 'Desc',
    date: new Date(),
    teacher_id: 7,
    users: [],        // pas inscrit au départ
    createdAt: new Date(),
    updatedAt: new Date()
  };

  const fakeTeacher: Partial<Teacher> = {
    id: 7,
    lastName: "Professeur",
    firstName: "Xavier",
  };

  let sessionServiceMock: Partial<SessionService>;
  let sessionApiMock: Partial<SessionApiService>;
  let teacherServiceMock: Partial<TeacherService>;

  beforeEach(async () => {
    // Stub SessionService
    sessionServiceMock = {
      sessionInformation,
      isLogged: true,
      //@ts-ignore
      isLoggedSubject: new BehaviorSubject<boolean>(true),
      $isLogged: () => of(true)
    };

    // Stub SessionApiService
    sessionApiMock = {
      detail: jest.fn().mockReturnValue(of(fakeSession)),
      delete: jest.fn().mockReturnValue(of(null)),
      participate: jest.fn().mockReturnValue(of(null)),
      unParticipate: jest.fn().mockReturnValue(of(null))
    };

    // Stub TeacherService
    teacherServiceMock = {
      detail: jest.fn().mockReturnValue(of(fakeTeacher))
    };

    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientTestingModule,
        MatSnackBarModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        ReactiveFormsModule
      ],
      declarations: [ DetailComponent ],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: SessionService,    useValue: sessionServiceMock },
        { provide: SessionApiService, useValue: sessionApiMock },
        { provide: TeacherService,    useValue: teacherServiceMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should ftech session and teacher on ngOnInit()', () => {
    // detail() doit avoir été appelé avec l’ID extrait de la route
    expect(sessionApiMock.detail).toHaveBeenCalledWith('123');
    expect(component.session).toEqual(fakeSession);

    // teacherService.detail() appelé avec le teacher_id en string
    expect(teacherServiceMock.detail).toHaveBeenCalledWith('7');
    expect(component.teacher).toEqual(fakeTeacher);

    // initialement non partcipant
    expect(component.isParticipate).toBe(false);
  });

  it('should click on button and navigate back', () => {
    const spy = jest.spyOn(window.history, 'back');
    component.back();
    expect(spy).toHaveBeenCalled();
  });

  it('should delete the session and navigate back', () => {
    const sessionId = '1';
    const snackBarOpenSpy = jest.spyOn(component['matSnackBar'], 'open');

    const sessionApiServiceDeleteSpy = jest
      .spyOn(sessionApiMock, 'delete')
      .mockReturnValue(of(null));

    component.sessionId = sessionId;

    component.delete();

    expect(sessionApiServiceDeleteSpy).toHaveBeenCalledWith(sessionId);

    expect(snackBarOpenSpy).toHaveBeenCalledWith('Session deleted !', 'Close', {
      duration: 3_000,
    });
  });

  it('should participate and fetch the session after', fakeAsync(() => {
    const participateSpy   = jest.spyOn(sessionApiMock, 'participate');
    const detailSpy = jest.spyOn(sessionApiMock, 'detail');

    component.participate();
    tick();

    expect(participateSpy).toHaveBeenCalledWith('123', '13');
    // detail() : 1x en ngOnInit + 1x après participate
    expect(detailSpy).toHaveBeenCalledTimes(2);
  }));

  it('should unparticipate and fetch the session after', fakeAsync(() => {
    const unParticipateSpy = jest.spyOn(sessionApiMock, 'unParticipate');
    const detailSpy = jest.spyOn(sessionApiMock, 'detail');

    component.unParticipate();
    tick();

    expect(unParticipateSpy).toHaveBeenCalledWith('123', '13');
    expect(detailSpy).toHaveBeenCalledTimes(2);
  }));
});
