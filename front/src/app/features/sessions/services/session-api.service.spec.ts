import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { expect } from '@jest/globals';

import { SessionApiService } from './session-api.service';
import { Session } from '../interfaces/session.interface';

describe('SessionApiService', () => {
  let service: SessionApiService;
  let httpMock: HttpTestingController;

  const mockSession: Session = {
    id: 1,
    name: 'Test Session 1',
    description: 'Description de test session 1',
    date: new Date(),
    teacher_id: 1,
    users: [],
  };

  const mockSessions: Session[] = [
    mockSession,
    {
      id: 2,
      name: 'Test Session 2',
      description: 'Description de test session 2',
      date: new Date(),
      teacher_id: 2,
      users: [],
    }
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [SessionApiService]
    });
    
    service = TestBed.inject(SessionApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('all()', () => {
    it('should return all sessions', () => {
      service.all().subscribe(sessions => {
        expect(sessions).toEqual(mockSessions);
        expect(sessions.length).toBe(2);
      });

      const req = httpMock.expectOne('api/session');
      expect(req.request.method).toBe('GET');
      req.flush(mockSessions);
    });

    it('should handle error when getting all sessions', () => {
      service.all().subscribe({
        next: () => fail('should have failed with 500 error'),
        error: (error) => {
          expect(error.status).toBe(500);
        }
      });

      const req = httpMock.expectOne('api/session');
      req.flush('Server error', { status: 500, statusText: 'Internal Server Error' });
    });
  });

  describe('detail()', () => {
    it('should return a specific session by id', () => {
      const sessionId = '1';

      service.detail(sessionId).subscribe(session => {
        expect(session).toEqual(mockSession);
        expect(session.id).toBe(1);
        expect(session.name).toBe('Test Session');
      });

      const req = httpMock.expectOne(`api/session/${sessionId}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockSession);
    });

    it('should handle 404 error when session not found', () => {
      const sessionId = '999';

      service.detail(sessionId).subscribe({
        next: () => fail('should have failed with 404 error'),
        error: (error) => {
          expect(error.status).toBe(404);
        }
      });

      const req = httpMock.expectOne(`api/session/${sessionId}`);
      req.flush('Session not found', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('create()', () => {
    it('should create a new session', () => {
      const newSession: Session = {
        name: 'Nouvelle Session',
        description: 'Description nouvelle',
        date: new Date(),
        teacher_id: 1,
        users: []
      };

      const createdSession: Session = {
        ...newSession,
        id: 3,
      };

      service.create(newSession).subscribe(session => {
        expect(session).toEqual(createdSession);
        expect(session.id).toBe(3);
        expect(session.name).toBe(newSession.name);
      });

      const req = httpMock.expectOne('api/session');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(newSession);
      req.flush(createdSession);
    });

    it('should handle validation error when creating session', () => {
      const invalidSession: Session = {
        name: '',
        description: '',
        date: new Date(),
        teacher_id: 0,
        users: []
      };

      service.create(invalidSession).subscribe({
        next: () => fail('should have failed with validation error'),
        error: (error) => {
          expect(error.status).toBe(400);
        }
      });

      const req = httpMock.expectOne('api/session');
      req.flush('Validation error', { status: 400, statusText: 'Bad Request' });
    });
  });

  describe('update()', () => {
    it('should update an existing session', () => {
      const sessionId = '1';
      const updatedSession: Session = {
        ...mockSession,
        name: 'Session Mise à Jour',
        description: 'Description mise à jour'
      };

      service.update(sessionId, updatedSession).subscribe(session => {
        expect(session).toEqual(updatedSession);
        expect(session.name).toBe('Session Mise à Jour');
      });

      const req = httpMock.expectOne(`api/session/${sessionId}`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(updatedSession);
      req.flush(updatedSession);
    });

    it('should handle 404 error when updating non-existent session', () => {
      const sessionId = '999';
      const updatedSession: Session = mockSession;

      service.update(sessionId, updatedSession).subscribe({
        next: () => fail('should have failed with 404 error'),
        error: (error) => {
          expect(error.status).toBe(404);
        }
      });

      const req = httpMock.expectOne(`api/session/${sessionId}`);
      req.flush('Session not found', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('delete()', () => {
    it('should delete a session', () => {
      const sessionId = '1';

      service.delete(sessionId).subscribe(response => {
        expect(response).toBeTruthy();
      });

      const req = httpMock.expectOne(`api/session/${sessionId}`);
      expect(req.request.method).toBe('DELETE');
      req.flush({});
    });

    it('should handle 404 error when deleting non-existent session', () => {
      const sessionId = '999';

      service.delete(sessionId).subscribe({
        next: () => fail('should have failed with 404 error'),
        error: (error) => {
          expect(error.status).toBe(404);
        }
      });

      const req = httpMock.expectOne(`api/session/${sessionId}`);
      req.flush('Session not found', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('participate()', () => {
    it('should allow user to participate in a session', () => {
      const sessionId = '1';
      const userId = '1';

      service.participate(sessionId, userId).subscribe(response => {
        expect(response).toBeUndefined(); // void return type
      });

      const req = httpMock.expectOne(`api/session/${sessionId}/participate/${userId}`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toBeNull();
      req.flush(null);
    });

    it('should handle error when user already participates', () => {
      const sessionId = '1';
      const userId = '1';

      service.participate(sessionId, userId).subscribe({
        next: () => fail('should have failed with conflict error'),
        error: (error) => {
          expect(error.status).toBe(409);
        }
      });

      const req = httpMock.expectOne(`api/session/${sessionId}/participate/${userId}`);
      req.flush('User already participates', { status: 409, statusText: 'Conflict' });
    });

    it('should handle 404 error when session not found for participation', () => {
      const sessionId = '999';
      const userId = '1';

      service.participate(sessionId, userId).subscribe({
        next: () => fail('should have failed with 404 error'),
        error: (error) => {
          expect(error.status).toBe(404);
        }
      });

      const req = httpMock.expectOne(`api/session/${sessionId}/participate/${userId}`);
      req.flush('Session not found', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('unParticipate()', () => {
    it('should allow user to unparticipate from a session', () => {
      const sessionId = '1';
      const userId = '1';

      service.unParticipate(sessionId, userId).subscribe(response => {
        expect(response).toBeUndefined(); // void return type
      });

      const req = httpMock.expectOne(`api/session/${sessionId}/participate/${userId}`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });

    it('should handle error when user is not participating', () => {
      const sessionId = '1';
      const userId = '1';

      service.unParticipate(sessionId, userId).subscribe({
        next: () => fail('should have failed with not found error'),
        error: (error) => {
          expect(error.status).toBe(404);
        }
      });

      const req = httpMock.expectOne(`api/session/${sessionId}/participate/${userId}`);
      req.flush('User not participating', { status: 404, statusText: 'Not Found' });
    });

    it('should handle 404 error when session not found for unparticipation', () => {
      const sessionId = '999';
      const userId = '1';

      service.unParticipate(sessionId, userId).subscribe({
        next: () => fail('should have failed with 404 error'),
        error: (error) => {
          expect(error.status).toBe(404);
        }
      });

      const req = httpMock.expectOne(`api/session/${sessionId}/participate/${userId}`);
      req.flush('Session not found', { status: 404, statusText: 'Not Found' });
    });
  });

  describe('Edge cases and additional scenarios', () => {
    it('should handle empty array response for all()', () => {
      service.all().subscribe(sessions => {
        expect(sessions).toEqual([]);
        expect(sessions.length).toBe(0);
      });

      const req = httpMock.expectOne('api/session');
      req.flush([]);
    });

    it('should handle network error', () => {
      service.all().subscribe({
        next: () => fail('should have failed with network error'),
        error: (error) => {
          expect(error.error instanceof ProgressEvent).toBeTruthy();
        }
      });

      const req = httpMock.expectOne('api/session');
      req.error(new ProgressEvent('Network error'));
    });

    it('should handle server timeout', () => {
      service.detail('1').subscribe({
        next: () => fail('should have failed with timeout error'),
        error: (error) => {
          expect(error.status).toBe(0);
        }
      });

      const req = httpMock.expectOne('api/session/1');
      req.error(new ProgressEvent('Timeout'), { status: 0 });
    });
  });
});