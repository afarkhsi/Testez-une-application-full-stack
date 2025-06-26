import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';
import { Session } from '../../interfaces/session.interface';
import { ListComponent } from './list.component';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import { SessionApiService } from '../../services/session-api.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

describe('ListComponent', () => {
  let fixture: ComponentFixture<ListComponent>;
  let component: ListComponent;

  // sessions factices
  const fakeSessions: Session[] = [
    { 
      id:       1,
      name:     'Session A',
      description: 'Desc A',
      date:     new Date('2025-07-01'),
      teacher_id: 2,
      users:    [10, 20],
      createdAt: new Date(),
      updatedAt: new Date()
    },
    {
      id:       2,
      name:     'Session B',
      description: 'Desc B',
      date:     new Date('2025-08-15'),
      teacher_id: 1,
      users:    [],
      createdAt: new Date(),
      updatedAt: new Date()
    }
  ];

  // stub pour SessionService
  const mockSessionInformation: SessionInformation = {
    id: 69,
    admin: true,
    token: '',
    type: '',
    username: 'foo',
    firstName: 'Foo',
    lastName: 'Bar'
  };
  const sessionServiceStub: Partial<SessionService> = {
    sessionInformation: mockSessionInformation
  };

  // stub pour SessionApiService
  const sessionApiStub: Partial<SessionApiService> = {
    all: jest.fn().mockReturnValue(of(fakeSessions))
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ListComponent],
      imports: [
        HttpClientTestingModule,
        MatCardModule,
        MatIconModule
      ],
      providers: [
        { provide: SessionService,    useValue: sessionServiceStub },
        { provide: SessionApiService, useValue: sessionApiStub }
      ]
    }).compileComponents();

    fixture  = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should retieve the list of sessions', fakeAsync(() => {
    expect(sessionApiStub.all).toHaveBeenCalled();

    let emitted: Session[]|undefined;
    component.sessions$.subscribe(data => emitted = data);
    tick();
    expect(emitted).toEqual(fakeSessions);
  }));
});