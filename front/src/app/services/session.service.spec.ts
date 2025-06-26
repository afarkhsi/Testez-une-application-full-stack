import { TestBed } from '@angular/core/testing';
import { SessionService } from './session.service';
import { SessionInformation } from '../interfaces/sessionInformation.interface';
import { expect } from '@jest/globals';

describe('SessionService', () => {
  let service: SessionService;

  // new fake user for testing
  const fakeUser: SessionInformation = {
    id: 1,
    admin: true,
    token: 'jwt',
    type: 'user',
    username: 'RandyHorton',
    firstName: 'Randy',
    lastName: 'Horton'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
  });

  it('Should create component', () => {
    expect(service).toBeTruthy();
  });

  it('should have sessionInformation undefined', () => {
    expect(service.isLogged).toBe(false);
    expect(service.sessionInformation).toBeUndefined();
  });

  it('should be false in $isLogged() and true after logIn()', () => {
    const emissions: boolean[] = [];
    service.$isLogged().subscribe(v => emissions.push(v));

    expect(emissions).toEqual([false]);

    // the fakeUser login
    service.logIn(fakeUser);
    expect(service.isLogged).toBe(true);
    expect(service.sessionInformation).toEqual(fakeUser);

    // $isLogged() should emit true
    expect(emissions).toEqual([false, true]);
  });

  it('should emet false in $isLogged() after logOut', () => {
    const emissions: boolean[] = [];
    service.$isLogged().subscribe(v => emissions.push(v));

    // the fakeUser login
    service.logIn(fakeUser);
    expect(service.isLogged).toBe(true);

    // the fakeUser logout
    service.logOut();
    expect(service.isLogged).toBe(false);
    expect(service.sessionInformation).toBeUndefined();

    // $isLogged() should emit false at the end
    expect(emissions).toEqual([false, true, false]);
  });
});
